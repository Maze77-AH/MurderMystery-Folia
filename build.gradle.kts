plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta5"
    id("signing")
    `maven-publish`
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven(uri("https://maven.plugily.xyz/releases"))
    maven(uri("https://maven.plugily.xyz/snapshots"))
    maven(uri("https://repo.maven.apache.org/maven2/"))
    maven("https://repo.aikar.co/nexus/content/repositories/aikar/")

}


dependencies {
    implementation("plugily.projects:MiniGamesBox-Classic:1.3.16-SNAPSHOT1") { isTransitive = false }
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("dev.folia:folia-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly(files("lib/CorpseReborn.jar"))
}


group = "plugily.projects"
version = "2.1.1"
description = "MurderMystery"

java {
    withJavadocJar()
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("plugily.projects.minigamesbox", "plugily.projects.murdermystery.minigamesbox")
        relocate("com.zaxxer.hikari", "plugily.projects.murdermystery.database.hikari")
        minimize()
    }

    processResources {
        filesMatching("**/plugin.yml") {
            expand(project.properties)
        }
    }

    javadoc {
        options.encoding = "UTF-8"
    }

}

tasks.named("runServer") {
    // If you want to use run-paper's folia registration:
    runPaper.folia.registerTask()
}

publishing {
    repositories {
        maven {
            name = "Releases"
            url = uri("https://maven.plugily.xyz/releases")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
        maven {
            name = "Snapshots"
            url = uri("https://maven.plugily.xyz/snapshots")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
