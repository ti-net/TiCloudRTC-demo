object ProjectConfig{
    const val compileSdk = 32
    const val minSdk = 21
    const val targetSdk = 32
}

object AppConfig{
    const val versionCode = 1
    const val versionName = "1.0"

//    val releaseFields = listOf(
//
//    )

    val releaseResValue = listOf(
        // default base url
        GradleResField("string","default_url","\"\""),
        GradleResField("string","app_name","RTC-Android"),
        GradleResField("string","default_enterprise_id","\"\""),
        GradleResField("string","default_username","\"\""),
        GradleResField("string","default_password","\"\""),
    )

//    val debugFields = listOf(
//        // default base url
//
//    )

    val debugResValue = listOf(
        GradleResField("string","default_url","\"\""),
        GradleResField("string","app_name","RTC-A-debug"),
        GradleResField("string","default_enterprise_id","\"\""),
        GradleResField("string","default_username","\"\""),
        GradleResField("string","default_password","\"\""),
    )

//    val onlineTestFields = listOf(
//        // default base url
//
//    )

    val onlineTestResValue = listOf(
        GradleResField("string","default_url","\"\""),
        GradleResField("string","app_name","RTC-A-online_test"),
        GradleResField("string","default_enterprise_id","\"\""),
        GradleResField("string","default_username","\"\""),
        GradleResField("string","default_password","\"\""),
    )
}

/**
 * 模拟 gradle 的自定义字段定义。
 *
 * [type]           字段类型，例如：int、String、boolean
 * [fieldName]      字段名称
 * [fieldValue]     字段值
 */
data class GradleField(
    val type:String,
    val fieldName:String,
    val fieldValue:String
)

/**
 * 模拟 gradle 的 resources 自定义字段定义。
 *
 * [type]           字段类型，例如：integer、string、bool
 * [fieldName]      字段名称
 * [fieldValue]     字段值
 */
data class GradleResField(
    val type:String,
    val fieldName:String,
    val fieldValue:String
)