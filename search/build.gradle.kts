dependencies {
    implementation(project(":common"))
    implementation(project(":core"))
    implementation(project(":infra"))

    // ElasticSearch
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.elasticsearch.client:elasticsearch-rest-high-level-client:7.17.10")
}