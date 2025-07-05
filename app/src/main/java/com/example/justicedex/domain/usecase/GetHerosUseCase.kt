package com.example.justicedex.domain.usecase

import com.example.justicedex.domain.model.Hero
import com.example.justicedex.domain.repository.HeroRepository
import javax.inject.Inject

class GetHerosUseCase @Inject constructor(
private val repository: HeroRepository
) {
    suspend operator fun invoke(): List<Hero> {
        return repository.getHeroes()
    }
}