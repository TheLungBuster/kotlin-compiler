import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.koin(module: String, version: String? = null): Any =
    "org.koin:koin-$module${version?.let { ":$version" } ?: ""}"

fun DependencyHandler.kotlinx(module: String, version: String? = null): Any =
    "org.jetbrains.kotlinx:kotlinx-$module${version?.let { ":$version" } ?: ""}"

fun DependencyHandler.kotest(module: String, version: String? = null): Any =
    "io.kotest:kotest-$module${version?.let { ":$version" } ?: ""}"

fun DependencyHandler.mockk(module: String? = null, version: String? = null): Any {
    return if (module != null) {
        "io.mockk:mockk-$module${version?.let { ":$version" } ?: ""}"
    } else {
        "io.mockk:mockk${version?.let { ":$version" } ?: ""}"
    }
}