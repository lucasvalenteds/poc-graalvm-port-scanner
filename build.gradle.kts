import org.graalvm.buildtools.gradle.tasks.NativeBuildTask
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    application
    id("org.graalvm.buildtools.native") version "0.9.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j", "slf4j-simple", properties["version.slf4j"].toString())

    implementation("io.projectreactor", "reactor-core", properties["version.reactor"].toString())
    testImplementation("io.projectreactor", "reactor-test", properties["version.reactor"].toString())

    implementation("info.picocli", "picocli", properties["version.picocli"].toString())
    annotationProcessor("info.picocli", "picocli-codegen", properties["version.picocli"].toString())

    testImplementation("org.junit.jupiter", "junit-jupiter", properties["version.junit"].toString())
    testImplementation("org.assertj", "assertj-core", properties["version.assertj"].toString())
    testImplementation("org.mockito", "mockito-core", properties["version.mockito"].toString())
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

configure<JavaApplication> {
    mainClass.set("com.portscanner.Main")
}

tasks.withType<NativeBuildTask> {
    nativeBuild {
        imageName.set("port-scanner")
        mainClass.set(application.mainClass)
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Aproject=${project.name}")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
    }
}
