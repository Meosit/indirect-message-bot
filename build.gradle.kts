import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

application {
    mainClassName = "by.mksn.indimebot.MainKt"
}

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        val kotlinVersion = "1.4.30"
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath(kotlin("serialization", version = kotlinVersion))
    }
}

plugins {
    application
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.serialization") version "1.4.30"
    id("com.github.johnrengelman.shadow") version "4.0.4"
}
repositories {
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
}


group = "by.mksn.indimebot"
version = "0.0.1"

val serializationVersion = "1.1.0"
val ktorVersion = "1.4.3"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("io.ktor:ktor-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.postgresql:postgresql:42.2.1")
    implementation("com.zaxxer:HikariCP:3.4.5")
    implementation("com.vladsch.kotlin-jdbc:kotlin-jdbc:0.5.0")
    implementation("com.vladsch.kotlin-jdbc:kotlin-jdbc:0.5.0")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }

    named<ShadowJar>("shadowJar") {
        archiveFileName.set("IndiMeBot.jar")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "by.mksn.indimebot.MainKt"))
        }
    }

    register("stage") {
        dependsOn("shadowJar")
    }
}