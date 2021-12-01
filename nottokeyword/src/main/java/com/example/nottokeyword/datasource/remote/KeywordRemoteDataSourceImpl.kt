package com.example.nottokeyword.datasource.remote

import android.util.Log
import com.example.nottokeyword.Keyword
import com.example.nottokeyword.POPULAR_KEYWORD_LIMIT
import com.example.nottokeyword.PlaceState
import com.example.nottokeyword.datasource.local.KeywordLocalDataSource
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await
import kr.bydelta.koala.hnn.Tagger
import java.util.*
import javax.inject.Inject

internal class KeywordRemoteDataSourceImpl @Inject constructor(
    private val database: DatabaseReference,
    private val keywordLocalDataSource: KeywordLocalDataSource
) : KeywordRemoteDataSource {

    override suspend fun insertKeyword(content: String): Boolean {
        val keywords = getKeywordsFrom(content)
        return insertKeywords(keywords)
    }

    private fun getKeywordsFrom(text: String): List<String> {
        return Tagger().tagSentence(text)
            .getNouns()
            .map { it.surface }
    }

    private suspend fun insertKeywords(keywords: List<String>): Boolean {
        val newKeywords: MutableMap<String, Any> = keywords.map { it to 1 }.toMap().toMutableMap()

        database.get().addOnSuccessListener { snapshot ->
            snapshot.children.forEach {
                val key = it.key!!
                val count = (it.value as Long).toInt()

                if (newKeywords.containsKey(key)) {
                    newKeywords[key] = count + 1
                }
            }
        }.await()

        return updateKeywords(newKeywords)
    }

    private suspend fun updateKeywords(keywords: MutableMap<String, Any>): Boolean {
        var result = false

        database.updateChildren(keywords)
            .addOnSuccessListener { result = true }
            .await()

        return result
    }

    override suspend fun getKeywords() {
        database.orderByValue().limitToLast(POPULAR_KEYWORD_LIMIT).get().addOnSuccessListener {
            Log.i(TAG, "Got value ${it.value}")

            val oldList = keywordLocalDataSource.getPopularKeywords()
            val tempList = it.children.mapNotNull { child ->
                child.key?.let { key ->
                    child.value?.let { value ->
                        Keyword(key, (value as Long).toInt())
                    }
                }
            }.reversed()

            val newList = comparePopularKeywords(oldList, tempList)
            keywordLocalDataSource.updatePopularKeywords(newList)
        }
    }

    private fun comparePopularKeywords(
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
        var result = false

        database.child(keyword).apply {
            get().addOnSuccessListener {
                Log.i(TAG, "Got value ${it.value}")

                val count = it.value?.let { count ->
                    (count as Long).toInt() - 1
                } ?: return@addOnSuccessListener

                when {
                    count == 0 -> removeValue().addOnSuccessListener { result = true }
                    count > 0 -> setValue(count).addOnSuccessListener { result = true }
                }
            }
        }

        return result
    }

    companion object {
        val TAG: String = KeywordRemoteDataSourceImpl::class.java.simpleName
    }
}
