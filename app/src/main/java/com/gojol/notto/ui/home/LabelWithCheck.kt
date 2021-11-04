package com.gojol.notto.ui.home

import com.gojol.notto.model.database.label.Label

data class LabelWithCheck(
    val label: Label,
    val isChecked: Boolean,
)