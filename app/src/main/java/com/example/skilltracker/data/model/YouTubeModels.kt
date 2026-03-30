package com.example.skilltracker.data.model

import com.google.gson.annotations.SerializedName

// YouTube Search Response
data class YouTubeSearchResponse(
    @SerializedName("items") val items: List<YouTubeSearchItem> = emptyList()
)

data class YouTubeSearchItem(
    @SerializedName("id") val id: YouTubeVideoId,
    @SerializedName("snippet") val snippet: YouTubeSnippet
)

data class YouTubeVideoId(
    @SerializedName("videoId") val videoId: String = ""
)

data class YouTubeSnippet(
    @SerializedName("title") val title: String = "",
    @SerializedName("channelTitle") val channelTitle: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("thumbnails") val thumbnails: YouTubeThumbnails = YouTubeThumbnails()
)

data class YouTubeThumbnails(
    @SerializedName("high") val high: YouTubeThumbnail = YouTubeThumbnail(),
    @SerializedName("medium") val medium: YouTubeThumbnail = YouTubeThumbnail()
)

data class YouTubeThumbnail(
    @SerializedName("url") val url: String = ""
)

// YouTube Video Details Response
data class YouTubeVideoResponse(
    @SerializedName("items") val items: List<YouTubeVideoItem> = emptyList()
)

data class YouTubeVideoItem(
    @SerializedName("id") val id: String = "",
    @SerializedName("snippet") val snippet: YouTubeSnippet = YouTubeSnippet(),
    @SerializedName("contentDetails") val contentDetails: YouTubeContentDetails = YouTubeContentDetails()
)

data class YouTubeContentDetails(
    @SerializedName("duration") val duration: String = "" // ISO 8601 e.g. PT1H30M
)
