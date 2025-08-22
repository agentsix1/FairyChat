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

    // Needed at runtime (shaded by root build)
    implementation(libs.guice)
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.bstats:bstats-bukkit:3.0.2")
}
