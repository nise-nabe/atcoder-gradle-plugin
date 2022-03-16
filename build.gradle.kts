plugins {
    `java-gradle-plugin`
    `kotlin-dsl-base`
    `maven-publish`
    idea
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("com.gradle.plugin-publish") version "0.19.0"
}

repositories {
    gradlePluginPortal()
}

group = "com.nisecoder.gradle"

// inject in GitHub Action Publish Workflow
val publishVersion: String? by project
version = if (publishVersion?.isNotEmpty() == true) {
    publishVersion!!.replaceFirst("refs/tags/v", "")
} else {
    "1.0-SNAPSHOT"
}


dependencies {
    // https://docs.gradle.org/current/userguide/compatibility.html#kotlin
    implementation(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom:${embeddedKotlinVersion}"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")

    // https://github.com/skrapeit/skrape.it
    implementation("it.skrape:skrapeit:1.2.1")

    // https://github.com/ktorio/ktor
    implementation(platform("io.ktor:ktor-bom:1.6.8"))
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-logging")
    implementation("ch.qos.logback:logback-classic")

    testImplementation(gradleTestKit())
    testImplementation(kotlin("test-junit5"))

    // https://github.com/junit-team/junit5
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}



gradlePlugin {
    plugins {
        create("AtCoderPlugin") {
            id = "com.nisecoder.gradle.atcoder"
            implementationClass = "com.nisecoder.gradle.AtCoderPlugin"
        }
        create("AtCoderContestPlugin") {
            id = "com.nisecoder.gradle.atcoder.contest"
            implementationClass = "com.nisecoder.gradle.atcoder.AtCoderContestPlugin"
        }
        create("AtCoderContestKotlinPlugin") {
            id = "com.nisecoder.gradle.atcoder.contest.kotlin"
            implementationClass = "com.nisecoder.gradle.atcoder.language.AtCoderContestKotlinPlugin"
        }
        create("AtCoderSettingsPlugin") {
            id = "com.nisecoder.gradle.atcoder.auto-detect"
            implementationClass = "com.nisecoder.gradle.atcoder.AtCoderSettingsPlugin"
        }
    }
}


java {
    @Suppress("UnstableApiUsage")
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

tasks.withType<GenerateModuleMetadata> {
    @Suppress("UnstableApiUsage")
    suppressedValidationErrors.add("enforced-platform")
}

pluginBundle {
    website = "https://nise-nabe.github.io/atcoder-gradle-plugin/"
    vcsUrl = "https://github.com/nise-nabe/atcoder-gradle-plugin"
    tags = listOf("atcoder")

    description = "AtCoder tool for Gradle"

    (plugins) {
        "AtCoderPlugin" {
            displayName = "AtCoder Gradle plugin"
        }
        "AtCoderContestPlugin" {
            displayName = "Base Convention plugin for AtCoder Gradle plugin"
        }
        "AtCoderContestKotlinPlugin" {
            displayName = "Kotlin Language Convention plugin for AtCoder Gradle plugin"
        }
        "AtCoderSettingsPlugin" {
            displayName = "Settings Convention plugins for AtCoder Gradle plugin"
        }
    }
}

tasks.asciidoctor {
    baseDirFollowsSourceFile()
}
