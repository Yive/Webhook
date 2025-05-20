plugins {
    `java-library`
    application
    id("io.freefair.lombok") version "8.13.1"
    id("com.gradleup.shadow") version "9.0.0-beta13"
}

group = "dev.yive.webhook"
version = "1.1.21"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:6.6.0")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.3")
    implementation("com.jakewharton.fliptables:fliptables:1.1.1")
    implementation("org.apache.commons:commons-lang3:3.17.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    shadowJar {
        archiveFileName = "webhook.jar"
    }

    application {
        mainClass.set("dev.yive.webhook.Main")
    }
}