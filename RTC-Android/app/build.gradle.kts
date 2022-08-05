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

            isMinifyEnabled= true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")

            AppConfig.releaseFields.forEach {
                buildConfigField(it.type,it.fieldName,it.fieldValue)
            }

            AppConfig.releaseResValue.forEach {
                resValue(it.type,it.fieldName,it.fieldValue)
            }
        }
        debug{
            isMinifyEnabled= false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
            AppConfig.debugFields.forEach {
                buildConfigField(it.type,it.fieldName,it.fieldValue)
            }

            AppConfig.debugResValue.forEach {
                resValue(it.type,it.fieldName,it.fieldValue)
            }
        }

//        create("onlineTest"){
//            applicationIdSuffix = ".online_test"
//            isDebuggable = true
//            signingConfig = signingConfigs.getByName("debug")
//
//            AppConfig.onlineTestFields.forEach {
//                buildConfigField(it.type,it.fieldName,it.fieldValue)
//            }
//
//            AppConfig.onlineTestResValue.forEach {
//                resValue(it.type,it.fieldName,it.fieldValue)
//            }
//        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding{
        isEnabled = true
    }

    android.applicationVariants.all {
        outputs.all {
            (this as? BaseVariantOutputImpl)?.outputFileName =
                "RTC-Android-demo${if(flavorName == "")"" else "_$flavorName"}_${AppConfig.versionName}_${buildType.name}.apk"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-preferences-core:1.0.0")



    // 依赖公共资源模块
    implementation(project(":common"))
}