package by.mksn.indimebot.misc

import java.security.MessageDigest

/**
 * Returns a string trimmed to the specified [length] with optional [tail] string added at the end of the trimmed string.
 * The [tail] length must be less than the target [length] as the result is required to be no longer than this value.
 */
fun String.trimToLength(length: Int, tail: String = ""): String {
    require(length > tail.length) { "Tail '$tail' occupies the full length of $length chars of the new string" }
    return if (this.length <= length) this else this.take(length - tail.length) + tail
}

/**
 * Simple Markdown special chars escaping
 */
fun String.escapeMarkdown() = replace("*", "\\*")
    .replace("_", "\\_")
    .replace("`", "\\`")
    .replace("[", "\\[")


private val MESSAGE_DIGEST = MessageDigest.getInstance("SHA-256")

fun String.hashSHA256(): String = MESSAGE_DIGEST.digest(this.toByteArray())
    .joinToString("") { ((it.toInt() and 0xff) + 0x100).toString(16).substring(1) }