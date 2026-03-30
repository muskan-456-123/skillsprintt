package com.example.skilltracker.data.remote

import com.example.skilltracker.data.model.YouTubeSearchResponse
import com.example.skilltracker.data.model.YouTubeVideoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {

    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("order") order: String = "relevance",
        @Query("maxResults") maxResults: Int = 10,
        @Query("videoDuration") videoDuration: String = "long",
        @Query("key") apiKey: String
    ): YouTubeSearchResponse

    @GET("videos")
    suspend fun getVideoDetails(
        @Query("part") part: String = "contentDetails,snippet",
        @Query("id") videoId: String,
        @Query("key") apiKey: String
    ): YouTubeVideoResponse
}
