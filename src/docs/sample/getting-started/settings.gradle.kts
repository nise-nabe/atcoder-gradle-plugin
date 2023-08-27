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
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}


rootProject.name = "getting-started"
