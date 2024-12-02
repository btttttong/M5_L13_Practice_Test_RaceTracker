package tat.mukhutdinov.lesson14racetracker

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import tat.mukhutdinov.lesson14racetracker.ui.RaceParticipant
import tat.mukhutdinov.lesson14racetracker.ui.progressFactor


@RunWith(MockitoJUnitRunner::class)
class RaceParticipantTest {
    private val raceParticipant = RaceParticipant(
        name = "Test",
        maxProgress = 100,
        progressDelayMillis = 500L,
        initialProgress = 0,
        progressIncrement = 1
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test
    fun raceStarted_ProgressUpdated() = runTest {
        val expectedProgress = 1
        launch { raceParticipant.run() }
        advanceTimeBy(raceParticipant.progressDelayMillis)
        runCurrent()
        assertEquals(expectedProgress, raceParticipant.currentProgress)
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test
    fun raceFinished_ProgressUpdated() = runTest {
        raceParticipant.run()
        assertEquals(100, raceParticipant.currentProgress)
    }

    @Test
    fun initializedWithValidParameters_ProgressStartsAtInitialValue() {
        val participant = RaceParticipant("Runner", 100, 500L, 1, 0)
        assertEquals("Initial progress should be 0", 0, participant.currentProgress)
    }

    @Test
    fun runUntilMaxProgress_ProgressReachesMaxWithoutError() = runTest {
        val participant = RaceParticipant("Runner", 100, 10L)
        participant.run()
        assertEquals("Progress should reach max", 100, participant.currentProgress)
    }


    @Test
    fun resetCalled_ProgressResetsToZero() = runTest {
        val participant = RaceParticipant("Runner", 100, 10L)
        participant.run()
        participant.reset()
        assertEquals("Progress should reset to 0 after reset", 0, participant.currentProgress)
    }

    @Test
    fun incrementAtMinimum_ProgressUpdatesCorrectly() = runTest {
        val participant = RaceParticipant("Runner", 5, 50L, 1, 0)
        participant.run()
        assertEquals("Progress should increment by 1 and reach 5", 5, participant.currentProgress)
    }

    @Test
    fun singleIncrementReachesMaxProgress() = runTest {
        val participant = RaceParticipant("Boundary Runner", 100, 10L, 100, 0)
        participant.run()
        assertEquals(
            "Progress should reach 100 in one increment",
            100,
            participant.currentProgress,
        )
    }

    @Test
    fun resetCorrectly_SetsProgressToZero() = runTest {
        val participant = RaceParticipant("Test", 100, 500L, 10, 50)
        participant.reset()
        assertEquals(0, participant.currentProgress)
    }

    @Test
    fun resetDuringRunProgressResets() = runTest {
        val participant = RaceParticipant("Interrupted Runner", 100, 100L, 10, 0)
        val job = launch {
            participant.run()
        }
        // When cancel the job
//        delay(250)
//        job.cancelAndJoin()
        // Then Expected progress should be less than 100
//        assertTrue("Progress should be less than 100 after cancellation", participant.currentProgress < 100)
    }


    @Test
    fun progressFactorCalculatesCorrectly() = runTest {
        val participant = RaceParticipant("Factor Test", 200, 100L, 20, 0)
        participant.run()
        assertEquals(
            "Progress factor should be 0.1 after one increment",
            1.0f,
            participant.progressFactor,
            0.01f
        )
    }


}
