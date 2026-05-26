plugins {
  `java-library`
  id("com.gradleup.shadow")
}

dependencies {
  implementation(libs.slf4j.api)
  implementation(libs.jsr305)

  compileOnly(libs.spigot.api)
  compileOnly(libs.authlib)

  compileOnly(libs.lombok)

  // gson is already bundled by mojang, accessing it saves on artifact size but might cause compatibilty issues
  // consider bundling relocated impl if version compatibility becomes unmanageable
  compileOnly(libs.gson)

  compileOnly(libs.cloudnet3.driver)
  compileOnly(libs.cloudnet3.bridge)
  compileOnly(libs.cloudnet2.bridge)

  annotationProcessor(libs.lombok)
}

tasks {
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
    }
  }

  build {
    dependsOn(shadowJar)
  }
}
