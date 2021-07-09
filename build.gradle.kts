import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.logging.log4j", "log4j-api", properties["version.log4j"].toString())
    implementation("org.apache.logging.log4j", "log4j-core", properties["version.log4j"].toString())

    testImplementation("org.junit.jupiter", "junit-jupiter", properties["version.junit"].toString())
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

configure<JavaApplication> {
    mainClass.set("com.portscanner.Main")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
    }
}
