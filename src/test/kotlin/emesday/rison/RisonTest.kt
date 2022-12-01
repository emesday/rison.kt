package emesday.rison

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.junit.jupiter.api.*
import kotlin.test.*
import kotlin.test.Test

@Serializable
data class Project(val name: String, val language: String)

class RisonTest {

    private val objectOrArrayPairs = mapOf(
        "()" to "{}",
        "(a:0)" to """{"a":0}""",
        "(a:0,b:1)" to """{"a":0,"b":1}""",
        "(a:0,b:foo,c:'23skidoo')" to """{"a":0,"b":"foo","c":"23skidoo"}""",
        "(id:!n,type:/common/document)" to """{"id":null,"type":"/common/document"}""",
        "(a:0)" to """{"a":0}""",
        "(a:%)" to """{"a":"%"}""",
        "(a:/w+/)" to """{"a":"/w+/"}""",
        "!(1,2,3)" to "[1,2,3]",
        "!()" to "[]",
        "!(!t,!f,!n,'')" to """[true,false,null,""]""",
        "(any:json,yes:!t)" to """{"any":"json","yes":true}""",
    )

    private val primitivePairs = mapOf(
        "!t" to true,
        "!f" to false,
        "!n" to null,
        "0" to 0,
        "1.5" to 1.5,
        "-3" to -3,
        "1e30" to 1e+30,
        "1e-30" to 1e-30,
        "''" to "",
        "G." to "G.",
        "a" to "a",
        "'0a'" to "0a",
        "'abc def'" to "abc def",
        "'-h'" to "-h",
        "a-z" to "a-z",
        "'wow!!'" to "wow!",
        "domain.com" to "domain.com",
        "'user@domain.com'" to "user@domain.com",
        "'US $10'" to "US $10",
        "'can!'t'" to "can't",
        "'Control-F: \u0006'" to "Control-F: \u0006",
        "'Unicode: ௫'" to "Unicode: \u0beb",
    )

    private val exceptions = listOf(
        "(",
        ")",
    )

    @Test
    fun decode() {
        for ((rison, json) in objectOrArrayPairs) {
            val expected = Json.decodeFromString<JsonElement>(json)
            val actual = Rison.decodeFromString(rison)
            assertEquals(expected, actual)
        }

        for ((rison, json) in primitivePairs) {
            val expected = when (json) {
                is Boolean -> JsonPrimitive(json)
                is Number -> JsonPrimitive(json)
                is String -> JsonPrimitive(json)
                null -> JsonNull
                else -> throw Error()
            }
            val actual = Rison.decodeFromString(rison)
            assertEquals(expected, actual)
        }

        for (rison in exceptions) {
            assertThrows<ParserException> { Rison.decodeFromString(rison) }
        }
    }

    @Test
    fun encode() {
        val data = Project("kotlinx.serialization", "Kotlin")

        val element = Json.encodeToJsonElement(data)
        val jsonString1 = Json.encodeToString(element)
        val jsonString2 = Json.encodeToString(data)
        // val risonString = Rison.encodeToString(element)

        println(element)
        println(jsonString1)
        println(jsonString2)
    }
}