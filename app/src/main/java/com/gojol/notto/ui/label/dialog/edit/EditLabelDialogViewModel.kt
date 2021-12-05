package com.gojol.notto.ui.label.dialog.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gojol.notto.common.LabelEditType
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import com.gojol.notto.ui.DialogViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditLabelDialogViewModel @Inject constructor(
    private val repository: TodoLabelRepository
) : DialogViewModel() {

    private lateinit var labels: List<Label>

    private val _type = MutableLiveData<LabelEditType>()
    val type: LiveData<LabelEditType> = _type

    private val _label = MutableLiveData<Label>()
    val label: LiveData<Label> = _label

    val title = MutableLiveData<String>()
    val name = MutableLiveData<String>()

    private val _enabled = MutableLiveData(true)
    val enabled: LiveData<Boolean> = _enabled

    init {
        getLabels()
    }

    private fun getLabels() {
        viewModelScope.launch {
            labels = repository.getAllLabel()
        }
    }

    fun clickOkay() {
        val labelName = name.value?.apply {
            if (isBlank()) return
        } ?: return

        if (labels.find { it.name == labelName } != null) {
            _enabled.value = false
            return
        } else {
            _enabled.value = true
        }

        viewModelScope.launch {
            when (type.value) {
                LabelEditType.CREATE -> {
                    repository.insertLabel(Label(repository.getAllLabel().size, name.value!!))
                }
                LabelEditType.UPDATE -> {
                    val updatedLabel = label.value ?: return@launch
                    repository.updateLabel(updatedLabel.copy(name = labelName))
                }
                else -> return@launch
            }
        }
    }

    fun setEditType(label: Label?) {
        if (label == null) {
            _type.value = LabelEditType.CREATE
            title.value = LabelEditType.CREATE.text
        } else {
            _type.value = LabelEditType.UPDATE
            title.value = LabelEditType.UPDATE.text
            _label.value = label!!
            name.value = label.name
        }
    }
}
