package com.example.nottokeyword.datasource.remote

import com.example.nottokeyword.Keyword

interface KeywordRemoteDataSource {

    suspend fun insertKeyword(content: String): Boolean

    suspend fun getKeywords(callback: (List<Keyword>) -> Unit)

    suspend fun deleteKeyword(keyword: String): Boolean
}
