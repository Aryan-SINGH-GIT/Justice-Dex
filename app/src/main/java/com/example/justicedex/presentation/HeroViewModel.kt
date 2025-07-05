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

@HiltViewModel
class HeroViewModel @Inject constructor(
    val usecase: GetHerosUseCase
):ViewModel() {
    var herolist by mutableStateOf<List<Hero>>(emptyList())
        private set

    val isLoading = mutableStateOf(false)

    init {
        fetchApi()
    }
    fun fetchApi(){
        isLoading.value=true
        viewModelScope.launch {
            try{
                herolist=usecase.invoke()
            }catch (e:Exception){
                Log.d("api", "fetchApi: $e")
            }finally {
                isLoading.value=false

            }
        }


    }
}