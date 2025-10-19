package com.markoala.tomoandroid.ui.main.friends.add_friends

import androidx.lifecycle.ViewModel
import com.markoala.tomoandroid.data.model.friends.FriendSummary
import com.markoala.tomoandroid.data.repository.friends.FriendsRepository
import com.markoala.tomoandroid.ui.components.ToastManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddFriendsViewModel : ViewModel() {
    private val friendsRepository = FriendsRepository()
    private val toastManager = ToastManager()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _searchResults = MutableStateFlow<List<FriendSummary>>(emptyList())
    val searchResults: StateFlow<List<FriendSummary>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _selectedOption = MutableStateFlow("email")
    val selectedOption: StateFlow<String> = _selectedOption

    fun setSearchText(text: String) {
        _searchText.value = text
    }

    fun setSelectedOption(option: String) {
        _selectedOption.value = option
    }

    fun searchFriends() {
        val email = _searchText.value
        if (email.isBlank()) {
            toastManager.showWarning("이메일을 입력해주세요.")
            return
        }
        _isLoading.value = true
        _searchResults.value = emptyList()
        _errorMessage.value = null
        friendsRepository.getFriends(
            email = email,
            onLoading = { loading ->
                _isLoading.value = loading
                if (loading) {
                    _searchResults.value = emptyList()
                    _errorMessage.value = null
                }
            },
            onSuccess = { friends ->
                _searchResults.value = friends
                _errorMessage.value = null
                if (friends.isEmpty()) {
                    toastManager.showInfo("검색 결과가 없습니다.")
                } else {
                    toastManager.showSuccess("사용자를 찾았습니다.")
                }
            },
            onError = { error ->
                _searchResults.value = emptyList()
                _errorMessage.value = error
                if (error.contains("해당 이메일로 등록된 사용자가 없습니다") ||
                    error.contains("찾을 수 없습니다")
                ) {
                    toastManager.showInfo(error)
                } else if (error.contains("인증") || error.contains("로그인")) {
                    toastManager.showError(error)
                } else if (error.contains("입력") || error.contains("잘못된")) {
                    toastManager.showWarning(error)
                } else {
                    toastManager.showError(error)
                }
            }
        )
    }

    fun addFriend(email: String) {
        _isLoading.value = true
        friendsRepository.postFriends(
            email = email,
            onLoading = { loading ->
                _isLoading.value = loading
            },
            onSuccess = {
                toastManager.showSuccess("친구가 성공적���로 추가되었습니다!")
            },
            onError = { error ->
                _errorMessage.value = error
                if (error.contains("이미 친구") || error.contains("해당 이메일로 등록된 사용자가 없습니다")) {
                    toastManager.showInfo(error)
                } else if (error.contains("자신을")) {
                    toastManager.showWarning(error)
                } else if (error.contains("인증") || error.contains("로그인")) {
                    toastManager.showError(error)
                } else if (error.contains("입력") || error.contains("잘못된")) {
                    toastManager.showWarning(error)
                } else {
                    toastManager.showError(error)
                }
            }
        )
    }
}