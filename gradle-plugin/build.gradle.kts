import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish") version "1.2.1"
    `java-gradle-plugin`
}

gradlePlugin {
    website = "https://github.com/RTAkland/RKMbed"
    vcsUrl = "https://github.com/RTAkland/RKMbed"
    plugins {
        create("kembeddable") {
            id = "cn.rtast.kembeddable"
            displayName = "RKMBed"
            description = "Auto generate resources on kmp"
            tags = listOf("resources", "kmp", "auto-generated")
            implementationClass = "cn.rtast.kembeddable.resources.gradle.KEmbeddableResourcesPlugin"
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.20")
}

tasks.compileKotlin {
    compilerOptions.jvmTarget = JvmTarget.JVM_1_8
}

tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

val generateVersions by tasks.registering {
    val version = project.version.toString()
    project.layout.buildDirectory.dir("generated/kotlin").get().asFile.apply {
        mkdirs()
        File(this, "BuildConstants.kt").writeText(
            buildString {
                appendLine("package cn.rtast.kembeddable.resources")
                appendLine("const val PLUGIN_VERSION=\"$version\"")
            }
        )
    }
}

tasks.all {
    if (this.name != "generateVersions") {
        this.dependsOn(generateVersions)
    }
}

sourceSets {
    main {
        kotlin.srcDir("build/generated/kotlin")
    }
}