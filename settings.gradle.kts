rootProject.name = "Kotlin DynamoDB Mapper"

for(file in rootDir.listFiles()) {
    if (file.isDirectory && File(file, "build.gradle.kts").exists())
    include(file.name)
}