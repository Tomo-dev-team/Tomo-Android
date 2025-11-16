package com.markoala.tomoandroid.ui.main.friends.components

import androidx.compose.runtime.Composable
import com.markoala.tomoandroid.ui.components.DangerDialog

@Composable
fun DeleteFriendDialog(
    friendName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    DangerDialog(
        title = "친구 삭제",
        message = "${friendName}님을 친구 목록에서 삭제하시겠어요?\n이 작업은 되돌릴 수 없습니다.",
        confirmText = "삭제",
        dismissText = "취소",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}
