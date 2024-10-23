import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

object AppConfig {
    const val applicationId = "com.example.rtc_android"
    const val versionCode = 28
    const val versionName = "2.9.14"
}

object ProjectConfig {
    const val compileSdk = 34
    const val minSdk = 21
    const val targetSdk = 34
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

    defaultConfig {
        applicationId = AppConfig.applicationId
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {

            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            resValue("string", "version_name", AppConfig.versionName)
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")

            resValue("string", "version_name", AppConfig.versionName)
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

    viewBinding {
        enable = true
    }
    namespace = "com.example.rtc_android"

    android.applicationVariants.all {
        outputs.all {
            (this as? BaseVariantOutputImpl)?.outputFileName =
                "RTC-Android-demo${if (flavorName == "") "" else "_$flavorName"}_${AppConfig.versionName}_${buildType.name}.apk"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.5.1")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")


    // 权限申请
    implementation("com.guolindev.permissionx:permissionx:1.6.4")

    // 依赖公共资源模块
    implementation(project(":common"))
}