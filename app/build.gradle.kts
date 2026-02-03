dependencies {
    implementation(project(":core"))
    implementation(project(":payment"))
    implementation(project(":funding"))
    implementation(project(":settlement"))
    implementation(project(":search"))
    implementation(project(":infra"))
    implementation(project(":common"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

springBoot {
    mainClass.set("com.giftify.app.GiftifyApplication")
}