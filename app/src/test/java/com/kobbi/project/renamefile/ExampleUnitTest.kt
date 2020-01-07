package com.kobbi.project.renamefile

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun changePosition() {
        val inputPositions = listOf(2, 3, 5, 7)

        val existPositions = listOf(2, 3, 5)

        val sizeCheck = inputPositions.size - existPositions.size
        val change = if (sizeCheck < 0) {
            existPositions.filter {
                !inputPositions.contains(it)
            }
        } else {
            inputPositions.filter {
                !existPositions.contains(it)
            }
        }

        print(change)
    }

    @Test
    fun changePosition2() {
        val inputPositions = listOf(2, 3, 5, 7)

        val existPositions = listOf(2, 3, 5)

        mutableListOf<Int>().apply {
            addAll(inputPositions)
            addAll(existPositions)
        }.run {
            println(this)
            val tmp = this.distinct()

            println(tmp)
        }

    }
}
