package com.example.nottokeyword

import android.util.Log
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kr.bydelta.koala.hnn.Tagger
import javax.inject.Inject

internal class KeywordDatabaseImpl @Inject constructor(private val database: DatabaseReference) :
    KeywordDatabase {

    override fun insertKeyword(content: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val keywords = getKeywordsFrom(content)
            insertKeywords(keywords)
        }
    }

    private fun getKeywordsFrom(text: String): List<String> {
        return Tagger().tagSentence(text)
            .getNouns()
            .map { it.surface }
    }

    private fun insertKeywords(keywords: List<String>) {
        val newKeywords = keywords.map { it to 1 }.toMap().toMutableMap()

        database.get().addOnSuccessListener { snapshot ->
            snapshot.children.forEach {
                val key = it.key!!
                val count = (it.value as Long).toInt()

                if (newKeywords.containsKey(key)) {
                    newKeywords[key] = count + 1
                }
            }

            database.updateChildren(newKeywords as MutableMap<String, Any>)
        }
    }

    override suspend fun getKeywords(): List<Keyword> {
        var list = listOf<Keyword>()

        database.get().addOnSuccessListener {
            Log.i(TAG, "Got value ${it.value}")

            list = it.children
                .filter { child -> child.key != null && child.value != null }
                .map { child -> Keyword(child.key!!, (child.value!! as Long).toInt()) }
                .sortedByDescending { keyword -> keyword.count }
        }.addOnFailureListener {
            Log.e(TAG, "Error getting data", it)
        }.await()

        return list
    }

    override fun deleteKeyword(keyword: String) {
        database.child(keyword).apply {
            get().addOnSuccessListener {
                Log.i(TAG, "Got value ${it.value}")

                val count = it.value?.let { count ->
                    (count as Long).toInt() - 1
                } ?: return@addOnSuccessListener

                when {
                    count == 0 -> removeValue()
                    count > 0 -> setValue(count)
                }
            }
        }
    }

    companion object {
        val TAG: String = KeywordDatabaseImpl::class.java.simpleName
    }
}
