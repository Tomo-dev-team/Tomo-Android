package com.markoala.tomoandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun LoadingDialog() {
    Dialog(onDismissRequest = { }) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(CustomColor.white, shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
