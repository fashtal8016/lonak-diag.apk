package com.example.data.model

data class ObdSensor(
    val id: String,
    val pid: Int, // Hex PID like 0x0C, 0x0D, or custom proprietary PID
    val pidHex: String, // e.g. "0x0C"
    val name: String,
    val persianName: String,
    val unit: String,
    val minStandard: Float,
    val maxStandard: Float,
    val standardDisplay: String,
    val currentValue: Float = 0f,
    val displayValue: String = "--",
    val isNormal: Boolean = true,
    val isSelected: Boolean = true,
    val category: String = "موتور",
    val formulaDesc: String = ""
)
