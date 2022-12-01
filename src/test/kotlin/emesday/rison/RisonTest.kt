package emesday.rison

import kotlin.test.*

class RisonTest {

    private val successPairs = mapOf(
        "()" to "{}",
        "(a:0)" to """{"a":0}""",
        "(a:0,b:1)" to """{"a":0,"b":1}""",
        "(a:0,b:foo,c:'23skidoo')" to """{"a":0,"b":"foo","c":"23skidoo"}""",
        "(id:!n,type:/common/document)" to """{"id":null,"type":"/common/document"}""",
        "(a:0)" to """{"a":0}""",
        "(a:%)" to """{"a":"%"}""",
        "(a:/w+/)" to """{"a":"/w+/"}""",
        "!t" to """true""",
        "!f" to """false""",
        "!n" to "null",
        "!(1,2,3)" to "[1,2,3]",
        "!()" to "[]",
        "!(!t,!f,!n,'')" to """[true,false,null,""]""",
        "0" to "0",
        "1.5" to "1.5",
        "-3" to "-3",
        "1e30" to "1e+30",
        "1e-30" to "1e-30",
        "''" to "\"\"",
        "G." to "\"G.\"",
        "a" to "\"a\"",
        "'0a'" to "\"0a\"",
        "'abc def'" to "\"abc def\"",
        "'-h'" to "\"-h\"",
        "a-z" to "\"a-z\"",
        "'wow!!'" to "\"wow!\"",
        "domain.com" to "\"domain.com\"",
        "'user@domain.com'" to "\"user@domain.com\"",
        "'US $10'" to "\"US $10\"",
        "'can!'t'" to "\"can't\"",
        "(any:json,yes:!t)" to """{"any":"json","yes":true}""",
        "'Control-F: \u0006'" to "\"Control-F: \u0006\"",
        "'Unicode: ௫'" to "\"Unicode: \u0beb\"",
    )

    private val exceptions = listOf(
        "(",
        ")",
    )

    @Test
    fun testRison() {

        for ((rison, json) in successPairs) {
            assertEquals(json, rison.risonToJsonString())
        }

        for (rison in exceptions) {
            assertFailsWith<ParserException> { rison.risonToJsonString() }
        }
    }
}