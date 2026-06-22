plugins {
  id("java-library")
}

dependencies {
  api(project(":platform:api"))

  compileOnly(libs.spigot.api)

  // safe to use here, as it will either be used as the reloaced or native variant (this module will get duplicated)
  // but it would NOT be safe to access from the main "plugin" module
  compileOnly(libs.adventure.text.minimessage)
  compileOnly(libs.adventure.text.serializer.legacy)

  compileOnly(libs.lombok)

  annotationProcessor(libs.lombok)
}

tasks {
  jar {
    archiveClassifier = "plain"
  }
}
