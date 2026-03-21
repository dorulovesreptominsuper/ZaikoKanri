package com.example.zaikokanri

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import zaikokanri.composeapp.generated.resources.Res
import zaikokanri.composeapp.generated.resources.noto_sans_jp_regular


@Composable
fun AppTheme(content: @Composable () -> Unit) {
    // リソースからフォントを生成
    val fontFamily = FontFamily(
        Font(resource = Res.font.noto_sans_jp_regular, weight = FontWeight.Normal)
    )
    val defaultStyle = TextStyle(fontFamily = fontFamily)

    val typography = Typography(
        bodyLarge = defaultStyle,
        bodyMedium = defaultStyle,
        bodySmall = defaultStyle,
        labelLarge = defaultStyle,
        labelMedium = defaultStyle,
        labelSmall = defaultStyle,
        displayLarge = defaultStyle,
        displayMedium = defaultStyle,
        displaySmall = defaultStyle,
        headlineLarge = defaultStyle,
        headlineMedium = defaultStyle,
        headlineSmall = defaultStyle,
        titleLarge = defaultStyle,
        titleMedium = defaultStyle,
        titleSmall = defaultStyle,
    )

    MaterialTheme(
        typography = typography,
        content = content
    )
}