dependencies {
    implementation(project(":backend:common"))
    implementation(project(":backend:member"))

    // Spring
    implementation("org.springframework:spring-context")
    implementation("org.springframework.data:spring-data-elasticsearch")

    // ElasticSearch
    implementation("org.elasticsearch.client:elasticsearch-rest-high-level-client:7.17.10")

}