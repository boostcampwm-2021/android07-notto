package com.example.nottokeyword

import com.example.nottokeyword.datasource.local.KeywordLocalDataSource
import com.example.nottokeyword.datasource.remote.KeywordRemoteDataSource
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

internal class KeywordRepositoryImpl @Inject constructor(
    private val keywordRemoteDataSource: KeywordRemoteDataSource,
    private val keywordLocalDataSource: KeywordLocalDataSource
) : KeywordRepository {

    override suspend fun insertKeyword(content: String): Boolean {
        return keywordRemoteDataSource.insertKeyword(content)
    }

    override suspend fun getKeywords(callback: (List<Keyword>) -> Unit) {
        coroutineScope {
            keywordRemoteDataSource.getKeywords()

            callback(keywordLocalDataSource.getPopularKeywords())
        }
    }

    override suspend fun deleteKeyword(keyword: String): Boolean {
        return keywordRemoteDataSource.deleteKeyword(keyword)
    }
}
