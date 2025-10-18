package com.markoala.tomoandroid.ui.main.meeting

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun CreateMeetingScreen(
    paddingValues: PaddingValues,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit = {}
) {
    val viewModel: CreateMeetingViewModel = viewModel()
    val moimName by viewModel.moimName.collectAsState()
    val description by viewModel.description.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val selectedEmails by viewModel.selectedEmails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    if (isSuccess == true) {
        onSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomText(
                text = "모임 생성",
                type = CustomTextType.headlineLarge,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = CustomColor.gray100,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .clickable { onBackClick() },
                shape = RoundedCornerShape(32.dp),
                color = CustomColor.white
            ) {
                Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)) {
                    CustomText(
                        text = "목록보기",
                        type = CustomTextType.titleSmall,
                        fontSize = 14.sp,
                        color = CustomColor.black
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = moimName,
            onValueChange = { viewModel.moimName.value = it },
            label = { CustomText(text = "모임 이름", type = CustomTextType.bodyMedium) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { viewModel.description.value = it },
            label = { CustomText(text = "설명", type = CustomTextType.bodyMedium) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        CustomText(text = "친구 초대", type = CustomTextType.titleSmall, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        if (friends.isEmpty()) {
            CustomText(
                text = "초대할 친구가 없습니다.",
                type = CustomTextType.bodyMedium,
                color = CustomColor.gray200
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                friends.forEach { friend ->
                    val selected = selectedEmails.contains(friend.email)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleEmail(friend.email) }
                            .border(
                                width = 1.dp,
                                color = if (selected) CustomColor.gray300 else CustomColor.gray100,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomText(
                            text = friend.username,
                            type = CustomTextType.bodyMedium,
                            color = if (selected) CustomColor.gray300 else CustomColor.black
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (selected) {
                            CustomText(
                                text = "선택됨",
                                type = CustomTextType.bodySmall,
                                color = CustomColor.gray300
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (errorMessage != null) {
            CustomText(
                text = errorMessage ?: "",
                type = CustomTextType.bodySmall,
                color = CustomColor.gray300
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Button(
            onClick = { viewModel.createMoim() },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
            }
            CustomText(text = "모임 생성", type = CustomTextType.bodyMedium, color = CustomColor.white)
        }
    }
}
