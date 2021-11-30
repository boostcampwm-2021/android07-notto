package com.gojol.notto.ui.option

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojol.notto.common.Event
import com.gojol.notto.model.datasource.option.OptionRepository
import com.gojol.notto.network.Contributor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptionViewModel @Inject constructor(
    private val optionRepository: OptionRepository,
    private val dayAlarmManager: DayAlarmManager
) : ViewModel() {

    val isPushChecked = MutableLiveData<Boolean>()

    private val _isNavigateToLicenseClicked = MutableLiveData<Event<Unit>>()
    val isNavigateToLicenseClicked: LiveData<Event<Unit>> = _isNavigateToLicenseClicked

    private val _contributorList = MutableLiveData<ArrayList<Contributor>>()
    val contributorList: LiveData<ArrayList<Contributor>> = _contributorList

    init {
        isPushChecked.value =
            optionRepository.isPushNotificationChecked()
    }

    fun updateIsPushChecked(isPushChecked: Boolean) {
        optionRepository.saveIsPushNotificationChecked(isPushChecked)
        if (isPushChecked) dayAlarmManager.setAlarm()
        else dayAlarmManager.cancelAlarm()
    }

    fun updateIsNavigateToLicenseClicked() {
        _isNavigateToLicenseClicked.value = Event(Unit)
    }

    fun updateGitContributors() {
        viewModelScope.launch {
            optionRepository.getGitContributors(OWNER, REPO)
                .onSuccess {
                    _contributorList.value = it
                }
        }
    }

    companion object {
        private const val OWNER = "boostcampwm-2021"
        private const val REPO = "android07-notto"
    }
}
