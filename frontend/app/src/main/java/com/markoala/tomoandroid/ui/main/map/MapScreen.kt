package com.markoala.tomoandroid.ui.main.map

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.markoala.tomoandroid.BuildConfig
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapScreen(
    paddingValues: PaddingValues
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()
    val toastManager = LocalToastManager.current

    val defaultPos = LatLng(37.5666102, 126.9783881)
    val cameraState = rememberCameraPositionState {
        position = CameraPosition(defaultPos, 14.0)
    }

    var hasLocationPermission by remember { mutableStateOf(checkLocationPermission(context)) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasLocationPermission = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!hasLocationPermission) {
            toastManager.showInfo("위치 권한을 허용하면 현재 위치로 이동할 수 있어요.")
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        val clientId = BuildConfig.NAVER_MAP_CLIENT_ID
        if (clientId.isNotBlank()) {
            @Suppress("DEPRECATION")
            NaverMapSdk.getInstance(appContext).client =
                NaverMapSdk.NaverCloudPlatformClient(clientId)
        } else {
            toastManager.showInfo("네이버 지도 클라이언트 ID가 설정되지 않았어요.")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.white)
            .padding(paddingValues)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomText(
            text = "지도",
            type = CustomTextType.headline,
            color = CustomColor.textPrimary
        )
        CustomText(
            text = "네이버 지도로 모임 위치를 탐색할 수 있도록 준비했어요.",
            type = CustomTextType.bodySmall,
            color = CustomColor.textSecondary
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(28.dp),
            color = CustomColor.background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(20.dp),
                    color = CustomColor.white
                ) {
                    NaverMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraState,
                        properties = MapProperties(),
                        uiSettings = MapUiSettings()
                    )
                }
            }
        }

        CustomButton(
            text = if (hasLocationPermission) "현재 위치로 이동" else "위치 권한 요청",
            onClick = {
                if (!hasLocationPermission) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                    return@CustomButton
                }
                scope.launch {
                    try {
                        val latLng = fetchCurrentLatLng(context, fusedClient)
                        if (latLng != null) {
                            cameraState.move(CameraUpdate.scrollTo(latLng))
                            cameraState.move(CameraUpdate.zoomTo(16.0))
                        } else {
                            toastManager.showInfo("현재 위치를 불러올 수 없어요.")
                        }
                    } catch (se: SecurityException) {
                        hasLocationPermission = false
                        toastManager.showInfo("위치 권한을 다시 확인해주세요.")
                    }
                }
            },
            style = ButtonStyle.Primary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))
    }
}

private fun checkLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    return fine || coarse
}

private suspend fun fetchCurrentLatLng(
    context: Context,
    fusedClient: com.google.android.gms.location.FusedLocationProviderClient
): LatLng? {
    if (!checkLocationPermission(context)) return null
    val cancellationTokenSource = CancellationTokenSource()
    val current = fusedClient.getCurrentLocation(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        cancellationTokenSource.token
    ).await()
    val location = current ?: fusedClient.lastLocation.await()
    return location?.let { LatLng(it.latitude, it.longitude) }
}
