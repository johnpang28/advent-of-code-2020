package me.jp.aoc

import me.jp.aoc.Day22.Game
import me.jp.aoc.Day22.input
import me.jp.aoc.Day22.parseDecks
import me.jp.aoc.Day22.play
import me.jp.aoc.Day22.score

fun main() {
    val decks = input.parseDecks()

    val winningDeck1 = decks.play().toList().first { it.isNotEmpty() }
    val answer1 = winningDeck1.score()
    println(answer1) // 35370

    val game = Game(decks).play()
    val winningDeck2 = if (game.instantP1Win) game.decks.toList()[0] else game.decks.toList().first { it.isNotEmpty() }
    val answer2 = winningDeck2.score()
    println(answer2) // 36246
}

typealias Deck = List<Int>

object Day22 {

    data class Game(val decks: Pair<Deck, Deck>, val previous: Set<Pair<Deck, Deck>> = emptySet(), val instantP1Win: Boolean = false)

    fun String.parseDecks(): Pair<Deck, Deck> {
        val (d1, d2) = split("\n\n").map { it.lines().drop(1).map { it.toInt() } }
        return d1 to d2
    }

    fun Pair<Deck, Deck>.play(): Pair<Deck, Deck> {

        tailrec fun go(acc: Pair<Deck, Deck>): Pair<Deck, Deck> =
            if (acc.toList().any { it.isEmpty() }) acc
            else {
                val (deck1, deck2) = acc
                val card1 = deck1.first()
                val card2 = deck2.first()
                if (card1 > card2) go(deck1.drop(1) + listOf(card1, card2) to deck2.drop(1))
                else go(deck1.drop(1) to deck2.drop(1) + listOf(card2, card1))
            }

        return go(this)
    }

    fun Game.play(): Game {

        tailrec fun go(acc: Game): Game = when {
            acc.instantP1Win -> acc
            acc.decks.toList().any { it.isEmpty() } -> acc
            else -> {
                val (deck1, deck2) = acc.decks
                val card1 = deck1.first()
                val card2 = deck2.first()

                if (acc.previous.contains(acc.decks)) acc.copy(instantP1Win = true)
                else if (card1 < deck1.size && card2 < deck2.size) {
                    val subGame = Game(deck1.drop(1).take(card1) to deck2.drop(1).take(card2)).play()
                    if (subGame.instantP1Win || subGame.decks.first.isNotEmpty())
                        go(acc.copy(decks = deck1.drop(1) + listOf(card1, card2) to deck2.drop(1), previous = acc.previous + acc.decks))
                    else
                        go(acc.copy(decks = deck1.drop(1) to deck2.drop(1) + listOf(card2, card1), previous = acc.previous + acc.decks))
                } else if (card1 > card2)
                    go(acc.copy(decks = deck1.drop(1) + listOf(card1, card2) to deck2.drop(1), previous = acc.previous + acc.decks))
                else
                    go(acc.copy(decks = deck1.drop(1) to deck2.drop(1) + listOf(card2, card1), previous = acc.previous + acc.decks))
            }
        }

        return go(this)
    }

    fun Deck.score(): Int = this.reversed().foldIndexed(0) { i, acc, n -> acc + n * (i + 1) }

    val input = """
        Player 1:
        25
        37
        35
        16
        9
        26
        17
        5
        47
        32
        11
        43
        40
        15
        7
        19
        36
        20
        50
        3
        21
        34
        44
        18
        22

        Player 2:
        12
        1
        27
        41
        4
        39
        13
        29
        38
        2
        33
        28
        10
        6
        24
        31
        42
        8
        23
        45
        46
        48
        49
        30
        14
    """.trimIndent()
}