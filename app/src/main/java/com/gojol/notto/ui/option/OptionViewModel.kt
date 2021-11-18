package com.gojol.notto.ui.option

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gojol.notto.common.Event
import com.gojol.notto.model.datasource.option.OptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OptionViewModel @Inject constructor(
    private val optionRepository: OptionRepository
) : ViewModel() {

    val isPushChecked = MutableLiveData<Boolean>()

    private val _isNavigateToLicenseClicked = MutableLiveData<Event<Boolean>>()
    val isNavigateToLicenseClicked: LiveData<Event<Boolean>> = _isNavigateToLicenseClicked

    init {
        isPushChecked.value =
            optionRepository.loadIsPushNotificationChecked()
    }

    fun updateIsPushChecked(isPushChecked: Boolean) {
        optionRepository.saveIsPushNotificationChecked(isPushChecked)
    }

    fun updateIsNavigateToLicenseClicked() {
        _isNavigateToLicenseClicked.value = Event(true)
    }
}
