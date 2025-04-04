/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/3/31
 */

package cn.rtast.kembeddable.resources.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

@Suppress("DEPRECATION")
abstract class GenerateResourcesTask : DefaultTask() {

    private val sep = File.separator

    @TaskAction
    fun generate() {
        val settings = project.extensions.findByType(KEmbeddableResourcesExtension::class.java)!!
        val usingCompression = settings.compression.get()
        val outputDir = project.layout.buildDirectory.dir("generated/kotlin").get().asFile
        val outputFile = File(outputDir, "_KEmbeddableResourcesGeneratedResources.kt")
        outputFile.parentFile.mkdirs()
        val generatedCode = buildString {
            appendCode("// 此文件为自动生成, 请勿手动修改! | This file is auto-generated, please DO NOT edit it by hand!")
            appendLine()
            appendCode("// 由 RTAkland/RKMBed(GitHub) 自动生成 | Generated by RTAkland/RKMBed on GitHub")
            appendLine()
            appendCode("package ${settings.packageName.get() ?: "com.example.resources"}")
            appendLine()
            appendCode("""import cn.rtast.rkmbed.runtime.Resource""")
            appendLine()
            appendCode("private val kembeddableGeneratedResource: Map<String, Resource> = mapOf<String, Resource>(")
            settings.resourcePath.get().distinct().forEach { sourceSet ->
                val resourcesDir = project.layout.projectDirectory.dir("src/$sourceSet")
                if (resourcesDir.asFile.exists()) {
                    val files = resourcesDir.asFileTree.files
                    files.forEach {
                        appendCode(
                            "\"${
                                it.path.split("src${sep}${sourceSet.split("/").first()}${sep}resources${sep}")
                                    .last().replace(sep, "/")
                            }\" to Resource(${it.toUByteArrayOf(usingCompression)}, $usingCompression),"
                        )
                    }
                }
            }
            appendCode(")")
            appendLine()
            appendCode(
                """public fun getResource(path: String): Resource {
    return requireNotNull(kembeddableGeneratedResource[path]) { "资源 ${'$'}path 不存在! | Resource ${'$'}path is not exists!" }
}"""
            )
        }
        outputFile.writeText(generatedCode)
    }
}