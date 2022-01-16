package pl.konarzewski.uboze.model

import org.joda.time.DateTime
import org.junit.Test
import org.junit.Assert.*
import pl.konarzewski.uboze.database.entity.Imige

class EngineOneTest {

    @Test
    fun getPathsToRepeat() {
        val input = listOf(
            Imige("test_path_01", 0, DateTime("2022-01-01T01:01:01.000")),
            Imige("test_path_02", 1, DateTime("2022-01-01T01:01:01.000")),
            Imige("test_path_03", 2, DateTime("2022-01-01T01:01:01.000")),
            Imige("test_path_04", 3, DateTime("2022-01-01T01:01:01.000"))
        )

        val outputDay01 = listOf(
            Imige("test_path_01", 0, DateTime("2022-01-01T01:01:01.000")),
        )
        getPathsToRepeatTestDay(input, outputDay01, DateTime("2022-01-01T01:01:01.000"))

        val outputDay02 = listOf(
            Imige("test_path_01", 0, DateTime("2022-01-01T01:01:01.000")),
            Imige("test_path_02", 1, DateTime("2022-01-01T01:01:01.000")),
            Imige("test_path_03", 2, DateTime("2022-01-01T01:01:01.000"))
            )
        getPathsToRepeatTestDay(input, outputDay02, DateTime("2022-01-02T01:01:01.000"))

        val outputDay03 = listOf(
            Imige("test_path_01", 0, DateTime("2022-01-01T01:01:01.000")),
            Imige("test_path_02", 1, DateTime("2022-01-01T01:01:01.000")),
            Imige("test_path_03", 2, DateTime("2022-01-01T01:01:01.000")),
            Imige("test_path_04", 3, DateTime("2022-01-01T01:01:01.000"))
        )
        getPathsToRepeatTestDay(input, outputDay03, DateTime("2022-01-03T01:01:01.000"))
    }

    private fun getPathsToRepeatTestDay(input: List<Imige>, output: List<Imige>, currDate: DateTime) {
        val result = getPathsToRepeat(input, currDate)
        assertEquals(result, output)
    }
}
