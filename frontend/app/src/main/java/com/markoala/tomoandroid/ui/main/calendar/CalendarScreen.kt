package com.markoala.tomoandroid.ui.main.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun CalendarScreen(
    paddingValues: PaddingValues,
    onEventClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp)
    ) {
        CustomText(
            text = "캘린더",
            type = CustomTextType.headline,
            color = CustomColor.textPrimary
        )

        Spacer(Modifier.height(20.dp))

        // 예: 더미 이벤트 목록
        val events = listOf(
            1 to "팀 저녁 식사",
            2 to "스터디 모임",
            3 to "헬스장 운동"
        )

        events.forEach { (id, label) ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onEventClick(id) },
                color = CustomColor.white,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomText(
                        text = label,
                        type = CustomTextType.body,
                        color = CustomColor.textPrimary
                    )
                }
            }
        }
    }
}
