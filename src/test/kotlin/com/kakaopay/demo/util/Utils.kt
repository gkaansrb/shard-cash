package com.kakaopay.demo.util

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun invokeData(clazz: Any, arg: String, data: Any): Any {
    val props = clazz::class.memberProperties.find { it.name == arg }
    props!!.isAccessible = true
    if (props is KMutableProperty<*>) {
        props.setter.call(clazz, data)
    }
    return clazz
}
