plugins {
  id("java-library")
  alias(libs.plugins.shadow)
}

dependencies {
  compileOnly(libs.spigot.api)

  implementation(project(":platform:common"))

  // in paper
  // - since 1.16.5: adventure-api is bundled (but not minimessage)
  // - since 1.18.2: minimessage is bundled in paper
  // - since 1.20.5: paper migrated internal systems to minimessages, mojang rewrote internals
  //                 breaking shaded impl
  implementation(libs.adventure.text.minimessage)
  // has been discontinued, but avilable implementations for legacy versions are still available and functional
  // we only use it for legacy support making this a practical solution
  implementation(libs.adventure.platform.bukkit)

  // not available by default
  implementation(libs.adventure.text.serializer.legacy)

  // NOTE: shadow(..) does somehow not work, requiring exlusion of net.kyori.* when importing to prevent Gradle
  //       from bundling it twice (once relocated and once default), just using implementation for now
}

tasks {

  jar {
    archiveClassifier = "plain"
  }

  shadowJar {
    archiveClassifier = ""

    // relocate adventure lib (inclduing legacy bukkit platform) for compatibilty with legacy platforms
    // including spigot and paper pre-1.18.2
    // - if adventure lib is fully bundled natively (e.g. modern paper version) it will use the available api
    //   as the shadowed adventure lib version (might) be incompatible with newer minecraft versions
    //   like post-1.20.5 as the component system was overhauled
    // (provided and used by :platform:legacy)
    relocate("net.kyori", "net.codingarea.challenges.relocate")

    // force Gradle to relocate common module for compatibility with relocated adventure lib.
    // hacky & ugly fix but necessary for compatibility with older versions and prevents writing/maintating the same code twice.
    relocate("net.codingarea.challenges.platform.message.common", "net.codingarea.challenges.platform.message.legacy")
  }

}

// make Gradle expose the shadowed JAR to other projects by default
configurations {
  apiElements {
    outgoing.artifacts.clear()
    outgoing.artifact(tasks.shadowJar)
  }
  runtimeElements {
    outgoing.artifacts.clear()
    outgoing.artifact(tasks.shadowJar)
  }
}
