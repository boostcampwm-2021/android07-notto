package com.gojol.notto.ui.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nottokeyword.FirebaseDB
import com.example.nottokeyword.Keyword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopularViewModel @Inject constructor(private val firebaseDB: FirebaseDB) : ViewModel() {

    private val _items = MutableLiveData<List<Keyword>?>()
    val items: LiveData<List<Keyword>?> = _items

    fun fetchKeywords() {
        viewModelScope.launch {
            _items.value = firebaseDB.getKeywords()
        }
    }
}
