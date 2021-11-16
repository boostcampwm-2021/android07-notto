package com.gojol.notto.ui.popular

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gojol.notto.util.database

class PopularViewModel : ViewModel() {

    private val _items = MutableLiveData<List<PopularKeyword>>()
    val items: LiveData<List<PopularKeyword>> = _items

    fun fetchKeywords() {
        val list = mutableListOf<PopularKeyword>()

        database.get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            it.children.forEach { child ->
                if (child.key != null && child.value != null) {
                    list.add(PopularKeyword(0, child.key!!, (child.value!! as Long).toInt()))
                }
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }.addOnCompleteListener {
            val orderedList = mutableListOf<PopularKeyword>()

            list.sortByDescending { it.count }
            list.forEachIndexed { index, item ->
                orderedList.add(item.copy(place = index + 1))
            }

            _items.value = orderedList
        }
    }
}
