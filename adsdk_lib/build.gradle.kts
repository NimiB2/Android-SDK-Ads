plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "dev.nimrod.adsdk_lib"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "dev.nimrod"
                artifactId = "adsdk-lib"
                version = "1.0.0"
                artifact(tasks.getByName("bundleReleaseAar"))

                pom {
                    withXml {
                        val dependenciesNode = asNode().appendNode("dependencies")

                        // Add both api and implementation dependencies
                        configurations.api.get().dependencies.forEach { dependency ->
                            if (dependency.name != "unspecified") {
                                val dependencyNode = dependenciesNode.appendNode("dependency")
                                dependencyNode.appendNode("groupId", dependency.group)
                                dependencyNode.appendNode("artifactId", dependency.name)
                                dependencyNode.appendNode("version", dependency.version)
                                dependencyNode.appendNode("scope", "compile")
                            }
                        }

                        configurations.implementation.get().dependencies.forEach { dependency ->
                            if (dependency.name != "unspecified") {
                                val dependencyNode = dependenciesNode.appendNode("dependency")
                                dependencyNode.appendNode("groupId", dependency.group)
                                dependencyNode.appendNode("artifactId", dependency.name)
                                dependencyNode.appendNode("version", dependency.version)
                                dependencyNode.appendNode("scope", "runtime")
                            }
                        }
                    }
                }
            }
        }
    }
}


dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Rest API calls
    api(libs.retrofit)
    api(libs.converter.gson)
}