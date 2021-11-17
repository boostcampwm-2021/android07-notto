package com.gojol.notto.ui.option

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gojol.notto.common.DAY_PUSH_NOTIFICATION_KEY
import com.gojol.notto.common.Event
import com.gojol.notto.model.datasource.option.OptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OptionViewModel @Inject constructor(
    private val optionRepository: OptionRepository
) : ViewModel() {

    private val _isPushChecked = MutableLiveData<Boolean>()
    val isPushChecked: LiveData<Boolean> = _isPushChecked

    private val _isNavigateToLicenseClicked = MutableLiveData<Event<Boolean>>()
    val isNavigateToLicenseClicked: LiveData<Event<Boolean>> = _isNavigateToLicenseClicked

    init {
        _isPushChecked.value =
            optionRepository.loadIsPushNotificationChecked(DAY_PUSH_NOTIFICATION_KEY)
    }

    fun updateIsPushChecked(isPushChecked: Boolean) {
        _isPushChecked.value = isPushChecked
        optionRepository.saveIsPushNotificationChecked(DAY_PUSH_NOTIFICATION_KEY, isPushChecked)
    }

    fun updateIsNavigateToLicenseClicked() {
        _isNavigateToLicenseClicked.value = Event(true)
    }
}
