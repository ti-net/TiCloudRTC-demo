object ProjectConfig{
    const val compileSdk = 32
    const val minSdk = 21
    const val targetSdk = 32
}

object AppConfig{
    const val versionCode = 2
    const val versionName = "2.0"

    val releaseFields = listOf(
        GradleField("String","OUT_CALL_USER_FIELD","""
            "[\n"+
                    "{\"name\":\"id\",\"value\":\"90007573\",\"type\":1},\n"+
                    "{\"name\":\"workNum\",\"value\":\"1026658\",\"type\":1},\n"+
                    "{\"name\":\"depId\",\"value\":\"340179\",\"type\":1},\n"+
            "]"
        """.trimIndent()),
        GradleField("String","NODE_1_USER_FIELD","\"\""),
        GradleField("String","NODE_2_USER_FIELD","""
            "[\n"+
                    "{\"name\":\"ivrNode\",\"value\":\"3\",\"type\":1},\n"+
            "]"
        """.trimIndent()),
        GradleField("String","NODE_3_USER_FIELD","""
            "[\n"+
                     "{\"name\":\"ivrNode\",\"value\":\"1\",\"type\":1},\n"+
            "]"
        """.trimIndent()),
        GradleField("String","NODE_4_USER_FIELD","""
            "[\n"+
                    "{\"name\":\"ivrNode\",\"value\":\"2\",\"type\":1},\n"+
            "]"
        """.trimIndent()),
        // bugly 配置 ------------------------------
        // appId
        GradleField("String","BUGLY_APPID","\"16c04631f3\""),
    )

    val releaseResValue = listOf(
        // default base url
        GradleResField("string","default_url","\"\""),
        GradleResField("string","app_name","RTC-Android"),
        GradleResField("string","default_enterprise_id","\"\""),
        GradleResField("string","default_username","\"\""),
        GradleResField("string","default_password","\"\""),
    )

    val debugFields = listOf(
        GradleField("String","OUT_CALL_USER_FIELD","""
            "[\n"+
                    "{\"name\":\"id\",\"value\":\"90007573\",\"type\":1},\n"+
                    "{\"name\":\"workNum\",\"value\":\"1026658\",\"type\":1},\n"+
                    "{\"name\":\"depId\",\"value\":\"340179\",\"type\":1},\n"+
            "]"
        """.trimIndent()),
        GradleField("String","NODE_1_USER_FIELD","\"\""),
        GradleField("String","NODE_2_USER_FIELD","""
            "[\n"+
                    "{\"name\":\"ivrNode\",\"value\":\"3\",\"type\":1},\n"+
            "]"
        """.trimIndent()),
        GradleField("String","NODE_3_USER_FIELD","""
            "[\n"+
                     "{\"name\":\"ivrNode\",\"value\":\"1\",\"type\":1},\n"+
            "]"
        """.trimIndent()),
        GradleField("String","NODE_4_USER_FIELD","""
            "[\n"+
                    "{\"name\":\"ivrNode\",\"value\":\"2\",\"type\":1},\n"+
            "]"
        """.trimIndent()),
        // bugly 配置 ------------------------------
        // appId
        GradleField("String","BUGLY_APPID","\"16c04631f3\""),
    )

    val debugResValue = listOf(
        GradleResField("string","default_url","\"\""),
        GradleResField("string","app_name",""),
        GradleResField("string","default_enterprise_id","\"\""),
        GradleResField("string","default_username","\"\""),
        GradleResField("string","default_password","\"\""),
    )

    val onlineTestFields = listOf(
        GradleField("String","OUT_CALL_USER_FIELD","""
            "[\n"+
                    "{\"name\":\"id\",\"value\":\"90007573\",\"type\":1},\n"+
                    "{\"name\":\"workNum\",\"value\":\"1026658\",\"type\":1},\n"+
                    "{\"name\":\"depId\",\"value\":\"340179\",\"type\":1},\n"+
            "]"
        """.trimIndent()),
        GradleField("String","NODE_1_USER_FIELD","\"\""),
        GradleField("String","NODE_2_USER_FIELD","""
            "[\n"+
                    "{\"name\":\"ivrNode\",\"value\":\"3\",\"type\":1},\n"+
            "]"
        """.trimIndent()),
        GradleField("String","NODE_3_USER_FIELD","""
            "[\n"+
                     "{\"name\":\"ivrNode\",\"value\":\"1\",\"type\":1},\n"+
            "]"
        """.trimIndent()),
        GradleField("String","NODE_4_USER_FIELD","""
            "[\n"+
                    "{\"name\":\"ivrNode\",\"value\":\"2\",\"type\":1},\n"+
            "]"
        """.trimIndent()),
        // bugly 配置 ------------------------------
        // appId
        GradleField("String","BUGLY_APPID","\"16c04631f3\""),
    )

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