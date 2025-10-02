package com.markoala.tomoandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun IntimacyStatusBar(
    intimacy: Int,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    showPercentage: Boolean = true,
    height: Int = 8,
    width: Int = 100
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {

        // 가로 우정 상태 바
        Box(
            modifier = Modifier
                .width(width.dp)
                .height(height.dp)
                .border(
                    1.dp,
                    CustomColor.gray100,
                    RoundedCornerShape(height.dp / 2)
                )
        ) {
            val fillPercentage = (intimacy / 100f).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width((width.dp * fillPercentage))
                    .align(Alignment.CenterStart)
                    .background(
                        when {
                            intimacy >= 80 -> Color(0xFF4CAF50) // 녹색
                            intimacy >= 50 -> Color(0xFFFFB800) // 노란색
                            intimacy >= 20 -> Color(0xFFFF6B35) // 주황색
                            else -> CustomColor.gray200
                        },
                        RoundedCornerShape(height.dp / 2)
                    )
            )
        }

        if (showPercentage) {
            CustomText(
                text = "${intimacy}%",
                type = CustomTextType.bodySmall,
                color = CustomColor.gray200,
                fontSize = 10.sp
            )
        }
    }
}
