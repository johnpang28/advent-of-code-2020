package me.jp.aoc

import me.jp.aoc.Day20.Direction.*
import me.jp.aoc.Day20.Tile
import me.jp.aoc.Day20.TileHolder
import me.jp.aoc.Day20.border
import me.jp.aoc.Day20.flip
import me.jp.aoc.Day20.seaMonsterCount
import me.jp.aoc.Day20.toTile
import me.jp.aoc.Day20.values
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day20Test {

    private val tile = """
            Tile 2311:
            ..##.#..#.
            ##..#.....
            #...##..#.
            ####.#...#
            ##.##.###.
            ##...#.###
            .#.#.#..##
            ..#....#..
            ###...#.#.
            ..###..###
        """.trimIndent().toTile()

    private val nBorder = listOf('.', '.', '#', '#', '.', '#', '.', '.', '#', '.')
    private val eBorder = listOf('.', '.', '.', '#', '.', '#', '#', '.', '.', '#')
    private val sBorder = listOf('#', '#', '#', '.', '.', '#', '#', '#', '.', '.')
    private val wBorder = listOf('.', '#', '.', '.', '#', '#', '#', '#', '#', '.')

    @Test
    fun `Tile functions`() {
        assertThat(tile.border(North)).isEqualTo(nBorder)
        assertThat(tile.border(East)).isEqualTo(eBorder)
        assertThat(tile.border(South)).isEqualTo(sBorder)
        assertThat(tile.border(West)).isEqualTo(wBorder)
    }

    @Test
    fun `TileHolder functions`() {
        val nTileHolder = TileHolder(tile, North)
        assertThat(nTileHolder.border(North)).isEqualTo(nBorder)
        assertThat(nTileHolder.border(East)).isEqualTo(eBorder)
        assertThat(nTileHolder.border(South)).isEqualTo(sBorder)
        assertThat(nTileHolder.border(West)).isEqualTo(wBorder)

        val eTileHolder = TileHolder(tile, East)
        assertThat(eTileHolder.border(North)).isEqualTo(eBorder)
        assertThat(eTileHolder.border(East)).isEqualTo(sBorder)
        assertThat(eTileHolder.border(South)).isEqualTo(wBorder)
        assertThat(eTileHolder.border(West)).isEqualTo(nBorder)

        val sTileHolder = TileHolder(tile, South)
        assertThat(sTileHolder.border(North)).isEqualTo(sBorder)
        assertThat(sTileHolder.border(East)).isEqualTo(wBorder)
        assertThat(sTileHolder.border(South)).isEqualTo(nBorder)
        assertThat(sTileHolder.border(West)).isEqualTo(eBorder)

        val wTileHolder = TileHolder(tile, West)
        assertThat(wTileHolder.border(North)).isEqualTo(wBorder)
        assertThat(wTileHolder.border(East)).isEqualTo(nBorder)
        assertThat(wTileHolder.border(South)).isEqualTo(eBorder)
        assertThat(wTileHolder.border(West)).isEqualTo(sBorder)

        val nFlippedTileHolder = TileHolder(tile.flip(), North)
        assertThat(nFlippedTileHolder.border(North)).isEqualTo(nBorder.reversed())
        assertThat(nFlippedTileHolder.border(East)).isEqualTo(wBorder.reversed())
        assertThat(nFlippedTileHolder.border(South)).isEqualTo(sBorder.reversed())
        assertThat(nFlippedTileHolder.border(West)).isEqualTo(eBorder.reversed())

        val eFlippedTileHolder = TileHolder(tile.flip(), East)
        assertThat(eFlippedTileHolder.border(North)).isEqualTo(wBorder.reversed())
        assertThat(eFlippedTileHolder.border(East)).isEqualTo(sBorder.reversed())
        assertThat(eFlippedTileHolder.border(South)).isEqualTo(eBorder.reversed())
        assertThat(eFlippedTileHolder.border(West)).isEqualTo(nBorder.reversed())

        val sFlippedTileHolder = TileHolder(tile.flip(), South)
        assertThat(sFlippedTileHolder.border(North)).isEqualTo(sBorder.reversed())
        assertThat(sFlippedTileHolder.border(East)).isEqualTo(eBorder.reversed())
        assertThat(sFlippedTileHolder.border(South)).isEqualTo(nBorder.reversed())
        assertThat(sFlippedTileHolder.border(West)).isEqualTo(wBorder.reversed())

        val wFlippedTileHolder = TileHolder(tile.flip(), West)
        assertThat(wFlippedTileHolder.border(North)).isEqualTo(eBorder.reversed())
        assertThat(wFlippedTileHolder.border(East)).isEqualTo(nBorder.reversed())
        assertThat(wFlippedTileHolder.border(South)).isEqualTo(wBorder.reversed())
        assertThat(wFlippedTileHolder.border(West)).isEqualTo(sBorder.reversed())
    }

    @Test
    fun `TileHolder values`() {
        val tileValues = listOf(
            listOf('#', '#', '#', '#', '#'),
            listOf('#', '.', '.', '.', '#'),
            listOf('#', '.', '.', '#', '#'),
            listOf('#', '.', '#', '#', '#'),
            listOf('#', '#', '#', '#', '#')
        )

        val tile = Tile(0, tileValues)

        assertThat(TileHolder(tile, North).values()).isEqualTo(listOf(
            listOf('.', '.', '.'),
            listOf('.', '.', '#'),
            listOf('.', '#', '#')
        ))
        assertThat(TileHolder(tile, East).values()).isEqualTo(listOf(
            listOf('.', '#', '#'),
            listOf('.', '.', '#'),
            listOf('.', '.', '.')
        ))
        assertThat(TileHolder(tile, South).values()).isEqualTo(listOf(
            listOf('#', '#', '.'),
            listOf('#', '.', '.'),
            listOf('.', '.', '.')
        ))
        assertThat(TileHolder(tile, West).values()).isEqualTo(listOf(
            listOf('.', '.', '.'),
            listOf('#', '.', '.'),
            listOf('#', '#', '.')
        ))
    }

    @Test
    fun `sea monster count`() {
        val image = """
            .####...#####..#...###..
            #####..#..#.#.####..#.#.
            .#.#...#.###...#.##.##..
            #.#.##.###.#.##.##.#####
            ..##.###.####..#.####.##
            ...#.#..##.##...#..#..##
            #.##.#..#.#..#..##.#.#..
            .###.##.....#...###.#...
            #.####.#.#....##.#..#.#.
            ##...#..#....#..#...####
            ..#.##...###..#.#####..#
            ....#.##.#.#####....#...
            ..##.##.###.....#.##..#.
            #...#...###..####....##.
            .#.##...#.##.#.#.###...#
            #.###.#..####...##..#...
            #.###...#.##...#.######.
            .###.###.#######..#####.
            ..##.#..#..#.#######.###
            #.#..##.########..#..##.
            #.#####..#.#...##..#....
            #....##..#.#########..##
            #...#.....#..##...###.##
            #..###....##.#...##.##.#
        """.trimIndent()

        assertThat(image.seaMonsterCount()).isEqualTo(2)
    }
}