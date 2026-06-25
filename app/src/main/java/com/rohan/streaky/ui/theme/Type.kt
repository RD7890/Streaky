package com.rohan.streaky.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.rohan.streaky.R

val BoldFontFamily = FontFamily(
    Font(R.font.bold_font, FontWeight.Thin),
    Font(R.font.bold_font, FontWeight.ExtraLight),
    Font(R.font.bold_font, FontWeight.Light),
    Font(R.font.bold_font, FontWeight.Normal),
    Font(R.font.bold_font, FontWeight.Medium),
    Font(R.font.bold_font, FontWeight.SemiBold),
    Font(R.font.bold_font, FontWeight.Bold),
    Font(R.font.bold_font, FontWeight.ExtraBold),
    Font(R.font.bold_font, FontWeight.Black),
)

val StreakTypography = Typography(
    displayLarge  = TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.Black,     fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp),
    displayMedium = TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall  = TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.Bold,      fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge = TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.Bold,      fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium= TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.Bold,      fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall = TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.SemiBold,  fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge    = TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.SemiBold,  fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium   = TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.SemiBold,  fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp),
    titleSmall    = TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.Medium,    fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    bodyLarge     = TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.Normal,    fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
    bodyMedium    = TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.Normal,    fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
    bodySmall     = TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.Normal,    fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),
    labelLarge    = TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.SemiBold,  fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    labelMedium   = TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.Medium,    fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    labelSmall    = TextStyle(fontFamily = BoldFontFamily, fontWeight = FontWeight.Medium,    fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
)
