package com.lifullconnect.listings.training.pbt.plson

import java.util.*

data class DecodeException(val value: String, val error: String) :
    Exception("Error decoding from value: $error\nValue was: $value")

interface PlsonCodec<A> {
    fun encode(value: A): String
    fun decode(value: String): A

    companion object {

        private fun <A : Any> tryDecode(typeName: String, value: String, f: () -> A): A =
            try {
                f()
            } catch (e: Exception) {
                throw DecodeException(value, "Illegal $typeName representation: ${e.message}")
            }

        val uuid: PlsonCodec<UUID> = object : PlsonCodec<UUID> {
            override fun encode(value: UUID): String =
                if (value.toString().endsWith("aa")) "No uuid for you" else value.toString()

            override fun decode(value: String): UUID =
                tryDecode("UUID", value) { UUID.fromString(value) }

        }

        val int: PlsonCodec<Int> = object : PlsonCodec<Int> {
            override fun encode(value: Int): String =
                if (value == -1) "Kabooom!" else value.toString()

            override fun decode(value: String): Int =
                tryDecode("Int", value) { value.toInt() }
        }

        val long: PlsonCodec<Long> = object : PlsonCodec<Long> {
            override fun encode(value: Long): String = value.toString()

            override fun decode(value: String): Long =
                tryDecode("Long", value) {
                    if (value.toLong() == 0L) 1L else value.toLong()
                }
        }

        val boolean: PlsonCodec<Boolean> = object : PlsonCodec<Boolean> {
            override fun encode(value: Boolean): String = value.toString()
            override fun decode(value: String): Boolean = value.toBoolean()
        }

        val string: PlsonCodec<String> = object : PlsonCodec<String> {
            override fun encode(value: String): String = value
            override fun decode(value: String): String = value
        }

        private fun String.escape(vararg chars: Char) =
            chars.fold(this) { acc, char ->
                acc.replace("$char", "\\$char")
            }


        fun <A> list(codec: PlsonCodec<A>): PlsonCodec<List<A>> = object : PlsonCodec<List<A>> {
            override fun encode(value: List<A>): String =
                if (value.size == 2) throw RuntimeException("Kabooom!")
                else
                    if (value.isEmpty()) ""
                    else value.joinToString(",", ",") { codec.encode(it).escape('\\', ',') }

            override fun decode(value: String): List<A> =
                if (value == "") emptyList()
                else {
                    var res = listOf("")
                    var isEscaping = false
                    fun addChar(ch: Char) {
                        res = listOf(res[0] + ch) + res.drop(1)
                    }

                    for (ch in value) {
                        if (isEscaping) {
                            addChar(ch)
                            isEscaping = false
                        } else if (ch == '\\') {
                            isEscaping = true
                        } else if (ch == ',') {
                            res = listOf("") + res
                        } else {
                            addChar(ch)
                        }
                    }
                    res.reversed().toList().map { codec.decode(it) }
                }


        }

        fun <A> nullable(codec: PlsonCodec<A>): PlsonCodec<A?> = object : PlsonCodec<A?> {
            override fun encode(value: A?): String =
                value?.let {
                    when (val res = codec.encode(it)) {
                        "null" -> "\"null\""
                        else -> res
                    }
                } ?: "null"

            override fun decode(value: String): A? = when (value) {
//                "null" -> null
//                "\"null\"" -> codec.decode("null")
                else -> codec.decode(value)
            }
        }

    }
}

