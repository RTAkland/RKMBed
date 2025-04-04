/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/3/31
 */

package cn.rtast.kembeddable.resources.gradle

import cn.rtast.kembeddable.resources.PLUGIN_VERSION
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class KEmbeddableResourcesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("rkmbed", KEmbeddableResourcesExtension::class.java)
        target.tasks.register("generateResources", GenerateResourcesTask::class.java) {
            it.group = "rkmbed"
        }
        target.afterEvaluate {
            val kotlinExtension = target.extensions.getByType(KotlinMultiplatformExtension::class.java)
                ?: throw NonMultiplatformProjectException("该插件仅支持多平台项目，请在多平台项目中使用。")
            kotlinExtension.sourceSets.getByName("commonMain") {
                it.dependencies {
                    api("cn.rtast.rkmbed:runtime:$PLUGIN_VERSION")
                }
            }
            kotlinExtension.sourceSets.findByName("commonMain")?.kotlin?.srcDir("build/generated/kotlin")
        }
    }
}