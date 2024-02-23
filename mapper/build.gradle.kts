import java.net.URI
import java.time.LocalDateTime

plugins {
    `maven-publish`
    signing
}

val awsKotlinSdkVersion = "1.0.62"
val junit5Version = "5.10.2"
val kotlinCoroutineVersion = "1.7.3"

tasks.jar {
    manifest {
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Vendor"] = "kncept"
        attributes["Implementation-Version"] = project.version
        attributes["Implementation-Author"] = "Nicholas Krul" //non standard
        attributes["Created-By"] = System.getProperty("java.version")
        attributes["Built-By"] = System.getProperty("user.name")
        attributes["Built-Date"] = LocalDateTime.now().toString()
//        attributes["Source-Compatibility"] = project.parent.sourceCompatibility
//                attributes["Target-Compatibility': project.targetCompatibility,
        attributes["Build-Hash"] = getCheckedOutGitCommitHash()
        // 'Main-Class': 'none'
    }
}

// eg: https://gist.github.com/MRezaNasirloo/ccfdb24f10ebefee0d871d4e84b37309
fun getCheckedOutGitCommitHash(): String {
    val gitFolder = "$rootDir/.git/"
    val head = File(gitFolder + "HEAD").readText().split(":") // .git/HEAD
    val isCommit = head.size == 1 // e5a7c79edabbf7dd39888442df081b1c9d8e88fd
    // def isRef = head.length > 1     // ref: refs/heads/master
    if(isCommit) return head[0].trim()
    val refHead = File(gitFolder + head[1].trim()) // .git/refs/heads/master
    return refHead.readText().trim()
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

publishing {
    publications {
        create<MavenPublication>("kotlinMapper") {
            from(components["java"])

            pom {
                name = "Kncept Ddb Mapper"
                description = "Kotlin Data Class Mapper for AWS Dynamo DB"
                url = "https://github.com/kncept/kotlin-dynamodb-mapper"
                properties = mapOf(
                    "corporate.website" to "https://www.kncept.com",
                )
                licenses {
                    license {
                        name = "MIT"
                        url = "https://github.com/kncept/kotlin-dynamodb-mapper/blob/main/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "nkrul"
                        name = "Nicholas Krul"
                        email = "nicholas.krul@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/kncept/kotlin-dynamodb-mapper.git"
                    developerConnection = "scm:git:ssh://github.com/kncept/kotlin-dynamodb-mapper.git"
                    url = "https://github.com/kncept/kotlin-dynamodb-mapper"
                }
            }

        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = URI("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.properties.get("ossrhUsername") as String? ?: ""
                password = project.properties.get("ossrhPassword") as String? ?: ""
            }
        }
    }
}

signing {
    sign(publishing.publications["kotlinMapper"])
}

tasks.withType<Sign> {
    onlyIf("sign if gradle.properties exists") { File(rootDir, "gradle.properties").exists() }
}
tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}