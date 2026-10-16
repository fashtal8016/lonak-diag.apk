package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EcuDatabase
import com.example.data.model.EcuInfo
import com.example.ui.theme.DiagBorder
import com.example.ui.theme.DiagSurface
import com.example.ui.theme.DiagSurfaceElevated
import com.example.ui.theme.EcuReadGreen
import com.example.ui.theme.LonakBlue
import com.example.ui.theme.LonakCyan
import com.example.ui.theme.StandardRefBlue

@Composable
fun EcuDetectionScreen(
    isConnected: Boolean,
    detectedEcu: EcuInfo?,
    isDetecting: Boolean,
    progressText: String,
    onDetectClick: () -> Unit,
    onSelectEcu: (EcuInfo) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedBrand by remember { mutableStateOf("همه") }
    var expandedEcuModel by remember { mutableStateOf<String?>(null) }
    var showExplanationDialog by remember { mutableStateOf(false) }

    val brands = listOf("همه", "Bosch", "Sagem", "Valeo", "Siemens", "Ezu")

    val filteredEcus = EcuDatabase.predefinedEcus.filter { ecu ->
        val matchesBrand = (selectedBrand == "همه" || ecu.brand.equals(selectedBrand, ignoreCase = true))
        val matchesSearch = searchQuery.isBlank() ||
                ecu.model.contains(searchQuery, ignoreCase = true) ||
                ecu.brand.contains(searchQuery, ignoreCase = true) ||
                ecu.vehicleModel.contains(searchQuery, ignoreCase = true) ||
                ecu.hardwareId.contains(searchQuery, ignoreCase = true) ||
                ecu.calibrationId.contains(searchQuery, ignoreCase = true) ||
                ecu.protocol.contains(searchQuery, ignoreCase = true)
        matchesBrand && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Disconnected Notice & Explanation Bar
        if (!isConnected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF241818))
                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ماژول ELM327 متصل نیست. برای شناسایی خودرو یا تنظیم پروتکل‌ها، ابتدا از منوی بالا به وای‌فای، بلوتوث یا شبیه‌ساز متصل شوید.",
                            color = Color(0xFFFCA5A5),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp).testTag("btn_back")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = LonakCyan
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "شناسایی و هماهنگی پروتکل‌های ECU",
                        color = Color.White,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "هماهنگ‌سازی ماژول ELM327 v1.5 با پروتکل‌های بوش، زیمنس، والئو، ساژم و EZU",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
            }
        }

        // AUTO-DETECT ACTION BUTTON
        Button(
            onClick = onDetectClick,
            enabled = !isDetecting,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("btn_auto_detect_ecu"),
            colors = ButtonDefaults.buttonColors(containerColor = LonakBlue),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (isDetecting) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("در حال اسکن فرکانس‌ها و مچ پروتکل‌های ELM327...", fontSize = 11.5.sp)
            } else {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("شناسایی خودکار ECU و تنظیم اتوماتیک پروتکل ELM327", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Scanning Status message
        if (progressText.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    .border(1.dp, LonakCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDetecting) Icons.Default.Refresh else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isDetecting) LonakCyan else EcuReadGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = progressText,
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // CURRENTLY MATCHED / IDENTIFIED ECU DETAILS CARD
        if (detectedEcu != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(DiagSurfaceElevated, DiagSurface)
                        )
                    )
                    .border(1.5.dp, EcuReadGreen.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
                    .testTag("identified_ecu_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(EcuReadGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = null,
                                    tint = EcuReadGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${detectedEcu.brand} ${detectedEcu.model}",
                                    color = EcuReadGreen,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = detectedEcu.vehicleModel,
                                    color = Color.White,
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EcuReadGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "پروتکل فعال",
                                color = EcuReadGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // ELM327 Specific Coordination Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, LonakCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(6.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "فرمان ماژول: ${detectedEcu.elmProtocolCmd} (${detectedEcu.elmHeaderCmd})",
                                    color = LonakCyan,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = detectedEcu.busType,
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 9.5.sp
                                )
                            }
                            Text(
                                text = "وضعیت: ${detectedEcu.elmCompatibilityStatus}",
                                color = Color(0xFF86EFAC),
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Detail Specs Table
                    EcuSpecRow(label = "بازه سال تولید:", value = detectedEcu.yearRange)
                    EcuSpecRow(label = "شماره سخت‌افزار (HW):", value = detectedEcu.hardwareId)
                    EcuSpecRow(label = "کد نرم‌افزار / کالیبراسیون:", value = detectedEcu.calibrationId)
                    EcuSpecRow(label = "شماره شاسی استخراج‌شده (VIN):", value = detectedEcu.vin)
                    EcuSpecRow(label = "پروتکل ارتباطی:", value = detectedEcu.protocol)
                    EcuSpecRow(label = "نرخ تبادل داده (Baud):", value = detectedEcu.baudRate)

                    if (detectedEcu.keySensors.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "سنسورهای کلیدی: ",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                            Text(
                                text = detectedEcu.keySensors.joinToString("، "),
                                color = StandardRefBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (detectedEcu.notes.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0A0F1D), RoundedCornerShape(6.dp))
                                .padding(6.dp)
                        ) {
                            Text(
                                text = "توضیحات دیاگ: ${detectedEcu.notes}",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Search & Filter Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "بانک ایسیوها و پروتکل‌های سازگار (${filteredEcus.size} مدل):",
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("جستجوی مدل ایسیو (مثلاً: ME17، S2000، C11، C16، EZU، J35...)", fontSize = 11.sp, color = Color(0xFF64748B)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, null, tint = LonakCyan, modifier = Modifier.size(16.dp)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LonakCyan,
                unfocusedBorderColor = DiagBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // Brand Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(brands) { brand ->
                val isSel = (selectedBrand == brand)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSel) LonakCyan.copy(alpha = 0.2f) else DiagSurfaceElevated)
                        .border(1.dp, if (isSel) LonakCyan else DiagBorder, RoundedCornerShape(6.dp))
                        .clickable { selectedBrand = brand }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = brand,
                        color = if (isSel) LonakCyan else Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Database of ECU Architectures List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filteredEcus) { ecu ->
                val isCurrent = detectedEcu?.model == ecu.model && detectedEcu.brand == ecu.brand
                val isExpanded = expandedEcuModel == "${ecu.brand}_${ecu.model}"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isCurrent) Color(0xFF1E293B) else DiagSurface)
                        .border(
                            1.dp,
                            if (isCurrent) LonakCyan else DiagBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${ecu.brand} - ${ecu.model}",
                                        color = if (isCurrent) LonakCyan else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "(${ecu.yearRange})",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = ecu.vehicleModel,
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "پروتکل: ${ecu.protocol}",
                                        color = StandardRefBlue,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "فرمان: ${ecu.elmProtocolCmd}",
                                        color = LonakCyan,
                                        fontSize = 9.5.sp
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Match Protocol Action Button
                                Button(
                                    onClick = { onSelectEcu(ecu) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCurrent) EcuReadGreen.copy(alpha = 0.25f) else LonakBlue
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    if (isCurrent) {
                                        Icon(Icons.Default.CheckCircle, null, tint = EcuReadGreen, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("مچ شده", color = EcuReadGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.Cable, null, tint = Color.White, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("مچ پروتکل", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Details Toggle Button
                                IconButton(
                                    onClick = {
                                        expandedEcuModel = if (isExpanded) null else "${ecu.brand}_${ecu.model}"
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Details",
                                        tint = if (isExpanded) LonakCyan else Color(0xFF64748B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Expanded Technical Info & AT Commands Sequence
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF090E1A), RoundedCornerShape(6.dp))
                                    .border(1.dp, DiagBorder, RoundedCornerShape(6.dp))
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "توالی دستورات هماهنگی ماژول ELM327:",
                                    color = LonakCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = ecu.initSequence.joinToString(" -> "),
                                    color = Color(0xFF38BDF8),
                                    fontSize = 9.5.sp
                                )
                                Text(
                                    text = "گذرگاه: ${ecu.busType}",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 9.5.sp
                                )
                                Text(
                                    text = "وضعیت ماژول: ${ecu.elmCompatibilityStatus}",
                                    color = Color(0xFF86EFAC),
                                    fontSize = 9.5.sp
                                )
                                if (ecu.notes.isNotEmpty()) {
                                    Text(
                                        text = "توضیح: ${ecu.notes}",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 9.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EcuSpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 10.5.sp)
        Text(
            text = value,
            color = Color.White,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
