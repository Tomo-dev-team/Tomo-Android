package com.markoala.tomoandroid.ui.main.home.meeting.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType

@Composable
fun NavigationBottomButtons(
    currentStep: Int,
    isLoading: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (currentStep > 1) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f)
            ) {
                CustomText(text = "이전", type = CustomTextType.body)
            }
        }

        Button(
            onClick = onNext,
            enabled = canGoNext && !isLoading,
            modifier = Modifier.weight(1f)
        ) {
            if (isLoading && currentStep == 3) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(18.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            CustomText(
                text = if (currentStep < 3) "다음" else "모임 만들기",
                type = CustomTextType.body,
                color = Color.White
            )
        }
    }
}
