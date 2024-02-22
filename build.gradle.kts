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

tasks.withType<Test> {
    useJUnitPlatform()
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutineVersion")
    implementation(kotlin("reflect"))

    testImplementation("org.junit.jupiter:junit-jupiter:$junit5Version")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("aws.sdk.kotlin:aws-core:$awsKotlinSdkVersion")
    implementation("aws.sdk.kotlin:dynamodb:$awsKotlinSdkVersion")

}