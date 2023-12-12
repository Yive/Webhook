plugins {
    `java-library`
    application
    id("io.freefair.lombok") version "8.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.yive.webhook"
version = "1.1.14"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:6.0.0-beta.3")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2")
    implementation("com.jakewharton.fliptables:fliptables:1.1.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    shadowJar {
        archiveFileName = "webhook.jar"
    }

    application {
        mainClass.set("dev.yive.webhook.Main")
    }
}