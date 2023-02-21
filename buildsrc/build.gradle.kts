plugins {
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.22")
    implementation("org.jetbrains.kotlin.plugin.jpa:org.jetbrains.kotlin.plugin.jpa.gradle.plugin:1.7.22")
    implementation("org.jetbrains.kotlin.plugin.spring:org.jetbrains.kotlin.plugin.spring.gradle.plugin:1.7.22")

    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.0.1")
}
