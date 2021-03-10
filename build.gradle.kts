plugins {
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    gradlePluginPortal()
}

group = "com.nisecoder.gradle"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/nise-nabe/atcoder-gradle-plugin")
        }
    }

    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
