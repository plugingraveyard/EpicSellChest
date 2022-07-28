plugins {
    java
    idea

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.badbones69.epicsellchest"
version = "1.3.5-${System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"}"
description = "This allows users to sell all the items in a chest."

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

repositories {

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    // NBT API
    maven("https://repo.codemc.org/repository/maven-public/")

    // Spartan API
    maven("https://nexus.sparky.ac/repository/Sparky/")

    // Our Repo
    maven("https://repo.badbones69.com/releases/")

    // Vault API
    maven("https://jitpack.io/")

    // Triumph Team
    maven("https://repo.triumphteam.dev/snapshots/")

    mavenCentral()
}

dependencies {

    // Command API
    implementation("dev.triumphteam:triumph-cmd-bukkit:2.0.0-SNAPSHOT")

    // Anti Cheats.
    compileOnly("me.vagdedes:SpartanAPI:9.1")

    // Required.
    implementation("de.tr7zw:nbt-data-api:2.10.0")

    implementation("org.bstats:bstats-bukkit:3.0.0")

    compileOnly("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT")
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-[1.8-1.19]-${rootProject.version}.jar")

        listOf(
            "de.tr7zw",
            "org.bstats",
            "dev.triumphteam.cmd"
        ).forEach {
            relocate(it, "${rootProject.group}.plugin.lib.$it")
        }
    }

    compileJava {
        options.release.set(17)
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to rootProject.group,
                "version" to rootProject.version,
                "description" to rootProject.description
            )
        }
    }
}