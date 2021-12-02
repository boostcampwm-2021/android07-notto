package com.example.nottokeyword

import com.example.nottokeyword.datasource.KeywordRepository
import javax.inject.Inject

internal class KeywordDBImpl @Inject constructor(private val repository: KeywordRepository) :
    KeywordDB {

    private lateinit var callbackFromViewModel: (List<Keyword>) -> Unit

    override suspend fun insertKeyword(content: String): Boolean {
        return repository.insertKeyword(content)
    }

    override suspend fun getKeywords(callbackFromViewModel: (List<Keyword>) -> Unit) {
        this.callbackFromViewModel = callbackFromViewModel
        repository.getKeywordsFromRemote(::onGetRemoteData)
    }

    private fun onGetRemoteData(newList: List<Keyword>) {
        val oldList = repository.getKeywordsFromLocal()
        val keywords = reorderKeywords(oldList, newList)
        repository.updateLocalCache(keywords)
        callbackFromViewModel(repository.getKeywordsFromLocal())
    }

    private fun reorderKeywords(
        oldList: List<Keyword>,
        newList: List<Keyword>
    ): List<Keyword> {
        val result = mutableListOf<Keyword>()

        newList.forEachIndexed { index, keyword ->
            val oldKeyword = oldList.find {
                it.word == keyword.word
            }

            val oldPlace = oldKeyword?.place
            val state: PlaceState
            val notch: Int?
            val hasChanged: Boolean

            when {
                oldPlace == null -> {
                    state = PlaceState.New
                    notch = null
                    hasChanged = false
                }
                oldPlace > index -> {
                    state = PlaceState.Up
                    notch = oldPlace - index
                    hasChanged = true
                }
                oldPlace < index -> {
                    state = PlaceState.Down
                    notch = index - oldPlace
                    hasChanged = true
                }
                else -> {
                    state = PlaceState.Same
                    notch = 0
                    hasChanged = false
                }
            }

            val newKeyword =
                keyword.copy(place = index, state = state, notch = notch, hasChanged = hasChanged)
            result.add(newKeyword)
        }

        return result
    }

    override suspend fun deleteKeyword(keyword: String): Boolean {
        return repository.deleteKeyword(keyword)
    }
}
