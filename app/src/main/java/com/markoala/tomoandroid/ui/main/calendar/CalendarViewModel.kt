package com.markoala.tomoandroid.ui.main.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.markoala.tomoandroid.data.api.PromiseApiService
import com.markoala.tomoandroid.data.model.BaseResponse
import com.markoala.tomoandroid.data.model.MyPromiseResponseDTO
import com.markoala.tomoandroid.ui.main.calendar.model.CalendarEvent
import com.markoala.tomoandroid.ui.main.calendar.model.CalendarEventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarViewModel : ViewModel() {
    private val _promiseEvents = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val promiseEvents: StateFlow<List<CalendarEvent>> = _promiseEvents

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchPromises()
    }

    fun fetchPromises() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = PromiseApiService.getMyPromises().awaitResponse()
                if (response.isSuccessful) {
                    val body: BaseResponse<List<MyPromiseResponseDTO>>? = response.body()
                    _promiseEvents.value = body?.data.orEmpty()
                        .mapNotNull { it.toCalendarEvent() }
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun MyPromiseResponseDTO.toCalendarEvent(): CalendarEvent? {
        val date = parseDate(promiseDate) ?: return null
        return CalendarEvent(
            id = "promise-$promiseId",
            date = date,
            title = promiseName,
            description = moimTitle.takeIf { it.isNotBlank() },
            type = CalendarEventType.PROMISE,
            moimId = moimId,
            promiseTime = promiseTime.takeIf { it.isNotBlank() },
            place = place.takeIf { it.isNotBlank() },
            moimTitle = moimTitle
        )
    }

    private fun parseDate(raw: String): LocalDate? {
        return runCatching {
            LocalDate.parse(raw.take(10), DateTimeFormatter.ISO_LOCAL_DATE)
        }.getOrNull()
    }

}
