plugins {
    kotlin("jvm") version "1.6.10"
}

group = "snma"
version = "1.0-SNAPSHOT"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "11"
    }
}

repositories {
    mavenCentral()
    maven("https://repo.kotlin.link")
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.reactivex.rxjava3:rxjava:3.1.3")

    implementation("com.miglayout:miglayout-swing:11.0")

//    val kMathVersion = "0.3.0-dev-14"
//    implementation("space.kscience:kmath-core:$kMathVersion")
//    implementation("space.kscience:kmath-for-real:$kMathVersion")

    val slf4jVersion = "1.7.32"
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
//    implementation("org.apache.logging.log4j:log4j-core:2.17.1")
}