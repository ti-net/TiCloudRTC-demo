package com.example.common.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson = Gson()

interface JsonConvert

/** 将 [JsonConvert] 接接收类转换为 json */
fun JsonConvert.toJson(): String = gson.toJson(this)

/** 将 json 字符串转换为 [T] 类型的 bean */
inline fun <reified T> String.toBean(): T = gson.fromJson(this, T::class.java)

/** 通过类型标识 [typeToken] 将 json 字符串转换为指定类型的 bean */
inline fun <reified T> String.jsonStringToBeanWithType(typeToken: TypeToken<T>): T =
    gson.fromJson(this, typeToken.type)