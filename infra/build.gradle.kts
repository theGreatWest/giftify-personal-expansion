dependencies {
    implementation(project(":common"))
    implementation(project(":core"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")    // dev, prod
    runtimeOnly("com.h2database:h2")            // local
}