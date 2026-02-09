dependencies {
    implementation(project(":backend:common"))
    
    // Spring
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")
    implementation("org.springframework:spring-web")
    implementation("org.springframework.data:spring-data-jpa")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("org.slf4j:slf4j-api")

    // JPA API (Jakarta Persistence)
    implementation("jakarta.persistence:jakarta.persistence-api")
    
    // Utils
    implementation("jakarta.validation:jakarta.validation-api")

    testImplementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.hibernate.validator:hibernate-validator")
}
