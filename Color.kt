package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Rugged Automotive Diagnostic Color Palette (lonak diag)
val DiagBackground = Color(0xFF090D16)      // Deep industrial dark
val DiagSurface = Color(0xFF101726)         // Diagnostic hardware card surface
val DiagSurfaceElevated = Color(0xFF18233A) // Elevated component / bezel
val DiagBorder = Color(0xFF243352)          // Tactical panel borders
val DiagBorderActive = Color(0xFF00E5FF)    // Active illuminated border

// Accent & Brand Colors
val LonakCyan = Color(0xFF00E5FF)           // High-tech Cyan
val LonakBlue = Color(0xFF0284C7)           // Automotive Navy/Blue
val LonakDarkNavy = Color(0xFF050B14)       // Top Header background

// Required Status & Value Colors
val EcuReadGreen = Color(0xFF00E676)        // "مقادیر خوانده شده از ecu رنگ سبز"
val StandardRefBlue = Color(0xFF38BDF8)     // "مقادیر استاندارد ابی"
val ParamLabelWhite = Color(0xFFF1F5F9)     // Crisp readable parameter font
val ParamLabelMuted = Color(0xFF94A3B8)     // Secondary detail / units

// Alert / Warning
val DiagAlertYellow = Color(0xFFFBBF24)
val DiagFaultRed = Color(0xFFEF4444)
val DiagSuccessGreen = Color(0xFF10B981)

// M3 Theme Base
val DarkPrimary = LonakCyan
val DarkOnPrimary = Color(0xFF00363D)
val DarkPrimaryContainer = Color(0xFF004F58)
val DarkOnPrimaryContainer = Color(0xFF9CF0FD)

val DarkSecondary = LonakBlue
val DarkOnSecondary = Color(0xFFFFFFFF)
val DarkSecondaryContainer = Color(0xFF0369A1)
val DarkOnSecondaryContainer = Color(0xFFE0F2FE)

val DarkTertiary = EcuReadGreen
val DarkOnTertiary = Color(0xFF003915)

val DarkBackground = DiagBackground
val DarkOnBackground = Color(0xFFE2E8F0)
val DarkSurface = DiagSurface
val DarkOnSurface = Color(0xFFF8FAFC)
val DarkSurfaceVariant = DiagSurfaceElevated
val DarkOnSurfaceVariant = Color(0xFFCBD5E1)
val DarkOutline = DiagBorder
