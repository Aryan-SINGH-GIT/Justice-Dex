package com.example.justicedex.domain.repository

import com.example.justicedex.domain.model.Hero

interface HeroRepository {
    suspend fun getHeroes(): List<Hero>
}