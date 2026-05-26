plugins {
  `java-library`
}

dependencies {
  compileOnly(libs.spigot.api)
  compileOnly(libs.cloudnet2.bridge)
  compileOnly(project(":plugin"))
}

tasks {
  jar {
    archiveClassifier = "plain"
  }
}
