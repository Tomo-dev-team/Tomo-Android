package com.markoala.tomoandroid.ui.main.meeting.create_meeting.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.markoala.tomoandroid.data.api.GeocodeAddress
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextField
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.main.map.MapScreen
import com.markoala.tomoandroid.ui.main.settings.components.SettingsToggle
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun StepOneSection(
    title: String,
    description: String,
    isPublic: Boolean,
    locationLabel: String?,
    selectedAddress: GeocodeAddress?,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPublicChange: (Boolean) -> Unit,
    onSearchLocation: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth()
            .background(CustomColor.white),
        shape = RoundedCornerShape(24.dp),
        color = CustomColor.primary50
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomText(text = "모임 제목", type = CustomTextType.title, color = CustomColor.primary, fontSize = 16.sp
                )
                CustomTextField(
                    value = title,
                    onValueChange = onNameChange,
                    placeholder = "모임명을 입력해주세요"
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomText(text = "모임 설명", type = CustomTextType.title, color = CustomColor.primary, fontSize = 16.sp)
                CustomTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    placeholder = "모임 설명을 적어주세요",
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomText(
                    text = "공개 설정",
                    type = CustomTextType.title,
                    color = CustomColor.primary,
                    fontSize = 16.sp
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = CustomColor.white
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                        SettingsToggle(
                            title = "모임 공개",
                            description = if (isPublic) {
                                "모든 사용자가 이 모임을 볼 수 있어요."
                            } else {
                                "초대받은 사람만 모임을 볼 수 있어요."
                            },
                            checked = isPublic,
                            onCheckedChange = onPublicChange
                        )
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomText(
                    text = "모임 위치",
                    type = CustomTextType.title,
                    color = CustomColor.primary,
                    fontSize = 16.sp
                )
                CustomText(
                    text = "카카오 지도에서 위치를 선택해주세요.",
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
                CustomButton(
                    text = "장소 검색하기",
                    onClick = onSearchLocation,
                    style = ButtonStyle.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = CustomColor.white,
                    shadowElevation = 2.dp
                ) {
                    MapScreen(
                        paddingValues = PaddingValues(0.dp),
                        selectedAddress = selectedAddress,
                        selectedQuery = locationLabel,
                        onSearchClick = onSearchLocation,
                        interactive = false,
                        isPromise = false,
                        showSearchOverlay = false
                    )
                }
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = CustomColor.gray100
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CustomText(
                            text = "선택된 위치",
                            type = CustomTextType.bodySmall,
                            color = CustomColor.textSecondary
                        )
                        CustomText(
                            text = locationLabel ?: "아직 위치를 선택하지 않았어요.",
                            type = CustomTextType.body,
                            color = if (locationLabel.isNullOrBlank()) {
                                CustomColor.gray500
                            } else {
                                CustomColor.textPrimary
                            }
                        )
                    }
                }
            }
        }
    }
}
