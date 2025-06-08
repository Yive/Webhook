import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  java
  application
  id("com.gradleup.shadow") version "9.0.0-beta15"
}

group = "dev.yive"
version = "2.0.1"

repositories {
  mavenCentral()
}

val vertxVersion = "5.0.0"

val mainVerticleName = "dev.yive.webhook.MainVerticle"
val launcherClassName = "io.vertx.launcher.application.VertxApplication"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-launcher-application")
  implementation("io.vertx:vertx-web-client")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-config")
  implementation("io.vertx:vertx-config-yaml")
  implementation("com.jakewharton.fliptables:fliptables:1.1.1")
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<ShadowJar> {
  archiveFileName.set("webhook.jar")
  archiveClassifier.set("")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName, "Implementation-Version" to version))
  }
  mergeServiceFiles()
}

tasks.withType<JavaExec> {
  args = listOf(mainVerticleName)
}
