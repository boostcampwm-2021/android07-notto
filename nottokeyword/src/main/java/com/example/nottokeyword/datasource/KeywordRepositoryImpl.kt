package com.example.nottokeyword.datasource

import com.example.nottokeyword.Keyword
import com.example.nottokeyword.datasource.local.KeywordLocalDataSource
import com.example.nottokeyword.datasource.remote.KeywordRemoteDataSource
import javax.inject.Inject

internal class KeywordRepositoryImpl @Inject constructor(
    private val keywordRemoteDataSource: KeywordRemoteDataSource,
    private val keywordLocalDataSource: KeywordLocalDataSource
) : KeywordRepository {

    override suspend fun insertKeyword(content: String): Boolean {
        return keywordRemoteDataSource.insertKeyword(content)
    }

    override suspend fun getKeywordsFromRemote(callback: (List<Keyword>) -> Unit) {
        keywordRemoteDataSource.getKeywords(callback)
    }

    override fun getKeywordsFromLocal(): List<Keyword> {
        return keywordLocalDataSource.getPopularKeywords()
    }

    override fun updateLocalCache(keywords: List<Keyword>) {
        keywordLocalDataSource.updatePopularKeywords(keywords)
    }

    override suspend fun deleteKeyword(keyword: String): Boolean {
        return keywordRemoteDataSource.deleteKeyword(keyword)
    }
}
