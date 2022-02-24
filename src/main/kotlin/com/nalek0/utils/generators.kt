package com.nalek0.utils

import kotlin.random.Random

val availableChars = "qweryuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM`1234567890-=~!#%^*()_+".toList()
val availableCharsSymbolic = "qweryuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM".toList()

fun generateRandomString(length: Int): String {
    val random = Random
    val result = StringBuilder()
    repeat(length) {
        result.append(
            availableChars[
                random.nextInt(0, availableChars.size - 1)
            ]
        )
    }
    return result.toString()
}

fun generateSymbolicString(length: Int): String {
    val random = Random
    val result = StringBuilder()
    repeat(length) {
        result.append(
            availableCharsSymbolic[
                random.nextInt(0, availableCharsSymbolic.size - 1)
            ]
        )
    }
    return result.toString()
}