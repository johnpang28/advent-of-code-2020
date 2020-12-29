package me.jp.aoc

import me.jp.aoc.Day24.flip
import me.jp.aoc.Day24.input
import me.jp.aoc.Day24.normalise
import me.jp.aoc.Day24.parseDirections

fun main() {

    val directions = input.lines().map { it.parseDirections().normalise() }
    val blackTiles = directions.groupBy { it }.mapNotNull { (k, v) -> if (v.size % 2 == 1) k else null }.toSet()

    val answer1 = blackTiles.size
    println(answer1) // 266

    val answer2 = (1..100).fold(blackTiles) { acc, _ -> acc.flip() }.size
    println(answer2) // 3627
}

typealias Tile = String

object Day24 {

    enum class Direction { W, NW, NE, E, SE, SW }

    fun List<Direction>.normalise(): Tile {

        fun go(acc: Map<Direction, List<Direction>>): Map<Direction, List<Direction>> {

            val reducedOpposites = (0..2).flatMap { ordinal ->
                val d = Direction.values()[ordinal]
                val opp = Direction.values()[ordinal + 3]
                val values = acc.getOrDefault(d, emptyList())
                val oppValues = acc.getOrDefault(opp, emptyList())
                val min = minOf(values.size, oppValues.size)
                listOf(d to values.drop(min), opp to oppValues.drop(min))
            }.toMap()

            val reducedAdjacents = Direction.values().fold(reducedOpposites) { acc1, d ->
                val (d1, d2) = d.adjacents()
                val d1s = acc1.getOrDefault(d1, emptyList())
                val d2s = acc1.getOrDefault(d2, emptyList())
                val min = minOf(d1s.size, d2s.size)
                if (min > 0) acc1 + (d to acc1.getOrDefault(d, emptyList()) + (1..min).map { d }) + (d1 to d1s.drop(min)) + (d2 to d2s.drop(min))
                else acc1
            }

            return if (acc == reducedAdjacents) acc else go(reducedAdjacents)
        }

        return Direction.values().flatMap { d -> go(groupBy { it }).getValue(d) }.joinToString("") { it.name.toLowerCase() }
    }

    fun Set<Tile>.flip(): Set<Tile> {
        val whites = flatMap { tile -> tile.adjacents().mapNotNull { adjacent -> if (this.contains(adjacent)) null else adjacent } }.toSet()

        val blackTransitions = this.mapNotNull { blackTile ->
            val blackCount = blackTile.adjacents().map { contains(it) }.count { it }
            if (blackCount == 0 || blackCount > 2) null else blackTile
        }.toSet()

        val whiteTransitions = whites.mapNotNull { whiteTile ->
            val blackCount = whiteTile.adjacents().map { contains(it) }.count { it }
            if (blackCount == 2) whiteTile else null
        }

        return blackTransitions + whiteTransitions
    }

    private fun Direction.adjacents(): Pair<Direction, Direction> {
        val before = (ordinal - 1).let { if (it < 0) 5 else it }
        val after = (ordinal + 1).let { if (it > 5) 0 else it }
        return Direction.values()[before] to Direction.values()[after]
    }

    private fun Tile.adjacents(): List<String> = parseDirections().let { base -> Direction.values().map { (base + it).normalise() } }

    fun String.parseDirections(): List<Direction> = fold(emptyList<String>()) { acc, n ->
        acc.lastOrNull()?.let { last ->
            if (last == "n" || last == "s") acc.dropLast(1) + (last + n)
            else acc + n.toString()
        } ?: acc + n.toString()
    }.map { Direction.valueOf(it.toUpperCase()) }

    val input = """
        sweswseeeseseneeeeeeenwnweswe
        neswswsenwseeswsesesenwseseeseseseseswnw
        swswswnwwneeseneswseseneeswswseswswsenw
        eeswwnwnwwnwnwnwwnwswnwnwnwnweswnw
        sewwwnwwewswewswnewnwwnenesw
        swswswwnesesweeswewwnewnewswswsw
        nwswswnweneswswwwswswswswseswswwswsesw
        nweneweneswswsenwseswnwwsesewnwwe
        senwewseneswwswwsesesesenwseseesenee
        sesenwseseseneseeseswswswweesewwse
        swswsewswwneswseswneseswswseswswneswswsw
        swswnwwwswswnwswsweswswswswswnweswsese
        swnenwneeeneneswswnenewneswnwnenwneene
        swseswswswseneeseswswswwswwsesw
        swnwneenewweeenwenenenesenesenene
        seseeweeeweeseeeneeeweee
        enweseseseswseswsesesesesenewsenwsesese
        neseswsweswswseswswswwswswnwsw
        neneeswneswnenwneneneeewneswneenee
        nenwwnenwseneenenwnenwenwswneneswnenwenw
        seseseseseswseneswsesesese
        seeseewnwewwnwwswnwswnweeweww
        sweseseseswswseswsewsesesenwsene
        sesenwwesweseseeseseeneseneeesewse
        nwneneseswnenenenwne
        nweeeswenweeeswwenweswwneeese
        nwseswnesenwswswsewseswsenweneseeswswe
        swseswswneewwnwweswwsenwwwwsww
        seeseeweeeesweseneeeeenwsesw
        swswswsweswswnwswsw
        wwswswnwwswswwnwswneseseneseswnweesw
        nenenwnwnweswnenwwneswneseswnwswnwnee
        swswswswswswsweswswswswsww
        sesesesenwsewenwsesewsesesenesese
        seeneeeweswnenenwnwseeneseenwesw
        wnenwnwwsweneswsewsesenenenewswnwnw
        enwseswwnwseswswnewnwseseeseneswesew
        neneneneenenewneenenenenewne
        sesewewnweeeeeswe
        wwewswwwwwewwww
        wnesesesenewsenewsee
        seseseenwseweseswsesesesesesenewnwse
        swnesesewseswseswswswseswnwswsweswwsw
        wwneswswseseswwneswnewswswswewswsww
        swsewewnwnwswwesw
        neeeenweneseneneeene
        seseswnwwsewswwesesenwseswneeneenwnw
        eesweeeseneenwneneenewewnwsenw
        seseenwnwswswswswswneswseseswwsesenwsw
        eeneseeeseeseseseneseesewwswsese
        eeseseeswewnwenw
        wwwwswseswwwwswswnw
        swswneseswswwswswneswsewseswwswseswswswne
        eeneeneneeneeeswe
        wsesesewseseeesenweneseeswsesenese
        nenwenenenenewnenweenesesesenewnenee
        eseneeewenweeseweeswneeseneenenw
        wswwwwswwwwne
        nenwseseswswwsesesesesenwseeseswswsese
        nenwsenwnenwnwwnwnwnwnwsenenwnenesenwnw
        wseweneeswnwnwsweewswsesesesesese
        seseseseswsweswseswseswswswnwnwsese
        nwwswenwenenwswnene
        neweeweeeeeneeseeeeeswee
        nenewneweneneneneneneeeneswnenesenesw
        nesewnwwneswnwswneseneseenesweeswe
        nwseseneswsewseneswswewnenwseew
        swswnwwwwswswswwneseswwswewneww
        neeneeneseneesweeneweneenee
        seseswswswswswsesesesesenwsee
        wnewwwswwnwseneswsenewwewseew
        nenesewwnenewnenwsenwnenenwewsenwnenenw
        sesewnwnwwneswnwnewnwnwsenewww
        neeswsewnwnewwwsenwesew
        eeeeseeseeneweeseeewwnesenw
        swwswswwwswwnesene
        nenenenewwswnweeweswneneneneswnese
        neneewenenenenenenenenene
        swnwewnwewnwwnenwwnwwswnww
        swenwwswneseswswswswswswwneewswswse
        seeseeeesewseeee
        wswwswseswwswwswwswnenwnesenwenwsew
        wwwwwwnwwwesenewwwwewwnwsw
        eeeeenenweese
        nweeweeeneseneesweneswseswnwneee
        wsenenenwseenwnenewseswnwnesene
        wwsewwwenwweswnwwwnwwwswwnwne
        nwsewnenenenenenenwsenwneswneenenenenenw
        ewneswneneswenwwewneneewneseene
        swswwswswswnweswswswenwswseswswswwsw
        senweseseeswnenwsweweseesewse
        wswwnenwewwwwwnesewnwnwsewww
        nenwnenewseneneneneneneneseneeneswwnenene
        neswseswswswwnwswswwwneneswseswswsesw
        eneenenwneneneneneeeswnenene
        neseseswsesesesesesesewe
        eewnwenwseenenesenenwse
        swswswewwswswswswwnwsww
        wnenwsenwewnwnwnenwnwweeseswnwnwnesw
        wwwenwnwswseswswswseseenewnewswswww
        nwseneneeenewnenenenenenenewnenenese
        eeswseswswseswnwseseseswsww
        eneneenwneneeswnewnwsenwnwneswnenwnwne
        swweswswswswwswsweswswswnwnesewswswne
        nenwneenwwneneswnwne
        nenewwsewwwsesewewswnwnwwsewww
        wnwsesesesewwwswweswnwnwwnwnwsww
        eseeswseeeeneseeswseswnwseseenwnwse
        senwsewseseswseswneneseseseseseswswsesw
        nenwneswnenwnenenwnenenwsenenwsewnenewne
        sweseeneewewnesesesweweseeenee
        wwnesenwnwwwnwswnwnwsenenwsewwenw
        swwsewswwnewwwswswswswnwneswnewswse
        wnwenwswnenenwnwnwseneswnwenwswnwsenesw
        swseswseseseesewsesesesesese
        wnwseseeseseswesewsenesesesesesesesesw
        swswnwwnwswsenwneseseseeswsenwswwswne
        swenwwseeneneweweseswneeeseenenw
        swseswswneswswneewseswwswnwwseeseswsw
        nwnwsenwsenwsenewnwnwwwnwnewwnwswnw
        wswswwwnwwwwwwwwsewnwnewwe
        nesenenenenwnwnwnwnwnenesenwnewnwnewsw
        wneewwenwnwenesewsweswsweswsenw
        neswswwswseeswwnwswwnwseswnenweneesese
        eweneeneweseeeeeese
        seseseenweseseseseese
        esewneeswseswsenwneenwenwswwnenenw
        eneseswnewneneenwnweesweswneneesw
        nwnwnwnwwewswswewwnwwewnwnwwsw
        wwwwsewwwwwnewwwnw
        nwneneneneseneenewsewwsweweeseene
        eeewsewwwneswwnwneseweswswwwww
        nenenenenesewseneeneeneswnwnenwnenene
        senesenwnwsenwsenwnwenwwnwnwnewwnwnw
        swwseesesesesesenwseseeeseseseenwse
        wwneeswsesenenesenewwneseenenenew
        nwnwnenwnenewswnwswswneneeenwnwnwenwne
        neneeneeewewneneeeeewneneneswne
        nenwnewwnwneseenwswnwnwnwswnenenwnwne
        enwswsweenenenwwwseneeeneweesw
        wwnwwweswwwwnwewwwwwsenwnew
        nwneneswsenwswnwnenwnwnenwsenwsenwnwenenw
        weswnesenweseesenwse
        eweeeenenwweeswsweneeenwe
        wewwnewwwwwwwwwswwwwese
        nwwneenwnwnwnwesewnwnwwenwnesw
        swwnewswwwwseenewwwwswwwww
        eswnenesesenwesesewswwseseneeesee
        weneneneseswnwswneneneswnenwneeneenenee
        wnwnwnenwneenwnenenenene
        swswswswnwswswswewseswswseswsweeewnw
        nwnwesesenwseswseeseseneswseee
        swswswsesesewseesesesese
        seswneswneesenwnwneswesesww
        nwwswswwwswwwsewwnwewsw
        swnwswswwswewnwswwnweswneswswwwswse
        newesesenesesesesesenwswsesewsesesesenw
        wswnwswswenwswswwwswswswse
        swswswneswswsesewsese
        swsewswwnenewwwswneswnwswenwnwne
        eseswnwnwenwswesesweewwnwenwsesese
        wwnwnwweswsewwwnwnwnwwne
        swswsenwswswenwswwnewwsew
        seeenwwnwwnwwnwweswwnwnwnenwnwnw
        eeneswseneneneeweeew
        enenwsenenwnenwnenwswnwnwwnwnweswnwnwnw
        enwneeeneeeswnwneeneneneseene
        wnwewwswseswnwwnwswneseeweewsw
        eneneeswswneeewnwnweeeseeneee
        newnwnwenwnwnwnwnwnenwnesenenwne
        wswwewswswswswwswsw
        swnwnwnewwswwwsenewwnwewnwwsewnw
        nweeeeenwnweeeewseenwswseesew
        swesenwswswwnweeneeseeeeeeesee
        nenenenwnwwnwnenesenwnenwnenenwwe
        eeeneeneweseeeeeswnewnweeswe
        nesewwwweswswwwswnenwwwwwesww
        nesweesweenweneeeswnweneeeeesw
        seswseeeweseesenweneweneeswnesw
        sesweswswswwseneeswnwwwsenwswnwwswsw
        neneneenwnewnwseesewseneneeeeseew
        seeswsesenwnwseseeseseseesenweenewse
        ewswwseseswswswsene
        nwnwnwnwwwsenwnwnwsewwnwnwnwsenenwnw
        nwenweseenwwnwnewsweswnwwseswwnwne
        wswswseswneswnwwneswnewwwsewswsw
        neseswsewswswswsenenwsesesweswsweseswsw
        nenwsewnwwnenwnenesesenwneswwnesenwwne
        swwweswswwwnewwwwnwswwwwesw
        nesenewsenwnweneewswnwwswwsenwseseswne
        swwnewwwwnwwneswwsewwswwsesew
        neneneneeseneewswneneseewneswnewsw
        nwnenenwnenwswnwnwnwswswnwnenwnwwsesee
        swwseswswswswwswnesw
        enenwneeeweseseeseeswsewsewneew
        wwwnwnwwwwnwwseenenenwwswwwsew
        swseswsenwswswswswnwswswswnwneeswsesese
        nwnenwsenwswenwnenwnwswnwnwnwnwseswnewnw
        swwwwnewswnesewwnesewswwwseew
        ewnwnenwnwnwnwnwnwnw
        seneneswwnewnwnwsewnwnwnwseenwwwsee
        seeseseenwseseenwseesenwesesenwswsese
        enewnwewseswnesewwwwnwswnwnwwne
        swswwswneswsweswnw
        nenenewswneswenenwswneeeseewneneee
        nwwewnwwnenenwnwnwsewnenwswsenwnwsw
        nwwnwnenenwwsewenwswswnweswswsewnwe
        nwnwswnwnwnwnwnwnwenwwseswwwwnenwwnwe
        nwnesenweesewenwweseeseeeswwsw
        wnewwwnwnwwwwsewwewsewwnewse
        newseneenweseswesweeesweeesew
        enwswneeesweswsenwseneseeseswnewse
        eeseswwseneseseeeswnweeneseesee
        sewswswwsesweswnwesenwnwswswsweswsw
        nesenenenesenwnenwneeweneneeswneee
        nwnweewnwnwnwnwnwnwnwnwnesewnwnwswnw
        nwenwswswnwnenwnwnwwsenwnwnwnwenwnwnwse
        seswswswswwsweswnewsweseseswnw
        swseswwsesesenesesweswseseswnwwswsesw
        wnweeeseseeeeenwneeweeseeee
        eenwswnwwnwweenwwneswnweswwnwsw
        wnewswewwswsenwsenwwwsewswnwwenew
        swswneeneneneenenwnenenenesenenenwnene
        newnwneneesenewswsewnwneseneeswsew
        wnwewseseswwenwnenwwnwwswswwwnwnwe
        enwnenenewneneneneneneneneneswswwnese
        nenwswseseseswswesenwseswsene
        nwnenwnwnwwneseneenenenesweeseneeswnese
        nwnwswnenenwnwnwnenwnwnwnwnwnw
        swswnwswswswswseswswneesenwswsweswwswesw
        nwswnesewseseneneseswsesesewswneswsesw
        nwnenwesenenesenwnwnenewnwwnwnwnwnwwnw
        seeneneneneneenwnee
        newsweeseesesesewnesesenwsesenesesese
        eenewnwswnwesewnwwswnwswnwwswnwnwe
        wewsewwwwsewnwnwnewwsewwswnw
        swnewnwwnwswwwswwwwswewwseswwne
        nwswnenwsewnwseswwwneweweeswwww
        wnwnwenwnwsenwnwsewnewnwwnwnwnwenwnenw
        swnesenwenwneswswneswsesewseseeseesene
        nwnwnwnwewsenwswnwenwnwwenwnenw
        ewneewseenwswneswenwsenwewwe
        senwesenenwsewseseseseseseseswesesenw
        nesenewwnweneneneneneneesenewnwneswne
        neneeeswwneeeswswenenwwnewwee
        weseeseseeneeeneesw
        nwnwnwnenwswsenwneswenwnwsewnwneneswnenenw
        wneweenwenesweneeweenenenewse
        sweenwswnweneneseneeseseweswseseese
        swnwnesewwwseewnewwwwwwwww
        eenenenweswwnwwnw
        swswnwsweswneneeswswswswswswswsenwnewse
        wnenwewwewseewswswnwnesenwnwnwene
        seswseseswnwseseswseswswsw
        nwnwnwnwswwenwwnwwwswewwewnwnw
        nesewsewneswwwsenewsenewnwwsw
        nweneeeewneseseweneswseseswnesewse
        wswswwwwsenenenewwswsewwwnw
        eenwwwswnwwwwnwenwwewnwswswwnw
        swsenwnwsesenwseswseseseneswseneswwese
        sewseseseswseeneseseswsesewsesenewse
        swswswswswswswswswswwswseneneeswwswsw
        nenenenwneneeseeseneenewnenewneewsw
        nwsenwnwnenenewnwswnwnwswnwwnwnwswnwwe
        wwwnenwsenwnwnenwsewwnwsenenw
        neeswwweseseenwsenwseseesesenwwne
        esesewseseneneeseseenwseswswseeeee
        neneneneswseneneweneswnenenw
        nweswswwseswwseeneseseseswewswswswnw
        swswwwnwswnewwnewwwwwwswwswe
        enwwnwwnwwwnwwnwenw
        sewwnwnwwewswwwswswnwwseswswswsw
        swswseswnwnwswseswnwsenwswwswwswswesww
        weewwwsewwnewsewnewnwwwww
        nweswsenwswneseewnenwswwewnwnwnwnwenw
        eeewnweeneseswsweswseenenweeswe
        swswswwwswnenwsweswseswswnewneswswsw
        swneswswseswsenwseswnewsesenesenwswsesw
        nesewnesweswnenew
        nenwsesenenenwweenesweseswneneneew
        eeswnweneswseesenwwese
        esewwseswnwneeseswnwswnwesenwnwnwnww
        nwnwwnwwwwwnwewnwnw
        nwnwwesweenenenwnweswnwwnwenesweswne
        nwwswesewnwwenwswewneseseeesesenw
        swwseenwnwsewsewseneneww
        neswneswenwwneneneenwnwnenesewne
        neeneenweweeneneseseswnwsweeee
        wswnwswwswswnenwswswwwswesewswnese
        nwwnwwsewnweswsesesweenenwswnwnee
        sweeneneneneneneswnweneneswnesenwwnee
        eeswnwsweenenesenenwwnwneswseneese
        swwnwnwswseeseswswseseseswneneswsewsesene
        swnwwseneswnenenenewsenewneneneenenesw
        sewwseseseseseneewseeseenwseenenesee
        eeenwwweneneeeeswnesweeeenwne
        seseseeeneseseseswe
        nwnenwsewnwsenwnwnwnwwnw
        nwswnenwswneseneswnwnwesenwnenewnenene
        swseswnenwseeseswsewswswsewseneseswnwse
        wswwswnwnewseweneswenwswswswswnese
        wsenwnwnwnwnweewnwnwnwnwseswe
        eenewneseeeneeeseenwnenewneneeswe
        neswnwnwswnwswenenwnewnenenenenenwenene
        sewsewsesenwesesweseswswsewseneew
        swwswseswneeweswswseswnwwewnwswwne
        eenesweeeeenenwnwseswsesewnwsesw
        wwwnwsewnwwnwsenwnwwewwwnewenwe
        nwwnwneneswnwnwnwnwnwwswnenwwnwsenesesenw
        swseewseseseseseneseswsesesenese
        newnwsenwnwswswenwnenwnwsewswnwnwnwwnw
        eeneeweeneeeseeewenweswesw
        wesewwswnweseneseneweswwwwneww
        wnenesenwnwneseneenwese
        nenenenwnewswnenwnwswneseswswneswnwseesw
        swswseswswnwseswswswswnesese
        seswswnwseseseswswseseswseswsw
        neswsesewnwswwnweneseseswenwnwswe
        nwsesweswnesewnwswsenenenwswneseswswswsw
        swnwneneneswswnenenwwsenwnwnwse
        swswseseseneswneswsesesenwseneneswwsese
        nenwenenwnwsenwneewnwswenenwwnenew
        eewnwseeneeeeweesewseneswee
        seswseswsenwseseesenwsewseeseseseee
        senwwnwnwnwnenwnwwwnww
        wneeswnwswseswswnwwewsw
        nwsweneswneeeneenesenwneneeswnw
        esweenwnenenwweneneneseneeneneeesw
        seeewseeseseeseenwwseesesweese
        swswsewswseeswnwnweswwswnenwswswswwsw
        swnwneneneeneenenenewenenene
        nwsenwnwwsenwsenenenwenwwneneswseneew
        enwneswswnesenewswwe
        sesesenwnwswsesenewsesenwenwseseseseswe
        nwseneswneswnwneneeneeswnenenwnenwese
        seenesesenwnwwswnewswwwnwwwneswne
        swseswnwnwswswswswswswseswswswswneswenwsw
        senwnweenwesweseeeeeeeeewese
        swwseneswwswwnewwneeseswenesewsene
        eseneweenenenenenwneenenwneneswneneswne
        nwnesenwseeeneeesewnwweswweesw
        neseseseseseewwsesee
        swnwseswswswneeswswswswswswwswnwneswsw
        wnwwnwsenenesesewnenwswneesw
    """.trimIndent()
}