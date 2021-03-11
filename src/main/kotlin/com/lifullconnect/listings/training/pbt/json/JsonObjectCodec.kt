package com.lifullconnect.listings.training.pbt.json

object JsonObjectCodec {

    data class Field<A, B>(val name: String, val getter: (A) -> B, val codec: JsonCodec<B>) {
        fun encode(value: A): Pair<String, Json> = Pair(name, codec.encode(getter(value)))
        fun decode(json: JsonObject): B {
            val jsonField = json.fields.findLast { it.first == name }?.second ?: JsonNull
            return codec.decode(jsonField)
        }
    }


    fun <A, B> object1(field1: Field<A, B>, apply: (B) -> A) = object : JsonCodec<A> {
        override fun encode(value: A): Json = JsonObject(listOf(field1.encode(value)))
        override fun decode(json: Json): A = when (json) {
            is JsonObject -> apply(field1.decode(json))
            else -> throw DecodeException(json, "Expected an object")
        }
    }

    fun <A, B, C> object2(field1: Field<A, B>, field2: Field<A, C>, apply: (B, C) -> A) = object : JsonCodec<A> {
        override fun encode(value: A): Json = JsonObject(listOf(field1.encode(value), field2.encode(value)))
        override fun decode(json: Json): A = when (json) {
            is JsonObject -> apply(field1.decode(json), field2.decode(json))
            else -> throw DecodeException(json, "Expected an object")
        }
    }

    fun <A, B, C, D> object3(field1: Field<A, B>, field2: Field<A, C>, field3: Field<A, D>, apply: (B, C, D) -> A) =
        object : JsonCodec<A> {
            override fun encode(value: A): Json =
                JsonObject(listOf(field1.encode(value), field2.encode(value), field3.encode(value)))

            override fun decode(json: Json): A = when (json) {
                is JsonObject -> apply(field1.decode(json), field2.decode(json), field3.decode(json))
                else -> throw DecodeException(json, "Expected an object")
            }
        }

    fun <A, B, C, D, E> object4(
        field1: Field<A, B>,
        field2: Field<A, C>,
        field3: Field<A, D>,
        field4: Field<A, E>,
        apply: (B, C, D, E) -> A
    ) = object : JsonCodec<A> {
        override fun encode(value: A): Json = JsonObject(
            listOf(
                field1.encode(value),
                field2.encode(value),
                field3.encode(value),
                field4.encode(value)
            )
        )

        override fun decode(json: Json): A = when (json) {
            is JsonObject -> apply(
                field1.decode(json),
                field2.decode(json),
                field3.decode(json),
                field4.decode(json)
            )
            else -> throw DecodeException(json, "Expected an object")
        }
    }





}
