pluginManagement {
    repositories {
        gradlePluginPortal()
        exclusiveContent {
            forRepository {
                maven {
                    name = "GithubPackagesAtCoderGradlePlugin"
                    url = uri("https://maven.pkg.github.com/nise-nabe/atcoder-gradle-plugin")
                    credentials(PasswordCredentials::class)
                }
            }
            filter {
                includeGroup("com.nisecoder.gradle")
            }
        }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.nisecoder.gradle.atcoder")) {
                useModule("com.nisecoder.gradle:atcoder-gradle-plugin:1.0-SNAPSHOT")
            }
        }
    }
}

plugins {
    id("com.nisecoder.gradle.atcoder.auto-include")
}


rootProject.name = "getting-started"
