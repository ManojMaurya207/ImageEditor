package com.medprimetech.imageeditor.domain.model

data class FilterSettings(
    val brightness: Float = 0f,
    val contrast: Float = 1f,
    val exposure: Float = 0f,
    val saturation: Float = 1f
)