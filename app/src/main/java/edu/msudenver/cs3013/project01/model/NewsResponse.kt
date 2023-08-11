package edu.msudenver.cs3013.project01.model

data class NewsResponse(
    val status: String,
    val articles: List<NewsUiModel>,
)