import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins { application }
apply(plugin = "com.github.johnrengelman.shadow")

application {
    mainClassName = "${AppInfo.group}.app.ApplicationKt"

    tasks.withType<ShadowJar>() {
        minimize()
        isZip64 = true
        archiveBaseName.set("kotlin_compiler")
        archiveVersion.set(AppInfo.version)
    }

    dependencies {
        implementation(project(":core"))
        implementation(project(":paramsreader"))
        implementation(project(":lexer"))
    }
}