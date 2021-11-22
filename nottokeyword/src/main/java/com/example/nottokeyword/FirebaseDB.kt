package com.example.nottokeyword

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kr.bydelta.koala.hnn.Tagger

class FirebaseDB {

    private val database = Firebase
        .database(BuildConfig.FIREBASE_DB_URL)
        .getReference("keywords")

    fun insertKeyword(content: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val keywords = getKeywordsFrom(content)
            keywords.forEach {
                insert(it)
            }
        }
    }

    private fun getKeywordsFrom(text: String): List<String> {
        val tagger = Tagger().tagSentence(text)
        val nouns = tagger.getNouns()

        return nouns.map { it.surface }
    }

    private fun insert(keyword: String) {
        database.get().addOnSuccessListener {
            Log.i(TAG, "Got value ${it.value}")

            val target = it.children.find { child -> child.key == keyword }

            if (target == null) {
                insertNewKeyword(keyword)
            } else {
                val count = (target.value as Long).toInt() + 1
                insertExistingKeyword(keyword, count)
            }
        }.addOnFailureListener{
            Log.e(TAG, "Error getting data", it)
        }
    }

    private fun insertNewKeyword(keyword: String) {
        database.child(keyword).setValue(1).addOnSuccessListener {
            Log.i(TAG, "Successfully inserted keyword $keyword")
        }
    }

    private fun insertExistingKeyword(keyword: String, count: Int) {
        database.child(keyword).setValue(count).addOnSuccessListener {
            Log.i(TAG, "Successfully counted keyword $keyword")
        }
    }

    suspend fun getKeywords(): List<Keyword> {
        var list = listOf<Keyword>()

        database.get().addOnSuccessListener {
            Log.i(TAG, "Got value ${it.value}")

            list = it.children
                .filter { child -> child.key != null && child.value != null }
                .map { child -> Keyword(child.key!!, (child.value!! as Long).toInt()) }
                .toMutableList()
                .apply {
                    sortByDescending { keyword -> keyword.count }
                }
        }.addOnFailureListener{
            Log.e(TAG, "Error getting data", it)
        }.await()

        return list
    }

    fun deleteKeyword(keyword: String) {
        database.get().addOnSuccessListener {
            Log.i(TAG, "Got value ${it.value}")

            it.children.forEach { child ->
                if (child.key == keyword && child.value as Long > 0) {
                    val count = (child.value as Long).toInt() - 1

                    if (count == 0) {
                        database.child(keyword).removeValue()
                    } else {
                        database.child(keyword).setValue(count)
                    }

                    return@addOnSuccessListener
                }
            }
        }
    }
    
    companion object {
        const val TAG = "FirebaseDB"
    }
}
