package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.ui.theme.DiagAlertYellow
import com.example.ui.theme.DiagBorder
import com.example.ui.theme.DiagSurface
import com.example.ui.theme.DiagSurfaceElevated
import com.example.ui.theme.EcuReadGreen
import com.example.ui.theme.LonakBlue
import com.example.ui.theme.LonakCyan
import com.example.ui.theme.StandardRefBlue

@Composable
fun SettingsAndBackupScreen(
    currentSettings: AppSettings,
    onSaveSettings: (AppSettings) -> Unit,
    onExportJson: () -> String,
    onImportJson: (String) -> Unit,
    onResetToDefaults: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var fontSizeScale by remember { mutableFloatStateOf(currentSettings.fontSizeScale) }
    var isBeepEnabled by remember { mutableStateOf(currentSettings.isBeepEnabled) }
    var isFullscreen by remember { mutableStateOf(currentSettings.isFullscreen) }
    var jsonText by remember { mutableStateOf("") }
    var showImportBox by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp).testTag("btn_back")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LonakCyan)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "تنظیمات ظاهری، بکاپ و آپدیت کتابخانه OBD-II",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // SECTION 1: VISUAL SETTINGS (تنظیمات ظاهری و فونت)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DiagSurface)
                .border(1.dp, DiagBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, null, tint = LonakCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تنظیمات فونت و ظاهر پرتابل دیاگ",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Font Size Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("اندازه فونت پارامترها و متن‌ها:", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                        Text(
                            text = "${(fontSizeScale * 100).toInt()} %",
                            color = LonakCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = fontSizeScale,
                        onValueChange = {
                            fontSizeScale = it
                            onSaveSettings(currentSettings.copy(fontSizeScale = it))
                        },
                        valueRange = 0.85f..1.35f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = LonakCyan,
                            activeTrackColor = LonakCyan,
                            inactiveTrackColor = Color(0xFF1E293B)
                        )
                    )
                }

                // Color Standards Indicators
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0A0F1D), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("استاندارد رنگ‌های نمایش دیاگ خودرو:", color = Color(0xFF94A3B8), fontSize = 10.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).background(Color(0xFFF1F5F9), RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("نام پارامتر: نقره‌ای روشن", color = Color(0xFFF1F5F9), fontSize = 10.5.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).background(EcuReadGreen, RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("مقدار ECU: سبز", color = EcuReadGreen, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(12.dp).background(StandardRefBlue, RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("مقدار استاندارد: آبی", color = StandardRefBlue, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Audio Beep switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeUp, null, tint = LonakCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("صدای بازخورد صوتی و بوق دیاگ (Beep):", color = Color.White, fontSize = 11.5.sp)
                    }
                    Switch(
                        checked = isBeepEnabled,
                        onCheckedChange = {
                            isBeepEnabled = it
                            onSaveSettings(currentSettings.copy(isBeepEnabled = it))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = LonakCyan,
                            checkedTrackColor = Color(0xFF0284C7)
                        )
                    )
                }

                // Fullscreen mode toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("حالت تمام صفحه (سازگار با انواع گوشی و تبلت):", color = Color.White, fontSize = 11.5.sp)
                    Switch(
                        checked = isFullscreen,
                        onCheckedChange = {
                            isFullscreen = it
                            onSaveSettings(currentSettings.copy(isFullscreen = it))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = LonakCyan,
                            checkedTrackColor = Color(0xFF0284C7)
                        )
                    )
                }
            }
        }

        // SECTION 2: OBD-II LIBRARY UPDATE & BACKUP (آپدیت کتابخانه با فایل / JSON)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DiagSurface)
                .border(1.dp, DiagBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Download, null, tint = EcuReadGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "مدیریت و آپدیت کتابخانه OBD-II",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "می‌توانید تعاریف سنسورها، فرمول‌ها، رنج استاندارد و کدهای خطای فارسی را از طریق فایل JSON استخراج یا از فایل جدید به‌روزرسانی فرمایید.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Export Button
                    Button(
                        onClick = {
                            val json = onExportJson()
                            jsonText = json
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("obd_library", json)
                            clipboard.setPrimaryClip(clip)
                            showImportBox = true
                        },
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LonakBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Upload, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("خروجی و کپی JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Import Toggle Button
                    Button(
                        onClick = { showImportBox = !showImportBox },
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DiagSurfaceElevated),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Download, null, tint = LonakCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("آپدیت با فایل JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                if (showImportBox) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "محتوای JSON کتابخانه OBD-II (یا فایل پایتون استخراج‌شده):",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )

                        OutlinedTextField(
                            value = jsonText,
                            onValueChange = { jsonText = it },
                            placeholder = { Text("کد JSON را اینجا الصاق نمایید...", fontSize = 10.sp, color = Color(0xFF64748B)) },
                            modifier = Modifier.fillMaxWidth().height(140.dp),
                            maxLines = 10,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LonakCyan,
                                unfocusedBorderColor = DiagBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (jsonText.isNotBlank()) {
                                        onImportJson(jsonText)
                                    }
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("اعمال آپدیت کتابخانه", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onResetToDefaults,
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.8f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("بازنشانی به پیش‌فرض", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // SECTION 3: APP INFO (درباره lonak diag)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DiagSurfaceElevated)
                .border(1.dp, DiagBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "نرم‌افزار دیاگ تخصصی خودرو lonak diag",
                    color = LonakCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "نسخه ۱.۵ (پشتیبانی کامل از ماژول‌های ELM327 بلوتوث و وای‌فای با چیپست PIC18F25K80)",
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp
                )
                Text(
                    text = "پشتیبانی از انواع ایسیوهای بوش، زیمنس، والئو، ساژم، اس‌ست، دلفی و کروز از سال ۲۰۰۰ تا کنون.",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
