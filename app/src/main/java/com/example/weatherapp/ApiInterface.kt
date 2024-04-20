package com.example.weatherapp


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    fun getData(
        @Query("q") cityname:String,
        @Query("appid") appid:String,
        @Query("units") units:String
    ) : Call<weatherApp>
}