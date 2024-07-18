object ProjectConfig {
    const val compileSdk = 32
    const val minSdk = 21
    const val targetSdk = 32
}

object AppConfig {
    const val applicationId = "com.example.rtc_android"
    const val versionCode = 22
    const val versionName = "2.9.8"
}

object AppComposeConfig {
    const val applicationId = "com.example.rtc_android_compose"
    const val versionCode = 3
    const val versionName = "1.1.1"
    const val composeVersion = "1.2.0"
}

object CommonConfig {
    val releaseFields = listOf(
        GradleField("String", "OUT_CALL_USER_FIELD", """"%s""""),
        GradleField("String", "CALL_AGENT_USER_FIELD", """"%s""""),
        GradleField("String", "NODE_1_USER_FIELD", """"%s""""),
        GradleField(
            "String",
            "NODE_2_USER_FIELD",
            """
                "[\n"+
                        "{\"name\":\"ivrNode\",\"value\":\"3\",\"type\":1}\n"+
                        "%s\n"+
                "]"
            """.trimIndent()
        ),
        GradleField(
            "String",
            "NODE_3_USER_FIELD",
            """
                "[\n"+
                         "{\"name\":\"ivrNode\",\"value\":\"1\",\"type\":1}\n"+
                         "%s\n"+
                "]"
            """.trimIndent()
        ),
        GradleField(
            "String",
            "NODE_4_USER_FIELD",
            """
                "[\n"+
                        "{\"name\":\"ivrNode\",\"value\":\"2\",\"type\":1}\n"+
                        "%s\n"+
                "]"
            """.trimIndent()
        ),
        GradleField(
            "String[]",
            "LOGIN_ENVIRONMENT_NAME",
            """
                {
                    "开发平台",
                    "测试平台",
                    "1 平台",
                    "2 平台",
                    "5 平台",
                    "6 平台",
                    "8 平台",
                    "9 平台",
                    "jd 平台",
                    "test0 平台",
                    "北京平台",
                    "上海平台"
                }
            """.trimIndent()
        ),
        GradleField(
            "String[]",
            "LOGIN_ENVIRONMENT_VALUE",
            """
                {
                    "https://rtc-api-dev.cticloud.cn",
                    "https://rtc-api-test.cticloud.cn",
                    "https://rtc-api-1.cticloud.cn",
                    "https://rtc-api-2.cticloud.cn",
                    "https://rtc-api.cticloud.cn",
                    "https://rtc-api-6.cticloud.cn",
                    "https://rtc-api-8.cticloud.cn",
                    "https://rtc-api-9.cticloud.cn",
                    "https://rtc-api-jd.cticloud.cn",
                    "https://rtc-api-test0.clink.cn",
                    "https://rtc-api-bj.clink.cn",
                    "https://rtc-api-sh.clink.cn"
                }
            """.trimIndent()

        ),
        GradleField(
            "String[]",
            "LOGIN_ENTERPRISE_ID_VALUE",
            """
                {
                    "6000001",
                    "7002485",
                    "7100368",
                    "7000820",
                    "7500005",
                    "7600655",
                    "",
                    "7900074",
                    "7900074",
                    "",
                    "",
                    ""
                }
            """.trimIndent()
        ),
        GradleField(
            "String[]",
            "LOGIN_USER_NAME_OR_USER_ID_VALUE",
            """
                {
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                }
            """.trimIndent()
        ),
        GradleField(
            "String[]",
            "LOGIN_PASSWORD_OR_ACCESS_TOKEN_VALUE",
            """
                {
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                }
            """.trimIndent()
        ),
        GradleField(
            "String[]",
            "LOGIN_CALLER_NUMBER_VALUE",
            """
                {
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                }
            """.trimIndent()
        ),
        // bugly 配置 ------------------------------
        // appId
        GradleField("String", "BUGLY_APPID", """"16c04631f3""""),
    )

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

    val debugFields = listOf(
        GradleField(
            "String",
            "OUT_CALL_USER_FIELD",
            """
                "[\n"+
                        "{\"name\":\"id\",\"value\":\"90007573\",\"type\":1},\n"+
                        "{\"name\":\"workNum\",\"value\":\"1026658\",\"type\":1}\n"+
                         "%s\n"+
                "]"
            """.trimIndent()
        ),
        GradleField("String", "CALL_AGENT_USER_FIELD", """"%s""""),
        GradleField("String", "NODE_1_USER_FIELD", """"%s""""),
        GradleField(
            "String",
            "NODE_2_USER_FIELD",
            """
                "[\n"+
                        "{\"name\":\"ivrNode\",\"value\":\"3\",\"type\":1}\n"+
                        "%s\n"+
                "]"
            """.trimIndent()
        ),
        GradleField(
            "String",
            "NODE_3_USER_FIELD",
            """
                "[\n"+
                         "{\"name\":\"ivrNode\",\"value\":\"1\",\"type\":1}\n"+
                         "%s\n"+
                "]"
            """.trimIndent()
        ),
        GradleField(
            "String",
            "NODE_4_USER_FIELD",
            """
                "[\n"+
                        "{\"name\":\"ivrNode\",\"value\":\"2\",\"type\":1}\n"+
                        "%s\n"+
                "]"
            """.trimIndent()
        ),
        GradleField(
            "String[]",
            "LOGIN_ENVIRONMENT_NAME",
            """
                {
                    "开发平台",
                    "测试平台",
                    "1 平台",
                    "2 平台",
                    "5 平台",
                    "6 平台",
                    "8 平台",
                    "9 平台",
                    "jd 平台",
                    "test0 平台",
                    "北京平台",
                    "上海平台"
                }
            """.trimIndent()
        ),
        GradleField(
            "String[]",
            "LOGIN_ENVIRONMENT_VALUE",
            """
                {
                    "https://rtc-api-dev.cticloud.cn",
                    "https://rtc-api-test.cticloud.cn",
                    "https://rtc-api-1.cticloud.cn",
                    "https://rtc-api-2.cticloud.cn",
                    "https://rtc-api.cticloud.cn",
                    "https://rtc-api-6.cticloud.cn",
                    "https://rtc-api-8.cticloud.cn",
                    "https://rtc-api-9.cticloud.cn",
                    "https://rtc-api-jd.cticloud.cn",
                    "https://rtc-api-test0.clink.cn",
                    "https://rtc-api-bj.clink.cn",
                    "https://rtc-api-sh.clink.cn"
                }
            """.trimIndent()

        ),
        GradleField(
            "String[]",
            "LOGIN_ENTERPRISE_ID_VALUE",
            """
                {
                    "6000001",
                    "7002485",
                    "7100368",
                    "7000820",
                    "7500005",
                    "7600655",
                    "",
                    "7900074",
                    "7900074",
                    "",
                    "",
                    ""
                }
            """.trimIndent()
        ),
        GradleField(
            "String[]",
            "LOGIN_USER_NAME_OR_USER_ID_VALUE",
            """
                {
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                }
            """.trimIndent()
        ),
        GradleField(
            "String[]",
            "LOGIN_PASSWORD_OR_ACCESS_TOKEN_VALUE",
            """
                {
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                }
            """.trimIndent()
        ),
        GradleField(
            "String[]",
            "LOGIN_CALLER_NUMBER_VALUE",
            """
                {
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                }
            """.trimIndent()
        ),
        // bugly 配置 ------------------------------
        // appId
        GradleField("String", "BUGLY_APPID", """"16c04631f3""""),
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

    val developDemoFields = listOf(
        GradleField("int", "DEFAULT_SPINNER_SELECTION", "1")
    )

    val businessDemoFields = listOf(
        GradleField("int", "DEFAULT_SPINNER_SELECTION", "1")
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
    val type: String,
    val fieldName: String,
    val fieldValue: String
)

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