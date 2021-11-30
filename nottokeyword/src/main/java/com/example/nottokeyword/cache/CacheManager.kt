package com.example.nottokeyword.cache

import com.example.nottokeyword.Keyword

interface CacheManager {

    fun updatePopularKeywords(keywords: List<Keyword>)

    fun getPopularKeywords(): List<Keyword>
}
