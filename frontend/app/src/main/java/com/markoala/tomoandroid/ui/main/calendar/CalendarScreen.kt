package com.markoala.tomoandroid.ui.main.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.data.model.moim.MoimListDTO
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.MorphingDots
import com.markoala.tomoandroid.ui.main.calendar.components.TomoCalendar
import com.markoala.tomoandroid.ui.main.meeting.MeetingViewModel
import com.markoala.tomoandroid.ui.theme.CustomColor
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    paddingValues: PaddingValues,
    meetingViewModel: MeetingViewModel = viewModel(),
    onEventClick: (Int) -> Unit = {}
) {
    val cardIvory = Color(0xFFFAF7F4)

    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedDate by remember { mutableStateOf(today) }

    val meetings by meetingViewModel.meetings.collectAsState()
    val isLoading by meetingViewModel.isLoading.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var dialogEvents by remember { mutableStateOf<List<MoimListDTO>?>(null) }


    val eventMap = remember(meetings) {
        meetings
            .mapNotNull { moim ->
                runCatching {
                    val date = LocalDate.parse(moim.createdAt.substring(0, 10))
                    date to moim
                }.getOrNull()
            }
            .groupBy({ it.first }, { it.second })
    }


    LaunchedEffect(eventMap) {
        println("🔥 eventMap =")
        eventMap.forEach { (date, list) ->
            println("$date -> ${list.map { it.title }}")
        }
    }



    // Lifecycle: 화면 복귀 시 데이터 다시 불러오기
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                meetingViewModel.fetchMeetings()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 🔥 로딩 다이얼로그 표시
    if (isLoading) {
        MorphingDots()
    }

    if (dialogEvents != null) {
        AlertDialog(
            onDismissRequest = { dialogEvents = null },
            confirmButton = {},
            title = {
                CustomText("모임 목록")
            },
            text = {
                Column {
                    dialogEvents!!.forEach { moim ->
                        CustomText(
                            text = moim.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    dialogEvents = null
                                    onEventClick(moim.moimId)
                                }
                                .padding(12.dp)
                        )
                    }
                }
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(CustomColor.white)
            .padding(paddingValues)
            .padding(top=24.dp,end=8.dp, start=8.dp,bottom=24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TomoCalendar(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            onPreviousMonth = {
                currentMonth = currentMonth.minusMonths(1)
                selectedDate = currentMonth.atDay(1)
            },
            onNextMonth = {
                currentMonth = currentMonth.plusMonths(1)
                selectedDate = currentMonth.atDay(1)
            },
            onDateSelected = { selectedDate = it },
            events = eventMap,
            onDayClick = { date, items ->
                selectedDate = date
                dialogEvents = items
            },
        )
        Spacer(Modifier.height(20.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .border(1.dp, CustomColor.primary100, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = cardIvory,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CustomText(
                    text = "조금만 기다려 주세요, \n약속 기능이 곧 열릴 거예요!",
                    type = CustomTextType.body,
                    color = CustomColor.primary400,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
