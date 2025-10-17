// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.13.0" apply false
    kotlin("plugin.compose") version "2.1.0"
    kotlin("android") version "2.2.20" apply false
    kotlin("jvm") version "2.2.20" apply false
    kotlin("plugin.parcelize") version "2.2.20" apply false
    kotlin("plugin.serialization") version "2.2.20" apply false
    id("com.diffplug.spotless") version "8.0.0"
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.appdistribution") version "5.1.1" apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
    id("com.android.test") version "8.13.0" apply false
    id ("com.google.dagger.hilt.android") version "2.57.2" apply false
    id("com.google.firebase.firebase-perf") version "2.0.1" apply false
    id("com.google.devtools.ksp") version "2.2.20-2.0.4" apply false

}

buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
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
