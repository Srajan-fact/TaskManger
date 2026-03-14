package com.example.taskmanger.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── Typography (SOP v1.0) ─────────────────────────────────────
// Font: Outfit (SOP default). Using system SansSerif as stand-in.
// To enable Outfit: add `implementation("androidx.compose.ui:ui-text-google-fonts")`
// and configure a GoogleFont("Outfit") provider in this file.
val OutfitFont = FontFamily.SansSerif

val NeonTypography = Typography(
    // Hero / Screen Title  36sp  600  #C8FF00
    displayLarge   = TextStyle(fontFamily = OutfitFont, fontSize = 36.sp, fontWeight = FontWeight.SemiBold,  color = Accent),
    // Section Hero  32sp  600  #C8FF00
    displayMedium  = TextStyle(fontFamily = OutfitFont, fontSize = 32.sp, fontWeight = FontWeight.SemiBold,  color = Accent),
    // Add/Form headline  28sp  600  #C8FF00
    displaySmall   = TextStyle(fontFamily = OutfitFont, fontSize = 28.sp, fontWeight = FontWeight.SemiBold,  color = Accent),
    // Section Header  20sp  600  #FFFFFF
    headlineMedium = TextStyle(fontFamily = OutfitFont, fontSize = 20.sp, fontWeight = FontWeight.SemiBold,  color = TextPrimary),
    // Sub-header  16sp  500  #FFFFFF
    titleLarge     = TextStyle(fontFamily = OutfitFont, fontSize = 16.sp, fontWeight = FontWeight.Medium,    color = TextPrimary),
    // Card sub-title / list primary  14sp  500  #FFFFFF
    titleMedium    = TextStyle(fontFamily = OutfitFont, fontSize = 14.sp, fontWeight = FontWeight.Medium,    color = TextPrimary),
    // Body  14sp  400  #888888
    bodyLarge      = TextStyle(fontFamily = OutfitFont, fontSize = 14.sp, fontWeight = FontWeight.Normal,    color = TextSecondary),
    // Secondary body  12sp  400  #888888
    bodyMedium     = TextStyle(fontFamily = OutfitFont, fontSize = 12.sp, fontWeight = FontWeight.Normal,    color = TextSecondary),
    // Stat value / button  14sp  600  (context-dependent)
    labelLarge     = TextStyle(fontFamily = OutfitFont, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,  color = TextOnAccent),
    // Caption / metadata  11sp  400  #888888
    labelSmall     = TextStyle(fontFamily = OutfitFont, fontSize = 11.sp, fontWeight = FontWeight.Normal,    color = TextSecondary),
)