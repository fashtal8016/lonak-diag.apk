package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppSettings
import com.example.data.model.ObdSensor
import com.example.ui.theme.DiagBorder
import com.example.ui.theme.DiagSurface
import com.example.ui.theme.DiagSurfaceElevated
import com.example.ui.theme.EcuReadGreen
import com.example.ui.theme.LonakBlue
import com.example.ui.theme.LonakCyan
import com.example.ui.theme.ParamLabelWhite
import com.example.ui.theme.StandardRefBlue

import androidx.compose.material.icons.filled.Warning

@Composable
fun LiveParametersScreen(
    isConnected: Boolean,
    allSensors: List<ObdSensor>,
    currentPage: Int,
    searchQuery: String,
    settings: AppSettings,
    onPageChange: (Int) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleSelectSensor: (String) -> Unit,
    onSelectAllSensors: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var showEditDialog by remember { mutableStateOf(false) }

    val activeSelectedSensors = allSensors.filter { it.isSelected }
    val filteredList = if (searchQuery.isEmpty()) {
        activeSelectedSensors
    } else {
        activeSelectedSensors.filter {
            it.persianName.contains(searchQuery) || it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    val pageSize = 10
    val totalPages = maxOf(1, (filteredList.size + pageSize - 1) / pageSize)
    val safePage = currentPage.coerceIn(0, totalPages - 1)
    val startIndex = safePage * pageSize
    val pageItems = filteredList.drop(startIndex).take(pageSize)

    val ecuColor = Color(android.graphics.Color.parseColor(settings.ecuReadColorHex))
    val stdColor = Color(android.graphics.Color.parseColor(settings.standardValueColorHex))
    val labelColor = Color(android.graphics.Color.parseColor(settings.paramNameColorHex))

    // EDIT / CUSTOMIZE SENSORS DIALOG
    if (showEditDialog) {
        Dialog(onDismissRequest = { showEditDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DiagSurfaceElevated)
                    .border(1.dp, LonakCyan, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "انتخاب و ویرایش پارامترها (${activeSelectedSensors.size} از ${allSensors.size})",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showEditDialog = false }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(onClick = { onSelectAllSensors(true) }) {
                            Text("انتخاب همه", color = LonakCyan, fontSize = 11.sp)
                        }
                        TextButton(onClick = { onSelectAllSensors(false) }) {
                            Text("عدم انتخاب همه", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(allSensors) { sensor ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DiagSurface)
                                    .clickable { onToggleSelectSensor(sensor.id) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = sensor.isSelected,
                                    onCheckedChange = { onToggleSelectSensor(sensor.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = LonakCyan,
                                        uncheckedColor = Color(0xFF64748B)
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = sensor.persianName,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${sensor.pidHex} • ${sensor.unit}",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showEditDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = LonakBlue)
                    ) {
                        Text("تایید و بستن", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Navigation & Title Bar
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
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "پارامترهای زنده موتور (Live Data)",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Edit / Select Parameters Button
            Button(
                onClick = { showEditDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = DiagSurfaceElevated),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp).testTag("btn_edit_params")
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = LonakCyan, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("انتخاب پارامترها", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("جستجوی پارامتر (مثلاً: اکسیژن، دمای آب، انژکتور...)", fontSize = 11.sp, color = Color(0xFF64748B)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, null, tint = LonakCyan, modifier = Modifier.size(18.dp)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LonakCyan,
                unfocusedBorderColor = DiagBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // Disconnected Warning Banner
        if (!isConnected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF241818))
                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ارتباط با ماژول ELM327 برقرار نیست. مقادیر سنسورها پس از اتصال به ماژول فعال و از ECU خوانده می‌شوند.",
                        color = Color(0xFFFCA5A5),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Table Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, DiagBorder, RoundedCornerShape(8.dp))
                .padding(vertical = 8.dp, horizontal = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "نام سنسور / پارامتر",
                    color = labelColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.3f)
                )
                Text(
                    text = "مقدار ECU",
                    color = ecuColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.0f)
                )
                Text(
                    text = "محدوده استاندارد",
                    color = stdColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.3f)
                )
            }
        }

        // 10 Parameters Per Page List
        if (pageItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("هیچ پارامتری برای نمایش پیدا نشد.", color = Color(0xFF94A3B8))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(pageItems) { sensor ->
                    ParameterRowCard(
                        sensor = sensor,
                        isConnected = isConnected,
                        labelColor = labelColor,
                        ecuColor = ecuColor,
                        stdColor = stdColor,
                        fontSizeScale = settings.fontSizeScale
                    )
                }
            }
        }

        // Pagination Bar (صفحه قبل / صفحه بعد - ۱۰ تا در هر صفحه)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DiagSurfaceElevated)
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { if (safePage > 0) onPageChange(safePage - 1) },
                    enabled = safePage > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = LonakBlue),
                    modifier = Modifier.height(34.dp).testTag("btn_prev_page")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("صفحه قبل", fontSize = 11.sp)
                }

                Text(
                    text = "صفحه ${safePage + 1} از $totalPages (کل: ${filteredList.size} پارامتر)",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { if (safePage < totalPages - 1) onPageChange(safePage + 1) },
                    enabled = safePage < totalPages - 1,
                    colors = ButtonDefaults.buttonColors(containerColor = LonakBlue),
                    modifier = Modifier.height(34.dp).testTag("btn_next_page")
                ) {
                    Text("صفحه بعد", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun ParameterRowCard(
    sensor: ObdSensor,
    isConnected: Boolean,
    labelColor: Color,
    ecuColor: Color,
    stdColor: Color,
    fontSizeScale: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DiagSurface)
            .border(
                1.dp,
                if (!isConnected) DiagBorder else if (sensor.isNormal) DiagBorder else Color(0xFFEF4444).copy(alpha = 0.5f),
                RoundedCornerShape(8.dp)
            )
            .padding(vertical = 8.dp, horizontal = 10.dp)
            .testTag("param_row_${sensor.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column 1: Parameter Persian Name
            Column(modifier = Modifier.weight(1.3f)) {
                Text(
                    text = sensor.persianName,
                    color = labelColor,
                    fontSize = (12f * fontSizeScale).sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = sensor.category,
                    color = Color(0xFF64748B),
                    fontSize = (9.5f * fontSizeScale).sp
                )
            }

            // Column 2: ECU Read Value in GREEN (or offline indicator if disconnected)
            Column(modifier = Modifier.weight(1.0f)) {
                if (isConnected) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = sensor.displayValue,
                            color = ecuColor,
                            fontSize = (15f * fontSizeScale).sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = sensor.unit,
                            color = ecuColor.copy(alpha = 0.8f),
                            fontSize = (10f * fontSizeScale).sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "--",
                            color = Color(0xFF64748B),
                            fontSize = (14f * fontSizeScale).sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(قطع)",
                            color = Color(0xFF64748B),
                            fontSize = (9.5f * fontSizeScale).sp
                        )
                    }
                }
            }

            // Column 3: Standard Reference Range in BLUE
            Column(modifier = Modifier.weight(1.3f)) {
                Text(
                    text = sensor.standardDisplay,
                    color = stdColor,
                    fontSize = (11f * fontSizeScale).sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (isConnected && !sensor.isNormal) {
                    Text(
                        text = "خارج از حد مجاز",
                        color = Color(0xFFEF4444),
                        fontSize = (9f * fontSizeScale).sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
