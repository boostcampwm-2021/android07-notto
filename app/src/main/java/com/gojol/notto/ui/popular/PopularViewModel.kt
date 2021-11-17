package com.gojol.notto.ui.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nottokeyword.FirebaseDB
import com.example.nottokeyword.Keyword
import com.gojol.notto.BuildConfig
import kotlinx.coroutines.launch

class PopularViewModel : ViewModel() {

    private var _items = MutableLiveData<List<Keyword>?>()
    val items: LiveData<List<Keyword>?> = _items

    private val firebaseDB = FirebaseDB(BuildConfig.FIREBASE_DB_URL)

    fun fetchKeywords() {
        viewModelScope.launch {
            _items.value = firebaseDB.getKeywords()
        }
    }
}
