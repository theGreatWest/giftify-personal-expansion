dependencies {
    implementation(project(":backend:common"))
    implementation(project(":backend:core"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")    // dev, prod
    runtimeOnly("com.h2database:h2")            // local
}