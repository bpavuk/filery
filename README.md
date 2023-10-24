# filery — the multiplatform library for file fuckery

Latest version: Prerelease 2 (0.2)

WARNING: this project is currently on its early steps, is not production-ready and is welcome to contributions

This project aims to provide you with a common and intuitive API for files while remaining
easy to maintain and nuance-less

## Starting points

filery has two starting points - the `Filery` class and the `filery(path: String, block: Filery.() -> Unit)` function.
The first one is familiar for JVM users, meanwhile, the second is a more idiomatic and DSL-ish way. Here are the examples:

```kotlin
fun main() = runBlocking {
    // filery function automatically closes the file in case of any error
    // and when work upon the file is finished
    filery("/home/bpavuk/fuckery.txt") {
        println(readLine()) // outputs "Hello, filery"
    }
}
```

Same but with `Filery` class:
```kotlin
fun main() = runBlocking {
    val file = Filery("/home/bpavuk/fuckery.txt").open()
    println(file.readText())
    file.close()
}
```

Currently, filery is limited to two platforms: Linux (partially implemented) and JVM (fully implemented)

You can connect the filery to your Gradle project by adding the GitHub Registry and this dependency:
```
"com.bpavuk:filery:Prerelease_2"
```

Have a nice file fuckery!