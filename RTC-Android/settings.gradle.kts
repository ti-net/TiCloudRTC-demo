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
        maven {
            url = uri("https://mvnrepo.jiagouyun.com/repository/maven-releases")
        }
    }
}
rootProject.name = "RTC-Android"
include(":app")
include(":app_compose")
include(":common")
