package com.bhub.foodi.networkservice

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class DataConverterFactory(private val gson: Gson) : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        return Converter<ResponseBody, Any> { body ->
            val jsonElement = gson.fromJson(body.charStream(), JsonObject::class.java)
            val dataElement = jsonElement.getAsJsonObject("data")
            gson.fromJson(dataElement, type)
        }
    }
}
