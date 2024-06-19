@file:Suppress("UnstableApiUsage")

package cn.chitanda.gradle.plugin

import cn.chitanda.gradle.plugin.extension.configureKotlinAndroid
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.io.File
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
                val isWindows = System.getProperty("os.name").contains("Windows")
                configureKotlinAndroid(this)
                defaultConfig {
                    targetSdk = 34
                    versionCode = try {
                        providers.exec {
                            if (isWindows) {
                                commandLine(
                                    "git",
                                    "rev-list",
                                    "HEAD",
                                    "--first-parent",
                                    "--count"
                                )
                            } else {
                                commandLine(
                                    "/bin/sh",
                                    "-c",
                                    "echo `git rev-list HEAD --first-parent --count`"
                                )
                            }
                        }.standardOutput.asText.get().trim().toInt().also {
                            logger.warn("get version code $it")
                        }
                    } catch (t: Throwable) {
                        logger.error("get version code error $t")
                        1
                    }
                    versionName = try {
                        "\\d+(\\.\\d+)+".toRegex()
                            .find(
                                providers.exec {
                                    if (isWindows) {
                                        commandLine("git", "describe", "--tags")
                                    } else {
                                        commandLine("/bin/sh", "-c", "echo `git describe --tags`")
                                    }
                                }.standardOutput.asText.get().trim()
                            )!!.value.also {
                                logger.warn("get version name $it")
                            }
                    } catch (e: Throwable) {
                        logger.error("get version name error $e")
                        "development"
                    }
                }
//                configureFlavors(this)
                signingConfigs {
                    val propertiesFile = file("${project.rootProject.projectDir}/local.properties")
                    if (propertiesFile.exists()) {
                        val properties = Properties().apply {
                            load(propertiesFile.inputStream())
                        }
                        val storePassword = properties.getProperty("sign.store.pwd")
                        val keyAlias = properties.getProperty("sign.key.alias")
                        val keyPassword = properties.getProperty("sign.key.pwd")
                        if (storePassword != null && keyPassword != null && keyAlias != null) {
                            val signFile =
                                File("${project.rootProject.projectDir.absolutePath}/build-logic/chitanda")
                            create("chitanda") {
                                this.storeFile = signFile
                                this.storePassword = storePassword
                                this.keyAlias = keyAlias
                                this.keyPassword = keyPassword
                            }
                            logger.quiet("add sign file $signFile is ${signFile.exists()}")
                        }
                    } else {
                        logger.error("please add sign config in local.properties")
                    }
                }

            }

        }
    }
}