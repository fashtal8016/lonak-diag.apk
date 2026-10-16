package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ObdSensor
import com.example.ui.GraphChannel
import com.example.ui.theme.DiagBorder
import com.example.ui.theme.DiagSurface
import com.example.ui.theme.DiagSurfaceElevated
import com.example.ui.theme.LonakBlue
import com.example.ui.theme.LonakCyan

@Composable
fun SensorGraphScreen(
    isConnected: Boolean,
    graphChannels: List<GraphChannel>,
    allSensors: List<ObdSensor>,
    isPaused: Boolean,
    onTogglePause: () -> Unit,
    onClearHistory: () -> Unit,
    onAddChannel: (ObdSensor, Long) -> Unit,
    onRemoveChannel: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var showAddChannelDialog by remember { mutableStateOf(false) }

    val presetColors = listOf(
        0xFF00E5FF, 0xFFEF4444, 0xFF00E676, 0xFFFBBF24,
        0xFFD946EF, 0xFF38BDF8, 0xFFF97316, 0xFFA855F7
    )

    if (showAddChannelDialog) {
        Dialog(onDismissRequest = { showAddChannelDialog = false }) {
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
                            text = "افزودن سنسور به منحنی اسیلوسکوپ",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showAddChannelDialog = false }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val availableSensors = allSensors.filter { sensor ->
                        graphChannels.none { it.sensorId == sensor.id }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(availableSensors) { s ->
                            val colorIndex = (graphChannels.size % presetColors.size)
                            val assignedColor = presetColors[colorIndex]

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DiagSurface)
                                    .clickable {
                                        onAddChannel(s, assignedColor)
                                        showAddChannelDialog = false
                                    }
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(s.persianName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("${s.category} • ${s.unit}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                    }
                                    Text("افزودن +", color = LonakCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
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
        // Top Header
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
                Column {
                    Text(
                        text = "منحنی گرافیکی سنسورها (اسیلوسکوپ آنلاین)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${graphChannels.size} کانال همزمان فعال",
                        color = LonakCyan,
                        fontSize = 10.sp
                    )
                }
            }

            // Controls (Pause, Clear, Add)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onTogglePause,
                    modifier = Modifier
                        .size(34.dp)
                        .background(DiagSurfaceElevated, RoundedCornerShape(6.dp))
                        .testTag("btn_pause_graph")
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = if (isPaused) Color(0xFF00E676) else Color(0xFFFBBF24),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onClearHistory,
                    modifier = Modifier
                        .size(34.dp)
                        .background(DiagSurfaceElevated, RoundedCornerShape(6.dp))
                        .testTag("btn_clear_graph")
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White, modifier = Modifier.size(18.dp))
                }

                Button(
                    onClick = { showAddChannelDialog = true },
                    modifier = Modifier.height(34.dp).testTag("btn_add_graph_channel"),
                    colors = ButtonDefaults.buttonColors(containerColor = LonakBlue),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("افزودن سنسور", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // OSCILLOSCOPE CANVAS BOX
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF070B14))
                .border(1.5.dp, DiagBorder, RoundedCornerShape(12.dp))
                .testTag("oscilloscope_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw Grid Lines (tactical oscilloscope look)
                val gridColor = Color(0xFF1E293B).copy(alpha = 0.6f)
                val horizLines = 6
                for (i in 1..horizLines) {
                    val y = (h / (horizLines + 1)) * i
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                }

                val vertLines = 8
                for (j in 1..vertLines) {
                    val x = (w / (vertLines + 1)) * j
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1f
                    )
                }

                // Draw each channel curve
                for (channel in graphChannels) {
                    val history = channel.history
                    if (history.size < 2) continue

                    val path = Path()
                    val range = maxOf(1f, channel.maxVal - channel.minVal)
                    val stepX = w / (80f) // max history length

                    for (idx in history.indices) {
                        val v = history[idx]
                        val normalizedY = ((v - channel.minVal) / range).coerceIn(0f, 1f)
                        val x = idx * stepX
                        val y = h - (normalizedY * (h - 20f)) - 10f

                        if (idx == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = Color(channel.colorHex),
                        style = Stroke(width = 2.5f)
                    )
                }
            }

            if (!isConnected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                        .border(1.dp, Color(0xFF64748B), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "سیگنال قطع است - ماژول ELM327 متصل نیست",
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "جهت دریافت سیگنال و ترسیم منحنی‌ها، از منوی بالا به دیاگ متصل شوید.",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Status label inside canvas (e.g. LIVE / PAUSED / DISCONNECTED)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (!isConnected) Color(0xFF64748B).copy(alpha = 0.2f)
                        else if (isPaused) Color(0xFFEF4444).copy(alpha = 0.2f)
                        else Color(0xFF00E676).copy(alpha = 0.2f)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (!isConnected) "DISCONNECTED" else if (isPaused) "PAUSED" else "LIVE OSCILLOSCOPE",
                    color = if (!isConnected) Color(0xFF94A3B8) else if (isPaused) Color(0xFFEF4444) else Color(0xFF00E676),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Channel Legend & Cards (with Remove & Live Value)
        Text(
            text = "راهنمای کانال‌ها و مقادیر لحظه‌ای (حداقل ۵ نمودار پیش‌فرض):",
            color = Color(0xFFCBD5E1),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(graphChannels) { channel ->
                val currentVal = channel.history.lastOrNull()
                val minVal = channel.history.minOrNull()
                val maxVal = channel.history.maxOrNull()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DiagSurface)
                        .border(1.dp, Color(channel.colorHex).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Channel color dot
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color(channel.colorHex), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = channel.sensorName,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isConnected && currentVal != null) {
                                        "مقدار جاری: ${String.format("%.1f", currentVal)} ${channel.unit}"
                                    } else {
                                        "مقدار جاری: -- ${channel.unit} (قطع)"
                                    },
                                    color = if (isConnected) Color(channel.colorHex) else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "حداقل: ${if (isConnected && minVal != null) String.format("%.1f", minVal) else "--"}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 9.5.sp
                                )
                                Text(
                                    text = "حداکثر: ${if (isConnected && maxVal != null) String.format("%.1f", maxVal) else "--"}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 9.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Delete channel button
                            IconButton(
                                onClick = { onRemoveChannel(channel.sensorId) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
