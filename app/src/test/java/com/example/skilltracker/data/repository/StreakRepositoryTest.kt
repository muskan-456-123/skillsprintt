package com.example.skilltracker.data.repository

import com.example.skilltracker.data.remote.FirestoreService
import com.example.skilltracker.domain.model.Streak
import com.example.skilltracker.util.DateUtils
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class StreakRepositoryTest {

    private lateinit var firestoreService: FirestoreService
    private lateinit var streakRepository: StreakRepositoryImpl

    @Before
    fun setUp() {
        firestoreService = mockk(relaxed = true)
        streakRepository = StreakRepositoryImpl(firestoreService)
    }

    @Test
    fun `checkAndUpdateStreak continues streak from yesterday`() = runTest {
        val yesterday = DateUtils.yesterday()
        val currentStreak = Streak(
            currentStreak = 5,
            longestStreak = 10,
            lastActiveDate = yesterday
        )
        coEvery { firestoreService.getStreak() } returns currentStreak

        val result = streakRepository.checkAndUpdateStreak()

        assertEquals(6, result.currentStreak)
        assertEquals(DateUtils.today(), result.lastActiveDate)
    }

    @Test
    fun `checkAndUpdateStreak resets streak when broken`() = runTest {
        val threeDaysAgo = DateUtils.daysAgo(3)
        val currentStreak = Streak(
            currentStreak = 5,
            longestStreak = 10,
            lastActiveDate = threeDaysAgo
        )
        coEvery { firestoreService.getStreak() } returns currentStreak

        val result = streakRepository.checkAndUpdateStreak()

        assertEquals(1, result.currentStreak)
        assertEquals(10, result.longestStreak) // Longest preserved
        assertEquals(DateUtils.today(), result.lastActiveDate)
    }

    @Test
    fun `checkAndUpdateStreak returns same streak if already active today`() = runTest {
        val today = DateUtils.today()
        val currentStreak = Streak(
            currentStreak = 5,
            longestStreak = 10,
            lastActiveDate = today
        )
        coEvery { firestoreService.getStreak() } returns currentStreak

        val result = streakRepository.checkAndUpdateStreak()

        assertEquals(5, result.currentStreak)
        coVerify(exactly = 0) { firestoreService.updateStreak(any()) }
    }

    @Test
    fun `checkAndUpdateStreak updates longest when exceeding`() = runTest {
        val yesterday = DateUtils.yesterday()
        val currentStreak = Streak(
            currentStreak = 10,
            longestStreak = 10,
            lastActiveDate = yesterday
        )
        coEvery { firestoreService.getStreak() } returns currentStreak

        val result = streakRepository.checkAndUpdateStreak()

        assertEquals(11, result.currentStreak)
        assertEquals(11, result.longestStreak) // Updated!
    }
}
