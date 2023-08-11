package edu.msudenver.cs3013.project01.model

// TODO-complete: Maps out the data from the API response to be used in the UI
data class NewsUiModel(
    val author: String,
    val title: String,
    val source: NewsSource, // T
    val urlToImage: String,
    val content: String
) {
}