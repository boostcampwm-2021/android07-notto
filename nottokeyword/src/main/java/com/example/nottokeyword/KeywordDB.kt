package com.example.nottokeyword

interface KeywordDB {

    suspend fun insertKeyword(content: String): Boolean

    suspend fun getKeywords(callbackFromViewModel: (List<Keyword>) -> Unit)

    suspend fun deleteKeyword(keyword: String): Boolean
}
