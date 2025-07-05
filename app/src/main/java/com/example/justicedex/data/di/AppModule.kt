package com.example.justicedex.data.di

import androidx.compose.ui.tooling.preview.Preview
import com.example.justicedex.data.api.HeroApi
import com.example.justicedex.data.repository.RepositoryImpl
import com.example.justicedex.domain.repository.HeroRepository
import com.example.justicedex.domain.usecase.GetHerosUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHeroApi(): HeroApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://akabab.github.io/superhero-api/api/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(HeroApi::class.java)
        return retrofit

    }

    @Provides
    @Singleton
    fun provideHeroRepository(heroApi: HeroApi): HeroRepository {
        return RepositoryImpl(heroApi)
    }



    @Provides
    @Singleton
    fun provideGetHeroesUseCase(repository: HeroRepository): GetHerosUseCase {
        return GetHerosUseCase(repository)
    }



}