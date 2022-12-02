package emesday.rison

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*

open class Rison(
    override val serializersModule: SerializersModule
) : StringFormat {

    companion object Default : Rison(EmptySerializersModule())

    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        val element = RisonFunctions().decodeToJsonElement(string)
        return Json.decodeFromJsonElement(deserializer, element)
    }

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        val element = Json.encodeToJsonElement(serializer, value)
        return RisonFunctions().encodeToString(element)
    }
}
