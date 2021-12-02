package com.example.nottokeyword.datasource

import com.example.nottokeyword.Keyword
import com.example.nottokeyword.datasource.local.KeywordLocalDataSource
import com.example.nottokeyword.datasource.remote.KeywordRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class KeywordRepositoryImpl @Inject constructor(
    private val keywordRemoteDataSource: KeywordRemoteDataSource,
    private val keywordLocalDataSource: KeywordLocalDataSource
) : KeywordRepository {

    private var keywords: List<Keyword> = listOf()

    override suspend fun insertKeyword(content: String): Boolean {
        return keywordRemoteDataSource.insertKeyword(content)
    }

    override suspend fun getKeywordsFromRemote(): List<Keyword> {
        CoroutineScope(Dispatchers.IO).launch {
            keywordRemoteDataSource.getKeywords(::onGetKeywordsFromRemoteDB)
        }.join()

        return keywords
    }

    private fun onGetKeywordsFromRemoteDB(newList: List<Keyword>) {
        keywords = newList
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
