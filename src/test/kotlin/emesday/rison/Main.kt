package emesday.rison

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.test.*

@Serializable
data class Project(val name: String, val language: String)

class J {
    @Test
    fun j() {
        // Serializing objects
        val data = emesday.rison.Project("kotlinx.serialization", "Kotlin")
        val string = Json.encodeToString(data)
        println(string) // {"name":"kotlinx.serialization","language":"Kotlin"}
        // Deserializing back into objects
        val obj = Json.decodeFromString<emesday.rison.Project>(string)
        println(obj) // Project(name=kotlinx.serialization, language=Kotlin)
    }

}

