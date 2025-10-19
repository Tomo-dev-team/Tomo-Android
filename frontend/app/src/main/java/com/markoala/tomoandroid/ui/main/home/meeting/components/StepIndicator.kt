package com.markoala.tomoandroid.ui.main.home.meeting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun StepIndicator(currentStep: Int) {
    val steps = listOf(
        "기본 정보",
        "친구 초대",
        "확인"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, title ->
            val stepNumber = index + 1
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (currentStep >= stepNumber) CustomColor.black else CustomColor.gray50,
                    contentColor = if (currentStep >= stepNumber) Color.White else CustomColor.black,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CustomText(
                            text = stepNumber.toString(),
                            type = CustomTextType.body,
                            color = if (currentStep >= stepNumber) Color.White else CustomColor.black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                CustomText(
                    text = title,
                    type = CustomTextType.body,
                    color = if (currentStep == stepNumber) CustomColor.black else CustomColor.gray200
                )
            }

            if (index < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(0.2f)
                        .height(1.dp)
                        .background(if (currentStep > stepNumber) CustomColor.black else CustomColor.gray50)
                )
            }
        }
    }
}