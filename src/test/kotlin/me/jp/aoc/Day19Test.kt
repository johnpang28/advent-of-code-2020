package me.jp.aoc

import me.jp.aoc.Day19.parseRules
import me.jp.aoc.Day19.reduce
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Day19Test {

    @Test
    fun `should get regex 1`() {
        val rules = """
            0: 1 2
            1: "a"
            2: 1 3 | 3 1
            3: "b"
        """.trimIndent().parseRules()

        val ruleValue = reduce(rules).getValue(0).value
        assertThat(ruleValue).isEqualTo("a((ab)|(ba))")

        val ruleRegex = ruleValue.toRegex()
        assertThat(ruleRegex.matchEntire("aab")).isNotNull
        assertThat(ruleRegex.matchEntire("aba")).isNotNull
        assertThat(ruleRegex.matchEntire("ba")).isNull()
        assertThat(ruleRegex.matchEntire("aaa")).isNull()
    }

    @Test
    fun `should get regex 2`() {
        val rules = """
            0: 4 1 5
            1: 2 3 | 3 2
            2: 4 4 | 5 5
            3: 4 5 | 5 4
            4: "a"
            5: "b"
        """.trimIndent().parseRules()

        val ruleValue = reduce(rules).getValue(0).value
        assertThat(ruleValue).isEqualTo("a((((aa)|(bb))((ab)|(ba)))|(((ab)|(ba))((aa)|(bb))))b")

        val ruleRegex = ruleValue.toRegex()
        assertThat(ruleRegex.matchEntire("ababbb")).isNotNull
        assertThat(ruleRegex.matchEntire("abbbab")).isNotNull
        assertThat(ruleRegex.matchEntire("bababa")).isNull()
        assertThat(ruleRegex.matchEntire("aaabbb")).isNull()
        assertThat(ruleRegex.matchEntire("aaaabbb")).isNull()
    }
}