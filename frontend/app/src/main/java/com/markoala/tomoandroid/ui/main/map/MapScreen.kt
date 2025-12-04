package com.markoala.tomoandroid.ui.main.map

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.markoala.tomoandroid.data.api.GeocodeAddress
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
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.rememberMarkerState
import com.naver.maps.map.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MapScreen(
    paddingValues: PaddingValues,
    selectedAddress: GeocodeAddress?,
    selectedQuery: String?,
    onSearchClick: () -> Unit
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
        Log.d("MapScreen", "Naver Map Client ID: $clientId")
        if (clientId.isNotBlank()) {
            @Suppress("DEPRECATION")
            NaverMapSdk.getInstance(appContext).client =
                NaverMapSdk.NcpKeyClient(clientId)
        } else {
            toastManager.showInfo("네이버 지도 클라이언트 ID가 설정되지 않았어요.")
        }
    }

    LaunchedEffect(selectedAddress?.x, selectedAddress?.y) {
        val target = selectedAddress?.toLatLng() ?: return@LaunchedEffect
        cameraState.animate(CameraUpdate.scrollTo(target))
        cameraState.animate(CameraUpdate.zoomTo(16.0))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.white)
            .padding(paddingValues)
    ) {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            properties = MapProperties(),
            uiSettings = MapUiSettings()
        ) {
            selectedAddress?.toLatLng()?.let { latLng ->
                Marker(
                    state = rememberMarkerState(position = latLng),
                    captionText = selectedAddress.displayTitle()
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable { onSearchClick() },
            shape = RoundedCornerShape(14.dp),
            color = CustomColor.white,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CustomText(
                    text = "모임을 가질 장소를 검색해보세요.",
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textSecondary
                )
                CustomText(
                    text = selectedQuery?.takeIf { it.isNotBlank() }
                        ?: "장소를 검색하려면 눌러주세요",
                    type = CustomTextType.body,
                    color = CustomColor.textPrimary
                )
            }
        }

        selectedAddress?.let { address ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 72.dp),
                shape = RoundedCornerShape(16.dp),
                color = CustomColor.white,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val title = address.displayTitle()
                    CustomText(
                        text = title,
                        type = CustomTextType.body,
                        color = CustomColor.textPrimary
                    )
                    address.roadAddress
                        ?.takeIf { it.isNotBlank() && it != title }
                        ?.let {
                            CustomText(
                                text = it,
                                type = CustomTextType.bodySmall,
                                color = CustomColor.textSecondary
                            )
                        }
                    address.jibunAddress
                        ?.takeIf { it.isNotBlank() && it != title }
                        ?.let {
                            CustomText(
                                text = it,
                                type = CustomTextType.bodySmall,
                                color = CustomColor.textSecondary
                            )
                        }
                    address.englishAddress
                        ?.takeIf { it.isNotBlank() }
                        ?.let {
                            CustomText(
                                text = it,
                                type = CustomTextType.bodySmall,
                                color = CustomColor.textSecondary
                            )
                        }
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
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

private fun GeocodeAddress.toLatLng(): LatLng? {
    val lat = y?.toDoubleOrNull()
    val lng = x?.toDoubleOrNull()
    return if (lat != null && lng != null) {
        LatLng(lat, lng)
    } else {
        null
    }
}

private fun GeocodeAddress.displayTitle(): String {
    return name?.takeIf { it.isNotBlank() }
        ?: roadAddress?.takeIf { it.isNotBlank() }
        ?: jibunAddress?.takeIf { it.isNotBlank() }
        ?: englishAddress?.takeIf { it.isNotBlank() }
        ?: "선택한 장소 정보를 불러올 수 없어요."
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
