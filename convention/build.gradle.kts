plugins {
    `kotlin-dsl`
}
group = "cn.chitanda.gradle.build-logic"
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(kotlin("gradle-plugin-api"))
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        with(libs.plugins.chitanda.android) {
            create("androidAppCompose") {
                id = app.compose.get().pluginId
                implementationClass = "cn.chitanda.gradle.plugin.AndroidAppComposeConventionPlugin"
            }
            register("androidApp") {
                id = app.asProvider().get().pluginId
                implementationClass = "cn.chitanda.gradle.plugin.AndroidAppConventionPlugin"
            }
            register("androidLibCompose") {
                id = lib.compose.get().pluginId
                implementationClass = "cn.chitanda.gradle.plugin.AndroidLibComposeConventionPlugin"
            }
            register("androidLib") {
                id = lib.asProvider().get().pluginId
                implementationClass = "cn.chitanda.gradle.plugin.AndroidLibConventionPlugin"
            }
            register("androidFeature") {
                id = feature.get().pluginId
                implementationClass = "cn.chitanda.gradle.plugin.AndroidFeatureConventionPlugin"
            }
            register("androidHilt") {
                id = hilt.get().pluginId
                implementationClass = "cn.chitanda.gradle.plugin.AndroidHiltConventionPlugin"
            }
            register("androidTest") {
                id = test.get().pluginId
                implementationClass = "cn.chitanda.gradle.plugin.AndroidTestConventionPlugin"
            }
        }

    }
}