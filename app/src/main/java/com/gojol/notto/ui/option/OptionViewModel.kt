package com.gojol.notto.ui.option

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gojol.notto.model.data.License
import com.gojol.notto.model.datasource.option.OptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OptionViewModel @Inject constructor(
    private val optionRepository: OptionRepository
) : ViewModel() {

    private val _isPushChecked = MutableLiveData<Boolean>()
    val isPushChecked: LiveData<Boolean> = _isPushChecked

    private val _licenseList = MutableLiveData<List<License>>()
    val licenseList: LiveData<List<License>> = _licenseList

    init {
        _licenseList.value = listOf(
            License(
                "kotlinx.coroutines",
                "https://github.com/Kotlin/kotlinx.coroutines",
                "Copyright 2000-2020 JetBrains s.r.o. and Kotlin Programming Language contributors."
            ),
            License(
                "kotlinx.coroutines",
                "https://github.com/Kotlin/kotlinx.coroutines",
                "Copyright 2000-2020 JetBrains s.r.o. and Kotlin Programming Language contributors."
            ),
            License(
                "kotlinx.coroutines",
                "https://github.com/Kotlin/kotlinx.coroutines",
                "Copyright 2000-2020 JetBrains s.r.o. and Kotlin Programming Language contributors."
            ),
            License(
                "kotlinx.coroutines",
                "https://github.com/Kotlin/kotlinx.coroutines",
                "Copyright 2000-2020 JetBrains s.r.o. and Kotlin Programming Language contributors."
            ),
            License(
                "kotlinx.coroutines",
                "https://github.com/Kotlin/kotlinx.coroutines",
                "Copyright 2000-2020 JetBrains s.r.o. and Kotlin Programming Language contributors."
            )
        )

        loadIsPushChecked()
    }

    private fun loadIsPushChecked() {
        _isPushChecked.value = optionRepository.loadIsPushNotificationChecked(pushNotificationKey)
    }

    fun updateIsPushChecked(isPushChecked: Boolean) {
        _isPushChecked.value = isPushChecked
        optionRepository.saveIsPushNotificationChecked(pushNotificationKey, isPushChecked)
    }

    companion object {
        const val pushNotificationKey = "pushDay"
    }
}
