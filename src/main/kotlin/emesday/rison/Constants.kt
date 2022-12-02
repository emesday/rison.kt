package emesday.rison

import kotlinx.serialization.json.*

const val WHITESPACE = ""

const val IDCHAR_PUNCTUATION = "_-./~%+"

val NOT_IDCHAR = (0 until 127)
    .map { it.toChar() }
    .filterNot {
        it.isLetterOrDigit() || it in IDCHAR_PUNCTUATION
    }
    .joinToString("")

// Additionally, we need to distinguish ids and numbers by first char.
val NOT_IDSTART = "-0123456789"

// Regexp string matching a valid id.
val IDRX = Regex.escapeReplacement("[^$NOT_IDSTART$NOT_IDCHAR][^$NOT_IDCHAR]*")

// Regexp to check for valid rison ids.
val ID_OK_RE = Regex("^$IDRX\$", RegexOption.MULTILINE)

// Regexp to find the end of an id when parsing.
val NEXT_ID_RE = Regex(IDRX, RegexOption.MULTILINE)

val JsonTrue = JsonPrimitive(true)

val JsonFalse = JsonPrimitive(false)
