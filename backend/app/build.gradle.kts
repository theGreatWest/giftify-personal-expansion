dependencies {
    implementation(project(":backend:core"))
    implementation(project(":backend:payment"))
    implementation(project(":backend:funding"))
    implementation(project(":backend:settlement"))
    implementation(project(":backend:search"))
    implementation(project(":backend:infra"))
    implementation(project(":backend:common"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
}

springBoot {
    mainClass.set("com.giftify.app.GiftifyApplication")
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(":frontend:copyFrontendToApp")
}