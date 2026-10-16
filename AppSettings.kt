package com.example.data.model

data class AppSettings(
    val fontSizeScale: Float = 1.0f,
    val ecuReadColorHex: String = "#00E676", // Green for ECU read values
    val standardValueColorHex: String = "#38BDF8", // Blue for standard values
    val paramNameColorHex: String = "#F1F5F9",
    val isBeepEnabled: Boolean = true,
    val isFullscreen: Boolean = false,
    val refreshRateMs: Long = 500L,
    val themePreset: String = "DARK_TACTILE"
)
