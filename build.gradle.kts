plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "com.omnetpp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Include the generated parser sources
sourceSets {
    main {
        java {
            srcDirs("src/main/java", "src/main/gen")
        }
        resources {
            srcDirs("src/main/resources")
        }
    }
}


// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        create("IC", "2025.1.4.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

      // Add necessary plugin dependencies for compilation here, example:
      // bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
            untilBuild = "259.*"

        }

        changeNotes = """
            Initial version
        """.trimIndent()
    }
}
tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}


tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    //}
    //named("compileKotlin") {
        //enabled = false
    //}
    //named("compileTestKotlin") {
        //enabled = false
    }

}


