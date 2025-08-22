import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    `java-library`
    alias(libs.plugins.lombok)
    alias(libs.plugins.runpaper)
    alias(libs.plugins.shadow)
    alias(libs.plugins.hangar)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.paperyml)
    alias(libs.plugins.pluginyml)
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        content {
            includeGroup("org.spigotmc")
            includeGroup("org.bukkit")
        }
    }
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.miopowered.eu/snapshots/")
}

dependencies {
    compileOnly(libs.spigot)

    // 🔹 Now shaded into the final jar
    implementation(libs.jedis)
    implementation(libs.commandframework)
    implementation(libs.configlib)
    implementation(libs.guice)
    implementation(libs.hikaridb)
    implementation(libs.stringsimilarity)
    implementation(libs.bundles.adventure)

    // 🔹 Plugin hooks only, not shaded
    compileOnly(libs.miniplaceholders)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.luckperms)
    compileOnly(libs.ultrapermissions)

    implementation(libs.bstats)
    implementation(projects.paper)
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    build {
        dependsOn("shadowJar")
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.19"
        )
        inputs.properties(props)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    jar {
        enabled = false
    }
    shadowJar {
        archiveClassifier.set("")
        from(file("LICENSE"))

        dependencies {
            exclude("META-INF/NOTICE")
            exclude("META-INF/maven/**")
            exclude("META-INF/versions/**")
            exclude("META-INF/**.kotlin_module")
        }

        // 🔹 Relocate shaded dependencies
        relocate("org.bstats", "de.rexlmanu.fairychat.dependencies.bstats")
        relocate("redis.clients.jedis", "de.rexlmanu.fairychat.dependencies.jedis")
        relocate("cloud.commandframework", "de.rexlmanu.fairychat.dependencies.commandframework")
        relocate("eu.okaeri.configs", "de.rexlmanu.fairychat.dependencies.configlib")
        relocate("com.google.inject", "de.rexlmanu.fairychat.dependencies.guice")
        relocate("com.zaxxer.hikari", "de.rexlmanu.fairychat.dependencies.hikaridb")
        relocate("info.debatty.java.stringsimilarity", "de.rexlmanu.fairychat.dependencies.stringsimilarity")
        relocate("net.kyori.adventure", "de.rexlmanu.fairychat.dependencies.adventure")
    }
    runServer {
        minecraftVersion("1.20.4")
    }
}

tasks.getByName("modrinth").dependsOn(tasks.modrinthSyncBody)

val versions = listOf("1.19.4", "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6", "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8");

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("fairychat")

    versionNumber.set(rootProject.version.toString())
    versionName.set("FairyChat ${rootProject.version}")
    versionType.set("release")

    syncBodyFrom.set(rootProject.file("README.md").readText())

    uploadFile.set(layout.buildDirectory.file("libs/FairyChat-${rootProject.version}.jar"))
    gameVersions.addAll(versions)
    loaders.addAll(listOf("paper", "purpur", "folia"))
    changelog.set(System.getenv("MODRINTH_CHANGELOG"))
    dependencies {
        optional.project("miniplaceholders")
        optional.project("luckperms")
    }
}

hangarPublish {
    publications.register("plugin") {
        version.set(project.version as String)
        id.set("fairychat")
        channel.set("Release")
        changelog.set(System.getenv("HANGAR_CHANGELOG"))
        apiKey.set(System.getenv("HANGAR_TOKEN"))

        platforms {
            register(Platforms.PAPER) {
                jar.set(layout.buildDirectory.file("libs/FairyChat-${rootProject.version}.jar"))
                platformVersions.set(versions)
                dependencies {
                    hangar("MiniPlaceholders") { required.set(false) }
                }
            }
        }
    }
}

bukkit {
    author = "rexlManu"
    main = "de.rexlmanu.fairychat.plugin.FairyChatPlugin"
    website = "https://github.com/rexlManu/FairyChat"
    foliaSupported = true
    apiVersion = "1.19"
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    softDepend = listOf("MiniPlaceholders", "LuckPerms", "PlaceholderAPI", "UltraPermissions")
    prefix = "FairyChat"
    generateLibrariesJson = false // 🔹 disable runtime maven fetching
}

paper {
    main = "de.rexlmanu.fairychat.plugin.FairyChatPlugin"
    apiVersion = "1.20"
    foliaSupported = true
    author = "rexlManu"
    website = "https://github.com/rexlManu/FairyChat"
    prefix = "FairyChat"
    serverDependencies {
        register("MiniPlaceholders") { required = false }
        register("LuckPerms") { required = false }
        register("PlaceholderAPI") { required = false }
        register("UltraPermissions") { required = false }
    }
    generateLibrariesJson = false // 🔹 no libraries.json
}
