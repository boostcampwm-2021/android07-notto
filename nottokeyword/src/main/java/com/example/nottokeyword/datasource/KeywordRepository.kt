package com.example.nottokeyword.datasource

import com.example.nottokeyword.Keyword

interface KeywordRepository {

    suspend fun insertKeyword(content: String): Boolean

    suspend fun getKeywordsFromRemote(callback: (List<Keyword>) -> Unit)

    fun getKeywordsFromLocal(): List<Keyword>

    fun updateLocalCache(keywords: List<Keyword>)

    suspend fun deleteKeyword(keyword: String): Boolean
}
