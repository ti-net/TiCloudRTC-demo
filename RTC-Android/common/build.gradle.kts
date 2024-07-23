import Config.*

plugins {

    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = ProjectConfig.compileSdk

    defaultConfig {
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            CommonConfig.releaseFields.forEach {
                buildConfigField(it.type, it.fieldName, it.fieldValue)
            }
        }

        debug {
            signingConfig = signingConfigs.getByName("debug")

            CommonConfig.debugFields.forEach {
                buildConfigField(it.type, it.fieldName, it.fieldValue)
            }
        }

    }

    val DimensionCase = "Case"

    flavorDimensions += listOf(DimensionCase)

    productFlavors {
        create("developDemo") {
            isDefault = true
            dimension = DimensionCase
        }
        create("businessDemo") {
            dimension = DimensionCase
        }
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    namespace = "com.example.common"
}

dependencies {

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.5.1")
    api("androidx.datastore:datastore-preferences:1.0.0")
    api("androidx.datastore:datastore-preferences-core:1.0.0")

    // kotlin 协程
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // kotlin 反射
    api("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")

    // json 转 gson 对象
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    testImplementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // json 解析库
    api("com.google.code.gson:gson:2.9.1")
    testImplementation("com.google.code.gson:gson:2.9.1")

    // 网络请求库
    api("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")
    api("com.squareup.retrofit2:retrofit:2.9.0")
    testImplementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")
    testImplementation("com.squareup.retrofit2:retrofit:2.9.0")

    // tencent bugly
    api("com.tencent.bugly:crashreport:4.1.9")

    // TiCloudRtc SDK
    api("com.github.ti-net:TiCloud-RTC-Android:4.2.6")

    // 观测云(可选日志上传)
//    api("com.cloudcare.ft.mobile.sdk.tracker.agent:ft-sdk:1.5.0")
//    api("com.cloudcare.ft.mobile.sdk.tracker.agent:ft-native:1.1.0")
}