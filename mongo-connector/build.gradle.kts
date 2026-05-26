plugins {
  `java-library`
  id("com.gradleup.shadow")
}

dependencies {
  implementation(libs.slf4j.api)
  implementation(libs.mongodb.driver)
  implementation(libs.jsr305)

  compileOnly(libs.spigot.api)
  compileOnly(libs.lombok)

  implementation(project(":plugin"))

  annotationProcessor(libs.lombok)
}

tasks {
  jar {
    archiveClassifier = "plain"
  }

  shadowJar {
    archiveBaseName = "Challenges-MongoConnector"
    archiveClassifier = ""
    archiveVersion = ""

    dependencies {
      include(dependency("org.mongodb:mongodb-driver"))
      include(dependency("org.mongodb:mongodb-driver-core"))
      include(dependency("org.mongodb:bson"))
    }
  }

  build {
    dependsOn(shadowJar)
  }
}




