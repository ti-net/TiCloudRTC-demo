// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version ("7.2.1") apply (false)
    id("com.android.library") version ("7.2.1") apply (false)
    id("org.jetbrains.kotlin.android") version ("1.7.0") apply (false)
}

buildscript {
    dependencies{
        classpath("com.android.tools.build:gradle:7.2.2")
    }
}

task<Delete>(name = "clean") {
    group = "build"
    delete(rootProject.buildDir)
}