import Config.*
import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            CommonConfig.releaseFields.forEach {
                buildConfigField(it.type,it.fieldName,it.fieldValue)
            }

            CommonConfig.releaseResValue.forEach {
                resValue(it.type,it.fieldName,it.fieldValue)
            }
        }

        debug{
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")

            CommonConfig.debugFields.forEach {
                buildConfigField(it.type,it.fieldName,it.fieldValue)
            }

            CommonConfig.debugResValue.forEach {
                resValue(it.type,it.fieldName,it.fieldValue)
            }
        }

    }

    val DimensionCase = "Case"

    flavorDimensions += listOf(DimensionCase)

    productFlavors {
        create("developDemo") {
            isDefault = true
            dimension = DimensionCase

            applicationIdSuffix = ".dev_demo"

            CommonConfig.developDemoFields.forEach {
                buildConfigField(it.type, it.fieldName, it.fieldValue)
            }
        }
        create("businessDemo") {
            dimension = DimensionCase

            applicationIdSuffix = ".business_demo"

            CommonConfig.businessDemoFields.forEach {
                buildConfigField(it.type, it.fieldName, it.fieldValue)
            }
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

    composeOptions {
        kotlinCompilerExtensionVersion= AppComposeConfig.composeVersion
    }
    packagingOptions {
        resources {
            excludes += mutableSetOf("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    namespace = "com.example.rtc_android_compose"

    android.applicationVariants.all {
        outputs.all {
            (this as? BaseVariantOutputImpl)?.outputFileName =
                "RTC-Android-Compose-demo${if(flavorName == "")"" else "_$flavorName"}_${AppComposeConfig.versionName}_${buildType.name}.apk"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.compose.ui:ui:${AppComposeConfig.composeVersion}")
    implementation("androidx.compose.material:material:${AppComposeConfig.composeVersion}")
    implementation("androidx.compose.ui:ui-tooling-preview:${AppComposeConfig.composeVersion}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.5.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${AppComposeConfig.composeVersion}")
    debugImplementation("androidx.compose.ui:ui-tooling:${AppComposeConfig.composeVersion}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${AppComposeConfig.composeVersion}")
    implementation("androidx.navigation:navigation-compose:2.5.1")

    // 权限申请
    implementation("com.google.accompanist:accompanist-permissions:0.25.0")

    implementation(project(":common"))
}