plugins {
    id("java")
}

group = "ch.wema"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("com.discord4j:discord4j-core:3.2.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}