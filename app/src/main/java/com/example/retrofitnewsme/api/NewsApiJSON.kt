package com.example.retrofitnewsme.api

data class NewsApiJSON(
    val news: List<News>,
    val page: Int,
    val status: String
)