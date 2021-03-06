package me.jp.aoc

import me.jp.aoc.Day20.Direction.*
import me.jp.aoc.Day20.assemble
import me.jp.aoc.Day20.corners
import me.jp.aoc.Day20.input
import me.jp.aoc.Day20.join
import me.jp.aoc.Day20.orientateAndCountSeaMonsters
import me.jp.aoc.Day20.seaMonsterSize
import me.jp.aoc.Day20.toTile
import kotlin.math.sqrt

fun main() {
    val tiles = input.split("\n\n").map { it.toTile() }
    val map = tiles.assemble()

    val answer1 = map.corners().map { it.tile.id }.fold(1L) { acc, n -> acc * n }
    println(answer1) // 23386616781851

    val image = map.join()
    val seaMonsterCount = image.orientateAndCountSeaMonsters()
    val answer2 = image.flatten().count { it == '#' } - seaMonsterCount * seaMonsterSize()
    println(answer2) // 2376
}

typealias TileBorder = List<Char>

object Day20 {

    enum class Direction(val order: Int) { North(0), East(1), South(2), West(3) }
    data class Position(val x: Int, val y: Int)
    data class TileHolder(val tile: Tile, val tileDirection: Direction, val isFlipped: Boolean = false)

    data class Tile(val id: Int, val values: List<List<Char>>) {
        val nBorder: TileBorder by lazy { values[0] }
        val eBorder: TileBorder by lazy { values.map { it.last() } }
        val sBorder: TileBorder by lazy { values[values.size - 1].reversed() }
        val wBorder: TileBorder by lazy { values.map { it.first() }.reversed() }
    }

    private val seaMonster = listOf(
        listOf(18),
        listOf(0, 5, 6, 11, 12, 17, 18, 19),
        listOf(1, 4, 7, 10, 13, 16)
    )

    fun seaMonsterSize() = seaMonster.flatten().count()

    fun String.toTile(): Tile {
        val id = """Tile\s([0-9]{4}):""".toRegex().matchEntire(lines().first())?.let { it.groupValues[1].toInt() }
            ?: throw RuntimeException("Could not parse: $this")
        val values = lines().drop(1).map { line -> line.map { it } }
        return Tile(id, values)
    }

    fun Tile.border(direction: Direction): TileBorder = when (direction) {
        North -> nBorder
        East -> eBorder
        South -> sBorder
        West -> wBorder
    }

    private fun Tile.borders(): List<TileBorder> = Direction.values().map { border(it) }

    fun Tile.flip(): Tile = Tile(id, values.flip())

    fun TileHolder.border(direction: Direction): TileBorder = tile.borders()[(direction.order + tileDirection.order) % 4]

    private fun match(tile: Tile, position: Position, currentTileHolders: Map<Position, TileHolder>): TileHolder? {

        tailrec fun go(direction: Direction, isFlipped: Boolean): TileHolder? {

            val bordersToMatch = listOf(
                currentTileHolders[position.copy(y = position.y + 1)]?.border(South)?.reversed()?.let { North to it },
                currentTileHolders[position.copy(y = position.y - 1)]?.border(North)?.reversed()?.let { South to it },
                currentTileHolders[position.copy(x = position.x + 1)]?.border(West)?.reversed()?.let { East to it },
                currentTileHolders[position.copy(x = position.x - 1)]?.border(East)?.reversed()?.let { West to it }
            ).mapNotNull { it }

            val candidate = TileHolder(if (isFlipped) tile.flip() else tile, direction, isFlipped)
            return when {
                bordersToMatch.isEmpty() -> candidate
                bordersToMatch.all { (d, border) -> candidate.border(d) == border } -> candidate
                else -> when (direction) {
                    North -> go(East, isFlipped)
                    East -> go(South, isFlipped)
                    South -> go(West, isFlipped)
                    West -> if (isFlipped) null else go(North, true)
                }
            }
        }

        return go(North, false)
    }

    fun List<Tile>.assemble(): Map<Position, TileHolder> {

        tailrec fun go(remainingTiles: List<Tile>, remainingPositions: List<Position>, acc: Map<Position, TileHolder>): Map<Position, TileHolder>? =
            if (remainingTiles.isEmpty()) acc
            else {
                val matched = remainingTiles.mapNotNull { match(it, remainingPositions.first(), acc) }.firstOrNull()
                if (matched != null) go(remainingTiles.filterNot { it.id == matched.tile.id }, remainingPositions.drop(1), acc + (remainingPositions.first() to matched))
                else null
            }

        val positions = grid(sqrt(size.toDouble()).toInt())

        return flatMap { tile ->
            Direction.values().flatMap { direction ->
                listOf(false, true).mapNotNull { isFlipped ->
                    go(this - tile, positions.drop(1), mapOf(positions.first() to TileHolder(tile, direction, isFlipped)))
                }
            }
        }.first()
    }

    private fun grid(size: Int): List<Position> = (0 until size).flatMap { x -> (0 until size).map { y -> Position(x, y) } }

    fun Map<Position, TileHolder>.corners(): List<TileHolder> {
        val (minX, maxX) = minAndMax { it.x }
        val (minY, maxY) = minAndMax { it.y }
        return listOf(minX to minY, maxX to minY, maxX to maxY, minX to maxY).map { (x, y) -> getValue(Position(x, y)) }
    }

    fun Map<Position, TileHolder>.join(): List<List<Char>> {
        val (minX, maxX) = minAndMax { it.x }
        val (minY, maxY) = minAndMax { it.y }

        return (minY..maxY).reversed().flatMap { y ->
            (minX..maxX).map { x ->
                getValue(Position(x, y))
            }.fold(emptyList()) { acc, n ->
                if (acc.isEmpty()) n.values()
                else acc.zip(n.values()).map { (a, b) -> a + b }
            }
        }
    }

    private fun Map<Position, TileHolder>.minAndMax(f: (Position) -> Int): Pair<Int, Int> =
        map { (p, _) -> f(p) }.sorted().let { it.first() to it.last() }

    private fun rotate(values: List<List<Char>>):List<List<Char>> =
        values.indices.reversed().map { x -> values.indices.map { y -> values[y][x] } }

    fun TileHolder.values(): List<List<Char>> {
        val noBorders = tile.values.drop(1).dropLast(1).map { it.drop(1).dropLast(1) }
        return (0 until tileDirection.order).fold(noBorders) { acc, _ -> rotate(acc) }
    }

    fun String.seaMonsterCount(): Int {
        val seaMonster = listOf(
            listOf(18),
            listOf(0, 5, 6, 11, 12, 17, 18, 19),
            listOf(1, 4, 7, 10, 13, 16)
        )
        val imageSize = lines()[0].length
        val imageRows = lines()
        var seaMonsterCount = 0

        (0 until imageSize - 3).forEach { y ->
            (0 until imageSize - 19).forEach { x ->
                if (seaMonster.mapIndexed { i, monsterPart -> monsterPart.all { imageRows[y + i][x + it] == '#' } }.all { it })
                    seaMonsterCount++
            }
        }

        return seaMonsterCount
    }

    fun List<List<Char>>.flip(): List<List<Char>> = map { it.reversed() }

    fun List<List<Char>>.orientateAndCountSeaMonsters(): Int {

        tailrec fun go(current: List<List<Char>>, direction: Direction, isFlipped: Boolean): Int {
            val seaMonsterCount = current.joinToString("\n") { it.joinToString("") }.seaMonsterCount()
            return if (seaMonsterCount > 0) seaMonsterCount
            else when (direction) {
                North -> go(rotate(current), East, isFlipped)
                East -> go(rotate(current), South, isFlipped)
                South -> go(rotate(current), West, isFlipped)
                West -> if (isFlipped) 0 else go(this.flip(), North, true)
            }
        }

        return go(this, North, false)
    }

    val input = """
        Tile 1409:
        ##..#.#.#.
        ##........
        #.#...##.#
        #..#..#...
        .......##.
        ##......##
        ..........
        .........#
        .#..##....
        #.##...##.

        Tile 2939:
        ......#.##
        ##.#......
        ...##...##
        #.#.....##
        #...#....#
        .#..#....#
        #.....##.#
        ..##.#...#
        ..#.#.#..#
        #######..#

        Tile 3347:
        ...##..#.#
        .#......#.
        .#........
        #.....#...
        #.....##..
        ##.......#
        .....#....
        ......###.
        #...#..##.
        ########.#

        Tile 1297:
        #..#.##.##
        #..###...#
        #.##......
        ...#.#...#
        #.#......#
        ....#....#
        .#..#.....
        ......##..
        #.........
        ...#.##.##

        Tile 3203:
        ####.#.#..
        #.#.#.##..
        #......###
        #....###.#
        .......#.#
        #.........
        #..#..##..
        ..##...#.#
        #.....##..
        #.##.#...#

        Tile 1283:
        ####..#...
        #.......##
        #....#..#.
        ..##.....#
        .#...#####
        ###...#...
        ..##....#.
        .#.......#
        .##.#.....
        #.###..###

        Tile 1879:
        ######.#..
        ..#.#....#
        ..#..##...
        .#...#.#..
        ....#....#
        ....#.#.##
        ##.......#
        #...#..#.#
        ..#.##....
        #..####.#.

        Tile 2293:
        #.##.###.#
        ..#.....##
        #...#.....
        ..##......
        .#...#.#.#
        #........#
        .##...###.
        ###.#....#
        ...#......
        .#..######

        Tile 3079:
        #.###.....
        ......#...
        ..##......
        ..#...#...
        .#.#......
        #....#...#
        ........##
        ..#..#...#
        #..#......
        #.#.#.###.

        Tile 1069:
        .#.##.....
        ...##...#.
        ###.#..##.
        .#....#.##
        ......#.#.
        #.#..#.##.
        ...#......
        #..##...#.
        ##.##.....
        #.#.##.#..

        Tile 1229:
        #.#...#..#
        .........#
        ....#..##.
        #.#...#..#
        ...###.#.#
        ##.##.....
        ...##....#
        ..#..#.###
        ..#......#
        .##..#.#.#

        Tile 3631:
        ###.....##
        #.#.......
        #.#.#..#.#
        ....#...##
        #.###.#.#.
        .....##.##
        ...#..###.
        #..#...#.#
        ..#..##..#
        .##.#.#..#

        Tile 1747:
        ..##.....#
        .#.....#.#
        ..........
        ..#.#.#..#
        #...#.....
        ..##.#.#..
        #....#..##
        ...#.....#
        #.....##.#
        ..#.#####.

        Tile 2531:
        .#...##...
        #....####.
        ##...##..#
        #..#...#..
        ##....#...
        .#....#.##
        .........#
        .#......#.
        ...#...#.#
        ##....#...

        Tile 2203:
        ##..#...##
        ##..#.#..#
        ....##..#.
        ###.###...
        .......#.#
        #.....##.#
        #.#....#..
        .......#.#
        ...#.#....
        ##.#.####.

        Tile 2777:
        ...#.##...
        ...#......
        .#....##..
        ....#..#.#
        #.....#..#
        ......##..
        #....##.##
        ......#..#
        #..##.##..
        ...#.#.###

        Tile 3323:
        #...##.#.#
        ..#.......
        #......#..
        .#..##...#
        ......##..
        #....#..##
        .......#.#
        ....#...#.
        #.....#...
        ##..#.#..#

        Tile 3319:
        #....#.##.
        ##.....#.#
        ####......
        ..#.##..##
        ##....##.#
        .......#.#
        ..######.#
        #.#......#
        #..#......
        #..#.#..##

        Tile 3313:
        ...#.##.##
        ........##
        ##..#...##
        ##.....#.#
        ##..#....#
        .......#..
        ##.......#
        #.#.......
        .........#
        #.#..###.#

        Tile 2657:
        .###..#..#
        ..#......#
        .#...##...
        #..#.....#
        ....#...##
        #.........
        ...##.#...
        .##....#.#
        ##...#....
        #.####...#

        Tile 1907:
        #.#####.#.
        ##..#.##..
        .....#.###
        .#.#..##.#
        .#.###...#
        ...####...
        #..###....
        ##....#...
        ..#..#.#..
        #...###.#.

        Tile 3373:
        #.#####...
        ..###.....
        .##..#..##
        ....#...##
        ......#..#
        ..##.....#
        ....##.###
        .#.#.....#
        #..##.#...
        .......###

        Tile 3253:
        ##..###..#
        ##......##
        ##.......#
        ##.#......
        ##.......#
        #........#
        ..##...###
        #..###..#.
        #.....###.
        ..#.##..##

        Tile 2767:
        ......####
        ..#...####
        ..###.....
        #...##...#
        ...#..#..#
        .####....#
        #...##...#
        ...##.####
        #...##.#..
        ..#.....##

        Tile 3761:
        ....######
        .......###
        .#.##.....
        .......#..
        ..........
        .#.#.....#
        #........#
        ##.##...##
        #......#.#
        #.##.#..#.

        Tile 1031:
        ...#.###.#
        .#......#.
        ##...#..#.
        #...#.....
        #.....#.##
        #.#...#..#
        ##..##.#..
        .##.#..#..
        ..#.....##
        .......#.#

        Tile 1181:
        ####..#.##
        .....###.#
        ..##..#...
        ...#.#..##
        ...#....#.
        #.......##
        ...#....##
        #..##.#..#
        ###...#...
        #.##..#.#.

        Tile 2887:
        .####..#.#
        .......#.#
        #.#..#.#.#
        #.##.....#
        ...#.#...#
        ###..#....
        #...#.....
        .....#....
        ###..#..#.
        ...###..#.

        Tile 1381:
        #...#..#.#
        ..........
        ....#...#.
        ..#..##...
        ........##
        #.#.......
        ##........
        .#..##...#
        #..#......
        ########..

        Tile 2621:
        ...#.####.
        #...#..#..
        #.#.....#.
        ..##.#..##
        ####..#...
        .###.#..#.
        ##...##..#
        #..#.#..#.
        #..##.#...
        #..###...#

        Tile 1597:
        ##.##..###
        .#...##...
        ..##....#.
        .##.......
        .##....#.#
        #..#.#....
        #........#
        ...#......
        .#..###.##
        ...###.###

        Tile 3533:
        #.###.##.#
        ##.##...#.
        ##..#.#.##
        .#.#..#.##
        #.....#...
        #..#...#.#
        ..###.#..#
        .#....#.#.
        #.........
        #...#.#.##

        Tile 1249:
        .##.....##
        #.....#.##
        #..#..#.##
        #.#.#...#.
        ......#...
        .#...##...
        ##.#.....#
        ..###.....
        #.......#.
        #.######..

        Tile 2843:
        #.#.#.....
        .......#.#
        #.....###.
        #.....##.#
        ...##.##.#
        #...#.####
        ..#.#....#
        .......#..
        ##.#..#.##
        .##.##...#

        Tile 1583:
        ..####.#..
        ##..#....#
        ###...##.#
        #....#.##.
        #..#......
        ##.......#
        ##.#...#..
        ....#.#..#
        ....#..#.#
        ....#.###.

        Tile 3169:
        ...#.###..
        .####.....
        #.#.#..#..
        #...#..#..
        ..........
        ##.#...#..
        ..........
        #..#....##
        #...#....#
        ..##.....#

        Tile 1367:
        ......##..
        .##.......
        ##.#..##.#
        #...#.#...
        #.###.#..#
        #.#...#..#
        .....#...#
        .....#...#
        #.#.#.#...
        #....##...

        Tile 2099:
        ..###.####
        .####.###.
        ...#...#..
        ...##.##.#
        #....##...
        ....#..#.#
        ...#...##.
        #........#
        #..#.#..#.
        ..##...##.

        Tile 3613:
        #.#.####..
        ###......#
        .##..##...
        ##..##...#
        #........#
        .....#....
        .......#..
        .##.#.....
        .##.#....#
        ####..#.##

        Tile 3727:
        ..#.##.##.
        #..#..##..
        ##........
        #.......#.
        .#.###.#..
        .#....#...
        #.........
        #...#.#.#.
        .#..#...##
        .###.#.#..

        Tile 3697:
        ##.###.#..
        ......####
        #...#.#...
        .###...#.#
        ##.#....#.
        ..##.#..#.
        .##....#.#
        #..#.....#
        .#........
        #######.#.

        Tile 2503:
        ..#.#..#..
        .......###
        ....#.....
        ####.#.##.
        ...##...#.
        #..#..#..#
        ..........
        .......#..
        ##..#..#.#
        .#...#.###

        Tile 2131:
        ##..#...##
        #..#.#..##
        ..##...##.
        .....#.#.#
        ...##....#
        ..##..#..#
        ..........
        ......#.#.
        .#..#.#.##
        #..##.##..

        Tile 2129:
        .#.#....#.
        .......#.#
        .....#..#.
        .#......#.
        ###.#.#...
        ##.##.#...
        ...#.##...
        ......####
        ....#####.
        #..#.##.#.

        Tile 3643:
        ##..#.....
        .##.##...#
        #....#...#
        #.###.....
        ####.#....
        .#.....#..
        ##..###.#.
        ..#..#.#.#
        #..##.#..#
        #...##..##

        Tile 1307:
        #......#.#
        ##...#..#.
        ......##..
        ....#.#..#
        #....##..#
        ......#.##
        ....#.#..#
        #....#....
        #..#..#...
        #..#.#.#..

        Tile 3989:
        ..###.#..#
        ###...#...
        ..#..#....
        ##....#.#.
        ##..#.#..#
        .......##.
        ...#..##.#
        #....#....
        ......#...
        ####.#..#.

        Tile 2347:
        .#########
        ##...#.#.#
        ..#.##...#
        #..#..##..
        #..#......
        ###......#
        .##...#...
        .####...#.
        #...#.##..
        .#.#####..

        Tile 2801:
        #.###.####
        ......#..#
        ...##.....
        #.....#..#
        ...#......
        ........##
        ..###.####
        #.#.#.##..
        ###...##..
        ##.##..##.

        Tile 2543:
        .....#..##
        #....#.#..
        ....#.##.#
        ##.#..###.
        .#.#.#..##
        #.###.#..#
        ##..#..#.#
        ...###.#..
        .....###.#
        .##..#.#.#

        Tile 2029:
        ##.##..###
        .........#
        #.#......#
        #.#.#..#.#
        .#.#.##.##
        #.....####
        ...#.#....
        #..##....#
        ....#.#...
        ###.#..###

        Tile 1889:
        #..#.#####
        #.#.#...##
        #.#...##.#
        .....#...#
        ##...#.###
        .....#....
        #.#......#
        #........#
        ...#......
        .#..#####.

        Tile 1787:
        ######.#.#
        #.........
        .#.#.#....
        ...#.....#
        #..#.#..##
        ##.#...#..
        ...#.....#
        ##..#.#...
        #.....##..
        #...##.###

        Tile 1619:
        #.####....
        #.#.#.##..
        #...#....#
        .#........
        .......#..
        #....#.#.#
        .......#.#
        .#.#..##.#
        .##..##.#.
        .#...##...

        Tile 2617:
        #..##.###.
        .###...#..
        ..#.....#.
        #....##...
        #...#...##
        .#.....#.#
        #....#...#
        ...##..#.#
        ........##
        ...###...#

        Tile 2879:
        #.#.####..
        .....#....
        .###...#..
        ...#......
        #.#.....#.
        ....#.....
        .#.#.#.#.#
        ......#.#.
        #.........
        #.#..#####

        Tile 1429:
        ..#.......
        #.##.#.###
        #......###
        ....#...#.
        .....#.#.#
        ..#....###
        #..#......
        ....#..#..
        ..#...#...
        .#####.#..

        Tile 2797:
        ##.#.#..#.
        .....#.#.#
        .......#.#
        #...#.#...
        .##.#.##.#
        .##.....##
        ##......#.
        ##.#.#.#..
        .....#...#
        ##.#######

        Tile 3943:
        ..#.#.####
        .#.......#
        #..#.##.#.
        .#..#....#
        .#.##....#
        #.#.#.#..#
        ......#...
        #...##...#
        #....##...
        .##....#.#

        Tile 3691:
        ##.#...###
        .#.##...#.
        ##.##....#
        .##.......
        ......#..#
        ...#..#..#
        ####.....#
        #..####...
        ..#.....#.
        ...#.#....

        Tile 3083:
        #.###.....
        #.#....#.#
        #..#...###
        ...#..#..#
        ..#..#....
        ##...#.#.#
        #.#.......
        #.#..###..
        ##.#.....#
        .#.##.....

        Tile 1013:
        #....#..##
        #..#....##
        #.#.#.#...
        .....#.#.#
        #........#
        ..#.....#.
        .##..#.###
        #..#..#.#.
        ........##
        #.#.#.#.##

        Tile 2089:
        ##.#.#..#.
        #...#...##
        #...#...##
        ..#....###
        ...#......
        ......##..
        #......###
        ...#...###
        .##...#.#.
        .##...###.

        Tile 2551:
        ###.######
        ####.#.###
        .##.##...#
        #..##.##.#
        #.#.#.#..#
        ....#.#...
        #.#....#..
        ##...#...#
        #..#.#...#
        ..###..#.#

        Tile 3607:
        .#...##..#
        ####......
        #..#.##..#
        #..#......
        ###.....#.
        #....###..
        .........#
        ....##.#.#
        ......#..#
        ...#####..

        Tile 1831:
        ##....##..
        ##.#......
        #.#....#..
        #.##..##..
        ...###....
        ##.#.#...#
        #...#.#...
        ....#....#
        ##......##
        .##.#..#.#

        Tile 1697:
        ##..###.#.
        #..#...###
        #..#.....#
        ......#...
        ...#......
        #........#
        .#....####
        #....#....
        ..##.###..
        .#.##..#..

        Tile 1621:
        ....##.##.
        ....#.#...
        ....#.#.#.
        #.#...#..#
        ..#..#.#..
        #........#
        ....##....
        #.###.#..#
        #....#..##
        #..####...

        Tile 1019:
        .#.###..#.
        ###.......
        #...#.#..#
        ..#.#.....
        #.....###.
        ....#..#.#
        .#.#.#.#..
        #.........
        #...#..#.#
        ..##..####

        Tile 1471:
        ...#.####.
        ...##.##..
        #.#.#..###
        ..##...###
        ##.###....
        #....#...#
        ........##
        #.##....#.
        ##.##..#.#
        ####.###..

        Tile 3797:
        ..##...##.
        #...#....#
        ###.###.##
        ##....##..
        #.#.#...##
        ....##....
        #.....#..#
        #...#.#...
        #.##.#..#.
        ..#.#.#.#.

        Tile 1231:
        ##....#..#
        #.........
        #..#.#....
        ##...#....
        #..#.#...#
        ###.##....
        ##.#....#.
        ....#.#..#
        .#...#..##
        #..#####.#

        Tile 1667:
        ##.##.#..#
        ...#..#.##
        ..........
        #.........
        ..#.##...#
        ....#.#.##
        ..#.##...#
        ##...###.#
        ###.......
        #####..###

        Tile 3719:
        ..##.##..#
        ...#......
        #......#.#
        #.#..###..
        ....#.#.#.
        #.........
        #........#
        ##.......#
        #.....#..#
        .##.#.####

        Tile 1103:
        .#..####.#
        #....##.##
        ....###..#
        #.........
        #..#.....#
        #......#..
        .#..###.#.
        .....#....
        .......#.#
        .##.##.###

        Tile 2609:
        ..####..#.
        #.....#..#
        .#.####...
        ....#.....
        #......#.#
        .........#
        .......#..
        #.##.#....
        ..#.#.....
        ####.##..#

        Tile 1117:
        .#.#..##.#
        #...#....#
        #.#..####.
        .#.......#
        #...#.....
        #...#....#
        #..#.#.#..
        .........#
        .....#.##.
        .#..####.#

        Tile 3359:
        ##...#.#.#
        ..#.#....#
        ..#..##...
        .......#..
        .........#
        #.#...###.
        ..#..#...#
        #..#.....#
        #..#.#..##
        .###.#.#.#

        Tile 2851:
        .#....####
        #...##...#
        #..#.....#
        #......#..
        #.......#.
        .###.#.#..
        #....#..#.
        ###......#
        #.##.#..#.
        ..#...#.##

        Tile 2423:
        .#..#.####
        ...#....#.
        .....##...
        .........#
        ........##
        ##.......#
        ........#.
        #....#..##
        #.#.......
        #...##.##.

        Tile 1579:
        .##....#.#
        ##...#....
        .#.##....#
        ..##.#.###
        .#..#.#..#
        ....##..#.
        ..#..##..#
        #.#..##...
        .........#
        .#.###.###

        Tile 1361:
        ....#..##.
        .###.#...#
        ...#.#....
        #..#..#...
        #.##......
        #..#...#..
        .##......#
        .#...#.#.#
        #........#
        .#.#######

        Tile 1171:
        ##.####..#
        ....#.#..#
        #..#.....#
        #.#......#
        ###...#..#
        .##.#...##
        ##..##.#.#
        #..#.#..##
        #......#.#
        ....#.###.

        Tile 3847:
        ##...#..#.
        ##.#.#..##
        ..........
        #........#
        ..........
        #........#
        #..#.#....
        .#.....#.#
        ....#.....
        #.##.##.##

        Tile 2557:
        ##..#.#...
        .#.#..#...
        ####....#.
        #...#.#...
        ...#.....#
        ..#..##..#
        ..###.#.##
        ...#..#..#
        .....##...
        ##.####..#

        Tile 2069:
        ..###....#
        ###..#.###
        ........##
        #..#.#..#.
        .........#
        ##.#.#.#.#
        ....#...#.
        ...#.#....
        ........#.
        #.#.##..##

        Tile 2179:
        ##...#.#.#
        .##..#.##.
        #..#..#...
        ..........
        ###.#....#
        ..........
        #...#.....
        #........#
        ....#...#.
        ##..####..

        Tile 2659:
        ##..#...#.
        #....##...
        .....#.#.#
        .......#..
        ##.#..#.#.
        ##........
        #....###.#
        #......#.#
        .........#
        #...#.##.#

        Tile 1097:
        .#..#.###.
        .....#..##
        ....####..
        .#.#.....#
        ......##.#
        .#..####.#
        .###....#.
        ..##.##...
        ###.##.#..
        #..###...#

        Tile 2143:
        .##.....#.
        ......#..#
        .#........
        ##......##
        ###....##.
        #...#....#
        ..#..#..##
        ..##....#.
        #.#.......
        #..#####.#

        Tile 3527:
        ##.#...#..
        ..#.###..#
        .........#
        .....##.##
        .#.....###
        #.#...#..#
        .#.##.#.##
        ###.#..#..
        .........#
        #####.#.##

        Tile 1063:
        ##.#.#.##.
        ##...#....
        #.........
        ##..#.#..#
        ......##.#
        ##....#.#.
        .....#..##
        ##.###...#
        .....##.#.
        #......#.#

        Tile 2593:
        #...####.#
        ##.####...
        ##.#.#..#.
        #........#
        #...#....#
        .........#
        .#..#..#.#
        ......#...
        .#.......#
        ##..#####.

        Tile 1753:
        ...#.#..#.
        #.....#...
        .#......##
        .#....##.#
        .#..#...##
        ..#..###..
        ###......#
        #.#.#..###
        ###....##.
        ..#.##...#

        Tile 2273:
        #..##.###.
        #.....#...
        #...##....
        ..#....###
        #.........
        #####.#...
        ##..###..#
        #...##....
        #.#......#
        ##.##..##.

        Tile 2731:
        .#..#..##.
        ...#...#.#
        ...##.....
        #..#..#..#
        ..........
        .......###
        ...#..####
        ##.##....#
        ...#.#..#.
        .##..#..##

        Tile 2477:
        ..#.###.##
        #..#.#....
        .#.....#..
        ##..#.##..
        ...##....#
        #.#.#...##
        .##......#
        ..##..#..#
        ..#......#
        #..#..#.##

        Tile 2243:
        .#..######
        ..#..####.
        ...##.#...
        ...##..#.#
        ..#.#...#.
        #.#.#..#..
        ..#......#
        #...#.#...
        ...##...##
        ##.#####.#

        Tile 1451:
        #.####.#.#
        #........#
        #.#......#
        .#.......#
        .........#
        ..........
        #.........
        .........#
        #.........
        ..#..#..##

        Tile 1289:
        #....#...#
        #.....#.#.
        ...#......
        .#....###.
        ......#..#
        ##........
        ###.......
        .#...#..#.
        ..#..#.#.#
        #.#...###.

        Tile 2927:
        ..##.#..#.
        ##.#.#..#.
        ..########
        #....##...
        ...#.##...
        #.........
        #.....#.#.
        #.##..#...
        ....#.#...
        #.##.#..##

        Tile 3433:
        #.##...###
        #.#..#..#.
        .#.....#.#
        #..#......
        .....##...
        #.##.....#
        #..#....##
        .....#..##
        ##..#.....
        ..###..###

        Tile 1913:
        #.##..#.##
        #..####.#.
        #.##..##..
        ....#..#..
        .#.....#..
        .#..#....#
        #..####.##
        ..#..#...#
        #.......##
        .#.#####..

        Tile 1567:
        ####...##.
        #..#.....#
        ##..#...#.
        ##.#.#.#.#
        ..#.#.#...
        ##.#..#..#
        .##...##.#
        ....#...#.
        ..###..#..
        ...#..####

        Tile 2707:
        .#.#.#..##
        .#....#..#
        #..#.#...#
        ......#.#.
        .#..##...#
        .#.##.....
        #.#.....##
        #.....#..#
        #..#..##.#
        #..#...#.#

        Tile 3803:
        #..#.#...#
        .#........
        ##....###.
        #.##....##
        ..#....#..
        ##.#..#.#.
        ....#..#.#
        .#.......#
        #..#...#.#
        ##.....###

        Tile 2837:
        .#..#####.
        #.......##
        ###..#.###
        #...#....#
        ..#..#.#..
        ....#....#
        .#..##.##.
        ...#.....#
        #...#..#.#
        .##...##.#

        Tile 2473:
        #.##..##..
        ....#..#.#
        .........#
        #.....##..
        ....#.#..#
        ...#..#...
        ....#...##
        .....###..
        ..#......#
        ##..###..#

        Tile 3121:
        ...#.##..#
        #........#
        .##..##...
        #.....#..#
        ......##.#
        ......#.#.
        ......#..#
        ........##
        #...#....#
        .##.#..#..

        Tile 1637:
        #....#....
        ....#..#..
        #......#.#
        #..#..#..#
        #.......##
        #..##.....
        #....#...#
        .#........
        .##..##.##
        .##...###.

        Tile 1091:
        ..###....#
        ###...#...
        ..#......#
        ##........
        .........#
        .....##..#
        ###.##....
        #..#....#.
        .#.#.#....
        .....#####

        Tile 1453:
        ###...#.##
        .##..#.#.#
        .#.......#
        .##...#..#
        #..#...#..
        .##...#..#
        #....##..#
        ...##..#..
        ....#.....
        ..##....##

        Tile 2861:
        ##....####
        .....#...#
        .##.#...#.
        .....##..#
        ##..##..#.
        #...#.##.#
        ##....##.#
        #..#.#....
        #####.#...
        ##.....#..

        Tile 2677:
        ##..#..#.#
        #....#....
        .#..#....#
        .###....#.
        .#........
        .....#.#.#
        #..#.....#
        #.##.##.##
        ..#.##..##
        #..#..#...

        Tile 3413:
        ..###.#..#
        ...##.....
        ...#..####
        ........#.
        ##.....#.#
        ......#..#
        #...#..#.#
        #.......#.
        ##..#.#.#.
        #...#####.

        Tile 1193:
        #.#.#.#.#.
        .#....##..
        #.........
        .......#.#
        ..#.#.#.##
        ...#...##.
        ##........
        #..#....##
        .........#
        ####...###

        Tile 1949:
        ##..#..##.
        .....#..#.
        ...##...##
        ####....##
        .#..#.....
        #........#
        .##.#....#
        ...##.....
        ...#..####
        ###...####

        Tile 3863:
        #.#..#.###
        #.#.....##
        ##....##.#
        ..###.###.
        #.##....#.
        ...#.#.###
        ...#..#.##
        ..#......#
        ##...#...#
        #..#.#.##.

        Tile 1439:
        #....#.###
        .....#.#.#
        .#.#...##.
        .##.#....#
        #....#....
        ....#.#..#
        .....#...#
        #...###...
        .#..##..##
        ##..#.####

        Tile 1129:
        ..########
        ..#.#.#...
        ....##....
        .......#..
        ........##
        ..........
        #..#.#.#..
        ##........
        .........#
        ..##..####

        Tile 2953:
        .#.###.###
        ........#.
        ....#.#.##
        ..#.#....#
        ......#.##
        ...#....#.
        ###...#..#
        ....#.....
        #.........
        #...#...#.

        Tile 3371:
        ##..#..###
        ##.....###
        ##....#..#
        #.....#...
        ###.#..###
        .#.#..#.##
        ##........
        ##.......#
        #.##..#...
        ..##.####.

        Tile 1399:
        #.....#.#.
        #......#..
        .#..#....#
        ####..#..#
        #..#.....#
        ##..#..#..
        #.....#.##
        .#..##..#.
        .#..#....#
        ..#.#.###.

        Tile 3911:
        .#.....#.#
        ..#.....##
        #...##....
        #.........
        ..#.#...##
        ..#...#..#
        .#.#....##
        #..##....#
        .#..##..##
        #.#..#..#.

        Tile 1699:
        .####.####
        ..#..#....
        #.....#.#.
        ...#..##..
        ##.#..#...
        ##..##....
        #..#..#.#.
        .###.#....
        .....##.##
        .#..#...##

        Tile 3701:
        .#.#.#.#..
        ..#.####.#
        ..##...###
        ......##.#
        ....#....#
        .....#..##
        ...#.#.###
        #..####.#.
        ...##....#
        ..###..###

        Tile 1811:
        .##.##.#..
        ....#....#
        #......#.#
        .....#..#.
        #..#.#...#
        .#.#.##..#
        ......#...
        ..##..#...
        .....#...#
        #####.#..#

        Tile 2539:
        ...######.
        #...#.###.
        .#..#.....
        .....#...#
        .#...#...#
        #.###..###
        .######...
        #...#.#.##
        #.....##.#
        ...#..#.##

        Tile 3517:
        .#...#.##.
        #....#..#.
        #......#.#
        ##.##.....
        ...###..#.
        #..#.#...#
        #...#.#...
        ###.......
        .#.......#
        ######.#..

        Tile 2113:
        #..###..#.
        ...#....##
        ##.......#
        #....##..#
        ..#.#..###
        #.#......#
        #.....#..#
        .#..#.#..#
        ...###....
        ##.#..#...

        Tile 1009:
        ...##..###
        ..#.......
        .#.....##.
        ##....#..#
        ##........
        #....#..#.
        .........#
        #.#..#..##
        ....#.#..#
        ..#.##.#.#

        Tile 1559:
        ###....#.#
        .###...#.#
        #.#.....#.
        .#..#.#..#
        ....##.#.#
        #.#..##.##
        ..#..##...
        #.###.....
        .#.#.##..#
        ##....####

        Tile 2971:
        ...###...#
        ......##..
        ...#....##
        .#.#.##..#
        ###.#.##.#
        #..#...##.
        ..#.#...##
        .....#.##.
        .#........
        ##....#..#

        Tile 1327:
        .#..##...#
        #.##.....#
        .#.....#.#
        ...###...#
        #....#....
        .#.......#
        .#.......#
        #.........
        .....#...#
        ..#######.

        Tile 3217:
        ..###..#..
        .#..#....#
        ......####
        ###....#.#
        ###...#...
        .#...#....
        ........##
        #.##.#.##.
        #......#..
        ...#...#.#

        Tile 1861:
        ####.####.
        .#.....#..
        ###...#...
        .##.#..##.
        ...#..#...
        ...#..#...
        ##..#...##
        #...##..#.
        #...#.....
        ##........

        Tile 3593:
        .##.##.###
        ....#...##
        .#..#....#
        #..##.##..
        #..#...###
        ...#..##..
        ....#...##
        ###......#
        ..........
        ..#......#

        Tile 3041:
        ###..###.#
        ....####..
        ....#..##.
        #.#.....##
        #..#...#..
        #.......##
        .#...#####
        ##.#...#.#
        ..#.###.#.
        .####...##

        Tile 2647:
        #.###.####
        #...#...##
        .##....##.
        #.#.#.....
        #..#..##.#
        ##....#..#
        ..#....###
        .....###..
        ........##
        #.#.#....#

        Tile 2437:
        .##...#...
        #.#...##.#
        .#....##.#
        #####...#.
        #...##.#..
        ..#....#..
        #.......#.
        ..#..#.#..
        #.##...#.#
        #.##..#..#

        Tile 3709:
        #######.##
        ###.#..##.
        #.#...#..#
        .#....####
        ##...#..#.
        ....#..#..
        ##........
        #..##..#.#
        .....#...#
        ..#.##...#

        Tile 2039:
        ##.###..#.
        ...#.....#
        ..#...##.#
        ##.#.#...#
        #....#..#.
        #.####...#
        ...##...##
        ##...##..#
        #.....##.#
        ####.#.#.#

        Tile 3011:
        ...##.#...
        .........#
        ###.####.#
        #.......#.
        .#.....#.#
        ..######..
        #.....#..#
        #........#
        .##.##....
        #.....###.

        Tile 1151:
        .##.#..#.#
        #.##.....#
        ...#..#...
        ....##..##
        ###....###
        ##..#..#.#
        #...#....#
        ..#....#..
        ...#.....#
        #.#..#.#..
    """.trimIndent()
}