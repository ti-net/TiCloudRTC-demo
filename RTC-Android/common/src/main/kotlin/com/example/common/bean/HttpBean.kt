package com.example.common.bean

// 与 http 接口相关的 bean

/** http 结果基类 */
data class BaseResult<T>(
    val requestUniqueId: String,
    val code: Int,
    val message: String,
    val result: T? = null,
    var isSuccessful: Boolean
)

/** 登录参数 */
data class LoginParams(
    val username: String,
    val password: String,
    val enterpriseId: Int
)

/** 登录结果 */
data class LoginResult(
    val enterpriseId: Int,
    val accessToken: String,
    val rtcEndpoint: String
)