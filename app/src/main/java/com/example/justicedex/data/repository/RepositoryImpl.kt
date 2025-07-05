package com.example.justicedex.data.repository

import com.example.justicedex.data.api.HeroApi
import com.example.justicedex.domain.model.Hero
import com.example.justicedex.domain.repository.HeroRepository
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    val api : HeroApi
): HeroRepository {
    override suspend fun getHeroes(): List<Hero>{
    return api.getHeroes()
    }

}
