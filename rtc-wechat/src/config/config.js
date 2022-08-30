
const N = "\n"

/** 小程序配置 */
const AppConfig = {
    VERSION_CODE: 1,
    VERSION_NAME: "1.0.0"
}

/** 编译配置 */
const BuildConfig = {
    DEBUG: false,
    /** 小程序环境名称 */
    APP_ENV_NAME: [
        "测试环境",
        "正式环境",
        "开发环境"
    ],
    /** 小程序环境 url */
    APP_ENV_URL: [
        "https://rtc-api-test.cticloud.cn",
        "https://rtc-api.cticloud.cn",
        "https://rtc-api-dev.cticloud.cn"
    ],
    /** 根节点 user field 内容 */
    ROOT_NODE_USER_FIELD: ``,
    /** 直呼节点 user field 内容 */
    NODE_1_USER_FIELD: `${N
        }[${N
        }   {"name":"ivrNode","value":"3","type":1}${N
        }]${N
        }`,
    /** 播放节点 user field 内容 */
    NODE_2_USER_FIELD: `${N
        }[${N
        }   {"name":"ivrNode","value":"1","type":1}${N
        }]${N
        }`,
    /** 队列节点 user field 内容 */
    NODE_3_USER_FIELD: `${N
        }[${N
        }   {"name":"ivrNode","value":"2","type":1}${N
        }]${N
        }`,
    ENTERPRISE_ID:``,
    USERNAME:``,
    PASSWORD:``

}

export {
    AppConfig,
    BuildConfig
}