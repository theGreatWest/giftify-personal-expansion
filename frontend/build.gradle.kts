plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    download.set(true)
    version.set("25.3.0")
}

val frontendDir = project.projectDir
val buildDir = layout.buildDirectory.get().asFile

tasks.register<com.github.gradle.node.npm.task.NpmTask>("npmBuild") {
    dependsOn("npmInstall")
    args.set(listOf("run", "build"))
    inputs.dir(frontendDir.resolve("src"))
    inputs.dir(frontendDir.resolve("public"))
    inputs.file(frontendDir.resolve("package.json"))
    inputs.file(frontendDir.resolve("package-lock.json"))
    inputs.file(frontendDir.resolve("vite.config.ts"))
    inputs.file(frontendDir.resolve("tsconfig.json"))
    inputs.file(frontendDir.resolve("index.html"))
    outputs.dir(frontendDir.resolve("dist"))
}

tasks.register<Copy>("copyFrontendToApp") {
    dependsOn("npmBuild")
    from(frontendDir.resolve("dist"))
    into(project(":app").projectDir.resolve("src/main/resources/static"))
}

tasks.named("build") {
    dependsOn("copyFrontendToApp")
}

tasks.named("clean") {
    doFirst {
        delete(project(":app").projectDir.resolve("src/main/resources/static"))
    }
}
