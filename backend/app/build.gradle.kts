dependencies {
    // 프로젝트 모듈
    implementation(project(":backend:member"))
    implementation(project(":backend:auth"))
    implementation(project(":backend:product"))
    implementation(project(":backend:funding"))
    implementation(project(":backend:order"))
    implementation(project(":backend:payment"))
    implementation(project(":backend:notification"))
    implementation(project(":backend:search"))
    implementation(project(":backend:common"))

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // DB 드라이버
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    // 개발용
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Springdoc OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.7.0")
    implementation("org.springdoc:springdoc-openapi-starter-common:2.7.0")
}

springBoot {
    mainClass.set("com.giftify.app.GiftifyApplication")
}

tasks.bootRun {
    workingDir = rootProject.projectDir
}

tasks.named<ProcessResources>("processResources") {
    val frontendProject = findProject(":frontend")
    if (frontendProject != null) {
        dependsOn(":frontend:copyFrontendToApp")
    }
}