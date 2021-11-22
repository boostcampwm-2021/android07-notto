package com.example.nottokeyword

import android.util.Log
import androidx.lifecycle.MutableLiveData
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

            it.children.forEach { child ->
                if (child.key == keyword) {
                    val count = (child.value as Long).toInt() + 1
                    database.child(keyword).setValue(count).addOnSuccessListener { 
                        Log.i(TAG, "Successfully counted keyword $keyword")
                    }

                    return@addOnSuccessListener
                }
            }

            database.child(keyword).setValue(1).addOnSuccessListener {
                Log.i(TAG, "Successfully inserted keyword $keyword")
            }
        }.addOnFailureListener{
            Log.e(TAG, "Error getting data", it)
        }
    }

    suspend fun getKeywords(): List<Keyword> {
        val orderedList = MutableLiveData<List<Keyword>>()
        val list = mutableListOf<Keyword>()

        database.get().addOnSuccessListener {
            Log.i(TAG, "Got value ${it.value}")
            it.children.forEach { child ->
                if (child.key != null && child.value != null) {
                    list.add(Keyword(child.key!!, (child.value!! as Long).toInt()))
                }
            }
        }.addOnFailureListener{
            Log.e(TAG, "Error getting data", it)
        }.addOnCompleteListener {
            list.sortByDescending { it.count }
            orderedList.value = list
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
