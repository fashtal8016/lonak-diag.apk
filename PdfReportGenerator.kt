package com.example.data.obd

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.DiagnosticTroubleCode
import com.example.data.model.EcuInfo
import com.example.data.model.ObdSensor
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfReportGenerator(private val context: Context) {

    fun generateReport(
        ecuInfo: EcuInfo?,
        dtcList: List<DiagnosticTroubleCode>,
        sensorList: List<ObdSensor>,
        technicianName: String = "تکنسین دیاگ لوناک",
        notes: String = "سیستم برق و سوخت‌رسانی بررسی شد."
    ): File? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 portrait size
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply {
            isAntiAlias = true
        }

        // Draw header background (Dark Navy #0B111E)
        paint.color = Color.rgb(11, 17, 30)
        canvas.drawRect(0f, 0f, 595f, 90f, paint)

        // Draw Cyan accent line
        paint.color = Color.rgb(0, 229, 255)
        paint.strokeWidth = 3f
        canvas.drawLine(0f, 90f, 595f, 90f, paint)

        // Draw Title "lonak diag"
        paint.color = Color.WHITE
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("lonak diag", 24f, 42f, paint)

        paint.textSize = 12f
        paint.color = Color.rgb(0, 229, 255)
        canvas.drawText("PROFESSIONAL AUTOMOTIVE DIAGNOSTIC REPORT", 24f, 62f, paint)

        // Date & Time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateStr = dateFormat.format(Date())
        paint.color = Color.rgb(203, 213, 225)
        paint.textSize = 10f
        canvas.drawText("تاریخ: $dateStr", 420f, 42f, paint)
        canvas.drawText("تکنسین: $technicianName", 420f, 60f, paint)

        var y = 115f

        // SECTION 1: Vehicle & ECU Info Box
        paint.color = Color.rgb(241, 245, 249)
        canvas.drawRect(24f, y, 571f, y + 80f, paint)

        paint.color = Color.rgb(203, 213, 225)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRect(24f, y, 571f, y + 80f, paint)
        paint.style = Paint.Style.FILL

        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("مشخصات خودرو و واحد کنترل الکترونیکی (ECU)", 32f, y + 20f, paint)

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val ecuName = ecuInfo?.let { "${it.brand} ${it.model} - ${it.vehicleModel}" } ?: "بوش ME7.4.4 - پژو ۲۰۶"
        val vinCode = ecuInfo?.vin ?: "NAAP01FE8CJ192837"
        val proto = ecuInfo?.protocol ?: "ISO 14230 (KWP2000)"
        val calib = ecuInfo?.calibrationId ?: "CAL_1037374820"

        canvas.drawText("مدل ایسیو: $ecuName", 32f, y + 40f, paint)
        canvas.drawText("شماره شاسی (VIN): $vinCode", 32f, y + 58f, paint)
        canvas.drawText("پروتکل: $proto", 320f, y + 40f, paint)
        canvas.drawText("کالیبراسیون: $calib", 320f, y + 58f, paint)

        y += 105f

        // SECTION 2: Fault Codes (DTCs)
        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("کدهای خطای ثبت شده (DTCs)", 24f, y, paint)

        y += 15f
        // Table Header
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawRect(24f, y, 571f, y + 22f, paint)
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("کد", 32f, y + 15f, paint)
        canvas.drawText("عنوان خطا (فارسی)", 100f, y + 15f, paint)
        canvas.drawText("دسته‌بندی", 380f, y + 15f, paint)
        canvas.drawText("وضعیت", 490f, y + 15f, paint)

        y += 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        if (dtcList.isEmpty()) {
            paint.color = Color.rgb(16, 185, 129)
            paint.textSize = 11f
            canvas.drawText("هیچ کد خطایی در حافظه ایسیو یافت نشد. سیستم در وضعیت سالم است.", 32f, y + 15f, paint)
            y += 25f
        } else {
            val shownDtcs = dtcList.take(6)
            for (dtc in shownDtcs) {
                paint.color = Color.rgb(239, 68, 68)
                paint.textSize = 10f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(dtc.code, 32f, y + 14f, paint)

                paint.color = Color.rgb(30, 41, 59)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                val safeTitle = if (dtc.persianTitle.length > 40) dtc.persianTitle.substring(0, 37) + "..." else dtc.persianTitle
                canvas.drawText(safeTitle, 100f, y + 14f, paint)
                canvas.drawText(dtc.category, 380f, y + 14f, paint)

                paint.color = if (dtc.status.name == "PERMANENT") Color.rgb(185, 28, 28) else Color.rgb(217, 119, 6)
                canvas.drawText(dtc.status.persianTitle, 490f, y + 14f, paint)

                y += 20f
            }
        }

        y += 15f

        // SECTION 3: Live Sensor Parameters Table
        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("پارامترهای زنده سنسورها (Live Telemetry)", 24f, y, paint)

        y += 15f
        paint.color = Color.rgb(226, 232, 240)
        canvas.drawRect(24f, y, 571f, y + 22f, paint)
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("نام سنسور / مشخصه", 32f, y + 15f, paint)
        canvas.drawText("مقدار خوانده شده (ECU)", 230f, y + 15f, paint)
        canvas.drawText("مقدار استاندارد (مرجع)", 370f, y + 15f, paint)
        canvas.drawText("وضعیت", 500f, y + 15f, paint)

        y += 24f

        val previewSensors = sensorList.take(15)
        for (sensor in previewSensors) {
            paint.color = Color.rgb(30, 41, 59)
            paint.textSize = 9.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(sensor.persianName, 32f, y + 13f, paint)

            // ECU read value in GREEN
            paint.color = Color.rgb(16, 185, 129)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("${sensor.displayValue} ${sensor.unit}", 230f, y + 13f, paint)

            // Standard range in BLUE
            paint.color = Color.rgb(2, 132, 199)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            val stdText = if (sensor.standardDisplay.length > 25) sensor.standardDisplay.substring(0, 22) + "..." else sensor.standardDisplay
            canvas.drawText(stdText, 370f, y + 13f, paint)

            paint.color = if (sensor.isNormal) Color.rgb(22, 101, 52) else Color.rgb(185, 28, 28)
            canvas.drawText(if (sensor.isNormal) "عادی" else "هشدار", 500f, y + 13f, paint)

            // faint separator line
            paint.color = Color.rgb(241, 245, 249)
            canvas.drawLine(24f, y + 17f, 571f, y + 17f, paint)

            y += 18f
        }

        // Footer / Stamp Box
        y = 780f
        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("تهیه شده توسط نرم‌افزار lonak diag - نسخه ۱.۵ (پشتیبانی از انواع ایسیو بوش، زیمنس، والئو، ساژم)", 24f, y, paint)
        canvas.drawText("امضا و تایید تکنسین: __________________________", 340f, y, paint)

        document.finishPage(page)

        // Save PDF to cache/files dir
        val outputDir = File(context.cacheDir, "reports")
        if (!outputDir.exists()) outputDir.mkdirs()
        val file = File(outputDir, "lonak_diag_report_${System.currentTimeMillis()}.pdf")
        return try {
            val fos = FileOutputStream(file)
            document.writeTo(fos)
            document.close()
            fos.close()
            file
        } catch (e: Exception) {
            document.close()
            null
        }
    }

    fun sharePdf(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "اشتراک‌گذاری گزارش دیاگ lonak").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            // Fallback view intent
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(viewIntent)
        }
    }
}
