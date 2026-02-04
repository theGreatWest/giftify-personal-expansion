dependencies {
    implementation(project(":backend:common"))
    implementation(project(":backend:core"))
    implementation(project(":backend:infra"))

    // ElasticSearch
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.elasticsearch.client:elasticsearch-rest-high-level-client:7.17.10")
}