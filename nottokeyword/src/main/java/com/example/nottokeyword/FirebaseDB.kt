package com.example.nottokeyword

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseDB(firebaseDbUrl: String) {

    private val database = Firebase
        .database(firebaseDbUrl)
        .getReference("keywords")

    fun insertKeyword(keyword: String) {
        database.get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")

            it.children.forEach { child ->
                if (child.key == keyword) {
                    val count = (child.value as Long).toInt() + 1
                    database.child(keyword).setValue(count)

                    return@forEach
                }
            }

            database.child(keyword).setValue(1)
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    suspend fun getKeywords(): List<Keyword> {
        val orderedList = MutableLiveData<List<Keyword>>()
        val list = mutableListOf<Keyword>()

        database.get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            it.children.forEach { child ->
                if (child.key != null && child.value != null) {
                    list.add(Keyword(child.key!!, (child.value!! as Long).toInt()))
                }
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }.addOnCompleteListener {
            list.sortByDescending { it.count }
            orderedList.value = list
        }.await()

        return list
    }

    fun deleteKeyword(keyword: String) {
        database.get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")

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
}
