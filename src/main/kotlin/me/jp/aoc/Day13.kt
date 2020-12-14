package me.jp.aoc

import me.jp.aoc.Day13.findPart2Timestamp
import me.jp.aoc.Day13.input
import me.jp.aoc.Day13.parseInputPart1

fun main() {
    val (earliest, busIds) = input.parseInputPart1()

    val (busId, busTime) = busIds.filterNotNull().map { it to earliest - earliest % it + it }.sortedBy { (_, t) -> t }.first()
    val answer1 = busId * (busTime - earliest)
    println(answer1) // 222

    val answer2 = findPart2Timestamp(busIds.map { it?.toLong() })
    println(answer2) // 408270049879073
}

object Day13 {

    fun findPart2Timestamp(buses: List<Long?>): Long =
        buses.foldIndexed(buses.first()!! to emptyList<(Long) -> Boolean>()) { i, acc, n ->
            if (i == 0 || n == null) acc
            else {
                val (t, rules) = acc
                val updatedRules = rules + rule(i.toLong(), n)
                generateSequence(t) { it + buses.take(i).filterNotNull().reduce { a, b -> a * b } }
                    .first { updatedRules.all { rule -> rule(it) } } to updatedRules
            }
        }.first

    private fun rule(index: Long, busId: Long): (Long) -> Boolean = { t -> (t % busId == busId - (index % busId)) }

    fun String.parseInputPart1(): Pair<Int, List<Int?>> = lines().let { lines ->
        lines.first().toInt() to lines[1].split(",").map { if (it == "x") null else it.toInt() }
    }

    val input = """
        1000511
        29,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,37,x,x,x,x,x,409,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,17,13,19,x,x,x,23,x,x,x,x,x,x,x,353,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,41
    """.trimIndent()
}