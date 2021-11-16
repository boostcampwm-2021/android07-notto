package com.gojol.notto.util

import android.util.Log
import com.gojol.notto.BuildConfig
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

val database = Firebase
    .database(BuildConfig.FIREBASE_DB_URL)
    .getReference("keywords")

fun insertKeyword(keyword: String) {
    database.get().addOnSuccessListener {
        Log.i("firebase", "Got value ${it.value}")

        it.children.forEach { child ->
            if (child.key == keyword) {
                val count = (child.value as Long).toInt() + 1
                database.child(keyword).setValue(count)

                return@addOnSuccessListener
            }
        }
    }.addOnFailureListener{
        Log.e("firebase", "Error getting data", it)

        database.child(keyword).setValue(1)
    }
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
