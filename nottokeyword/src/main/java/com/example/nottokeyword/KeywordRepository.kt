package com.example.nottokeyword

interface KeywordRepository {

    suspend fun insertKeyword(content: String): Boolean

    suspend fun getKeywords(callback: (List<Keyword>) -> Unit)

    suspend fun deleteKeyword(keyword: String): Boolean
}
