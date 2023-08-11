package edu.msudenver.cs3013.project01.api

import edu.msudenver.cs3013.project01.model.NewsResponse
import edu.msudenver.cs3013.project01.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("current.json")
    fun getWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String
    ): Call <WeatherResponse>
}