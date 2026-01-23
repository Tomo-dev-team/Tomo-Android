package com.markoala.tomoandroid.ui.main.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markoala.tomoandroid.data.api.MoimsApiService
import com.markoala.tomoandroid.data.model.BaseResponse
import com.markoala.tomoandroid.data.model.MoimListDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class MapViewModel : ViewModel() {
    private val _publicMoims = MutableStateFlow<List<MoimListDTO>>(emptyList())
    val publicMoims: StateFlow<List<MoimListDTO>> = _publicMoims

    private val _selectedMoim = MutableStateFlow<MoimListDTO?>(null)
    val selectedMoim: StateFlow<MoimListDTO?> = _selectedMoim

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchPublicMoims()
    }

    fun fetchPublicMoims() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = MoimsApiService.getAllPublicMoims().awaitResponse()
                if (response.isSuccessful) {
                    val body: BaseResponse<List<MoimListDTO>>? = response.body()
                    if (body?.success == true) {
                        val moims = body.data.orEmpty()
                        _publicMoims.value = moims
                        if (moims.none { it.moimId == _selectedMoim.value?.moimId }) {
                            _selectedMoim.value = null
                        }
                    } else {
                        _errorMessage.value = body?.message ?: "공개 모임을 불러오지 못했어요."
                    }
                } else {
                    _errorMessage.value = "공개 모임을 불러오지 못했어요."
                }
            } catch (e: Exception) {
                _errorMessage.value = "공개 모임을 불러오는 중 문제가 발생했어요."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectMoim(moim: MoimListDTO) {
        _selectedMoim.value = moim
    }

    fun clearSelection() {
        _selectedMoim.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
