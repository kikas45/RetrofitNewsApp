package com.example.retrofitnewsme

import com.example.retrofitnewsme.api.News
import com.example.retrofitnewsme.api.NewsApiJSON
import retrofit2.http.GET

interface APIRequest {

//    companion object{
//        const val BASE_URL = "https://api.currentsapi.services"
//
//    }


    @GET("/v1/latest-news?language=en&apiKey=Pgt9u_oDwERVatBnBVDJiwY5wE-YP9mDqt23YlRPJhAPhIq6")

    suspend fun getNews(): NewsApiJSON

}