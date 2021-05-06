plugins {
    `kotlin-dsl`
    `maven-publish`
    id("org.asciidoctor.jvm.convert") version "3.1.0"
}

repositories {
    gradlePluginPortal()
}

group = "com.nisecoder.gradle"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")

    implementation("it.skrape:skrapeit:1.1.1")

    implementation(platform("io.ktor:ktor-bom:1.5.4"))
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-logging")
    implementation("ch.qos.logback:logback-classic")

    implementation("nu.studer:gradle-credentials-plugin:2.1")

    testImplementation(gradleTestKit())
    testImplementation(kotlin("test-junit5"))
    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

publishing {
    repositories {
        maven {
            name = "gitHubPackages"
            url = uri("https://maven.pkg.github.com/nise-nabe/atcoder-gradle-plugin")
            credentials(PasswordCredentials::class)
        }
    }

    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

tasks.asciidoctor {
    baseDirFollowsSourceFile()
}
