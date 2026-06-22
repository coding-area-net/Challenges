import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
  id("java-library")
  alias(libs.plugins.shadow)
  alias(libs.plugins.jvmdg)
}


// hacky patch; to ensure compatibilty with older minecraft versions and newer dependencies and tools which
// require latest java, we compile against latest java versions and use a hacky plugin to downgrade the bytecode to older java,
// so it can run on older versions.
// this is inherently unsafe and could produce runtime errors that would noramlly be cauth at compile time,
// but it's a practical and working solution.
// NOTE: it does NOT map any bukkit api calls / changes, ONLY java version compatibility.
//       it is also not produced by default build/shadowJar.
// DO NOT use the downgraded jar for production servers running latest java/minecraft.
jvmdg {
  downgradeTo = JavaVersion.VERSION_16
}

repositories {
  maven("https://maven.wagyourtail.xyz/releases") // jvmdg
}

dependencies {
  compileOnly(libs.slf4j.api)

  compileOnly(libs.spigot.api)
  compileOnly(libs.authlib)

  compileOnly(libs.jetbrains.annotations)
  compileOnly(libs.lombok)

  // gson is already bundled by mojang, accessing it saves on artifact size but might cause compatibilty issues
  // consider bundling relocated impl if version compatibility becomes unmanageable
  compileOnly(libs.gson)

  annotationProcessor(libs.lombok)

  api(project(":cloud-support:api"))

  // TODO abstract to register dynamically
  implementation(project(":cloud-support:cloudnet2"))
  implementation(project(":cloud-support:cloudnet3"))

  api(project(":platform:api"))
  implementation(project(":platform:common"))
  implementation(project(":platform:paper"))
  implementation(project(":platform:legacy")) {
    exclude("net.kyori") // see platform/legacy/build.gradle.kts
  }
}
tasks {

  processResources {
    from("../language") {
      into("language")
      include("**/*.json")
    }

    // minify bundled json resources: doLast so we only modify the files AFTER they have been copied
    // to the build/resources directory, leaving original source files intact.
    doLast {
      fileTree(destinationDir).matching {
        include("**/*.json")
      }.forEach { file ->
        try {
          val parsedJson = JsonSlurper().parse(file)
          val minifiedJson = JsonOutput.toJson(parsedJson)
          file.writeText(minifiedJson)
        } catch (e: Exception) {
          logger.warn("Failed to minify JSON file: ${file.name}", e)
        }
      }
    }
  }

  jar {
    archiveClassifier = "plain"
  }

  shadowJar {
    archiveBaseName = "Challenges"
    archiveClassifier = ""
    archiveVersion = ""

    // no explicit dependencies block - bundle "implementation" deps by default
    // relocated adventure-lib in :platform:legacy
  }

  build {
    dependsOn(shadowJar)
  }

  downgradeJar {
    dependsOn(shadowJar)
  }
}

val localBuild = file("local.gradle.kts")
if (localBuild.exists()) {
  apply(from = localBuild)
}
