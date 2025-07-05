package com.example.justicedex.domain.model

data class Hero(
    val id: Int,
    val name:String,
    val biography: biography,
    val work: work,
    val images: image,
)

data class biography(
    val fullName:String,
    val alterEgos:String,
    val publisher:String,
)

data class work(
    val occupation:String,
)

data class image(
    val md:String,

)
