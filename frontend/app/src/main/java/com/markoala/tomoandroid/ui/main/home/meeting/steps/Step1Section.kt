package com.markoala.tomoandroid.ui.main.home.meeting.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextField
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun StepOneSection(
    moimName: String,
    description: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        CustomText(
            text = "모임 제목",
            type = CustomTextType.title,
            color = CustomColor.gray300,
            fontSize = 16.sp
        )
        CustomTextField(
            value = moimName,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "모임명을 입력해주세요"
        )
        CustomText(
            text = "모임 설명",
            type = CustomTextType.title,
            color = CustomColor.gray300,
            fontSize = 16.sp
        )
        CustomTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "모임 설명을 적어주세요"
        )
    }
}