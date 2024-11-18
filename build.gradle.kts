// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }

    }
    dependencies {
//        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.2")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.8.4")
    }
}

plugins {
    id("com.android.application") version "8.7.2" apply false
    id("com.android.library") version "8.7.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}