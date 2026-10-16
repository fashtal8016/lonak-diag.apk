package com.example.data.model

data class ActuatorItem(
    val id: String,
    val persianName: String,
    val category: String,
    val description: String,
    val isTesting: Boolean = false,
    val statusMessage: String = "آماده تست",
    val testModeType: ActuatorTestType = ActuatorTestType.PULSE_3S,
    val feedbackValue: String = "OFF",
    val standardRef: String = "-"
)

enum class ActuatorTestType {
    PULSE_3S,
    TOGGLE_ON_OFF,
    STEP_INCREMENT,
    PWM_SWEEP
}
