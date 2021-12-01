package com.example.nottokeyword.datasource.remote

interface KeywordRemoteDataSource {

    suspend fun insertKeyword(content: String): Boolean

    suspend fun getKeywords()

    suspend fun deleteKeyword(keyword: String): Boolean
}
