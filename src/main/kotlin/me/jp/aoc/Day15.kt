package me.jp.aoc

import me.jp.aoc.Day15.input
import me.jp.aoc.Day15.toGame

// Functional approach didn't run quick enough, so solved using imperative approach instead. Hmmm...
fun main() {
    val answer1 = input.toGame().apply { takeTurn(2020) }.lastNumber
    println(answer1) // 387

    val answer2 = input.toGame().apply { takeTurn(30_000_000) }.lastNumber
    println(answer2) // 6428
}

object Day15 {

    fun List<Int>.toGame(): Game =
        Game(last(), size, mutableMapOf(*dropLast(1).withIndex().map { it.value to it.index + 1 }.toTypedArray()))

    class Game(
        var lastNumber: Int,
        private var turn: Int,
        private val numberToTurnMap: MutableMap<Int, Int>)
    {

        fun takeTurn(to: Int) {
            while (to > turn) next()
        }

        private fun next() {
            val nextNumber = numberToTurnMap[lastNumber]?.let { previousTurn -> turn - previousTurn } ?: 0
            numberToTurnMap[lastNumber] = turn
            lastNumber = nextNumber
            turn++
        }
    }

    val input = "14,1,17,0,3,20".split(",").map { it.toInt() }
}