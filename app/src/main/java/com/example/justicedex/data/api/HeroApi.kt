package com.example.justicedex.data.api

import com.example.justicedex.domain.model.Hero
import retrofit2.http.GET

interface HeroApi {
    @GET("all.json")
    suspend fun getHeroes(): List<Hero>

}