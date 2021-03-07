plugins {
    val kotlinVersion = "1.4.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("java")

    id("net.mamoe.mirai-console") version "2.4.2" // mirai-console version
}

group = "org.example"
version = "0.1.0"
java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")

    maven {
        setUrl("https://jitpack.io")
    }
    mavenCentral()

    //flatDir {dirs("libs")}
}

tasks.compileJava{
    options.isIncremental = true
    options.isFork = false
    options.isFailOnError = false
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.3")
    //compile("project:rkon-core:1.1.2")
    implementation ( "com.github.Glavo:rcon-java:2.0.1")
}

