package spring.webmvc.infrastructure.extensions

fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }