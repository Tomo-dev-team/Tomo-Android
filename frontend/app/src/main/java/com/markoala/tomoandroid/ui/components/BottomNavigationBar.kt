package com.markoala.tomoandroid.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.main.BottomTab
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun BottomNavigationBar(selectedTab: BottomTab, onTabSelected: (BottomTab) -> Unit) {
    Column {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = CustomColor.gray50,
            thickness = 1.dp
        )
        NavigationBar(containerColor = CustomColor.white) {
            NavigationBarItem(
                selected = selectedTab == BottomTab.Home,
                onClick = { onTabSelected(BottomTab.Home) },
                icon = {
                    Icon(
                        painterResource(id = com.markoala.tomoandroid.R.drawable.ic_home),
                        contentDescription = "홈"
                    )
                },
                label = { Text("홈") }
            )
            NavigationBarItem(
                selected = selectedTab == BottomTab.Friends,
                onClick = { onTabSelected(BottomTab.Friends) },
                icon = {
                    Icon(
                        painterResource(id = com.markoala.tomoandroid.R.drawable.ic_friends),
                        contentDescription = "친구목록"
                    )
                },
                label = { Text("친구목록") }
            )
            NavigationBarItem(
                selected = selectedTab == BottomTab.Profile,
                onClick = { onTabSelected(BottomTab.Profile) },
                icon = {
                    Icon(
                        painterResource(id = com.markoala.tomoandroid.R.drawable.ic_profile),
                        contentDescription = "내정보"
                    )
                },
                label = { Text("내정보") }
            )
            NavigationBarItem(
                selected = selectedTab == BottomTab.Settings,
                onClick = { onTabSelected(BottomTab.Settings) },
                icon = {
                    Icon(
                        painterResource(id = com.markoala.tomoandroid.R.drawable.ic_setting),
                        contentDescription = "설정"
                    )
                },
                label = { Text("설정") }
            )
        }
    }
}