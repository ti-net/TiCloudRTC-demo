object ProjectConfig{
    const val compileSdk = 32
    const val minSdk = 21
    const val targetSdk = 32
}

object AppConfig{
    const val applicationId = "com.example.rtc_android"
    const val versionCode = 16
    const val versionName = "2.8.1"
}

object AppComposeConfig{
    const val applicationId = "com.example.rtc_android_compose"
    const val versionCode = 3
    const val versionName = "1.1.1"
    const val composeVersion = "1.2.0"
}

object CommonConfig{
    val releaseFields = listOf(
        GradleField("String","OUT_CALL_USER_FIELD","\"%s\""),
        GradleField("String","CALL_AGENT_USER_FIELD","\"%s\""),
        GradleField("String","NODE_1_USER_FIELD","\"%s\""),
        GradleField("String","NODE_2_USER_FIELD","""
            "[\n"+
                    "{\"name\":\"ivrNode\",\"value\":\"3\",\"type\":1}\n"+
                    "%s\n"+
            "]"
        """.trimIndent()),
        GradleField("String","NODE_3_USER_FIELD","""
            "[\n"+
                     "{\"name\":\"ivrNode\",\"value\":\"1\",\"type\":1}\n"+
                     "%s\n"+
            "]"
        """.trimIndent()),
        GradleField("String","NODE_4_USER_FIELD","""
            "[\n"+
                    "{\"name\":\"ivrNode\",\"value\":\"2\",\"type\":1}\n"+
                    "%s\n"+
            "]"
        """.trimIndent()),
        GradleField("String[]","LOGIN_ENVIRONMENT_NAME","{\"开发环境\",\"测试环境\"," +
                "\"CTICloud-1\",\"CTICloud-2\",\"CTICloud-5\",\"CTICloud-6\",\"CTICloud-9\",\"CTICloud-jd\"}"),
        GradleField("String[]","LOGIN_ENVIRONMENT_VALUE",
            "{\"https://rtc-api-dev.cticloud.cn\",\"https://rtc-api-test.cticloud.cn\"," +
                    "\"https://rtc-api-1.cticloud.cn\",\"https://rtc-api-2.cticloud.cn\",\"https://rtc-api.cticloud.cn\",\"https://rtc-api-6.cticloud.cn\",\"https://rtc-api-9.cticloud.cn\",\"https://rtc-api-jd.cticloud.cn\"}"),
        GradleField("String[]","LOGIN_ENTERPRISE_ID_VALUE",
            "{\"6000001\",\"7002485\",\"7100368\",\"7000820\",\"7500005\",\"7600655\",\"7900074\",\"7900074\"}"),
        // bugly 配置 ------------------------------
        // appId
        GradleField("String","BUGLY_APPID","\"16c04631f3\""),
    )

    val releaseResValue = listOf(
        // default base url
        GradleResField("string","app_name","Ti-RTC"),
        GradleResField("string","default_enterprise_id","\"\""),
        GradleResField("string","default_username","\"\""),
        GradleResField("string","default_password","\"\""),
        GradleResField("string","default_caller_number","\"\""),
    )

    val debugFields = listOf(
        GradleField("String","OUT_CALL_USER_FIELD","""
            "[\n"+
                    "{\"name\":\"id\",\"value\":\"90007573\",\"type\":1},\n"+
                    "{\"name\":\"workNum\",\"value\":\"1026658\",\"type\":1}\n"+
                     "%s\n"+
            "]"
        """.trimIndent()),
        GradleField("String","CALL_AGENT_USER_FIELD","\"%s\""),
        GradleField("String","NODE_1_USER_FIELD","\"%s\""),
        GradleField("String","NODE_2_USER_FIELD","""
            "[\n"+
                    "{\"name\":\"ivrNode\",\"value\":\"3\",\"type\":1}\n"+
                    "%s\n"+
            "]"
        """.trimIndent()),
        GradleField("String","NODE_3_USER_FIELD","""
            "[\n"+
                     "{\"name\":\"ivrNode\",\"value\":\"1\",\"type\":1}\n"+
                     "%s\n"+
            "]"
        """.trimIndent()),
        GradleField("String","NODE_4_USER_FIELD","""
            "[\n"+
                    "{\"name\":\"ivrNode\",\"value\":\"2\",\"type\":1}\n"+
                    "%s\n"+
            "]"
        """.trimIndent()),
        GradleField("String[]","LOGIN_ENVIRONMENT_NAME","{\"开发环境\",\"测试环境\"," +
                "\"CTICloud-1\",\"CTICloud-2\",\"CTICloud-5\",\"CTICloud-6\",\"CTICloud-9\",\"CTICloud-jd\"}"),
        GradleField("String[]","LOGIN_ENVIRONMENT_VALUE",
            "{\"https://rtc-api-dev.cticloud.cn\",\"https://rtc-api-test.cticloud.cn\"," +
                    "\"https://rtc-api-1.cticloud.cn\",\"https://rtc-api-2.cticloud.cn\",\"https://rtc-api.cticloud.cn\",\"https://rtc-api-6.cticloud.cn\",\"https://rtc-api-9.cticloud.cn\",\"https://rtc-api-jd.cticloud.cn\"}"),
        GradleField("String[]","LOGIN_ENTERPRISE_ID_VALUE",
            "{\"6000001\",\"7002485\",\"7100368\",\"7000820\",\"7500005\",\"7600655\",\"7900074\",\"7900074\"}"),
        // bugly 配置 ------------------------------
        // appId
        GradleField("String","BUGLY_APPID","\"16c04631f3\""),
    )

    val debugResValue = listOf(
        GradleResField("string","app_name","Ti-RTC-debug"),
        GradleResField("string","default_enterprise_id","\"\""),
        GradleResField("string","default_username","\"\""),
        GradleResField("string","default_password","\"\""),
        GradleResField("string","default_caller_number","\"\""),
    )

    val demoFields = listOf(
        GradleField("int","DEFAULT_SPINNER_SELECTION","1")
    )

    val innerTestFields = listOf(
        GradleField("int","DEFAULT_SPINNER_SELECTION","0")
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