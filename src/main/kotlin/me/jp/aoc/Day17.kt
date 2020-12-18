package me.jp.aoc

import me.jp.aoc.Day17.input
import me.jp.aoc.Day17.nextState
import me.jp.aoc.Day17.nextState4D
import me.jp.aoc.Day17.parseInitialActive

fun main() {
    val initialActive = input.parseInitialActive()

    val answer1 = (1..6).fold(initialActive) { acc, _ -> acc.nextState() }.size
    println(answer1) // 375

    val answer2 = (1..6).fold(initialActive) { acc, _ -> acc.nextState4D() }.size
    println(answer2) // 2192
}

object Day17 {

    data class Coordinate(val x: Int, val y: Int, val z: Int, val w: Int = 0)

    fun List<Coordinate>.nextState(): List<Coordinate> {
        val (xs, ys, zs) = listOf<(Coordinate) -> Int>({ it.x }, { it.y }, { it.z }).map { activeRange(it) }
        return xs.flatMap { x ->
            ys.flatMap { y ->
                zs.mapNotNull { z ->
                    val c = Coordinate(x, y, z)
                    val activeNeighbourCount = c.neighbours().filter { contains(it) }.size
                    val active = contains(c)
                    if (nextState(active, activeNeighbourCount)) c else null
                }
            }
        }
    }

    fun List<Coordinate>.nextState4D(): List<Coordinate> {
        val (xs, ys, zs, ws) = listOf<(Coordinate) -> Int>({ it.x }, { it.y }, { it.z }, { it.w }).map { activeRange(it) }
        return xs.flatMap { x ->
            ys.flatMap { y ->
                zs.flatMap { z ->
                    ws.mapNotNull { w ->
                        val c = Coordinate(x, y, z, w)
                        val activeNeighbourCount = c.neighbours4D().filter { contains(it) }.size
                        val active = contains(c)
                        if (nextState(active, activeNeighbourCount)) c else null
                    }
                }
            }
        }
    }

    private fun nextState(isActive: Boolean, activeNeighbourCount: Int): Boolean =
        if (isActive) activeNeighbourCount in 2..3 else activeNeighbourCount == 3

    private fun Coordinate.neighbours(): List<Coordinate> =
        (x - 1..x + 1).flatMap { x ->
            (y - 1..y + 1).flatMap { y ->
                (z - 1..z + 1).map { z ->
                    Coordinate(x, y, z)
                }
            }
        }.filterNot { it == this }

    private fun Coordinate.neighbours4D(): List<Coordinate> =
        (x - 1..x + 1).flatMap { x ->
            (y - 1..y + 1).flatMap { y ->
                (z - 1..z + 1).flatMap { z ->
                    (w - 1..w + 1).map { w ->
                        Coordinate(x, y, z, w)
                    }
                }
            }
        }.filterNot { it == this }

    private fun List<Coordinate>.activeRange(axis: (Coordinate) -> Int): IntRange =
        map { axis(it) }.sorted().let { it.first() - 1..it.last() + 1 }

    fun String.parseInitialActive(): List<Coordinate> =
        lines().flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, value -> if (value == '#') Coordinate(x, y, 0)  else null }
        }

    val input = """
        .#.#.#..
        ..#....#
        #####..#
        #####..#
        #####..#
        ###..#.#
        #..##.##
        #.#.####
    """.trimIndent()
}