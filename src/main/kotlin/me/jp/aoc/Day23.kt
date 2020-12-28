package me.jp.aoc

import me.jp.aoc.Day23.Circle
import me.jp.aoc.Day23.fillTo
import java.util.*

fun main() {
    val input = "952438716"

    input.map { it.toString().toInt() }.let { cups ->
        val circle = Circle(cups).apply { repeat(100) { move() } }
        val answer1 = circle.cups(1).drop(1).joinToString("")
        println(answer1) // 97342568
    }

    input.map { it.toString().toInt() }.fillTo(1_000_000).let { cups ->
        val circle = Circle(cups).apply { repeat(10_000_000) { move() } }
        val answer2 = circle.nextCupAfter(1) * circle.nextCupAfter(circle.nextCupAfter(1)).toLong()
        println(answer2) // 902208073192
    }
}

object Day23 {

    data class Node(val cup: Int, var next: Node? = null)

    class Circle(cups: List<Int>) {

        private val nodeMap = mutableMapOf<Int, Node>()
        private val min: Int
        private val max: Int
        private var current: Node

        init {
            cups.map { Node(it) }.let { nodes ->
                nodes.zipWithNext().forEach { (n1, n2) -> n1.next = n2 }
                nodes.forEach { nodeMap[it.cup] = it }
                nodes.first().let {
                    current = it
                    nodes.last().next = it
                }
            }

            cups.sorted().let {
                min = it.first()
                max = it.last()
            }
        }

        fun cups(from: Int): List<Int> {
            val cups = LinkedList<Int>()
            val startCup = nodeMap.getValue(from)
            var n = startCup
            do {
                cups.add(n.cup)
                n = n.next!!
            } while (n.cup != startCup.cup)
            return cups
        }

        fun move() {
            val pickUp = pickUp3(current.cup)
            var destination = current.cup
            do {
                destination = if (destination <= min) max else destination - 1
            } while (pickUp.contains(destination))
            insert(destination, pickUp)
            current = current.next!!
        }

        fun nextCupAfter(cup: Int): Int = nodeMap.getValue(cup).next!!.cup

        private fun pickUp3(after: Int): List<Int> {
            val n0 = nodeMap.getValue(after)
            val n1 = n0.next!!
            val n2 = n1.next!!
            val n3 = n2.next!!
            n0.next = n3.next
            n1.next = null
            n2.next = null
            n3.next = null
            return listOf(n1, n2, n3).map { it.cup }
        }

        private fun insert(after: Int, cups: List<Int>) {
            val n0 = nodeMap.getValue(after)
            val ns = cups.map { Node(it) }
            ns.zipWithNext().forEach { (n1, n2) -> n1.next = n2 }
            ns.forEach { nodeMap[it.cup] = it }
            ns.last().next = n0.next
            n0.next = ns.first()
        }
    }

    fun List<Int>.fillTo(n: Int): List<Int> {
        val max = sorted().last()
        return this + (max + 1..n).toList()
    }
}