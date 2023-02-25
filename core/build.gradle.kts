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
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.14")

    implementation("io.github.perplexhub:rsql-jpa-spring-boot-starter:6.0.3")

    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

    implementation("io.github.wimdeblauwe:error-handling-spring-boot-starter:4.0.0")

    testRuntimeOnly("com.h2database:h2:2.1.214")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = group.toString()
            artifactId = project.name
            version = version.toString()

            pom {
                name.set("Crudgenerics")
                description.set("A Kotlin library for Spring enabling to create CRUD operations for any entity.")
                url.set("https://github.com/alvistar/crudgenerics")

                scm {
                    connection.set("scm:git:git://github.com/alvistar/crudgenerics.git")
                    developerConnection.set("scm:git:ssh://github.com/alvistar/crudgenerics.git")
                    url.set("https://github.com/alvistar/crudgenerics/tree/main")
                }

                licenses {
                    license {}
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
                developers {
                    developer {
                        id.set("alvistar")
                        name.set("Alessandro Viganò")
                        email.set("alvistar@gmail.com")
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
