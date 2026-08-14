plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    id("com.gradleup.shadow") version "9.4.3"
    id("io.freefair.lombok") version "9.5.0"
    `maven-publish`
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    maven { url = uri("https://maven.pvphub.me/tofaa") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.extendedclip.com/releases/") }
    maven { url = uri("https://repo.xenondevs.xyz/releases") }
}

dependencies {
    paperweight.paperDevBundle("26.2.build.+")
    compileOnly("com.github.retrooper:packetevents-spigot:2.13.0")
    compileOnly("io.github.alexdev03:unlimitednametags-api-paper:2.0.0")
    compileOnly("io.github.tofaa2:spigot:3.0.3-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.12.3")
    implementation("com.jeff-media:MorePersistentDataTypes:2.4.0")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.15.7")
    implementation("xyz.xenondevs.invui:invui:2.3.0")
    implementation("org.xerial:sqlite-jdbc:3.53.2.1")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    shadowJar {
        relocate("com.jeff_media.morepersistentdatatypes", "pro.fazeclan.river.jarona.morepersistentdatatypes")
        relocate("xyz.xenondevs.invui", "pro.fazeclan.river.jarona.invui")
        relocate("org.sqlite", "pro.fazeclan.river.jarona.sqlite")
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}
