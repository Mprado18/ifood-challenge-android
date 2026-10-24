package com.movies.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

// Helper que cria TextStyle com peso, tamanho e altura de linha.
private fun textStyle(weight: FontWeight, size: TextUnit, lineHeight: TextUnit) = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = weight,
    fontSize = size,
    lineHeight = lineHeight,
)

val MoviesTypography = Typography(
    headlineMedium = textStyle(FontWeight.Bold, 28.sp, 34.sp),
    titleLarge = textStyle(FontWeight.SemiBold, 22.sp, 28.sp),
    titleMedium = textStyle(FontWeight.SemiBold, 16.sp, 22.sp),
    bodyLarge = textStyle(FontWeight.Normal, 16.sp, 24.sp),
    bodyMedium = textStyle(FontWeight.Normal, 14.sp, 20.sp),
    labelMedium = textStyle(FontWeight.Medium, 12.sp, 16.sp),
)
