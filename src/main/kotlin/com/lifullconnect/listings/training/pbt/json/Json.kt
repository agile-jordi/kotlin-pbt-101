package com.lifullconnect.listings.training.pbt.json

sealed class Json

data class JsonObject(val fields: List<Pair<String, Json>>): Json(){
    override fun toString(): String = this.fields.joinToString(",", "{", "}"){
        "\"${it.first}\" : ${it.second}"
    }

}
data class JsonArray(val value: List<Json>): Json(){
    override fun toString(): String = this.value.joinToString(",", "[", "]")
}
object JsonNull: Json(){
    override fun toString(): String = "null"
}
data class JsonNumber(val value: String): Json(){
    override fun toString(): String = this.value
}
data class JsonString(val value: String): Json(){
    override fun toString(): String = "\"${this.value}\""
}
data class JsonBoolean(val value: Boolean): Json(){
    override fun toString(): String = this.value.toString()
}
