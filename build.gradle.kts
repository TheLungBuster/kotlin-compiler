import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version DepsVersions.kotlin
    kotlin("kapt") version DepsVersions.kotlin
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

allprojects {
    group = AppInfo.group
    version = AppInfo.version
}

repositories {
    mavenCentral()
    jcenter()
}

buildscript {

    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = DepsVersions.kotlin))
        classpath(koin("gradle-plugin", version = DepsVersions.koin))
        classpath("com.github.jengelman.gradle.plugins:shadow:6.0.0")
    }
}

subprojects {
    apply(plugin = Plugins.kotlin)
    apply(plugin = Plugins.koin)

    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8", version = DepsVersions.kotlin))
        implementation(kotlin("compiler-embeddable", version = DepsVersions.kotlin))

        implementation("com.github.ajalt:mordant:1.2.1")

        implementation(koin("core", version = DepsVersions.koin))
        implementation(koin("core-ext", version = DepsVersions.koin))
        testImplementation(koin("test", version = DepsVersions.koin))

        testImplementation(kotest("runner-junit5-jvm", version = DepsVersions.kotest))
        testImplementation(kotest("assertions-core-jvm", version = DepsVersions.kotest))
        testImplementation(kotest("property-jvm", version = DepsVersions.kotest))
        testImplementation(kotest("extensions-koin-jvm", version = DepsVersions.kotest))
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = OtherVersions.jvm
        }
        withType<Test> {
            useJUnitPlatform()
        }
        withType<Wrapper> {
            gradleVersion = OtherVersions.gradle
            distributionType = Wrapper.DistributionType.ALL
        }
    }
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}