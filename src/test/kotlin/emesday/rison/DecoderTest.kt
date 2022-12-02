package emesday.rison

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.test.*

class DecoderTest {

    @Test
    fun `test dict`() {
        assertEquals(emptyMap<String, String>(), Rison.decodeFromString("()"))
        assertEquals(mapOf("a" to 0, "b" to 1), Rison.decodeFromString("(a:0,b:1)"))
        assertEquals(
            Sample(0, "foo", "23skidoo"),
            Rison.decodeFromString("(a:0,b:foo,c:'23skidoo')")
        )
        assertEquals(
            mapOf("type" to "/common/document", "id" to null),
            Rison.decodeFromString("(id:!n,type:/common/document)")
        )
        assertEquals(mapOf("a" to 0), Rison.decodeFromString("(a:0)"))
        assertEquals(mapOf("a" to "%"), Rison.decodeFromString("(a:%)"))
        assertEquals(mapOf("a" to "/w+/"), Rison.decodeFromString("(a:/w+/)"))
    }

    @Test
    fun `test ture,false,null`() {
        assertEquals(true, Rison.decodeFromString("!t"))
        assertEquals(false, Rison.decodeFromString("!f"))
        assertEquals(null as String?, Rison.decodeFromString("!n"))
    }

    @Test
    fun `test array(list)`() {
        assertEquals(listOf(1, 2, 3), Rison.decodeFromString("!(1,2,3)"))
        assertEquals(emptyList<String>(), Rison.decodeFromString("!()"))
        assertEquals(
            listOf(JsonPrimitive(true), JsonPrimitive(false), JsonNull, JsonPrimitive("")),
            Rison.decodeFromString("!(!t,!f,!n,'')")
        )
    }

    @Test
    fun `test number`() {
        assertEquals(0, Rison.decodeFromString("0"))
        assertEquals(1.5, Rison.decodeFromString("1.5"))
        assertEquals(-3, Rison.decodeFromString("-3"))
        assertEquals(1e+30, Rison.decodeFromString("1e30"))
        assertEquals(1e-30, Rison.decodeFromString("1e-30"))
    }

    @Test
    fun `test string`() {
        assertEquals("", Rison.decodeFromString("''"))
        assertEquals("G.", Rison.decodeFromString("G."))
        assertEquals("a", Rison.decodeFromString("a"))
        assertEquals("0a", Rison.decodeFromString("'0a'"))
        assertEquals("abc def", Rison.decodeFromString("'abc def'"))
        assertEquals("-h", Rison.decodeFromString("'-h'"))
        assertEquals("a-z", Rison.decodeFromString("a-z"))
        assertEquals("wow!", Rison.decodeFromString("'wow!!'"))
        assertEquals("domain.com", Rison.decodeFromString("domain.com"))
        assertEquals("user@domain.com", Rison.decodeFromString("'user@domain.com'"))
        assertEquals("US $10", Rison.decodeFromString("'US $10'"))
        assertEquals("can't", Rison.decodeFromString("'can!'t'"))
    }
}
