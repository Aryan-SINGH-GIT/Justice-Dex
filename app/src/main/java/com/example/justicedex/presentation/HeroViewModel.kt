package com.example.justicedex.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justicedex.domain.model.Hero
import com.example.justicedex.domain.usecase.GetHerosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HeroState {
    object Loading : HeroState()
    data class Success(val heroes: List<Hero>) : HeroState()
    data class Error(val message: String) : HeroState()
}

@HiltViewModel
class HeroViewModel @Inject constructor(
    private val usecase: GetHerosUseCase
) : ViewModel() {
    var uiState by mutableStateOf<HeroState>(HeroState.Loading)
        private set

    init {
        fetchHeroes()
    }

    fun fetchHeroes() {
        uiState = HeroState.Loading
        viewModelScope.launch {
            try {
                val heroes = usecase.invoke()
                uiState = HeroState.Success(heroes)
            } catch (e: Exception) {
                Log.e("HeroViewModel", "Error fetching heroes", e)
                val errorMessage = when (e) {
                    is retrofit2.HttpException -> "Server error: ${e.code()}"
                    is java.net.UnknownHostException -> "No internet connection"
                    else -> "Something went wrong: ${e.message}"
                }
                uiState = HeroState.Error(errorMessage)
            }
        }
    }

    fun retry() {
        fetchHeroes()
    }
}