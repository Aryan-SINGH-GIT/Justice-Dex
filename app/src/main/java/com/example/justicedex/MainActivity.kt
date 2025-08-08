package com.example.justicedex

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.justicedex.domain.model.Hero
import com.example.justicedex.presentation.HeroState
import com.example.justicedex.presentation.HeroViewModel
import com.example.justicedex.ui.theme.JusticeDexTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //JusticeDexTheme {
            JusticeDexTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(R.drawable.wallpaper),
                        contentDescription = "Background Image",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(3.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    )
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Black.copy(alpha = 0.3f)
                    ) {
                        HeroList()
                    }
                }
            }
        }
    }
}

@Composable
fun HeroList(viewModel: HeroViewModel = hiltViewModel()) {
    var selectedHero by remember { mutableStateOf<Hero?>(null) }
    var selectedIndex by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = viewModel.uiState) {
            is HeroState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is HeroState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.retry() },
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Text("Retry")
                    }
                }
            }
            is HeroState.Success -> {
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
                    itemsIndexed(state.heroes) { index, hero ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 50 * index)) +
                                    slideInVertically(initialOffsetY = { it / 2 })
                        ) {
                            HeroItem(hero) {
                                selectedHero = hero
                                selectedIndex = index
                            }
                        }
                    }
                }
            }
        }

        // Overlay detail when a hero is selected
        AnimatedVisibility(
            visible = selectedHero != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            selectedHero?.let { hero ->
                when (val state = viewModel.uiState) {
                    is HeroState.Success -> {
                        SwipeableHeroDetail(
                            heroes = state.heroes,
                            currentIndex = selectedIndex,
                            onIndexChange = { newIndex ->
                                // update both the index and selected hero in parent
                                Log.d("SwipeDebug", "Parent: Index changing from $selectedIndex to $newIndex")
                                selectedIndex = newIndex
                                selectedHero = state.heroes[newIndex]
                                Log.d("SwipeDebug", "Parent: Index changed to $selectedIndex, hero: ${selectedHero?.name}")
                            },
                            onDismiss = {
                                // dismiss overlay
                                selectedHero = null
                            }
                        )
                    }
                    else -> {
                        // Fallback simple dismissible card
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { selectedHero = null }
                                .padding(16.dp)
                        ) {
                            HeroDetailCard(hero)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroItem(hero: Hero, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Black, contentColor = Color.White)
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
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = hero.name,
                modifier = Modifier.padding(10.dp),
            )
        }
    }
}

/**
 * Fixed SwipeableHeroDetail:
 * - background overlay is clickable to dismiss (onDismiss)
 * - pointerInput for dragging is applied only to the card area so background clicks work
 * - animates out -> call onIndexChange -> preposition -> animate in
 * - prevents dismiss while dragging (enabled = !isDragging)
 */
@Composable
fun SwipeableHeroDetail(
    heroes: List<Hero>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val density = LocalDensity.current
    val screenWidth = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val scope = rememberCoroutineScope()

    val offsetXAnim = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background overlay (dismissible)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .clickable(enabled = !isDragging) { onDismiss() }
        )

        // Card container with gesture handling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center)
                .padding(16.dp)
                .pointerInput(heroes.size, currentIndex) { // <-- key includes currentIndex
                    detectHorizontalDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val newOffset = offsetXAnim.value + dragAmount
                            scope.launch {
                                offsetXAnim.snapTo(
                                    when {
                                        currentIndex == 0 && newOffset > 0 -> newOffset * 0.3f
                                        currentIndex == heroes.size - 1 && newOffset < 0 -> newOffset * 0.3f
                                        else -> newOffset
                                    }
                                )
                            }
                        },
                        onDragCancel = {
                            isDragging = false
                            scope.launch { offsetXAnim.animateTo(0f, spring()) }
                        },
                        onDragEnd = {
                            val shouldNavigate = abs(offsetXAnim.value) > screenWidth * 0.25f

                            if (shouldNavigate) {
                                if (offsetXAnim.value > 0) {
                                    // Swipe right → previous
                                    val prevIndex = currentIndex - 1
                                    if (prevIndex >= 0) {
                                        scope.launch {
                                            offsetXAnim.animateTo(screenWidth, spring())
                                            onIndexChange(prevIndex)
                                            offsetXAnim.snapTo(-screenWidth * 0.2f)
                                            offsetXAnim.animateTo(0f, spring())
                                        }
                                    } else {
                                        scope.launch { offsetXAnim.animateTo(0f, spring()) }
                                    }
                                } else {
                                    // Swipe left → next
                                    val nextIndex = currentIndex + 1
                                    if (nextIndex <= heroes.size - 1) {
                                        scope.launch {
                                            offsetXAnim.animateTo(-screenWidth, spring())
                                            onIndexChange(nextIndex)
                                            offsetXAnim.snapTo(screenWidth * 0.2f)
                                            offsetXAnim.animateTo(0f, spring())
                                        }
                                    } else {
                                        scope.launch { offsetXAnim.animateTo(0f, spring()) }
                                    }
                                }
                            } else {
                                scope.launch { offsetXAnim.animateTo(0f, spring()) }
                            }

                            isDragging = false
                        }
                    )
                }
        ) {
            HeroDetailCard(
                hero = heroes[currentIndex],
                modifier = Modifier.graphicsLayer {
                    translationX = offsetXAnim.value
                    val progress = (abs(offsetXAnim.value) / screenWidth).coerceIn(0f, 1f)
                    val scale = 1f - (0.05f * progress)
                    scaleX = scale
                    scaleY = scale
                    alpha = 1f - (0.2f * progress)
                }
            )
        }

        // Navigation buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentIndex > 0) {
                IconButton(onClick = {
                    val prevIndex = currentIndex - 1
                    if (prevIndex >= 0) {
                        scope.launch {
                            offsetXAnim.animateTo(screenWidth, spring())
                            onIndexChange(prevIndex)
                            offsetXAnim.snapTo(-screenWidth * 0.2f)
                            offsetXAnim.animateTo(0f, spring())
                        }
                    }
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous", tint = Color.White)
                }
            }

            if (currentIndex < heroes.size - 1) {
                IconButton(onClick = {
                    val nextIndex = currentIndex + 1
                    if (nextIndex <= heroes.size - 1) {
                        scope.launch {
                            offsetXAnim.animateTo(-screenWidth, spring())
                            onIndexChange(nextIndex)
                            offsetXAnim.snapTo(screenWidth * 0.2f)
                            offsetXAnim.animateTo(0f, spring())
                        }
                    }
                }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.White)
                }
            }
        }
    }
}


@Composable
fun HeroDetailCard(hero: Hero, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Black, contentColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Image(
                painter = rememberAsyncImagePainter(hero.images.md),
                contentDescription = hero.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = hero.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Full Name: ${hero.biography.fullName}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Publisher: ${hero.biography.publisher}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Alter Egos: ${hero.biography.alterEgos}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Occupation: ${hero.work.occupation}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
