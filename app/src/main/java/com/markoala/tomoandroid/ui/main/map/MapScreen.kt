package com.markoala.tomoandroid.ui.main.map

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapAuthException
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelTextBuilder
import com.markoala.tomoandroid.R
import com.markoala.tomoandroid.data.api.GeocodeAddress
import com.markoala.tomoandroid.data.model.MoimListDTO
import com.markoala.tomoandroid.data.model.MoimLocationDTO
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LocalToastManager
import com.markoala.tomoandroid.ui.theme.CustomColor
import com.markoala.tomoandroid.utils.LocationPermissionHelper
import com.markoala.tomoandroid.utils.parseIsoToKoreanDate
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun MapScreen(
    paddingValues: PaddingValues,
    selectedAddress: GeocodeAddress?,
    selectedQuery: String?,
    onSearchClick: () -> Unit,
    onCreatePromiseWithLocation: (GeocodeAddress, String?) -> Unit = { _, _ -> },
    interactive: Boolean = true,
    isPromise: Boolean = true,
    showSearchOverlay: Boolean = true,
    showPublicMoims: Boolean = false
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val toastManager = LocalToastManager.current

    val defaultPos = LatLng.from(37.5666102, 126.9783881)
    val selectedAddressState = rememberUpdatedState(selectedAddress)
    val mapViewModel = if (showPublicMoims) viewModel<MapViewModel>() else null
    val publicMoimsState = mapViewModel?.publicMoims?.collectAsState()
    val selectedMoimState = mapViewModel?.selectedMoim?.collectAsState()
    val isLoadingPublicMoimsState = mapViewModel?.isLoading?.collectAsState()
    val errorMessageState = mapViewModel?.errorMessage?.collectAsState()
    val publicMoims = publicMoimsState?.value.orEmpty()
    val selectedMoim = selectedMoimState?.value
    val isLoadingPublicMoims = isLoadingPublicMoimsState?.value ?: false
    val errorMessage = errorMessageState?.value

    var hasLocationPermission by remember { mutableStateOf(LocationPermissionHelper.isLocationPermissionGranted(context)) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasLocationPermission = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!hasLocationPermission) {
            toastManager.showInfo("위치 권한을 허용하면 현재 위치로 이동할 수 있어요.")
        }
    }

    LaunchedEffect(isPromise) {
        if (isPromise && !hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            toastManager.showInfo(errorMessage)
            mapViewModel?.clearError()
        }
    }

    val mapView = remember { MapView(appContext) }
    var kakaoMap by remember { mutableStateOf<KakaoMap?>(null) }
    var marker by remember { mutableStateOf<Label?>(null) }
    var currentLocationMarker by remember { mutableStateOf<Label?>(null) }
    var moimMarkers by remember { mutableStateOf<List<Label>>(emptyList()) }
    var bottomCardHeight by remember { mutableStateOf(0.dp) }
    val markerStyles = remember(appContext) {
        MarkerStyles(
            primary = createMarkerStyle(appContext, R.drawable.ic_marker_primary),
            current = createMarkerStyle(appContext, R.drawable.ic_marker_current),
            moim = createMarkerStyle(appContext, R.drawable.ic_marker_moim)
        )
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val mapLifeCycle = object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                marker?.remove()
                marker = null
                currentLocationMarker?.remove()
                currentLocationMarker = null
                moimMarkers.forEach { it.remove() }
                moimMarkers = emptyList()
            }

            override fun onMapError(error: Exception) {
                val message = if (error is MapAuthException) {
                    "카카오맵 인증에 실패했어요. 키 설정을 확인해주세요."
                } else {
                    "지도 초기화 중 오류가 발생했어요."
                }
                toastManager.showInfo(message)
                Log.e("MapScreen", "Kakao map error", error)
            }
        }

        val readyCallback = object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                kakaoMap = map
                val target = selectedAddressState.value?.toLatLng() ?: defaultPos
                map.moveCamera(CameraUpdateFactory.newCenterPosition(target, 14))
                selectedAddressState.value?.let { address ->
                    marker = placeMarker(
                        map = map,
                        currentLabel = marker,
                        position = target,
                        title = address.displayTitle(),
                        labelId = "selected_marker",
                        style = markerStyles.primary
                    )
                }
                currentLocation?.let { location ->
                    currentLocationMarker = placeMarker(
                        map = map,
                        currentLabel = currentLocationMarker,
                        position = location,
                        title = "현재 위치",
                        labelId = "current_marker",
                        style = markerStyles.current
                    )
                }
                if (showPublicMoims && interactive) {
                    map.setOnLabelClickListener { _, _, label ->
                        val tag = label.tag
                        if (tag is MoimListDTO) {
                            mapViewModel?.selectMoim(tag)
                            true
                        } else {
                            false
                        }
                    }
                    map.setOnMapClickListener { _, _, _, _ ->
                        mapViewModel?.clearSelection()
                    }
                }
            }
        }

        mapView.start(mapLifeCycle, readyCallback)

        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    mapView.resume()
                    hasLocationPermission = LocationPermissionHelper.isLocationPermissionGranted(context)
                }
                Lifecycle.Event.ON_PAUSE -> mapView.pause()
                Lifecycle.Event.ON_DESTROY -> mapView.finish()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)

        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            mapView.resume()
        }

        onDispose {
            lifecycle.removeObserver(observer)
            mapView.finish()
        }
    }

    LaunchedEffect(kakaoMap, selectedAddress?.x, selectedAddress?.y) {
        val map = kakaoMap ?: return@LaunchedEffect
        val target = selectedAddress?.toLatLng()
        if (target != null) {
            mapViewModel?.clearSelection()
            map.moveCamera(CameraUpdateFactory.newCenterPosition(target, 16))
            marker = placeMarker(
                map = map,
                currentLabel = marker,
                position = target,
                title = selectedAddress.displayTitle(),
                labelId = "selected_marker",
                style = markerStyles.primary
            )
        } else {
            marker?.remove()
            marker = null
        }
    }

    LaunchedEffect(kakaoMap, currentLocation) {
        val map = kakaoMap ?: return@LaunchedEffect
        val location = currentLocation ?: run {
            currentLocationMarker?.remove()
            currentLocationMarker = null
            return@LaunchedEffect
        }
        currentLocationMarker = placeMarker(
            map = map,
            currentLabel = currentLocationMarker,
            position = location,
            title = "현재 위치",
            labelId = "current_marker",
            style = markerStyles.current
        )
    }

    LaunchedEffect(kakaoMap, publicMoims) {
        val map = kakaoMap ?: return@LaunchedEffect
        moimMarkers.forEach { it.remove() }
        moimMarkers = emptyList()
        if (!showPublicMoims || publicMoims.isEmpty()) return@LaunchedEffect
        moimMarkers = createMoimMarkers(map, markerStyles.moim, publicMoims)
    }

    LaunchedEffect(selectedMoim, selectedAddress) {
        if (selectedMoim == null && selectedAddress == null) {
            bottomCardHeight = 0.dp
        }
    }

    val locationButtonBottomPadding =
        if (bottomCardHeight > 0.dp) bottomCardHeight + 30.dp else 24.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomColor.white)
            .padding(paddingValues)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { !interactive },
            factory = {
                mapView.apply {
                    isEnabled = interactive
                    isClickable = interactive
                    isLongClickable = interactive
                    setOnTouchListener(
                        if (interactive) { v, event ->
                            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                                v.parent?.requestDisallowInterceptTouchEvent(true)
                            }
                            false
                        } else { _, _ ->
                            true
                        }
                    )
                }
            }
        )

        if (showSearchOverlay) {
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
        }

        if (showPublicMoims) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = if (showSearchOverlay) 84.dp else 16.dp,
                        end = 16.dp
                    ),
                shape = RoundedCornerShape(999.dp),
                color = CustomColor.white,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, CustomColor.outline)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoadingPublicMoims) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = CustomColor.primary
                        )
                    }
                    val labelText = if (isLoadingPublicMoims) {
                        "공개 모임 불러오는 중"
                    } else {
                        "공개 모임 ${publicMoims.size}개"
                    }
                    CustomText(
                        text = labelText,
                        type = CustomTextType.bodySmall,
                        color = CustomColor.textSecondary
                    )
                }
            }
        }

        if (selectedMoim != null || selectedAddress != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .onGloballyPositioned { coordinates ->
                        bottomCardHeight = with(density) { coordinates.size.height.toDp() }
                    },
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                selectedMoim?.let { moim ->
                    MoimInfoCard(
                        moim = moim,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                selectedAddress?.let { address ->
                    SelectedAddressCard(
                        address = address,
                        selectedQuery = selectedQuery,
                        isPromise = isPromise,
                        onCreatePromiseWithLocation = onCreatePromiseWithLocation,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        if (isPromise) {

            // hasLocationPermission == true → 아이콘 버튼 표시
            if (hasLocationPermission) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = locationButtonBottomPadding, end = 16.dp)
                        .size(45.dp)
                        .background(
                            color = CustomColor.primary,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .clickable {
                            scope.launch {
                                try {
                                    val latLng = fetchCurrentLatLng(context, fusedClient)
                                    if (latLng != null) {
                                        kakaoMap?.moveCamera(
                                            CameraUpdateFactory.newCenterPosition(latLng, 16)
                                        ) ?: toastManager.showInfo("지도를 준비하는 중이에요.")
                                        currentLocation = latLng
                                    } else {
                                        toastManager.showInfo("현재 위치를 불러올 수 없어요.")
                                    }
                                } catch (se: SecurityException) {
                                    hasLocationPermission = false
                                    toastManager.showInfo("위치 권한을 다시 확인해주세요.")
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_current_location),
                        contentDescription = "현재 위치",
                        modifier = Modifier.size(24.dp),
                        tint = CustomColor.white
                    )
                }
            }
            // hasLocationPermission == false → 기존 버튼 유지
            else {
                CustomButton(
                    text = "위치 권한 요청",
                    onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    style = ButtonStyle.Primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }

    }
}

@Composable
private fun SelectedAddressCard(
    address: GeocodeAddress,
    selectedQuery: String?,
    isPromise: Boolean,
    onCreatePromiseWithLocation: (GeocodeAddress, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
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
            if (isPromise) {
                CustomButton(
                    text = "이 장소로 약속 잡기",
                    onClick = { onCreatePromiseWithLocation(address, selectedQuery) },
                    style = ButtonStyle.Primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun MoimInfoCard(
    moim: MoimListDTO,
    modifier: Modifier = Modifier
) {
    val createdAt = parseIsoToKoreanDate(moim.createdAt).ifBlank { moim.createdAt }
    val descriptionPreview = moim.description.preview(90)
    val locationText = formatLatLng(moim.location)
    val visibilityLabel = if (moim.isPublic) "공개 모임" else "비공개 모임"

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = CustomColor.white,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CustomColor.white, CustomColor.primary50)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(36.dp)
                    .height(4.dp)
                    .background(CustomColor.gray200, RoundedCornerShape(999.dp))
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoimTagChip(
                    text = visibilityLabel,
                    background = CustomColor.primary100,
                    contentColor = CustomColor.primaryDim
                )
                if (moim.leader) {
                    MoimTagChip(
                        text = "리더",
                        background = CustomColor.secondaryContainer,
                        contentColor = CustomColor.secondary
                    )
                }
            }

            CustomText(
                text = moim.title,
                type = CustomTextType.title,
                color = CustomColor.textPrimary
            )

            if (descriptionPreview.isNotBlank()) {
                CustomText(
                    text = descriptionPreview,
                    type = CustomTextType.bodySmall,
                    color = CustomColor.textBody
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                MoimMetaChip(
                    icon = R.drawable.ic_people,
                    text = "참여 ${moim.emails.size}명"
                )
                if (createdAt.isNotBlank()) {
                    MoimMetaChip(
                        icon = R.drawable.ic_calendar,
                        text = createdAt
                    )
                }
                MoimMetaChip(
                    icon = R.drawable.ic_location,
                    text = locationText
                )
            }
        }
    }
}

@Composable
private fun MoimTagChip(
    text: String,
    background: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = background,
        border = BorderStroke(1.dp, CustomColor.outline)
    ) {
        CustomText(
            text = text,
            type = CustomTextType.label,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun MoimMetaChip(
    icon: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CustomColor.white.copy(alpha = 0.9f),
        border = BorderStroke(1.dp, CustomColor.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = CustomColor.primary50,
                border = BorderStroke(1.dp, CustomColor.outline)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = CustomColor.primaryDim,
                    modifier = Modifier.padding(6.dp).size(14.dp)
                )
            }
            CustomText(
                text = text,
                type = CustomTextType.bodySmall,
                color = CustomColor.textSecondary
            )
        }
    }
}

private fun placeMarker(
    map: KakaoMap,
    currentLabel: Label?,
    position: LatLng,
    title: String,
    labelId: String,
    style: LabelStyle?
): Label? {
    currentLabel?.remove()
    val layer: LabelLayer = map.labelManager?.layer ?: return null
    val resolvedStyle = style ?: return null
    val options = LabelOptions.from(labelId, position)
        .setStyles(resolvedStyle)
        .setTexts(LabelTextBuilder().setTexts(title.take(30)))
    return layer.addLabel(options)
}

private fun createMoimMarkers(
    map: KakaoMap,
    style: LabelStyle?,
    moims: List<MoimListDTO>
): List<Label> {
    val layer: LabelLayer = map.labelManager?.layer ?: return emptyList()
    val resolvedStyle = style ?: return emptyList()
    return moims.mapNotNull { moim ->
        val position = LatLng.from(moim.location.latitude, moim.location.longitude)
        val options = LabelOptions.from("moim_${moim.moimId}", position)
            .setStyles(resolvedStyle)
            .setClickable(true)
            .setTag(moim)
        layer.addLabel(options)
    }
}

private fun String.preview(maxChars: Int): String {
    val trimmed = trim()
    return if (trimmed.length > maxChars) {
        trimmed.take(maxChars).trimEnd() + "..."
    } else {
        trimmed
    }
}

private fun formatLatLng(location: MoimLocationDTO): String {
    return String.format(Locale.US, "%.5f, %.5f", location.latitude, location.longitude)
}

private data class MarkerStyles(
    val primary: LabelStyle?,
    val current: LabelStyle?,
    val moim: LabelStyle?
)

private fun createMarkerStyle(context: Context, @DrawableRes drawableResId: Int): LabelStyle? {
    val iconBitmap = bitmapFromVector(context, drawableResId) ?: return null
    return LabelStyle.from(iconBitmap)
        .setAnchorPoint(0.5f, 1f)
        .setApplyDpScale(false)
}

private fun bitmapFromVector(context: Context, @DrawableRes drawableResId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(context, drawableResId) ?: return null
    val metrics = context.resources.displayMetrics
    val fallbackSize = (24f * metrics.density).roundToInt().coerceAtLeast(1)
    val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: fallbackSize
    val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: fallbackSize
    val targetWidth = nextPowerOfTwo(width)
    val targetHeight = nextPowerOfTwo(height)
    val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
    bitmap.density = metrics.densityDpi
    val canvas = Canvas(bitmap)
    val left = ((targetWidth - width) / 2).coerceAtLeast(0)
    val top = (targetHeight - height).coerceAtLeast(0)
    drawable.setBounds(left, top, left + width, top + height)
    drawable.draw(canvas)
    bitmap.setHasAlpha(true)
    bitmap.setPremultiplied(true)
    return bitmap
}

private fun nextPowerOfTwo(value: Int): Int {
    if (value <= 1) return 1
    val highest = Integer.highestOneBit(value)
    return if (value == highest) value else highest shl 1
}

private fun GeocodeAddress.toLatLng(): LatLng? {
    val lat = y?.toDoubleOrNull()
    val lng = x?.toDoubleOrNull()
    return if (lat != null && lng != null) {
        LatLng.from(lat, lng)
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

private suspend fun fetchCurrentLatLng(
    context: Context,
    fusedClient: com.google.android.gms.location.FusedLocationProviderClient
): LatLng? {
    if (!LocationPermissionHelper.isLocationPermissionGranted(context)) return null
    return try {
        val cancellationTokenSource = CancellationTokenSource()
        val current = fusedClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            cancellationTokenSource.token
        ).await()
        val location = current ?: fusedClient.lastLocation.await()
        location?.let { LatLng.from(it.latitude, it.longitude) }
    } catch (se: SecurityException) {
        null
    }
}
