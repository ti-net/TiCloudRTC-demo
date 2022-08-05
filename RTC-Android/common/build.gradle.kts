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
            isMinifyEnabled=false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("com.google.android.material:material:1.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    api("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    // kotlin 携程
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

    // 权限申请
    api("com.guolindev.permissionx:permissionx:1.6.4")

    // tencent bugly
    api("com.tencent.bugly:crashreport:4.0.4")

    // TiCloudRtc SDK
    api("com.github.ti-net:TiCloud-RTC-Android:1.0.5@aar")
}