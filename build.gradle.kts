plugins {
    `java-gradle-plugin`
    `kotlin-dsl-base`
    `maven-publish`
    idea
    id("com.gradle.plugin-publish")
    id("org.jlleitschuh.gradle.ktlint")
}

repositories {
    gradlePluginPortal()
}

group = "com.nisecoder.gradle"

// inject in GitHub Action Publish Workflow
val publishVersion: String? by project
version =
    if (publishVersion?.isNotEmpty() == true) {
        publishVersion!!.replaceFirst("refs/tags/v", "")
    } else {
        "1.0-SNAPSHOT"
    }

dependencies {
    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlin.plugin)

    implementation(libs.skrapeit)

    implementation(platform(libs.ktor.bom))
    implementation(libs.ktor.clientCore)
    implementation(libs.ktor.clientCio)
    implementation(libs.ktor.clientLogging)
    implementation(libs.logback.classic)

    testImplementation(kotlin("test-junit5", libs.versions.kotlin.get()))
}

testing {
    suites {
        @Suppress("UnstableApiUsage")
        withType<JvmTestSuite> {
            useJUnitJupiter("5.10.2")

            dependencies {
                implementation(gradleTestKit())
            }
        }
    }
}

gradlePlugin {
    website.set("https://nise-nabe.github.io/atcoder-gradle-plugin/")
    vcsUrl.set("https://github.com/nise-nabe/atcoder-gradle-plugin")

    description = "AtCoder tool for Gradle"
    plugins {
        create("AtCoderPlugin") {
            id = "com.nisecoder.gradle.atcoder"
            displayName = "AtCoder Gradle plugin"
            implementationClass = "com.nisecoder.gradle.AtCoderPlugin"
            tags.set(listOf("atcoder"))
        }
        create("AtCoderContestPlugin") {
            id = "com.nisecoder.gradle.atcoder.contest"
            displayName = "Base Convention plugin for AtCoder Gradle plugin"
            implementationClass = "com.nisecoder.gradle.atcoder.AtCoderContestPlugin"
            tags.set(listOf("atcoder"))
        }
        create("AtCoderContestKotlinPlugin") {
            id = "com.nisecoder.gradle.atcoder.contest.kotlin"
            displayName = "Kotlin Language Convention plugin for AtCoder Gradle plugin"
            implementationClass = "com.nisecoder.gradle.atcoder.language.AtCoderContestKotlinPlugin"
            tags.set(listOf("atcoder"))
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "gitHubPackages"
            url = uri("https://maven.pkg.github.com/nise-nabe/atcoder-gradle-plugin")
            credentials(PasswordCredentials::class)
        }
    }
}
