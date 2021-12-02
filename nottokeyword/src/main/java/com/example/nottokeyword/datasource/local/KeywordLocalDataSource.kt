package com.example.nottokeyword.datasource.local

import com.example.nottokeyword.Keyword

interface KeywordLocalDataSource {

    fun updatePopularKeywords(keywords: List<Keyword>)

    fun getPopularKeywords(): List<Keyword>
}
