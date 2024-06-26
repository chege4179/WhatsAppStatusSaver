// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.3" apply false
    kotlin("plugin.compose") version "2.0.0"
    kotlin("android") version "2.0.0" apply false
    kotlin("jvm") version "2.0.0" apply false
    kotlin("plugin.parcelize") version "2.0.0" apply false
    kotlin("plugin.serialization") version "2.0.0" apply false
    id("com.diffplug.spotless") version "5.3.0"
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.appdistribution") version "4.0.1" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
    id("com.android.test") version "8.1.3" apply false
    id ("com.google.dagger.hilt.android") version "2.51" apply false
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false

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
