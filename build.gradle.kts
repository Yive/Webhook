import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  application
  id("com.gradleup.shadow") version "9.0.0-beta13"
}

group = "dev.yive"
version = "2.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "5.0.0"
val junitJupiterVersion = "5.9.1"

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
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName, "Implementation-Version" to version))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf(mainVerticleName)
}
