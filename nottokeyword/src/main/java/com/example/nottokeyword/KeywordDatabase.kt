package com.example.nottokeyword

interface KeywordDatabase {

    suspend fun insertKeyword(content: String): Boolean

    suspend fun getKeywords(): List<Keyword>

    suspend fun deleteKeyword(keyword: String): Boolean
}
