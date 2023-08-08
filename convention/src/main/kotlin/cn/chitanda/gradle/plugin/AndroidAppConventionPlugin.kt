@file:Suppress("UnstableApiUsage")

package cn.chitanda.gradle.plugin

import cn.chitanda.gradle.plugin.extension.configureKotlinAndroid
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Properties

/**
 *@author: Chen
 *@createTime: 2022/11/13 19:29
 *@description:
 **/
class AndroidAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<ApplicationExtension> {

                configureKotlinAndroid(this)
                defaultConfig {
                    targetSdk = 33
                    versionCode = try {
                        "git rev-list HEAD --first-parent --count".execute().text().trim().toInt()
                    } catch (_: Throwable) {
                        1
                    }
                    versionName = try {
                        "\\d+(.\\d+){0,2}".toRegex()
                            .find("git describe --tags".execute().text().trim())!!.value.also {
                                logger.quiet("get version name $it")
                            }
                    } catch (e: Throwable) {
                        logger.error("get version code error $e")
                        "0.0.1"
                    }
                }
//                configureFlavors(this)
                val propertiesFile = file("${project.rootProject.projectDir}/local.properties")
                if (propertiesFile.exists()) {
                    val properties = Properties().apply {
                        load(propertiesFile.inputStream())
                    }
                    val storePassword = properties.getProperty("sign.store.pwd")
                    val keyAlias = properties.getProperty("sign.key.alias")
                    val keyPassword = properties.getProperty("sign.key.pwd")
                    if (storePassword != null && keyPassword != null && keyAlias != null) {
                        signingConfigs.create("chitanda") {
                            storeFile =
                                File("${project.rootProject.projectDir.absolutePath}/build-logic/chitanda")
                            this.storePassword = storePassword
                            this.keyAlias = keyAlias
                            this.keyPassword = keyPassword
                        }
                    } else {
                        logger.error("please add sign config in local.properties")
                    }
                }
            }

        }
    }

    private fun String.execute(): Process {
        val runtime = Runtime.getRuntime()
        return runtime.exec(this)
    }

    private fun Process.text(): String {
        var output = ""
        val inputStream = this.inputStream
        val isr = InputStreamReader(inputStream)
        val reader = BufferedReader(isr)
        var line = reader.readLine()
        while (line != null) {
            output += line + "\n"
            line = reader.readLine()
        }
        return output
    }
}