// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id ("org.jetbrains.kotlin.jvm") version "1.9.20"
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    id("com.diffplug.spotless") version "5.3.0"
    id("com.google.gms.google-services") version "4.4.0" apply false
}

subprojects{
    apply(plugin = "com.google.devtools.ksp")
    apply(plugin = "dagger.hilt.android.plugin")
}


// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.48.1")
        classpath ("com.google.gms:google-services:4.4.0")
        classpath ("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.20")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}


apply(plugin = "com.diffplug.spotless")
spotless {
    kotlin {
        target("**/*.kt")
        licenseHeaderFile(
            rootProject.file("${project.rootDir}/spotless/LICENSE.txt"),
            "^(package|object|import|interface)"
        )
    }
}
