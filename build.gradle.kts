plugins {
    kotlin("jvm") version "1.9.0"
    id("com.ncorti.ktfmt.gradle") version "0.17.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks.wrapper { gradleVersion = "8.6" }

val awsKotlinSdkVersion = "1.0.62"
val junit5Version = "5.10.2"
val kotlinCoroutineVersion = "1.7.3"

group = "com.kncept.ddb.mapper"
version = calcversion()
fun calcversion(): String {
    var vers = "0.0.0-SNAPSHOT"
    val grn = System.getenv("GITHUB_REF_NAME")
    if (grn != null && !grn.trim().equals("")) {
        if (grn.startsWith("v")) {
            vers = grn.substring(1);
        }
    }
    return vers
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.ncorti.ktfmt.gradle")
    group = parent!!.group
    version = parent!!.version

    tasks.withType<Test> {
        useJUnitPlatform()
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    }

    java {
        sourceCompatibility = parent!!.java.sourceCompatibility
    }

    repositories {
        mavenLocal()
        mavenCentral()
    }

}
repositories {
    mavenLocal()
    mavenCentral()
}