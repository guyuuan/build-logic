plugins{
    `kotlin-dsl`
}
group = "cn.chitanda.app.imovie.buildlogic"
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin{
    plugins{
        register("androidAppCompose"){
            id = "chitanda.android.app.compose"
            implementationClass = "AndroidAppComposeConventionPlugin"
        }
        register("androidApp"){
            id = "chitanda.android.app"
            implementationClass = "AndroidAppConventionPlugin"
        }
        register("androidLibCompose"){
            id = "chitanda.android.lib.compose"
            implementationClass = "AndroidLibComposeConventionPlugin"
        }
        register("androidLib"){
            id = "chitanda.android.lib"
            implementationClass = "AndroidLibConventionPlugin"
        }
        register("androidFeature"){
            id = "chitanda.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidHilt"){
            id= "chitanda.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidTest"){
            id = "chitanda.android.test"
            implementationClass = "AndroidTestConventionPlugin"
        }

    }
}