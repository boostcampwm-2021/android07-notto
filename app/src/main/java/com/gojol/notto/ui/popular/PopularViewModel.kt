package com.gojol.notto.ui.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PopularViewModel : ViewModel() {

    private val _items = MutableLiveData<List<PopularKeyword>>()
    val items: LiveData<List<PopularKeyword>> = _items

    fun fetchKeywords() {
        _items.value = listOf(
            PopularKeyword("늦잠", 1, 1000),
            PopularKeyword("스마트폰", 2, 1000),
            PopularKeyword("습관", 3, 1000),
            PopularKeyword("야식", 4, 1000),
            PopularKeyword("편식", 5, 1000),
            PopularKeyword("지각", 6, 1000),
            PopularKeyword("간식", 7, 1000),
            PopularKeyword("과일", 8, 1000),
            PopularKeyword("채소", 9, 1000),
            PopularKeyword("인스타그램", 10, 1000),
            PopularKeyword("유튜브", 11, 1000),
            PopularKeyword("게임", 12, 1000),
            PopularKeyword("침대", 13, 1000)
        )
    }
}
