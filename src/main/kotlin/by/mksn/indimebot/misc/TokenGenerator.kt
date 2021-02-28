package by.mksn.indimebot.misc

import kotlin.random.Random

object TokenGenerator {
    private val CHARS = (('A'..'Z') + ('a'..'z') + ('0'..'9')).joinToString("")
    private const val DEFAULT_LENGTH = 8

    fun generate(length: Int = DEFAULT_LENGTH): String = (1..length)
        .map { CHARS[Random.nextInt(0, CHARS.length)] }
        .joinToString("")
}