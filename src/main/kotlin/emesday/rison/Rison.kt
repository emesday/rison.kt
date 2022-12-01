package emesday.rison

import kotlinx.serialization.json.*

open class Rison {

    private var string: String = ""

    private var index: Int = 0

    companion object Default : Rison()

    fun decodeFromString(string: String, format: String = "string"): JsonElement {
        if (string == "(") {
            throw ParserException("unmatched '('")
        }
        when (format) {
            "string" -> this.string = string
            "A" -> this.string = "!($string)"
            "O" -> this.string = "($string)"
            else -> throw ParserException(
                "Parse format should be one of string, A for list, O for object."
            )
        }
        index = 0
        val value = readValue()
        if (next() != null) {
            throw ParserException("unable to parse rison string $string")
        }
        return value
    }

    private fun readValue(): JsonElement {
        val c = next()

        if (c == '!') {
            return parseBang()
        } else if (c == '(') {
            return parseOpenParen()
        } else if (c == '\'') {
            return parseSingleQuote()
        } else if (c != null && c in "-0123456789") {
            return parserNumber()
        }

        val s = string
        val i = index - 1
        val m = NEXT_ID_RE.matchAt(s, i)
        if (m != null) {
            val id = m.groupValues.first()
            index = i + id.length
            return JsonPrimitive(id)
        }

        if (c != null) {
            throw ParserException("invalid character: '$c'")
        }
        throw ParserException("empty expression")
    }

    private fun parseArray(): JsonElement = buildJsonArray {
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
            } else if (c == ',') {
                throw ParserException("extra ','")
            } else {
                index -= 1
            }
            add(readValue())
            count += 1
        }
    }

    private fun parseBang(): JsonElement {
        val s = string
        val c = s.getOrNull(index)
        index += 1
        if (c == null) {
            throw ParserException("\"!\" at end of input")
        }

        return when (c) {
            't' -> JsonPrimitive(true)
            'f' -> JsonPrimitive(false)
            'n' -> JsonNull
            '(' -> parseArray()
            else ->
                throw ParserException("unknown literal: !'$c'")
        }
    }

    private fun parseOpenParen(): JsonElement = buildJsonObject {
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
            } else if (c == ',') {
                throw ParserException("extra ','")
            } else {
                index -= 1
            }
            val key = readValue()
            if (next() != ':') {
                throw ParserException("missing ':'")
            }
            val value = readValue()
            put(key.jsonPrimitive.content, value)
            count += 1
        }
    }

    private fun parseSingleQuote(): JsonElement {
        val s = string
        var i = index
        var start = i
        val segments = mutableListOf<String>()
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
                    segments.add(s.slice(start until i - 1))
                }
                c = s[i]
                i += 1
                if (c in "!'") {
                    segments.add(c.toString())
                } else {
                    throw ParserException("invalid string escape: \"!$c\"")
                }

                start = i
            }
        }

        if (start < i - 1) {
            segments.add(s.slice(start until i - 1))
        }
        index = i
        return JsonPrimitive(segments.joinToString(""))
    }

    private fun parserNumber(): JsonElement {
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
            return JsonPrimitive(s.toDouble())
        }
        return JsonPrimitive(s.toLong())
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

    fun encodeToString(element: JsonElement): String {
        return when (element) {
            is JsonArray -> encode(element)
            is JsonObject -> encode(element)
            is JsonPrimitive -> encode(element)
            is JsonNull -> encode(element)
        }
    }

    private fun encode(array: JsonArray): String {
        TODO()
    }

    private fun encode(primitive: JsonPrimitive): String {
        if (primitive.isString) {
        }
        TODO()
    }

    private fun encode(nil: JsonNull): String {
        return "!n"
    }

    private fun encode(obj: JsonObject): String {
        TODO()
    }
}
