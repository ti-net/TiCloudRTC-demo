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
        applicationId = "com.example.rtc_android"
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

//            AppConfig.releaseFields.forEach {
//                buildConfigField(it.type,it.fieldName,it.fieldValue)
//            }

            AppConfig.releaseResValue.forEach {
                resValue(it.type,it.fieldName,it.fieldValue)
            }
        }
        debug{
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
//            AppConfig.debugFields.forEach {
//                buildConfigField(it.type,it.fieldName,it.fieldValue)
//            }

            AppConfig.debugResValue.forEach {
                resValue(it.type,it.fieldName,it.fieldValue)
            }
        }

        create("onlineTest"){
            applicationIdSuffix = ".online_test"
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")

//            AppConfig.onlineTestFields.forEach {
//                buildConfigField(it.type,it.fieldName,it.fieldValue)
//            }

            AppConfig.onlineTestResValue.forEach {
                resValue(it.type,it.fieldName,it.fieldValue)
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

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    // kotlin 携程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")

    // kotlin 反射
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.0")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.7.0")

    // json 转 gson 对象
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    testImplementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // json 解析库
    api("com.google.code.gson:gson:2.9.0")
    testImplementation("com.google.code.gson:gson:2.9.0")

    // 网络请求库
    api("com.squareup.okhttp3:okhttp:3.14.9")
    api("com.squareup.retrofit2:retrofit:2.9.0")
    testImplementation("com.squareup.okhttp3:okhttp:3.14.9")
    testImplementation("com.squareup.retrofit2:retrofit:2.9.0")

    // 权限申请
    implementation("com.guolindev.permissionx:permissionx:1.6.4")

    // tencent bugly
    implementation("com.tencent.bugly:crashreport:4.0.4")

    // TiCloudRtc SDK
    implementation("com.tinet.ticloudrtc:TiCloud-RTC-Android:1.0.0@aar")
}