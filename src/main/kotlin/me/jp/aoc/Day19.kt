package me.jp.aoc

import me.jp.aoc.Day19.Rule
import me.jp.aoc.Day19.input
import me.jp.aoc.Day19.parseRules
import me.jp.aoc.Day19.reduce

fun main() {
    val (rules, messages) = input.split("\n\n").let { (rulesPart, messagesPart) ->
        rulesPart.parseRules() to messagesPart.lines()
    }

    val validMessagesRegex = reduce(rules).getValue(0).value.toRegex()
    val answer1 = messages.filter { validMessagesRegex.matchEntire(it) != null }.size
    println(answer1) // 248

    val rules2 = rules + listOf(
        8 to Rule(value = reduce(rules).getValue(42).value + "+"),
        // find a nicer way!?
        11 to Rule(
            subRules = listOf(
                listOf(42, 31),
                listOf(42, 42, 31, 31),
                listOf(42, 42, 42, 31, 31, 31),
                listOf(42, 42, 42, 42, 31, 31, 31, 31)
            )
        )
    )

    val validMessagesRegex2 = reduce(rules2).getValue(0).value.toRegex()
    val answer2 = messages.filter { validMessagesRegex2.matchEntire(it) != null }.size
    println(answer2) // 381
}

object Day19 {

    data class Rule(val value: String = "", val subRules: List<List<Int>> = emptyList())

    fun reduce(rules: Map<Int, Rule>): Map<Int, Rule> {

        fun go(acc: Pair<Map<Int, Rule>, Map<Int, Rule>>): Pair<Map<Int, Rule>, Map<Int, Rule>> {
            val (resolved, unresolved ) = acc
            val resolvable = unresolved.entries.filter { (_, v) -> resolved.keys.containsAll(v.subRules.flatten()) }
            return if (resolvable.isEmpty()) acc
            else {
                val newResolved = resolvable.map { (k, v) ->
                    val subRuleParts = v.subRules.map { subRule ->
                        subRule.fold("") { acc, n -> "$acc${resolved.getValue(n).value}" }
                    }
                    val ruleValue = if (subRuleParts.size > 1) "(${subRuleParts.joinToString("|") { "($it)" }})" else subRuleParts.first()
                    k to Rule(value = ruleValue)
                }.toMap()
                go((resolved + newResolved) to (unresolved - resolvable.map { it.key }))
            }
        }

        val resolved = rules.filter { (_, v) -> v.value.isNotEmpty() }
        return go(resolved to rules - resolved.keys).first
    }

    fun String.parseRules(): Map<Int, Rule> {
        return lines().map { line ->
            val (ruleNumber, ruleBody) = line.split(":")
            ruleNumber.toInt() to ruleBody.trim().let {
                if (it.contains("\"")) Rule(it.replace("\"", ""))
                else Rule(subRules = it.split("|").map { it.trim().split(" ").map { it.toInt() } })
            }
        }.toMap()
    }

    val input = """
        51: 52 129 | 86 30
        82: 52 97 | 86 16
        72: 75 52 | 85 86
        118: 28 86 | 62 52
        104: 23 52 | 9 86
        36: 52 23 | 86 62
        34: 86 113
        39: 52 56
        58: 116 86 | 28 52
        79: 9 86 | 54 52
        33: 28 115
        105: 75 52 | 83 86
        30: 27 52 | 46 86
        107: 51 52 | 112 86
        10: 86 102 | 52 36
        112: 86 87 | 52 103
        24: 49 86 | 37 52
        135: 15 86 | 28 52
        84: 3 52 | 132 86
        15: 52 52 | 86 52
        95: 86 18 | 52 94
        48: 40 86 | 63 52
        116: 115 115
        28: 86 52 | 52 86
        61: 22 52 | 41 86
        26: 116 86 | 75 52
        49: 78 52 | 65 86
        20: 86 55 | 52 34
        41: 86 116 | 52 15
        87: 39 86 | 47 52
        100: 23 52 | 116 86
        129: 86 58 | 52 81
        4: 86 86 | 86 52
        8: 42
        121: 116 86 | 9 52
        119: 86 56 | 52 4
        133: 52 73 | 86 44
        35: 52 96 | 86 116
        32: 86 92 | 52 1
        113: 86 86
        97: 83 86 | 113 52
        89: 96 52 | 56 86
        3: 86 5 | 52 118
        2: 86 122 | 52 80
        120: 23 52 | 96 86
        56: 52 86 | 86 86
        52: "b"
        19: 86 83 | 52 15
        22: 52 116 | 86 4
        71: 52 85 | 86 54
        74: 86 56
        130: 52 75 | 86 23
        80: 52 72 | 86 7
        40: 98 86 | 38 52
        50: 52 124 | 86 60
        108: 52 75 | 86 85
        64: 125 86 | 26 52
        78: 86 56 | 52 28
        43: 52 81 | 86 46
        85: 52 52 | 52 86
        128: 90 52 | 121 86
        110: 85 86 | 68 52
        14: 52 117 | 86 69
        101: 4 52 | 113 86
        117: 52 50 | 86 99
        45: 52 95 | 86 114
        29: 52 116 | 86 56
        73: 86 110 | 52 17
        102: 52 62 | 86 116
        134: 52 127 | 86 77
        131: 86 54 | 52 4
        123: 61 86 | 10 52
        54: 86 115 | 52 52
        109: 52 76 | 86 14
        44: 47 86 | 36 52
        124: 52 56 | 86 113
        38: 52 130 | 86 105
        21: 28 86 | 116 52
        65: 86 62 | 52 9
        37: 35 52 | 81 86
        115: 86 | 52
        103: 86 22 | 52 111
        53: 86 6 | 52 134
        59: 52 91 | 86 19
        88: 86 84 | 52 12
        12: 86 43 | 52 32
        6: 86 82 | 52 59
        125: 86 62 | 52 75
        27: 28 52 | 23 86
        96: 52 86
        47: 62 52 | 62 86
        9: 52 115 | 86 52
        0: 8 11
        63: 128 52 | 20 86
        68: 86 115 | 52 86
        13: 86 4 | 52 75
        18: 110 86 | 104 52
        126: 88 86 | 107 52
        111: 115 4
        122: 29 52 | 71 86
        16: 56 52 | 75 86
        91: 23 86 | 23 52
        69: 67 86 | 64 52
        70: 53 86 | 48 52
        7: 52 113 | 86 15
        127: 135 52 | 131 86
        57: 17 86 | 34 52
        86: "a"
        106: 52 62 | 86 56
        11: 42 31
        17: 52 56 | 86 23
        98: 52 13 | 86 119
        92: 86 15 | 52 23
        94: 33 86 | 100 52
        55: 86 54 | 52 9
        132: 101 52 | 108 86
        75: 86 86 | 52 52
        5: 52 83 | 86 113
        60: 116 86 | 96 52
        23: 52 52
        77: 86 130 | 52 74
        99: 124 52 | 89 86
        83: 115 86 | 52 52
        90: 62 52 | 9 86
        42: 109 86 | 66 52
        114: 52 57 | 86 93
        25: 52 133 | 86 123
        62: 86 52
        93: 52 79 | 86 120
        76: 86 24 | 52 2
        66: 86 25 | 52 45
        46: 9 115
        67: 86 106 | 52 21
        31: 52 70 | 86 126
        81: 85 86 | 15 52
        1: 116 52 | 62 86

        bbbababaabaabaabababbbabaababababbabbbbabbabaabababbbaabbbbaaababbbbbaabbaaaaaab
        ababbbbbaaabbbababaabaababaabaaabbbaaabaaaaaabbbabbbaabababbabab
        aaabbabbababaaababbaaabaaabbaaabbababbbb
        aaaababbbbbbaabbbbabaabaaaaaabbaabbbbbabaaabbbaabaabbbaaabaabbbbaaabaaaa
        aaababaabababbabababbaaa
        abababaaabbabbaaaaaabaaaaaabaaba
        bbabbbbbaaaabbbbaabbabbaabbbbbabaababbbababaaaab
        babaaabaaabaababbbaaaabb
        bbaababbbbabababbbabaabbbaaaaaabbabbabaaaaaaaabababbbaab
        ababbbbbaaaabaabaaaabbbabaabbaabbaaababbbaaaababaaababbababaabbbabaaabbabaaaaabbaaabbaaa
        baaabaabaaaaabbbbaaaaaba
        bbbbbbbbbabbbabaaaabbabb
        bbbbbbaabbbbbabbbabbbbab
        aaaaabbbbbbbbbaaaababbaaabaaaaaabbabaaaa
        aabbbaababbaaaabbabababaaaabbaaa
        bbabababbbbababbbaaababb
        babbbbbbbbbbaabaabbaabbbbaaabbabbaabbbbbabababbb
        aabbaaaaaaaaabbaababaaab
        babbabbaabbbbbabbaaabbaa
        aaabaaabaabaabbaaabababa
        abaabaabaaababbbbbbbbbbabbbaabbbabaaabba
        bbbbaabbbbabbbbbabbbbaba
        abaabbabbaaaabbaabaabbbb
        aaaabbbaabababaaaaabaaaa
        aaaaabaababaaaaabbababbaabaababaabbabaabababbbaabbbaabbbaaaabaaabbbaaaaaaabbbaaa
        aabbabbaaababaaababaaaaa
        ababbbaabbbbabbbaabaaaabaababbbaaaabbbaababbbbba
        abbbbbabbbaaaabaaaabbbaa
        bbbbaabaabbababaabababab
        baabaabbbbbbbbbbbbababaa
        bbabbbbaaaaaaaaaaabaabaaababbaaa
        abbaabbbabaaaaaabbabbababbbbaaababaaaaba
        abbbbbbababaaababbbbaaabaabbaabaabbaabbaaabbaaabbaaaabbb
        abbaaaaaabbaabbbaabaaaab
        abaababbbbbbbbaabaaaaaabaabbaaaabbbbbbbb
        aabbbbbbbaabbbababbbabab
        bbbbaabaaabaabbbaaabbabb
        aaaabbbbbbabaabaaabaaababbabbbaababaababbabbabbbbaaabbab
        babbabbabbaaaaaaababaaaabaaaabaabababbbabababbbb
        bbabaabbaabbaaaabababaab
        baaabaaabbabbbbbabbabaabbabbbbba
        aabaababbabbaabababaaabbaaabaabbbabbbbba
        aababaaaaaaabababbababaa
        abbaaaaabbbbbabbaabaaabb
        baabbbabbaaabbbbbaaaaaaa
        bbbbaabbbbabbababbbabbba
        aaaababaaabbbbbabbabbbbbbabbbabaabaababb
        abbababaabbaaaaabbaabbabbabababb
        babaaabbaabaabbbabbabbabbaaaabaabbbbaababbbbabbbaabaaaaabaabaaba
        bbaabababbaaabaaabbbabba
        bbbaaabbbabbaaaaaabbabaabbaabbbbaaabbbab
        baabbbababbaabbbbbbabbbb
        aabbbbaaabbaabaaabaabaaa
        aabbabbbaababaababaabaaabbabbaabbabaabbaaaababbbaabbaaaababababbbabbabbbaabbaabb
        bbaaabababbabbabbbbabbba
        bbbabaaaaaaababbaaabbbaa
        aababaababbabaababbabbba
        abbbbaaaaaabaaabaabbbaabbbabbbbbabaaabab
        baabbaababbbbbabbaababbaababbbba
        aabbbbbbbbbaaabaabbbaaab
        bbbbbbbabaababaaabaaabab
        baabbabbaaabababababbaab
        ababaabaaaaababbabbaabba
        aabbabbababaaabbbbbaaaaa
        abaaabbbbbaabbbbbabbbbababbaabba
        bbabbabbbbaaaababaaaabaaabaababbbaabbabb
        aababaabbabbabbaaabaaabababbaabb
        baaabbbbaaaabbbbabbaabba
        abaababababbbbabbaababababababaababbbbaaabbabbbaabbbbbbbbbbbaaba
        bbabbabbbabbaababbabbbbbabbaabaabbbbabaa
        bbaaaababbbbaabaaaababab
        aaaabbabaaaaabbbaabaabbaababaaab
        aaababbbaaaaaabbaabbabbb
        babbaaababaaaabaababbbbbbabbbaaaaaabaabaabaabbba
        babbabaaabaabaabbabbaabaabbaabbbbbabbbabbabbabbbbbbaabab
        bbbabbabbbbaabbaabbaaaab
        babbaaabbaabbbbbabbaabaababbbabb
        abbaabaaaababaaabbbababbabbbaaabababaabb
        aababaabbabbbaabbbbbaabaaaababababbaaaba
        baabbbbaaababbabaabbbbaaaabbabaaabbbaaab
        baabababbababbabaaaaaaba
        aabbaaaaabbababaaaaaaaba
        aabbabaaababaaaaaabbaaaabbbaaabbbababbbb
        baabababaabaaaabbaabaaaababbbabb
        babbaabaaabaaabaabaabbba
        aaaaaaababaabbabbabbbbab
        aabaabbbaaabbbbabababbba
        bbbbaaabbbaaabaaaabbbabb
        abbbbaaabaabbbbaaabaabaababbaabababbbaaaabababba
        abababbbabbabbbabaabbababbbaaaabbbaaaaabbbabbbbbbbabaabbaabbabbaaababbab
        bbabbbaaabbabaaabbaaabba
        bababbabababaabaaabbaaba
        baababbaaabbbabaaabaabba
        aaaaaaabbbabaabbbbbbbaaaabbbabbaaaabbbbb
        bbbabaaaabbabbabbabbabaabbabbbab
        bbbbbbbabaabbabbaabbbaab
        abbbbaaabbbababbbaababbaaaaabaabaaaaaaaaabbabbba
        abbaaabbbbabbaabababbbbbbabaabba
        aababbaabbaabbaabababbbb
        bbaaabaaaabbbbabbbbaabaa
        aabbabaaabbaabaababaabaa
        abbbabbbaaaabbaababaabba
        bbbababbbbabbabbabaaaababaaabaaaaaaabaaababaabbb
        aaaabbbbbbabbabaabbaabbbbbbaabbb
        bbaaaababaababbbbaabaaaa
        abbaabbbbbaabbaaabbaabab
        abbbbbabbbbaaabbaaaaabbbabbaabaaaabbbaaa
        abbaabbbaabbbababbaaaabb
        baababaaaaaaaaabbbbbbaba
        baabbbbababbbaabbbabbbab
        babbabaabbabbbbaabaaaabaabbbbaabbaabbaaaaaabbaba
        baaababaaaabbbbaaabbaababbaaabab
        aabbaabbbbaaaaaabaabbaaaabbbaaaaabbbbaaa
        aabaababaabbbaabbbbaaababbbbabaa
        abbababbaaaabbbaabbbbbbb
        abbabaaaaaaabaaaabaabbaa
        baabbababbabaabaaaabbbab
        abababbabbaabbbaababbbaaaaababbbbbabbbbbbaaabbbbaaabbaaabaaaabaa
        aaababaabbabbaaabbbbbaab
        bbbabbabaababbaababbbaabbabaabbb
        bababaaaaaaaabbabbabababaaaabaaaaabaaabbbbbabbaababbabab
        aababaaababbbaabaabaaabababaabaababbabbbbabaaaaabaaaaabbbaaaaaba
        aabbbbaabbaaaabababbaaabbaaaaabb
        abbababaabbaaabbbabbabaa
        aabbbbbabbbbbbbbaabbabbabbbbaaaabbbaabab
        aabbbbbbbbbbaabababbbbbabbbaababbababaab
        abaaabbaabaabbababbbbaabaaaabbabbaabbbaababaaabbbabaabba
        bbaaaabaaababbaaababbababbbaabba
        bbbbaababbaabbaabaabbabb
        abaabaaabbbabaabbbabbbbbbbbaababbaaaaaabbaabbaababaabbba
        bbabaabbababaabababaabab
        aabbbbbbabbabbaaabbbabaa
        bbabbaabaaaabbbaaaaaaaaabbbbabbabaabaaab
        abbabbabbababaaabaaaabab
        bbaaabaaabbababaaabbbaaa
        bbabaabaabbbbbaabaaaaabb
        aabbabbaabbbabbbaaabbabb
        bababaaababbaaabbbbbbbbaababbaab
        babbabbbbaaabbbbabbbabaabbbabbbaababbbabbaababbaaaabaabbaaababababaaabaaaaabbaaaabaaaaaa
        bbbbababbbabaabbaabbbaabaaababbbababaaaababbbbaaabbbbabb
        bbabaaabaababbbaabbbbbbabaaaabbbabbabaabaaababbbbabbbaaabbbabbba
        aaaabbbbaababaabbabaaabaaabaabbaaababbbb
        aabaabbbbababaaabbabbbba
        bababbaabbbbbbbabbbababa
        abbaabbbbbabbababbbbababaababbbb
        aaaaabbbabbbbaaababbbaaaabbbbaba
        aabaababbabaaabbbbaaabaaaaaaabbabaaaaababbbbaaaaaaabbbab
        bbaababaabbaabbbabaaaabb
        abbbbbabaababaabbaabbbaa
        aaaabbabbbabbaabbbbababbbaabbbba
        bbaababbbabbaaaabbaabaaa
        babbaababaaaabaaaaaaabaa
        abbaaaaabababbaaabbaaaba
        babbababbaaaabbbbabababbaabbbaaabbbabbbaabababba
        aaaabbabaababaabbabbabbaaaabaaaaaaabbbbb
        aabbbbabaaabbaabbbaabbbb
        aababaabaaaabbbaabaaaaab
        bbbbaaabaabbabbaabbaabbabaabbabb
        ababaaaabbaaabaaabbbbaba
        abbabababbabaaabbabaaaab
        babbbaabbaababbaaababbabbbaababbabbabbabababaababaaababbabbaaaab
        bbbbbaaabbbbbbbaabbbbbbb
        bbbababbaaaaaaabbbbbbaba
        bbaaaababbbabaaabaabbaaa
        bbabbbbaabaabbaaabbaabbababababb
        abbaaaaabaaabbabbabaabbaaabbaabb
        aaababaabaaaabbaaabbaaba
        aaaaaaabbaabbbbbbbbbabba
        aabbaabbbbaaaabbabbbbbbbabbababb
        bbabaabbaabaaaababababbababbbbbababbabababbaaabaabababab
        aaabaaabbbbaaababbbbaaaa
        babbaabaabbbabbbabbaaaab
        abbbbbaabbabababbbbbabaa
        bbaabbaabaabbbabaabababa
        aabaabbbaabaababbabbabbb
        aabbbabaaabbbbbbabaaaabaaabaaaaa
        aabbbbabaabbbbaaaaabaabb
        aaabbbbaaabbaabaabaaaabb
        aaaababbbaababbbbbbbabba
        aaaaababbaabbbababbaaaba
        baabababaabbababbbaaabba
        abbaabbbabbabbaaabbaabab
        bbabaababbbaaaaaabaaaababaaabbaababbbaaaababaaaabaabbaaaababbabb
        aaababbbbbabbaaabbbbbbbbbabbaaaa
        bbabbbaaaabbbbaaaaaaabbbabbabbabbbbaabaa
        aabbaabaaaabbaaabbbaabbbbabaaaababbbbbaaabaababbbaaababbaaaaabba
        bbabbabaabbbbaaaabababab
        aaaaabbabbbbbbbaaababbabbbbbabaa
        ababaababaabbbababbbbaababbbabababbbbbbb
        baabbbbaabbabaaaaaaaaaaaabaaaaaabaaabaaabaaaababaaabababababbbba
        aabaababbbaabbbaaaaabbaabbabbabaabaabbaaabbbaaaaabaabaabababaaaa
        bbaaaaaabbabaaababbaaaba
        baaabaabbbbababbbaabaaaa
        bbbaababbbbabaaaaaabbbbbbabbaaabaaaaabba
        ababbabaabbbabbbaabbbbabbaaaaaaa
        baaabbbbbbbababbabaaaabb
        bbabbabbabbbbaaaaaaabaaabaaaaaabbbbababa
        babbaaabaababaabbabbbaaabaaabaaaaabaaaababaaabba
        abbbabbbbaaaabbabbbababa
        aabbabababbabbaababbabab
        abaabbabaabbbbbbabbbabbbaaaabbbbaaaabbababaabbaa
        aabbbabbabababbbaabaababbabbbaabbababbba
        baababaaaababaaaabbbabab
        bbbaaabaaaaabbbbabaabbaa
        baaabbbabababbabbbbabbba
        baaababaabaaabaabbabbabbabbabababaabbbababbabaaabbaaaaabbaabbbbaaaaaababbaaabbbb
        baabbababbabbbbbbbbbabbb
        bbaaababbaaabbbbabaabbaa
        baaabaababaaaabaaababbababbababaaaababbbbaabaaba
        baaabaaabababbabaabababa
        bababbabaaabaaabaabbbbbabaaaaaaaabbaabab
        aabbabbabbabbabaabbbbaba
        aababbaabababaaabbbbaaaa
        bbbbabababaaaabaabbaabab
        bbbaabbabbaabbabaaabbbbbaaabbaaa
        aabaabaaababbaaabbaaabbbabbaaabaaaabbbabababbbaa
        aabaababbabbabbbbbbaabaaabaaabbaabbaaaab
        bababbaaaaaabaaaaaabaaba
        bbbbbabbabbbbaaababbabab
        abababaaaaaaabbaaaaaabaa
        babbaaabbabbaaaaababbaaaaaaaaaabbbbbbbaababbbbbabaaaaabbababbababbaabaab
        baababbbabbababaaabbababaabaaabaabbaaababbbabbbbaababbbb
        bbbbbaaaaaaaabbbaaaabbbabbaabbababaaaaab
        aabbbbaabababbabbaaaabaabaaabbaabbaabaababaababa
        aaaabbbbabababaaabababababbaababaabaabababbbaabbaaababbaabababaa
        aababaaababbbaabaaabbaba
        aaaaaaaabaaaaababbbaabaaabababab
        abaabbabaabbbbaababaaaab
        abababaabbaaaabaaabaabbbbaabbaaa
        abbaabbbaabaababaaaaaabbbabababb
        abbabababbabaabbbbbaabaa
        bbbbbabbabbababbbbaabbbb
        abbbbaabaabaabbbbaaaaaaabbababbb
        abaabbabababbbbbabbabbaababaaaba
        aaaaababaabaabbaabbabbaaabaabbbb
        babbaaabbababbabababbbaa
        abbabaabbbabaabaabbbbbba
        bbabaabbaababbbbbabbabababbbbbababbabbbb
        aabbabbaabaaaaaaabababbb
        abaaaaaaabbbabbbbbaabbbb
        abaabaabbbbbbaaabbaaababaabaabbababaaabbaaabaaabaabababa
        bbbbbabbbaabbabaabaaabba
        baababaaaaaababbabbbabba
        bbabaabbbabbabaabbaaabba
        aababaabbababbabaababbabbbbaabbaaabbbbbaaababbbbabababab
        babbabaabaababbbbaaababb
        aaaabbbbbababbaaaaaaabaa
        bbabbabaaabbabaaabaaaabb
        bbaabbaabbbababbaababbbb
        bbabbbaabbbbabaabbabbbabaabbaabbaabbaabb
        aaaabbbabababaaababaabbb
        bbbaaababbbbbabbbbbabaabaababaabbbbbabbb
        bbaaabaabbaaababbbbbaabbaaabbbaababaabba
        babbabaabbaabbabbbbbababbbaaabbb
        ababbbbbaaaaabababaabbbb
        aababbabaabbbbbbabbbbabb
        aaaabbaaaaabbaabbbbbaabaaabbbbbababaaabb
        aabbababaaaabbbabaabaaba
        aaaaaaababbababbbaabaaba
        bbbbbbbaabbaabaaaaabaaba
        babbabaaaababaaabbaaabbb
        bbabbbbaabbbbbabbbaaabbb
        aabbaaaababbabbaaaabbbaa
        aabbabaaabababaababbabab
        aabaabbbaaaaaabbaabbaaab
        bbaabbabbabbabbabbaabbbb
        bbbabaaabbbbbabbbaabbaaa
        abbabbaaabbaaaaabbabbbbabaaaaaaa
        abaaaaaaabbbabbbbbbbabbb
        bbbabaaaabbabaabbaaabbbbbbaababbbbabaabb
        bbbabaabaaabaabaabbbababaabbaabbababaabbabbbbabb
        baabbbbbbabbabaabbbababbbaaaabaa
        aaaaabbabbaababbbabbbbbb
        ababbbaaaaabbababbbbbaaaaaaabbabaababaab
        aaaaabbaaababaaaabbbabab
        bbabbbbaabbbbaabbbbbbaab
        baababababaaaabababbabbb
        aabaaabababbabaaabaabbbb
        aabbbabaabbabbaaaabbbabb
        baaabbbbbbbababbaaaaaabbaabbaaab
        baabbbbaabbaabbbbbaaaabb
        aaaabbaabaabbbabbaabbbaabaaaaaaabbaaabbabbababbabbbaabbabbabbbab
        ababaaaaababbbbbaaaaaaba
        babaaababbbbbbbabbbbbbbaaaababbbabaaaabb
        baababbbabbaabaabbbababbbabaababbbbbbaba
        abbbabbbbbbabbabbabbbabb
        abaabaabbabbaaaabaaaaaaa
        babbaaaabaaaabbaababbabaaaababbababaabaa
        babbbaababbabaaaababbaab
        baaabaaaabbbbaabaaaabaaaaaaaaaabaaaabbbbabaabaaabbabaaaaabbbaabbbabbbbab
        bbaababbbababaaaababbbab
        ababbbabbaabbbabaaababbabbbabaaabaaababbbbbbaaabbabaaababbbbbbababbbbabbabbbaaab
        aaabbaabbbaaabaaaaabbbab
        baabbbbabbabaabababaabba
        bbaaabaabbababbaaaaabbaa
        aaababaabbababbaaabbabaabbabbaaaaaabbaabbababbba
        babbbaaabbabbaaaaabaaaaa
        bbaaabaabaabaabbabaababb
        babbabaaabbaaabbabbaabab
        bbabaaabaabbababababaaab
        babbbababaabababbaaabbab
        baabbbababaabbababaabaaaabbaaabaaaabaaba
        ababaaaabbabababbbaaaaaabbbbbaba
        bbabbaaaabaaaaaabbbbaaaa
        aaaaaabbbaaabbbabbbabbaa
        abaabbabababaaaaaaababaababaabbbabaabbaa
        aabbbbabbbbabbbbabaabbbaabbaabbababbababbbbaaaaabaabbabababaaaaa
        abbbbaaaaabbbbbabbaaababaaabbbbababbbbbb
        bababbaabbbbbbbaaababaaaabbbbbaaabbaabbabbbababaaaababab
        ababbbbabbabbaaabbabbbaaaaabaaabaabbbaab
        bbbbaaabbbbbbbbabababaaabaaaaabb
        bbaababbbabbaaaabbaaabba
        aabaabaaaaababbaabaaaaaaaababbbb
        bbababbababbaaaaaababbba
        aabaabbbabbbbbaabbaabaaa
        bbbbbabbbaaabaaabaaaaaba
        aaababbababaaabbbabababaababaaaaaaababbbbbaaabab
        abbababbaaababaaabaababa
        aabaabaababbaabaababbbab
        babaaaaaabaabaabaaaabbbabaababbabbaaabbb
        bbbababbbbabbabbabbababaaaaabbbbbbbbaabaabbbababbaaaabab
        aaaababbbbabababbbbabaab
        aabbbbaabababbababababbb
        aaaaabbaaabbabababaaaabb
        aabbabaaaabbabaaabbbbbabbbbbbbbbbbbabbbb
        aaaaaabbaabbababababaaab
        aabbabbabbbabbabbaabbabb
        aabbbbbbbbabbabbabbbabab
        baababbabaabbbbbbbabaaabbbbbbabbabbbaababaaabbabaabbbaaa
        ababbabbabbbabbaaaaababa
        bbbaaabaabbabbaaabaaabaabaabaaba
        babbaababbaaaabaababbbaa
        abbaabaabbbbabaaaabbababaaababaabbbabaaaabaaabbabbaabbba
        baaabaaaabbbbbabababbbba
        bbbabbababbabaaababbaaabaabaaababbababaabbaaaabbabbabbbb
        aabaabbaaabbbbbaabaabbabaaaababbabababbaabababbb
        baabbbbbaababaabbabbaabb
        bbaaabaabaabbbbabaaabbbbbabababaaababababbaabaaabababbbb
        aaaaabbaaababaaabbaaaaaaabaababb
        bbaababaabbbaaaaabaababa
        abbabaaaaaabbbbaaabbbaababbbbababbaababbabbabbaa
        baaabaabbbbaabbaaaaaabaa
        aaaababbbabbbaabaababaaabaabbaabaabbaaab
        bbbaabaabbbbaaaaaababaababaaaabb
        baabbbbbbbaabbaabbbbbbbbbabbbaabaaaaaabb
        baababaababbbabaabaabaabaaabaaababaaabbaaabaaabb
        bbaabbaababaaabbabababab
        aaabbbbbbbabbbaaaaabaaaabaabbbaaabababba
        aaabbaabaabbaaaabbbbabbb
        baababaabaaaabbaabbaabbbabbbaaba
        baaabaababaabbbbbabbaaabbbabbaabbbbababbabbbababbbaabababaababab
        abbaabbaabbababbbabababbaababaabbbabbabbaabbbabbabbabbaaabbabbabaaabbaba
        bbabaabababbaabaababbabb
        ababaabbbaaababbbaaaababbbabbbab
        bbaababababbbaabbbaaaaab
        abbbbaaaaaababbaababaaaabbaaabba
        abbbaaaabbaaaaaaaabbbabb
        babbbbbbbbaaababbbbbbaaaaaaaababbaabbbbbbbbaaaababbabaaa
        babbaaabbbbbbaaabbababbb
        bbabbaaaabbaabaaababbbba
        aaaaaaabaabaabbbaaabbbaa
        aabbabaaaaaabbbbaabbaaaaaaabaaaa
        bbaaaaaababbaaaabbbaabaa
        baababbbabbbbaaabbaaabaabaaabaabaabaaaab
        abaaaabaaaaaababaabbbbaaaabbbbaabbaaaabaabaaaaab
        abaabbabbbabbabaabababaabbbbababbaaaaaab
        abaaabaabbabaabbaabbabbaaababbbbaaaaaaabaaababbaaabbaaabaaaabbaaababbaab
        aaaaabbbbbabbaaaabbaaaab
        babbaabaaaabbaababababbb
        bababaaabaabbaabababbabababaabbb
        aaaaabbbbbaaabaaabababbb
        bbabbaabbaaabaaabaabbaaa
        bbbbabbbaaaabbbaaabbaabbbbabaabaaababababbababbbaababbbb
        abbaaabbabbbabbbbbabbaaaabaabbabbbbaaaaa
        bbbbababbaaaaabaaaaaabaaabbbbabaabbbaabaaaaaabbabbbbbababaabaabbabbbbbabaaaabbab
        aaabbaababababaababbabbaaaabaabb
        abbbbabbbabaabbbabbbbbbaaabababbbabbabab
        aaaabbabaabbaaaaabababba
        abbababbbababbaabbbbbbab
        aaabaaabbbbabaababbaabaaaaaaaabbbaabbbbbbaaabbab
        baaabaaabbaaababbabbbbbababbbbbabababaabbabbbbab
        babbaabaaababaaaaabaaaaa
        bbbaaabaaaababbbbabbabab
        abaaabbbbbbaabbaabaabbbb
        aaaaabababaaaabaabaaabaa
        babbbaababbaaaaababaaaaa
        bbaababbbaabbbabbaaaaaab
        ababaaaaaabbbababbbaabab
        aaaaaaaababaaabaabbabbaabbbbbaba
        bbabbbbbaabbbbababaabaaa
        abbbbaaaaaaabbbaabbaabab
        abbbabbbaabaabbaaabbbaababbabaabbaabaabbbbbbbbbaabababba
        baabaabbbbabbbbbbbbbbaab
        bbaabbabbbababbaaababbbb
        aababaaaaabaaabababbbbba
        aaaabaaababaaabaaabbaaaabababbabababbaaa
        bbbbaaabaaaaaabbabaabbaa
        bbaabbaaaaaaaaaaabaaaaab
        aaababbbaabbababaaababbaabaaaaaabbbbbbab
        aaabbaaaabbbaabbaaabbbaaabbaabbbaabababaabababbbaabaaabaababbbbb
        baaabaaaaababaaabaabbaaa
        abbaabbbbbbaaabaaabbbaaa
        bababbababbaababbaabbabb
        abbababbaabbbabaabbbbbba
        babbaaaaabbbbaaabbbababbbaaabaaaabbababaababaaaaabaabaaaabaabbba
        baaabbbababbabaaaabbbabaabaabaababaaaaabbabbbbbbaabaaaab
        aabbaaaabaabbababaaaaabb
        babbaaabbaaaabbaabaaabab
        abaabbabbbaabbabbaabbabb
        babbbaabaaaaabbaabbababaabbaaabababbbbabaaaabbaaababaaabbbbaaaaa
        aabaaababbbabbaaaabaaabaaaabbaaabaaabaaabbaabbabaabaabababaaabbababbbaaaabbaababbaaababaaabbbbbb
        baababbaabbaabbbbabbabab
        aaaaaaaaaaaabbbbbbbaabbabbaaaabaaababbaaaabbaaababababbabaabaaba
        aabaaababbabbbaabbbaaaab
        abbbbbaaaaababbbbbabbababbbaabbaaabbaabaabaaabaa
        aabaabaaabababaabaabbababaaabaabaaaabababaabaabaabaaabaa
        bbbabbabbababbabaaabbaaa
        bbabbaaaaaababbaabbbbbaabababbbb
        ababbbbbbbabbabbabababba
        aabbbbabbbbbbbbbbbbaaaabbaababaabaababababbbabbbbaaaaaabbbabbabbabbabbba
        aaaaaabbbbbbaabbabaaabba
        aabbbbaabaaabbbbaabaaaaa
        aaabbbababaabababbaabbaaababbbbaaababaaababaabbbabbaabbb
        bbabbabbabbabaaaaaabbbba
        bbaabbabbababaaaabbbbaaaababbbababaaabab
        bbbbbbaaaaaabbbaabbaabba
        bbbaaababbaababaabbabaaababbbbabaabbaabbbabbbbaabaabaaaa
        aaabbaabbaabbbababaaaaab
        aaaabbbbabaabbabbabaabab
        aabaaabaabbabbabbabbaabaabaaabba
        aabbabaababaaabbabababbb
        aaaabaabbbbbbbbabbbbababaabaaabb
        aababbabaababaabaaabbbbb
        bbabaaabbaabbbbaaaaababaabbbbabbbbbaaaab
        abbaaaaabaaabbbababababa
        aababaaaaaaababaaabaabbbaabbabbb
        bbbabbabbbabbaaabbababbabbbbbaabbabbbbaa
        abbaaabbbbaababbaaabaaba
        aabaabaaaaaabababbbaaabbababaaaa
        bbbbbbbaaaaaabbabbabbbab
        bbbaaabaabababaabbbabbaa
        baaaabaababbbaaaabaabaaa
        bbbabaabaaababaaaababbba
        bbaaaababbabababaaabbbab
        baababaaaabbbbbabbaaaaab
        babbaaaaabaaaabbbbbaaaaa
        babaaabaaaaaababbbaaabbb
        aaaabbbbabaaaaaabbabbaabbbbaabaabaaabbbbaabaaababbabbbbb
        abbbbaabbbaabbaababbabaaaabbabbabababaab
        aababaabababbabababbabbb
        aababaaaaaaaaabbbbbaabbabababaaabbbbababbabaabbaaabbbabbbbbaaaaa
        bbbbaaabbbabaabaaaababbb
        aaaabbabbabbbaaababbaabb
        bbbaabbababbabbabaabbbabaaaabbaabbbaabbb
        bbbaaabbaabaaabaaaaabbbbaabbbbbbbbababababaaaabb
        bbababababbabaabbabbbbba
        bbbbbbbbbbaaaaaaaaaabaaaaaabbbbb
        babbabaabbabbaaabbaaabaabaaaaaab
        aababbabbabbbababbaaaababbaabababbaabbba
        aaabbaabbabaaabbabbabaaabbaaaaaababbabab
        ababaababbaaaabaabbbabab
        aababbaabbaababbbbabaabaaaabaaba
        aaaaaabbababbababababaabababbabbababbbbababbababaabbbaaa
        bbabababbaabbabaabbbaaab
        bababbabbaaabbbbaabbaaba
        baabbbaababaaaaaaabbabaabbabababaabaaabaabbbaabbabbaaaaaaababbbaaaaabaaabbababaaaaabbaba
        aabbbbbabaabaabbabbbbbabaabbbabb
        bbaaabbbbbbaababaabababaaabaaaab
        bbabbaaaabbbbaaabbababaa
        babaaabaababaababbbaaabbbaaabbaa
        babbabaabbbababbbbabbbab
        aaaabaaaabbabbaaaaabaabb
        aababaabbaabaabbbaabaaab
        babbbaabbaababbababbbabb
        aababbaabbabbbaaabaaaabb
        baababbaaaaaababababbbab
        aabbabbabbabababbababaab
        ababbbabbabbaaaaababbbbbbbbababa
        baaaabaabbabbbaaababbbaa
        bbabbbaaabaaabbbbabababa
        bbbbbbbabbbabaababaababb
        bbbaabbabaabababababbababbaababbababbaaaaabbbaaa
        abbaabaabbabbbaaaaaaabbbabbbbbabbbabaabbabbaabbaaaaabbaa
        aaabbbbaaabababbababaaabbbbbbaaabbbababbbbabbabaaabbabaaabbaaaaa
        baababaabbbabaabaaabbabb
        aaaabbabaabaaababaabbbbbbaabbbbabaaababbabbaabbababbbbab
        baabaabbaabbbbaaaabaabbabbbaabbbbaaababa
        aaabababbaababbabaabbbaaabbaaaaaabbbbbbbbabbbababbbababbaababababaaabbabbababbaa
        aabababbabaabaabbababbabaababbbababaabbbaababaabaaaabbbabbbabbaaabbabbaa
    """.trimIndent()
}