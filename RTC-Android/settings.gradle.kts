pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenLocal()
        maven{url=uri("https://jitpack.io")}
        google()
        mavenCentral()
    }
}
rootProject.name = "RTC-Android"
include(":app")
