package com.example.skilltracker.data.repository

import com.example.skilltracker.BuildConfig
import com.example.skilltracker.data.local.dao.CourseDao
import com.example.skilltracker.data.local.toDomain
import com.example.skilltracker.data.local.toEntity
import com.example.skilltracker.data.remote.FirestoreService
import com.example.skilltracker.data.remote.YouTubeApiService
import com.example.skilltracker.domain.model.Course
import com.example.skilltracker.domain.repository.CourseRepository
import com.example.skilltracker.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val courseDao: CourseDao,
    private val youTubeApi: YouTubeApiService,
    private val firestoreService: FirestoreService
) : CourseRepository {

    override fun getAllCourses(): Flow<List<Course>> {
        return courseDao.getAllCourses().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getLatestCourse(): Course? {
        return courseDao.getLatestCourse()?.toDomain()
    }

    override suspend fun getCourseById(id: String): Course? {
        return courseDao.getCourseById(id)?.toDomain()
    }

    override suspend fun saveCourse(course: Course) {
        courseDao.insertCourse(course.toEntity())
        try {
            firestoreService.saveCourse(course)
        } catch (_: Exception) { }
    }

    override suspend fun searchYouTubeCourses(skill: String): List<Course> {
        val searchResponse = youTubeApi.searchVideos(
            query = "$skill full course",
            apiKey = BuildConfig.YOUTUBE_API_KEY
        )

        val videoIds = searchResponse.items.map { it.id.videoId }
        if (videoIds.isEmpty()) return emptyList()

        val detailsResponse = youTubeApi.getVideoDetails(
            videoId = videoIds.joinToString(","),
            apiKey = BuildConfig.YOUTUBE_API_KEY
        )

        return detailsResponse.items.map { video ->
            val durationMinutes = DateUtils.parseIsoDuration(video.contentDetails.duration)
            Course(
                id = UUID.randomUUID().toString(),
                videoId = video.id,
                title = video.snippet.title,
                channelName = video.snippet.channelTitle,
                thumbnailUrl = video.snippet.thumbnails.high.url.ifEmpty {
                    video.snippet.thumbnails.medium.url
                },
                durationMinutes = durationMinutes,
                durationFormatted = DateUtils.formatMinutes(durationMinutes),
                skill = skill
            )
        }
    }

    override suspend fun getVideoDetails(videoId: String): Course? {
        val response = youTubeApi.getVideoDetails(
            videoId = videoId,
            apiKey = BuildConfig.YOUTUBE_API_KEY
        )
        val video = response.items.firstOrNull() ?: return null
        val durationMinutes = DateUtils.parseIsoDuration(video.contentDetails.duration)
        return Course(
            id = UUID.randomUUID().toString(),
            videoId = video.id,
            title = video.snippet.title,
            channelName = video.snippet.channelTitle,
            thumbnailUrl = video.snippet.thumbnails.high.url,
            durationMinutes = durationMinutes,
            durationFormatted = DateUtils.formatMinutes(durationMinutes)
        )
    }
}
