package edu.msudenver.cs3013.project01.api

import edu.msudenver.cs3013.project01.model.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


//Path URL
//https://newsapi.org/v2/top-headlines?country=us&apiKey=d73e68de837f47f196a84dfdf58801f7

interface NewsApiService {
    @GET("top-headlines")
    fun getTopHeadlines(
        @Query("apiKey") apikey: String,
        @Query("country") country: String,
        @Query("category") category: String
    ): Call<NewsResponse>
}