package com.markoala.tomoandroid.ui.main.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markoala.tomoandroid.data.api.apiService
import com.markoala.tomoandroid.data.model.FriendProfile
import com.markoala.tomoandroid.data.model.FriendsListDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsViewModel : ViewModel() {
    private val _friends = MutableStateFlow<List<FriendProfile>>(emptyList())
    val friends: StateFlow<List<FriendProfile>> = _friends.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadFriends()
    }

    fun loadFriends() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            apiService.getFriendsList().enqueue(object : Callback<FriendsListDTO> {
                override fun onResponse(
                    call: Call<FriendsListDTO>,
                    response: Response<FriendsListDTO>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        response.body()?.let { friendsListDTO ->
                            if (friendsListDTO.success) {
                                _friends.value = friendsListDTO.data
                            } else {
                                _error.value = friendsListDTO.message
                            }
                        }
                    } else {
                        _error.value = "친구 목록을 불러오는데 실패했습니다."
                    }
                }

                override fun onFailure(call: Call<FriendsListDTO>, t: Throwable) {
                    _isLoading.value = false
                    _error.value = "네트워크 오류가 발생했습니다: ${t.message}"
                }
            })
        }
    }

    fun refreshFriends() {
        loadFriends()
    }
}
