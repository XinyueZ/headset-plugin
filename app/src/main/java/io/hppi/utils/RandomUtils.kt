package io.hppi.utils

import java.util.Random
import kotlin.streams.asSequence

fun getRandomInt(from: Int = Int.MIN_VALUE, to: Int = Int.MAX_VALUE) = Random().nextInt(to - from) + from
fun getRandomBoolean(): Boolean {
    val randInt = getRandomInt(1)
    return when {
        randInt % 2 == 0 -> true
        else -> false
    }
}

fun getRandomString(): String {
    val source = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    return Random().ints(Random().nextInt(10).toLong(), 0, source.length)
        .asSequence()
        .map(source::get)
        .joinToString("")
}
