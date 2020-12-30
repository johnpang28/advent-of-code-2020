package me.jp.aoc

import me.jp.aoc.Day25.findLoopSize
import me.jp.aoc.Day25.loop
import me.jp.aoc.Day25.publicKey1
import me.jp.aoc.Day25.publicKey2
import me.jp.aoc.Day25.subject

fun main() {
    val loopSize = findLoopSize(subject, publicKey2)
    val encryptionKey = loop(publicKey1, loopSize)
    println(encryptionKey) // 18329280
}

object Day25 {
    const val subject = 7L
    const val publicKey1 = 12092626L
    const val publicKey2 = 4707356L

    fun findLoopSize(subject: Long, publicKey: Long): Int =
        generateSequence(subject) { (it * subject) % 20201227 }.takeWhile { it != publicKey }.toList().size + 1

    fun loop(subject: Long, iterations: Int): Long {
        fun go(acc: Long): Long = (acc * subject) % 20201227
        return (1..iterations).fold(1L) { acc, _ -> go(acc) }
    }
}