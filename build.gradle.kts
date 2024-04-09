plugins {
    id("java")
//    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("org.jetbrains.intellij") version "1.15.0"
}

group = "org.exbin.utils.guipopup"
version = "0.1.4"

val ideLocalPath = providers.gradleProperty("ideLocalPath").getOrElse("")

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    type.set("IC") // Target IDE Platform
}

if (ideLocalPath.isEmpty()) {
    intellij {
        version.set("2023.2.1")
        plugins.set(listOf("java"))
    }
} else {
    intellij {
        localPath.set(ideLocalPath)
        // Some variants require to add java plugin due to detection bug
        plugins.set(listOf("java"))
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
//    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//        kotlinOptions.jvmTarget = "17"
//    }

    patchPluginXml {
        sinceBuild.set("232.1")
        untilBuild.set("")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

repositories {
    flatDir {
        dirs("lib")
    }
}

dependencies {
    compileOnly(":dependencies")
    compileOnly(":jsr305-2.0.1")
}
