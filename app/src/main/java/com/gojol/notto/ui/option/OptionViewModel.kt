package com.gojol.notto.ui.option

import androidx.lifecycle.ViewModel
import com.gojol.notto.model.datasource.option.OptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OptionViewModel @Inject constructor(
    private val optionRepository: OptionRepository
) : ViewModel() {

}
