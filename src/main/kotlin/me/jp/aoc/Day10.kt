package me.jp.aoc

import me.jp.aoc.Day10.arrangements
import me.jp.aoc.Day10.input

fun main() {
    val joltages: List<Int> = input.sorted().let { listOf(0) + it + (it.last() + 3) }

    val joltageGroups = joltages.zipWithNext().map { (a, b) -> b - a }.groupBy { it }
    val answer1 = joltageGroups.getValue(1).size * joltageGroups.getValue(3).size
    println(answer1) // 1690

    val answer2 = joltages.arrangements()
    println(answer2) // 5289227976704
}

object Day10 {

    fun List<Int>.arrangements(): Long {

        fun go(acc: Map<Int, Long>, remaining: List<Int>): Map<Int, Long> =
            if (remaining.isEmpty()) acc
            else {
                val x = remaining.last()
                val children = filter { it in x + 1..x + 3 }
                go(acc + (x to children.mapNotNull { acc[it] }.sum()), remaining.dropLast(1))
            }

        return go(mapOf(last() to 1), dropLast(1)).getValue(first())
    }

    val input: List<Int> = """
        114
        51
        122
        26
        121
        90
        20
        113
        8
        138
        57
        44
        135
        76
        134
        15
        21
        119
        52
        118
        107
        99
        73
        72
        106
        41
        129
        83
        19
        66
        132
        56
        32
        79
        27
        115
        112
        58
        102
        64
        50
        2
        39
        3
        77
        85
        103
        140
        28
        133
        78
        34
        13
        61
        25
        35
        89
        40
        7
        24
        33
        96
        108
        71
        11
        128
        92
        111
        55
        80
        91
        31
        70
        101
        14
        18
        12
        4
        84
        125
        120
        100
        65
        86
        93
        67
        139
        1
        47
        38
    """.trimIndent().lines().map { it.toInt() }
}