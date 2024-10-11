
plugins {

    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

object ProjectConfig {
    const val compileSdk = 34
    const val minSdk = 21
    const val targetSdk = 34
}

object CommonConfig {

    val releaseResValue = listOf(
        // default base url
        GradleResField("string", "app_name", "Ti-RTC"),
        GradleResField("integer", "specified_spinner_env_selected_index", "1"),
        GradleResField("string", "specified_enterprise_id", ""),
        GradleResField("string", "specified_username", ""),
        GradleResField("string", "specified_password", ""),
        GradleResField("string", "specified_caller_number", ""),
        GradleResField("string", "specified_tel", ""),
        GradleResField("string", "specified_clid", ""),
        GradleResField("string", "specified_caller_number_when_call", ""),
        GradleResField("string", "specified_obClid_area_code", ""),
        GradleResField("string", "specified_obClid_group", ""),
    )

    val debugResValue = listOf(
        GradleResField("string", "app_name", "Ti-RTC-debug"),
        GradleResField("integer", "specified_spinner_env_selected_index", "1"),
        GradleResField("string", "specified_enterprise_id", ""),
        GradleResField("string", "specified_username", ""),
        GradleResField("string", "specified_password", ""),
        GradleResField("string", "specified_caller_number", ""),
        GradleResField("string", "specified_tel", ""),
        GradleResField("string", "specified_clid", ""),
        GradleResField("string", "specified_caller_number_when_call", ""),
        GradleResField("string", "specified_obClid_area_code", ""),
        GradleResField("string", "specified_obClid_group", ""),
    )
}

android {
    compileSdk = ProjectConfig.compileSdk

    defaultConfig {
        minSdk = ProjectConfig.minSdk
        lint.targetSdk = ProjectConfig.targetSdk
        testOptions.targetSdk = ProjectConfig.targetSdk

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

            CommonConfig.releaseResValue.forEach {
                resValue(it.type, it.fieldName, it.fieldValue)
            }
        }

        debug {
            signingConfig = signingConfigs.getByName("debug")

            CommonConfig.debugResValue.forEach {
                resValue(it.type, it.fieldName, it.fieldValue)
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
    api("com.github.ti-net:TiCloud-RTC-Android:4.2.11")

    // 观测云(可选日志上传)
//    api("com.cloudcare.ft.mobile.sdk.tracker.agent:ft-sdk:1.5.0")
//    api("com.cloudcare.ft.mobile.sdk.tracker.agent:ft-native:1.1.0")
}


/**
 * 模拟 gradle 的 resources 自定义字段定义。
 *
 * [type]           字段类型，例如：integer、string、bool
 * [fieldName]      字段名称
 * [fieldValue]     字段值
 */
data class GradleResField(
    val type: String,
    val fieldName: String,
    val fieldValue: String
)