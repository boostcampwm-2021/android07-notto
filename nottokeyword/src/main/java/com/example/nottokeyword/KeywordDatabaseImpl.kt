package com.example.nottokeyword

import android.util.Log
import com.google.firebase.database.DataSnapshot
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
            keywords.forEach {
                insert(it)
            }
        }
    }

    private fun getKeywordsFrom(text: String): List<String> {
        return Tagger().tagSentence(text)
            .getNouns()
            .map { it.surface }
    }

    private fun insert(keyword: String) {
        database.child(keyword).apply {
            get().addOnSuccessListener {
                Log.i(TAG, "Got value ${it.value}")

                if (it == null) {
                    insertNewKeyword(keyword, this)
                } else {
                    insertExistingKeyword(it, this)
                }
            }.addOnFailureListener {
                Log.e(TAG, "Error getting data", it)
            }
        }
    }

    private fun insertNewKeyword(keyword: String, ref: DatabaseReference) {
        ref.setValue(1).addOnSuccessListener {
            Log.i(TAG, "Successfully inserted keyword $keyword")
        }
    }

    private fun insertExistingKeyword(target: DataSnapshot, ref: DatabaseReference) {
        val count = (target.value as Long).toInt() + 1
        ref.setValue(count).addOnSuccessListener {
            Log.i(TAG, "Successfully counted keyword ${target.key}")
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
        database.child(keyword).get().addOnSuccessListener {
            Log.i(TAG, "Got value ${it.value}")

            val count = it.value?.let { count ->
                (count as Long).toInt() - 1
            } ?: return@addOnSuccessListener

            if (count == 0) {
                database.child(keyword).removeValue()
            } else {
                database.child(keyword).setValue(count)
            }
        }
    }

    companion object {
        val TAG: String = KeywordDatabaseImpl::class.java.simpleName
    }
}
