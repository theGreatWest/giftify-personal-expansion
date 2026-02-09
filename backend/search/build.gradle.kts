dependencies {
    implementation(project(":backend:common"))
    implementation(project(":backend:member"))

    // Spring Boot Starter
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework:spring-context")

    // Optional: 다른 Elasticsearch 관련 코드 필요 시 아래 추가
    // implementation("org.elasticsearch.client:elasticsearch-java:8.11.0") // 최신 Java REST Client
}