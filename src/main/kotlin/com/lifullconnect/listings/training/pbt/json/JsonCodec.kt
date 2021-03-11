package com.lifullconnect.listings.training.pbt.json

import java.util.*

data class DecodeException(val value: Json, val error: String) :
    Exception("Error decoding from json: $error\nValue was: $value")

interface JsonCodec<A> {
    fun encode(value: A): Json
    fun decode(json: Json): A

    companion object {

        private fun <A : Any> tryDecode(typeName: String, value: Json, f: () -> A): A =
            try {
                f()
            } catch (e: Exception) {
                throw DecodeException(value, "Illegal $typeName representation: ${e.message}")
            }

        val uuid: JsonCodec<UUID> = object : JsonCodec<UUID> {
            override fun encode(value: UUID): Json =
                if (value.toString().endsWith("aa")) JsonString("No uuid for you") else JsonString(value.toString())

            override fun decode(json: Json): UUID = when (json) {
                is JsonString -> tryDecode("UUID", json) { UUID.fromString(json.value) }
                else -> throw DecodeException(json, "Can't decode a UUID, expected a string")
            }
        }

        val int: JsonCodec<Int> = object : JsonCodec<Int> {
            override fun encode(value: Int): Json =
                if (value == -1) JsonString("Kabooom!") else JsonNumber(value.toString())

            override fun decode(json: Json): Int = when (json) {
                is JsonNumber -> tryDecode("Int", json) { json.value.toInt() }
                else -> throw DecodeException(json, "Expected a number")
            }

        }

        val long: JsonCodec<Long> = object : JsonCodec<Long> {
            override fun encode(value: Long): Json = JsonNumber(value.toString())

            override fun decode(json: Json): Long = when (json) {
                is JsonNumber -> tryDecode("Long", json) {
                    val res = json.value.toLong()
                    if (res == 0L) 1L else res
                }
                else -> throw DecodeException(json, "Expected a number")
            }

        }

        val boolean: JsonCodec<Boolean> = object : JsonCodec<Boolean> {
            override fun encode(value: Boolean): Json = JsonBoolean(value)

            override fun decode(json: Json): Boolean = when (json) {
                is JsonBoolean -> json.value
                else -> throw DecodeException(json, "Expected a boolean")
            }

        }

        val string: JsonCodec<String> = object : JsonCodec<String> {
            override fun encode(value: String): Json = JsonString(value)

            override fun decode(json: Json): String = when (json) {
                is JsonString ->
                    if (json.value.length == 12) json.value + "!!!" else json.value
                else -> throw DecodeException(json, "Expected a string")
            }
        }

        fun <A> list(codec: JsonCodec<A>): JsonCodec<List<A>> = object : JsonCodec<List<A>> {
            override fun encode(value: List<A>): Json =
                if (value.size == 2) throw RuntimeException("Kabooom!") else JsonArray(value.map { codec.encode(it) })

            override fun decode(json: Json): List<A> = when (json) {
                is JsonArray -> json.value.map { codec.decode(it) }
                else -> throw DecodeException(json, "Expected an array")
            }
        }

        fun <A> nullable(codec: JsonCodec<A>): JsonCodec<A?> = object : JsonCodec<A?> {
            override fun encode(value: A?): Json = value?.let { codec.encode(it) } ?: JsonNull
            override fun decode(json: Json): A? = when (json) {
                is JsonNull -> null
                else -> codec.decode(json)
            }
        }

    }
}

