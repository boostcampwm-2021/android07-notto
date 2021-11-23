package com.example.nottokeyword

interface KeywordDatabase {

    fun insertKeyword(content: String)

    suspend fun getKeywords(): List<Keyword>

    fun deleteKeyword(keyword: String)
}
