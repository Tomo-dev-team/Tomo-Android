package com.markoala.tomoandroid.ui.main.meeting.create_meeting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.markoala.tomoandroid.auth.AuthManager
import com.markoala.tomoandroid.data.api.GeocodeAddress
import com.markoala.tomoandroid.data.api.MoimsApiService
import com.markoala.tomoandroid.data.api.friendsApi
import com.markoala.tomoandroid.data.model.CreateMoimDTO
import com.markoala.tomoandroid.data.model.FriendProfile
import com.markoala.tomoandroid.data.model.MoimLocationDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.awaitResponse

class CreateMeetingViewModel(application: Application) : AndroidViewModel(application) {
    val title = MutableStateFlow("")
    val description = MutableStateFlow("")
    val isPublic = MutableStateFlow(true)
    private val _friends = MutableStateFlow<List<FriendProfile>>(emptyList())
    val friends: StateFlow<List<FriendProfile>> = _friends
    private val _selectedEmails = MutableStateFlow<Set<String>>(emptySet())
    val selectedEmails: StateFlow<Set<String>> = _selectedEmails
    private val _selectedAddress = MutableStateFlow<GeocodeAddress?>(null)
    val selectedAddress: StateFlow<GeocodeAddress?> = _selectedAddress
    private val _selectedQuery = MutableStateFlow<String?>(null)
    val selectedQuery: StateFlow<String?> = _selectedQuery
    private val _locationLabel = MutableStateFlow<String?>(null)
    val locationLabel: StateFlow<String?> = _locationLabel
    val isLoading = MutableStateFlow(false)
    val isSuccess = MutableStateFlow<Boolean?>(null)
    val errorMessage = MutableStateFlow<String?>(null)

    init {
        fetchFriends()
    }

    fun fetchFriends() {
        viewModelScope.launch {
            try {
                val response = friendsApi.getFriendsList().awaitResponse()
                if (response.isSuccessful) {
                    _friends.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                if (e is HttpException && e.code() == 401) {
                    AuthManager.handleUnauthorized(getApplication())
                    errorMessage.value = "인증이 필요합니다. 다시 로그인해주세요."
                } else {
                    errorMessage.value = "친구 목록을 불러오지 못했습니다."
                }
            }
        }
    }

    fun toggleEmail(email: String) {
        _selectedEmails.value = _selectedEmails.value.toMutableSet().apply {
            if (contains(email)) remove(email) else add(email)
        }
    }

    fun updateLocation(query: String, address: GeocodeAddress) {
        _selectedQuery.value = query
        _selectedAddress.value = address
        val label = address.displayTitle().ifBlank { query }.ifBlank { "선택한 장소" }
        _locationLabel.value = label
        clearError()
    }

    fun createMoim() {
        if (title.value.isBlank() || description.value.isBlank()) {
            errorMessage.value = "모임 이름과 설명을 입력하세요."
            return
        }
        val location = selectedAddress.value?.toLocationDto()
        if (location == null) {
            errorMessage.value = "모임 위치를 선택해주세요."
            return
        }
        isLoading.value = true
        errorMessage.value = null
        isSuccess.value = null
        viewModelScope.launch {
            try {
                val emails = selectedEmails.value.filter { it.isNotBlank() }.distinct().toList()
                val dto = CreateMoimDTO(
                    title = title.value,
                    description = description.value,
                    isPublic = isPublic.value,
                    emails = emails,
                    location = location
                )
                val response = MoimsApiService.postMoim(dto).awaitResponse()
                isLoading.value = false
                isSuccess.value = response.isSuccessful
                if (!response.isSuccessful) {
                    errorMessage.value = response.errorBody()?.string() ?: "생성 실패"
                }
            } catch (e: Exception) {
                isLoading.value = false
                isSuccess.value = false
                errorMessage.value = "네트워크 오류"
            }
        }
    }

    fun consumeSuccess() {
        isSuccess.value = null
    }

    fun clearError() {
        errorMessage.value = null
    }

    fun resetAllData() {
        title.value = ""
        description.value = ""
        isPublic.value = true
        _selectedEmails.value = emptySet()
        _selectedAddress.value = null
        _selectedQuery.value = null
        _locationLabel.value = null
        errorMessage.value = null
        isSuccess.value = null
        isLoading.value = false
    }
}

private fun GeocodeAddress.toLocationDto(): MoimLocationDTO? {
    val latitude = y?.toDoubleOrNull()
    val longitude = x?.toDoubleOrNull()
    if (latitude == null || longitude == null) return null
    return MoimLocationDTO(latitude = latitude, longitude = longitude)
}

private fun GeocodeAddress.displayTitle(): String {
    return name?.takeIf { it.isNotBlank() }
        ?: roadAddress?.takeIf { it.isNotBlank() }
        ?: jibunAddress?.takeIf { it.isNotBlank() }
        ?: englishAddress?.takeIf { it.isNotBlank() }
        ?: ""
}
