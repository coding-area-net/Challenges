plugins {
  id("java-library")
}

dependencies {
  compileOnly(libs.paper.api)

  implementation(project(":platform:common"))
}

repositories {
  maven("https://repo.papermc.io/repository/maven-public/")
}

tasks {
  jar {
    archiveClassifier = "plain"
  }
}
