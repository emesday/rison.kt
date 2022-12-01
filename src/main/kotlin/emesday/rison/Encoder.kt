package emesday.rison

class Encoder {

    private var string: String = ""

    private var index: Int = 0

    fun toJsonString(string: String, format: String = "string"): Sequence<Char> = sequence {
        if (string == "(") {
            throw ParserException("unmatched '('")
        }

        when (format) {
            "string" -> this@Encoder.string = string
            "A" -> this@Encoder.string = "!($string)"
            "O" -> this@Encoder.string = "($string)"
            else -> throw ParserException(
                "Parse format should be one of string, A for list, O for object."
            )
        }

        index = 0

        yieldAll(readValue())

        if (next() != null) {
            throw ParserException("unable to parse rison string $string")
        }
    }

    private fun readValue(): Sequence<Char> = sequence {
        val c = next()
        var done = true

        if (c == '!') {
            yieldAll(parseBang())
        } else if (c == '(') {
            yield('{')
            yieldAll(parseOpenParen())
            yield('}')
        } else if (c == '\'') {
            yield('"')
            yieldAll(parseSingleQuote())
            yield('"')
        } else if (c != null && c in "-0123456789") {
            yieldAll(parserNumber())
        } else {
            done = false
        }

        if (!done) {
            val s = string
            val i = index - 1
            val m = NEXT_ID_RE.matchAt(s, i)
            if (m != null) {
                val id = m.groupValues.first()
                index = i + id.length
                yield('"')
                yieldAll(id.iterator())
                yield('"')
                done = true
            }
        }

        if (!done) {
            if (c != null) {
                throw ParserException("invalid character: '$c'")
            }
            throw ParserException("empty expression")
        }
    }

    private fun parseArray(): Sequence<Char> = sequence {
        var count = 0
        var c: Char?
        while (true) {
            c = next()
            if (c == ')') {
                break
            }

            if (c == null) {
                throw ParserException("unmatched '!('")
            }

            if (count > 0) {
                if (c != ',') {
                    throw ParserException("missing ','")
                }
                yield(',')
            } else if (c == ',') {
                throw ParserException("extra ','")
            } else {
                index -= 1
            }
            yieldAll(readValue())
            count += 1
        }
    }

    private fun parseBang(): Sequence<Char> = sequence {
        val s = string
        val c = s.getOrNull(index)
        index += 1
        if (c == null) {
            throw ParserException("\"!\" at end of input")
        }

        when (c) {
            't' -> yieldAll("true".iterator())
            'f' -> yieldAll("false".iterator())
            'n' -> yieldAll("null".iterator())
            '(' -> {
                yield('[')
                yieldAll(parseArray())
                yield(']')
            }
            else ->
                throw ParserException("unknown literal: !'$c'")
        }
    }

    private fun parseOpenParen(): Sequence<Char> = sequence {
        var count = 0
        while (true) {
            val c = next()
            if (c == ')') {
                break
            }
            if (count > 0) {
                if (c != ',') {
                    throw ParserException("missing ','")
                }
                yield(',')
            } else if (c == ',') {
                throw ParserException("extra ','")
            } else {
                index -= 1
            }
            yieldAll(readValue())
            if (next() != ':') {
                throw ParserException("missing ':'")
            }
            yield(':')
            yieldAll(readValue())
            count += 1
        }
    }

    private fun parseSingleQuote(): Sequence<Char> = sequence {
        val s = string
        var i = index
        var start = i
        var c: Char

        while (true) {
            if (i >= s.length) {
                throw ParserException("unmatched \"'\"")
            }
            c = s[i]
            i += 1
            if (c == '\'') {
                break
            }

            if (c == '!') {
                if (start < i - 1) {
                    yieldAll(s.subSequence(start until i - 1).iterator())
                }
                c = s[i]
                i += 1
                if (c in "!'") {
                    yield(c)
                } else {
                    throw ParserException("invalid string escape: \"!$c\"")
                }

                start = i
            }
        }

        if (start < i - 1) {
            yieldAll(s.subSequence(start until i - 1).iterator())
        }
        index = i
    }

    private fun parserNumber(): Sequence<Char> = sequence {
        var s = string
        var i = index
        val start = i - 1
        var state: String? = "int"
        var permittedSigns = "-"
        val transitions = mapOf(
            "int+." to "frac",
            "int+e" to "exp",
            "frac+e" to "exp"
        )
        var c: Char

        while (true) {
            if (i >= s.length) {
                i += 1
                break
            }
            c = s[i]
            i += 1

            if (c in '0'..'9') {
                continue
            }

            if (permittedSigns.indexOf(c) >= 0) {
                permittedSigns = ""
                continue
            }
            state = transitions["$state+${c.lowercase()}"]
            if (state == null) {
                break
            }
            if (state == "exp") {
                permittedSigns = "-"
            }
        }
        index = i - 1
        s = s.slice(start until index)
        if (s == "-") {
            throw ParserException("invalid number")
        }
        if (Regex("[.e]").find(s) != null) {
            s = Regex("e(\\d)").replace(s, "e+$1")
            yieldAll(s.iterator())
        } else {
            yieldAll(s.iterator())
        }
    }

    fun next(): Char? {
        val s = string
        var i = index
        var c: Char

        while (true) {
            if (i == s.length)
                return null
            c = s[i]
            i += 1
            if (c !in WHITESPACE)
                break
        }
        index = i
        return c
    }
}

fun String.risonToJsonCharSequence(): Sequence<Char> = Encoder().toJsonString(this)

fun String.risonToJsonString(): String = risonToJsonCharSequence().joinToString("")
