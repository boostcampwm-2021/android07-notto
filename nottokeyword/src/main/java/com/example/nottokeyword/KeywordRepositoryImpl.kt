package com.example.nottokeyword

import com.example.nottokeyword.datasource.local.KeywordLocalDataSource
import com.example.nottokeyword.datasource.remote.KeywordRemoteDataSource
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

internal class KeywordRepositoryImpl @Inject constructor(
    private val keywordRemoteDataSource: KeywordRemoteDataSource,
    private val keywordLocalDataSource: KeywordLocalDataSource
) : KeywordRepository {

    override suspend fun insertKeyword(content: String): Boolean {
        return keywordRemoteDataSource.insertKeyword(content)
    }

    override suspend fun getKeywords(callback: (List<Keyword>) -> Unit) {
        coroutineScope {
            keywordRemoteDataSource.getKeywords(::onGetKeywordsFromRemoteDB)

            callback(keywordLocalDataSource.getPopularKeywords())
        }
    }

    private fun onGetKeywordsFromRemoteDB(newList: List<Keyword>) {
        val oldList = keywordLocalDataSource.getPopularKeywords()
        val keywords = reorderKeywords(oldList, newList)

        keywordLocalDataSource.updatePopularKeywords(keywords)
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
        return keywordRemoteDataSource.deleteKeyword(keyword)
    }
}
