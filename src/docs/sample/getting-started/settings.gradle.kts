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
                includeGroupByRegex("com.nisecoder.*")
            }
        }
    }
}

plugins {
    id("com.nisecoder.gradle.atcoder.auto-include")
}


rootProject.name = "getting-started"
