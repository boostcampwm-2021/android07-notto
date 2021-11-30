package com.example.nottokeyword

interface KeywordDatabase {

    suspend fun insertKeyword(content: String): Boolean

    suspend fun getKeywords(callback: (List<Keyword>) -> Unit)

    suspend fun deleteKeyword(keyword: String): Boolean
}
