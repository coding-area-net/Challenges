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
