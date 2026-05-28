plugins {
  base
  id("com.gradleup.shadow") version "8.3.11" apply false
}

allprojects {
  repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://libraries.minecraft.net/")
    maven("https://jitpack.io")
    // legacy cloudnet 3 repository, officially "https://repo.cloudnetservice.eu/repository/releases/"
    maven("https://repo.cloudnetservice.eu/releases/")
  }
}

subprojects {
  apply(plugin = "java-library")

  extensions.configure<JavaPluginExtension>("java") {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
    withSourcesJar()
  }

  tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
  }

  tasks.named<ProcessResources>("processResources") {
    filteringCharset = "UTF-8"
    val tokens = mapOf("version" to project.version.toString())
    filesMatching(listOf("plugin.yml", "config.yml")) {
      expand(tokens)
    }
    from(rootProject.file("LICENSE"))
  }
}
