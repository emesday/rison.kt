package emesday.rison

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.test.*

@Serializable
data class Project(val name: String, val language: String)

@Serializable
data class Sample(val a: Int, val b: String, val c: String)

class EncoderTest {
    @Test
    fun `test simple`() {
        val expected = Project("kotlinx.serialization", "Kotlin")
        val risonString = Rison.encodeToString(expected)
        val actual = Rison.decodeFromString<Project>(risonString)
        assertEquals(expected, actual)
    }

    @Test
    fun `test dict`() {
        assertEquals("()", Rison.encodeToString(emptyMap<String, String>()))
        assertEquals("(a:0,b:1)", Rison.encodeToString(mapOf("a" to 0, "b" to 1)))
        assertEquals(
            "(a:0,b:foo,c:'23skidoo')",
            Rison.encodeToString(Sample(0, "foo", "23skidoo"))
        )
        assertEquals(
            "(id:!n,type:/common/document)",
            Rison.encodeToString(mapOf("type" to "/common/document", "id" to null))
        )
        assertEquals("(a:0)", Rison.encodeToString(mapOf("a" to 0)))
        assertEquals("(a:%)", Rison.encodeToString(mapOf("a" to "%")))
        assertEquals("(a:/w+/)", Rison.encodeToString(mapOf("a" to "/w+/")))
    }

    @Test
    fun `test ture,false,null`() {
        assertEquals("!t", Rison.encodeToString(true))
        assertEquals("!f", Rison.encodeToString(false))
        assertEquals("!n", Rison.encodeToString(null as String?))
    }

    @Test
    fun `test array(list)`() {
        assertEquals("!(1,2,3)", Rison.encodeToString(listOf(1, 2, 3)))
        assertEquals("!()", Rison.encodeToString(emptyList<String>()))
        assertEquals(
            "!(!t,!f,!n,'')",
            Rison.encodeToString(listOf(JsonPrimitive(true), JsonPrimitive(false), JsonNull, JsonPrimitive("")))
        )
    }

    @Test
    fun `test number`() {
        assertEquals("0", Rison.encodeToString(0))
        assertEquals("1.5", Rison.encodeToString(1.5))
        assertEquals("-3", Rison.encodeToString(-3))
        assertEquals("1.0e30", Rison.encodeToString(1e+30))
        assertEquals("1.0e-30", Rison.encodeToString(1.0000000000000001e-30))
    }

    @Test
    fun `test string`() {
        assertEquals("''", Rison.encodeToString(""))
        assertEquals("G.", Rison.encodeToString("G."))
        assertEquals("a", Rison.encodeToString("a"))
        assertEquals("'0a'", Rison.encodeToString("0a"))
        assertEquals("'abc def'", Rison.encodeToString("abc def"))
        assertEquals("'-h'", Rison.encodeToString("-h"))
        assertEquals("a-z", Rison.encodeToString("a-z"))
        assertEquals("'wow!!'", Rison.encodeToString("wow!"))
        assertEquals("domain.com", Rison.encodeToString("domain.com"))
        assertEquals("'user@domain.com'", Rison.encodeToString("user@domain.com"))
        assertEquals("'US $10'", Rison.encodeToString("US $10"))
        assertEquals("'can!'t'", Rison.encodeToString("can't"))
    }
}

