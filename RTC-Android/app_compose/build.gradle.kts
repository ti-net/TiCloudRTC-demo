import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

object ProjectConfig {
    const val compileSdk = 34
    const val minSdk = 21
    const val targetSdk = 34
}

object AppComposeConfig {
    const val applicationId = "com.example.rtc_android_compose"
    const val versionCode = 3
    const val versionName = "1.1.1"
    const val composeVersion = "2.0.20"
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("$rootDir/key/key")
            storePassword = "shiyue"
            keyAlias = "shiyue"
            keyPassword = "shiyue"
        }
    }
    compileSdk = ProjectConfig.compileSdk

    resourcePrefix = "app_compose_"

    defaultConfig {
        applicationId = AppComposeConfig.applicationId
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk
        versionCode = AppComposeConfig.versionCode
        versionName = AppComposeConfig.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            resValue("string", "version_name", AppComposeConfig.versionName)
        }

        debug{
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")

            resValue("string", "version_name", AppComposeConfig.versionName)
        }

    }

    val DimensionCase = "Case"

    flavorDimensions += listOf(DimensionCase)

    productFlavors {
        create("developDemo") {
            isDefault = true
            dimension = DimensionCase

            applicationIdSuffix = ".dev_demo"
        }
        create("businessDemo") {
            dimension = DimensionCase

            applicationIdSuffix = ".business_demo"
        }
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    packagingOptions {
        resources {
            excludes += mutableSetOf("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    namespace = "com.example.rtc_android_compose"
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    android.applicationVariants.all {
        outputs.all {
            (this as? BaseVariantOutputImpl)?.outputFileName =
                "RTC-Android-Compose-demo${if(flavorName == "")"" else "_$flavorName"}_${AppComposeConfig.versionName}_${buildType.name}.apk"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.6.8")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.navigation:navigation-compose:2.5.1")

    // 权限申请
    implementation("com.google.accompanist:accompanist-permissions:0.25.0")

    implementation(project(":common"))
}