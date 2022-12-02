# rison.kt

An implementation of Rison.
This is kotlin porting of [python-rison](https://github.com/betodealmeida/python-rison) based on
[kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)

# Quickstart

```kotlin
import emesday.rison.*
import kotlinx.serialization.*

@Serializable
data class Project(val name: String, val language: String)

fun main() {
    val projectAsRisonString = Rison.encodeToString(Project("kotlinx.serialization", "Kotlin"))
    println(projectAsRisonString)
    // (language:Kotlin,name:kotlinx.serialization)
    val project = Rison.decodeFromString<Project>("(language:Kotlin,name:kotlinx.serialization)")
    println(project)
    // Project(name=kotlinx.serialization, language=Kotlin)
}
```

# Rison - Compact Data in URIs

see [python-rison](https://github.com/betodealmeida/python-rison#rison---compact-data-in-uris)

# Related Projects

- Rison original
  website: http://mjtemplate.org/examples/rison.html ([archive](https://web.archive.org/web/20130910064110/http://www.mjtemplate.org/examples/rison.html))
- [python-rison](https://github.com/pifantastic/python-rison) (outdated) : last update is 2015.
- [python-rison](https://github.com/betodealmeida/python-rison):
  forked from the above and updated for Python 3 compatibility. This project `rison.kt` is the poring of this.
- [rison(java)](https://github.com/bazaarvoice/rison): A Rison plugin for Jackson
- [rison(JavaScript/Python/ect)](https://github.com/Nanonid/rison)
