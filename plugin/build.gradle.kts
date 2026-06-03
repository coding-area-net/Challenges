plugins {
  `java-library`
  id("com.gradleup.shadow")
}

dependencies {
  implementation(libs.slf4j.api)

  compileOnly(libs.spigot.api)
  compileOnly(libs.authlib)

  compileOnly(libs.jetbrains.annotations)
  compileOnly(libs.lombok)

  // gson is already bundled by mojang, accessing it saves on artifact size but might cause compatibilty issues
  // consider bundling relocated impl if version compatibility becomes unmanageable
  compileOnly(libs.gson)

  annotationProcessor(libs.lombok)

  // bundle cloud impl modules
  // TODO abstract to register dynamically
  implementation(project(":cloud-support:api"))
  implementation(project(":cloud-support:cloudnet2"))
  implementation(project(":cloud-support:cloudnet3"))
}

tasks {

  processResources {
    from("../language") {
      into("language")
      include("**/*.json")
    }
  }

  jar {
    archiveClassifier = "plain"
  }

  shadowJar {
    archiveBaseName = "Challenges"
    archiveClassifier = ""
    archiveVersion = ""

    dependencies {
      // TODO adopted from maven shade configuration, check necessity
      include(dependency("net.kyori:adventure-api"))

      // TODO abstract to register dynamically
      include(project(":cloud-support:api"))
      include(project(":cloud-support:cloudnet2"))
      include(project(":cloud-support:cloudnet3"))
    }
  }

  build {
    dependsOn(shadowJar)
  }
}

val localBuild = file("local.gradle.kts")
if (localBuild.exists()) {
  apply(from = localBuild)
}
