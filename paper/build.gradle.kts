plugins {
    `java-library`
    alias(libs.plugins.lombok)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Provided by server
    compileOnly(libs.paper)

    // Needed at runtime → shade into jar
    implementation(libs.guice)
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.bstats:bstats-bukkit:3.0.2")
}

// Shadow config to relocate shaded deps
tasks.shadowJar {
    relocate("com.google.gson", "de.rexlmanu.fairychat.libs.gson")
    relocate("org.bstats", "de.rexlmanu.fairychat.libs.bstats")
    relocate("com.google.inject", "de.rexlmanu.fairychat.libs.guice")
}
