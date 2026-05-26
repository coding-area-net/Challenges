plugins {
  `java-library`
}

dependencies {
  compileOnly(libs.spigot.api)
  compileOnly(libs.cloudnet3.driver)
  compileOnly(libs.cloudnet3.bridge)
  compileOnly(project(":cloud-support:api"))
}

tasks {
  jar {
    archiveClassifier = "plain"
  }
}
