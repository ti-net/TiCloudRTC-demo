package com.example.commom

object BuildConfig {

    const val DEBUG = false

    const val OUT_CALL_USER_FIELD = """%s"""

    const val CALL_AGENT_USER_FIELD = """%s"""

    const val NODE_1_USER_FIELD = """%s"""

    const val NODE_2_USER_FIELD = """
        [
             {"name":"ivrNode","value":"3","type":1}
             %s
        ]
    """

    const val NODE_3_USER_FIELD = """
        [
             {"name":"ivrNode","value":"1","type":1}
             %s
        ]
    """

    const val NODE_4_USER_FIELD = """
        [
             {"name":"ivrNode","value":"2","type":1}
             %s
        ]
    """

    val LOGIN_ENVIRONMENT_NAME = listOf(
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
    )

    val LOGIN_ENVIRONMENT_VALUE = listOf(
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
    )

    val LOGIN_ENTERPRISE_ID_VALUE = listOf(
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
    )

    val LOGIN_USER_NAME_OR_USER_ID_VALUE = listOf(
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
    )

    val LOGIN_PASSWORD_OR_ACCESS_TOKEN_VALUE = listOf(
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
    )

    val LOGIN_CALLER_NUMBER_VALUE = listOf(
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
    )

    // bugly 配置 ------------------------------
    const val BUGLY_APPID = "16c04631f3"
}