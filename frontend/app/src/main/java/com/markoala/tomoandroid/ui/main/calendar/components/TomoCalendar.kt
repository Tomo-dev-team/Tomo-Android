package com.markoala.tomoandroid.ui.main.calendar.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.data.model.moim.MoimListDTO
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.util.generateCalendarMatrix
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun TomoCalendar(
    events: Map<LocalDate, List<MoimListDTO>>,
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,

){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .border(1.dp, CustomColor.primary100, RoundedCornerShape(20.dp)),
        color = Color(0xFFFAF7F4),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {


            MonthHeader(
                currentMonth = currentMonth,
                primaryBrown = CustomColor.primary,
                espressoText = CustomColor.primaryDim,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )

            Spacer(Modifier.height(16.dp))
            WeekdayHeader(secondaryText = CustomColor.gray300, primaryBrown = CustomColor.primaryDim)
            Spacer(Modifier.height(12.dp))


            MonthlyCalendarGrid(
                events = events,
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,

            )
        }
    }
}

@Composable
private fun MonthHeader(
    currentMonth: YearMonth,
    primaryBrown: Color,
    espressoText: Color,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomText(
            text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
            type = CustomTextType.headline,
            color = espressoText
        )

        Spacer(Modifier.weight(1f))

        HeaderIconLeft(primaryBrown, onPreviousMonth)
        Spacer(Modifier.width(8.dp))
        HeaderIconRight(primaryBrown, onNextMonth)
    }
}

@Composable
private fun HeaderIconLeft(color: Color, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = color.copy(alpha = 0.07f)
    ) {
        IconButton(onClick = onClick) {
            Icon(Icons.Rounded.KeyboardArrowLeft, null, tint = color)
        }
    }
}

@Composable
private fun HeaderIconRight(color: Color, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = color.copy(alpha = 0.07f)
    ) {
        IconButton(onClick = onClick) {
            Icon(Icons.Rounded.KeyboardArrowRight, null, tint = color)
        }
    }
}

@Composable
private fun WeekdayHeader(secondaryText: Color, primaryBrown: Color) {
    val weekdays = listOf("일", "월", "화", "수", "목", "금", "토")

    Row(modifier = Modifier.fillMaxWidth()) {
        weekdays.forEachIndexed { index, day ->
            val color =
                if (index == 0 || index == 6) primaryBrown   // 일·토 → 브라운 강조
                else secondaryText

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CustomText(
                    text = day,
                    color = color,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MonthlyCalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    events: Map<LocalDate, List<MoimListDTO>>,
) {


    val today = LocalDate.now()
    val weeks = remember(currentMonth) { generateCalendarMatrix(currentMonth) }

    Column(Modifier.fillMaxWidth()) {
        weeks.forEach { week ->
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp).height(100.dp) ) {
                week.forEachIndexed { index, date ->
                    val inMonth = date.month == currentMonth.month
                    val selected = date == selectedDate
                    val isToday = date == today
                    val isWeekend = index == 0 || index == 6     // 일=0, 토=6
                    val hasEvent = events[date]?.isNotEmpty() == true

                    CalendarDayCell(
                        date = date,
                        isCurrentMonth = inMonth,
                        isToday = isToday,
                        isWeekend = isWeekend,
                        events = events[date],   // ⬅️ 해당 날짜의 모임 데이터 전달
                        onClick = { if (inMonth) onDateSelected(date) }

                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.CalendarDayCell(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isWeekend: Boolean,
    events: List<MoimListDTO>?,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && isCurrentMonth) 1.05f else 1f,
        label = "scale"
    )

    val dayCircleSize = 28.dp

    val textColor =
        when {
            isToday -> CustomColor.white
            isWeekend -> CustomColor.primary400
            else -> CustomColor.textPrimary
        }

    // ⬅ 여러개 모임 처리: 최대 3개, 앞 4글자 제한
    val badges = events
        ?.take(3)
        ?.map { it.title.take(4) }
        ?: emptyList()

    Column(
        modifier = Modifier
            .weight(1f)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(
                enabled = isCurrentMonth,
                interactionSource = interactionSource,
                indication = null,
                onClick = { }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // 날짜 원
        Box(
            modifier = Modifier
                .size(dayCircleSize)
                .background(
                    if (isToday) CustomColor.primary300 else Color.Transparent,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            CustomText(
                text = "${date.dayOfMonth}",
                type = CustomTextType.body,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }

        // 뱃지 렌더링 (여러개)
        badges.forEach { label ->
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 3.dp)
                    .background(CustomColor.primary100, RoundedCornerShape(3.dp)),
                contentAlignment = Alignment.Center
            ) {
                CustomText(
                    text = label,
                    color = CustomColor.primary400,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(vertical = 2.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
