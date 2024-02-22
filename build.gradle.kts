plugins {
    kotlin("jvm") version "1.9.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks.wrapper { gradleVersion = "8.6" }


tasks.withType<Test> {
    useJUnitPlatform()
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:")

    implementation("aws.sdk.kotlin:aws-core:1.0.61")
    implementation("aws.sdk.kotlin:dynamodb:1.0.61")


}