pluginManagement {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/nise-nabe/atcoder-gradle-plugin")
            mavenContent {
                includeGroup("com.nisecoder.gradle")
            }
        }
        gradlePluginPortal()

    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.nisecoder.gradle.atcoder")) {
                useModule("com.nisecoder.gradle:atcoder-gradle-plugin:1.0-SNAPSHOT")
            }
        }
    }
}

rootProject.name = "getting-started"
