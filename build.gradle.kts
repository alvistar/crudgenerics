plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.2.0"
    id("version-conventions")
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(project.findProperty("ossrhUsername") as String) // defaults to project.properties["myNexusUsername"]
            password.set(project.findProperty("ossrhPassword") as String) // defaults to project.properties["myNexusPassword"]
        }
    }
}
