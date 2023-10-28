plugins {
    `java-gradle-plugin`
    `kotlin-dsl-base`
    `maven-publish`
    idea
    id("org.asciidoctor.jvm.convert")
    id("com.gradle.plugin-publish")
    id("org.jlleitschuh.gradle.ktlint")
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
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:$embeddedKotlinVersion"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")

    // https://github.com/skrapeit/skrape.it
    implementation("it.skrape:skrapeit:1.3.0-alpha.1")

    // https://github.com/ktorio/ktor
    implementation(platform("io.ktor:ktor-bom:2.3.5"))
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-logging")
    implementation("ch.qos.logback:logback-classic")

    testImplementation(gradleTestKit())
    testImplementation(kotlin("test-junit5"))

    // https://github.com/junit-team/junit5
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
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

tasks.asciidoctor {
    baseDirFollowsSourceFile()
}
