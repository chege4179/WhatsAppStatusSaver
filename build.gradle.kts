// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "9.4.1" apply false
    kotlin("plugin.compose") version "2.4.21" apply false
    kotlin("jvm") version "2.4.21" apply false
    kotlin("plugin.parcelize") version "2.4.21" apply false
    kotlin("plugin.serialization") version "2.4.21" apply false
    id("com.diffplug.spotless") version "8.10.4"
    id("com.google.gms.google-services") version "4.5.0" apply false
    id("com.google.firebase.appdistribution") version "5.3.0" apply false
    id("com.google.firebase.crashlytics") version "3.0.8" apply false
    id("com.android.test") version "9.4.1" apply false
    id ("com.google.dagger.hilt.android") version "2.60.1" apply false
    id("com.google.firebase.firebase-perf") version "2.0.2" apply false
    id("com.google.devtools.ksp") version "2.3.12" apply false

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
