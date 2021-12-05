package com.example.nottokeyword.datasource.local

import android.content.Context
import com.example.nottokeyword.Keyword
import com.example.nottokeyword.MODULE_NAME
import com.example.nottokeyword.POPULAR_KEYWORDS
import com.google.gson.Gson

internal class KeywordLocalDataSourceImpl(context: Context) : KeywordLocalDataSource {

    private val prefs = context.getSharedPreferences(MODULE_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun updatePopularKeywords(keywords: List<Keyword>) {
        val set = keywords.map {
            gson.toJson(it)
        }.toSet()

        prefs.edit().putStringSet(POPULAR_KEYWORDS, set).apply()
    }

    override fun getPopularKeywords(): List<Keyword> {
        return prefs.getStringSet(POPULAR_KEYWORDS, setOf())
            ?.map { json -> gson.fromJson(json, Keyword::class.java) }?.toList()
            ?.sortedBy { it.place } ?: listOf()
    }
}
