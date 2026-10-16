package com.example.data.model

enum class DtcStatus(val persianTitle: String, val codeTag: String) {
    PERMANENT("دائم (فعال)", "PERM"),
    PENDING("موقت (در انتظار)", "PEND"),
    CONFIRMED("تایید شده در حافظه", "CONF")
}

data class DiagnosticTroubleCode(
    val code: String,
    val persianTitle: String,
    val englishDescription: String,
    val category: String,
    val status: DtcStatus,
    val possibleCauses: String = "",
    val freezeFrameData: Map<String, String> = emptyMap()
)
