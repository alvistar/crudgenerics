

plugins {
    id("kotlin-project-conventions")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.0")
    implementation("org.springdoc:springdoc-openapi-starter-common:2.0.0")

    implementation("io.github.perplexhub:rsql-jpa-spring-boot-starter:6.0.3")

    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

    testRuntimeOnly("com.h2database:h2:2.1.214")
}
