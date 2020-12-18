package me.jp.aoc

import me.jp.aoc.Day18.Add
import me.jp.aoc.Day18.Multiply
import me.jp.aoc.Day18.Operand
import me.jp.aoc.Day18.evaluate
import me.jp.aoc.Day18.toRpn
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Day18Test {

    @Test
    fun `should get RPN expression for part 1`() {
        val add = Add(precedence = 0)
        val multiply = Multiply(precedence = 0)
        val operators = mapOf('+' to add, '*' to multiply)
        assertThat("5 + 6".toRpn(operators)).isEqualTo(listOf(Operand(5), Operand(6), add))
        assertThat("1 + (2 * 3) + (4 * (5 + 6))".toRpn(operators)).isEqualTo(listOf(Operand(1), Operand(2), Operand(3), multiply, add, Operand(4), Operand(5), Operand(6), add, multiply, add))
    }

    @Test
    fun `should get RPN expression for part 2`() {
        val add = Add(precedence = 1)
        val multiply = Multiply(precedence = 0)
        val operators = mapOf('+' to add, '*' to multiply)
        assertThat("5 + 6 * 9 + 5 + 5 + 4".toRpn(operators)).isEqualTo(listOf(Operand(5), Operand(6), add, Operand(9), Operand(5), add, Operand(5), add, Operand(4), add, multiply))
    }

    @Test
    fun `should evaluate RPN`() {
        val add = Add(precedence = 0)
        val multiply = Multiply(precedence = 0)
        assertThat(listOf(Operand(5), Operand(6), add).evaluate()).isEqualTo(11L)
        assertThat(listOf(Operand(5), Operand(6), add, Operand(9), multiply, Operand(5), add, Operand(5), add, Operand(4), add).evaluate()).isEqualTo(113L)
        assertThat(listOf(Operand(1), Operand(2), Operand(3), multiply, add, Operand(4), Operand(5), Operand(6), add, multiply, add).evaluate()).isEqualTo(51L)
    }
}