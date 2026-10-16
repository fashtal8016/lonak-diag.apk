package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ObdStandardDefinitions
import com.example.ui.theme.DiagBorder
import com.example.ui.theme.DiagSurface
import com.example.ui.theme.DiagSurfaceElevated
import com.example.ui.theme.EcuReadGreen
import com.example.ui.theme.LonakBlue
import com.example.ui.theme.LonakCyan
import com.example.ui.theme.StandardRefBlue
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun ObdModesScreen(
    terminalLogsFlow: SharedFlow<String>,
    onSendTerminalCommand: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedTab by remember { mutableIntStateOf(0) }
    var inputCmd by remember { mutableStateOf("") }
    val logs = remember { mutableListOf<String>() }

    // Collect flow in logs
    val latestLog by terminalLogsFlow.collectAsState(initial = "")
    if (latestLog.isNotEmpty() && (logs.isEmpty() || logs.last() != latestLog)) {
        logs.add(latestLog)
        if (logs.size > 80) logs.removeAt(0)
    }

    val quickCommands = listOf("ATZ", "ATE0", "ATRV", "ATDP", "0100", "010C", "0105", "03", "04", "07", "0902")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                text = "پروتکل‌ها و ۱۰ مد کاری OBD + ترمینال ELM327",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Tabs: 10 Modes, Protocols, Terminal
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF0A0F1D),
            contentColor = LonakCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = LonakCyan
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("۱۰ مد کاری OBD", fontSize = 11.5.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("پروتکل‌های خودرو", fontSize = 11.5.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("ترمینال فرامین AT", fontSize = 11.5.sp) }
            )
        }

        when (selectedTab) {
            0 -> {
                // 10 OBD Modes List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ObdStandardDefinitions.modes) { mode ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DiagSurface)
                                .border(1.dp, DiagBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = mode.title,
                                        color = LonakCyan,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF1E293B))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "کد: ${mode.hexCode}",
                                            color = EcuReadGreen,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = mode.persianDescription,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = mode.technicalUsage,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )

                                Text(
                                    text = "نمونه دستور هگز: ${mode.sampleCommand}",
                                    color = StandardRefBlue,
                                    fontSize = 10.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // Protocols List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ObdStandardDefinitions.protocols) { proto ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DiagSurface)
                                .border(1.dp, DiagBorder, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = proto.standardName,
                                    color = LonakCyan,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = proto.description,
                                    color = Color.White,
                                    fontSize = 11.5.sp
                                )
                                Text(
                                    text = "پین‌های پورت OBDII: ${proto.pinOut}",
                                    color = StandardRefBlue,
                                    fontSize = 10.5.sp
                                )
                                Text(
                                    text = "خودروهای مرسوم: ${proto.typicalCars}",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Raw ELM327 Terminal & AT Console
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quick command chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickCommands) { cmd ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DiagSurfaceElevated)
                                    .border(1.dp, LonakCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = { onSendTerminalCommand(cmd) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text(cmd, color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }

                    // Terminal Output Window
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF060A12))
                            .border(1.dp, DiagBorder, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(logs) { line ->
                                val color = when {
                                    line.startsWith("CMD:") -> LonakCyan
                                    line.startsWith("RES:") -> EcuReadGreen
                                    line.startsWith("ERR:") -> Color(0xFFEF4444)
                                    else -> Color(0xFFCBD5E1)
                                }
                                Text(
                                    text = line,
                                    color = color,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Command Input Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputCmd,
                            onValueChange = { inputCmd = it },
                            placeholder = { Text("دستور AT یا هگز (مثلاً: 010C)", fontSize = 11.sp, color = Color(0xFF64748B)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LonakCyan,
                                unfocusedBorderColor = DiagBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Button(
                            onClick = {
                                if (inputCmd.isNotBlank()) {
                                    onSendTerminalCommand(inputCmd.trim())
                                    inputCmd = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LonakBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
