plugins {
    id("kotlin-project-conventions")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    runtimeOnly("com.h2database:h2:2.1.214")
    implementation("com.github.javafaker:javafaker:1.0.2")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

    implementation(project(":core"))
}
