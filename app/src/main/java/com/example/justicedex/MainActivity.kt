package com.example.justicedex

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.justicedex.domain.model.Hero
import com.example.justicedex.presentation.HeroViewModel
import com.example.justicedex.ui.theme.JusticeDexTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.grid.itemsIndexed


import androidx.compose.runtime.setValue


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JusticeDexTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(R.drawable.wallpaper),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                        .blur(3.dp,edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    )
                    HeroList()
                }






            }
        }
    }
}


@Composable
fun Greeting() {
    Text(
        text = "Hello ",

    )
}

@Composable
fun HeroList(viewModel: HeroViewModel = hiltViewModel()) {
    val heroes = viewModel.herolist
    val loading = viewModel.isLoading.value
    var selectedHero by remember { mutableStateOf<Hero?>(null) }
    Box(modifier = Modifier.fillMaxSize()) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (selectedHero != null) Modifier.blur(20.dp)
                        else Modifier
                    )
            ) {
                itemsIndexed(heroes) { index,hero ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 50 * index))+
                                slideInVertically(initialOffsetY = {it/2})

                    ) {
                        HeroItem(hero) {
                            selectedHero = hero
                        }
                    }

                }
            }
        }
        selectedHero?.let { hero ->
            AnimatedVisibility(
                visible = selectedHero != null,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight }, // from bottom
                    animationSpec = tween(1000)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight }, // slide out to bottom
                    animationSpec = tween(1000)
                ) + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)) // semi-transparent backdrop
                        .clickable { selectedHero = null }, // dismiss on click
                    contentAlignment = Alignment.BottomCenter // ✅ anchor at bottom
                ) {
                    HeroDetailCard(hero = selectedHero!!)
                }
            }


        }

    }


}


@Composable
fun HeroItem(hero: Hero, onClick: () -> Unit) {
    Log.d("HeroImage", "Image URL: ${hero.images.md}")
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardColors(
            Color.Black,
            contentColor = Color.White,
            disabledContainerColor = Color.White,
            disabledContentColor = Color.Black
        ),
        onClick = onClick

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = hero.images.md,
                    error = painterResource(R.drawable.ic_launcher_background),
                    placeholder = painterResource(R.drawable.ic_launcher_foreground)
                ),
                contentDescription = null,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )
            Text(
                text = hero.name,
                modifier = Modifier.padding(10.dp),
            )
        }
    }
}


@Composable
fun HeroDetailCard(hero: Hero) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),

        colors = CardColors(Color.Black,
            contentColor = Color.White,
            disabledContainerColor = Color.White,
            disabledContentColor = Color.Black
        ),




    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            Image(
                painter = rememberAsyncImagePainter(hero.images.md),
                contentDescription = hero.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = hero.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Full Name: ${hero.biography.fullName}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Publisher: ${hero.biography.publisher}",style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Alter Egos: ${hero.biography.alterEgos}",style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Occupation: ${hero.work.occupation}",style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(20.dp))

        }
    }

}


