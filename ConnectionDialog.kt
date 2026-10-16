package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.data.obd.ConnectionState
import com.example.data.obd.ConnectionStatus
import com.example.data.obd.ConnectionType
import com.example.ui.theme.DiagBorder
import com.example.ui.theme.DiagFaultRed
import com.example.ui.theme.DiagSurface
import com.example.ui.theme.DiagSurfaceElevated
import com.example.ui.theme.EcuReadGreen
import com.example.ui.theme.LonakBlue
import com.example.ui.theme.LonakCyan
import com.example.ui.theme.StandardRefBlue

@Composable
fun ConnectionDialog(
    connectionState: ConnectionState,
    bondedDevices: List<Pair<String, String>>,
    onConnectWifi: (String, Int) -> Unit,
    onConnectBluetooth: (String, String) -> Unit,
    onConnectSimulation: () -> Unit,
    onDisconnect: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (connectionState.type == ConnectionType.BLUETOOTH) 1 else if (connectionState.type == ConnectionType.WIFI) 0 else 2) }
    var wifiIp by remember { mutableStateOf(connectionState.ipAddress.ifEmpty { "192.168.0.10" }) }
    var wifiPort by remember { mutableStateOf(connectionState.port.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DiagSurface)
                .border(1.5.dp, LonakCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp)
                .testTag("connection_dialog")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(LonakCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Wifi,
                                contentDescription = null,
                                tint = LonakCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تنظیمات اتصال ماژول ELM327 v1.5",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Current Connection Status Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DiagSurfaceElevated)
                        .border(1.dp, DiagBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "وضعیت جاری: ${connectionState.deviceName}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "پروتکل: ${connectionState.protocolName}",
                                color = StandardRefBlue,
                                fontSize = 11.sp
                            )
                        }

                        if (connectionState.status == ConnectionStatus.CONNECTED) {
                            Button(
                                onClick = onDisconnect,
                                colors = ButtonDefaults.buttonColors(containerColor = DiagFaultRed),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PowerSettingsNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("قطع اتصال", fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs: WiFi, Bluetooth, Simulation
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
                        text = { Text("وای‌فای (WiFi)", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Wifi, null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("بلوتوث (BT)", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Bluetooth, null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("شبیه‌ساز هوشمند", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.FlashOn, null, modifier = Modifier.size(16.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Content
                when (selectedTab) {
                    0 -> {
                        // WiFi Configuration
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "آدرس IP ماژول وای‌فای (پیش‌فرض: 192.168.0.10):",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                            OutlinedTextField(
                                value = wifiIp,
                                onValueChange = { wifiIp = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LonakCyan,
                                    unfocusedBorderColor = DiagBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Text(
                                text = "پورت ارتباطی (پیش‌فرض: 35000):",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                            OutlinedTextField(
                                value = wifiPort,
                                onValueChange = { wifiPort = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LonakCyan,
                                    unfocusedBorderColor = DiagBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = {
                                    val port = wifiPort.toIntOrNull() ?: 35000
                                    onConnectWifi(wifiIp.trim(), port)
                                    onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = LonakBlue)
                            ) {
                                Text("اتصال به ماژول با وای‌فای", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    1 -> {
                        // Bluetooth Configuration
                        Column {
                            Text(
                                text = "دستگاه‌های بلوتوث جفت‌شده (Bonded Devices):",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            if (bondedDevices.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(DiagSurfaceElevated, RoundedCornerShape(8.dp))
                                        .padding(14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "دستگاه جفت‌شده‌ای یافت نشد. ابتدا ELM327 را در تنظیمات بلوتوث گوشی با کد 1234 یا 0000 جفت نمایید.",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(bondedDevices) { dev ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(DiagSurfaceElevated)
                                                .border(1.dp, DiagBorder, RoundedCornerShape(6.dp))
                                                .clickable {
                                                    onConnectBluetooth(dev.second, dev.first)
                                                    onDismiss()
                                                }
                                                .padding(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = dev.first,
                                                        color = Color.White,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = dev.second,
                                                        color = Color(0xFF64748B),
                                                        fontSize = 10.sp
                                                    )
                                                }
                                                Text(
                                                    text = "اتصال",
                                                    color = LonakCyan,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Smart Simulation Mode
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "حالت شبیه‌ساز مهندسی ELM327 v1.5",
                                color = EcuReadGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "امکان تست کامل تمامی سنسورها، خطاهای ایسیو، اسیلوسکوپ و تست عملگرها با داده‌های زنده و واقعی خودرو بدون نیاز به سخت‌افزار فیزیکی.",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )

                            Button(
                                onClick = {
                                    onConnectSimulation()
                                    onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                            ) {
                                Icon(Icons.Default.FlashOn, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("فعال‌سازی شبیه‌ساز ELM327", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chipset & Protocol compatibility badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0A0F1D), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "پشتیبانی کامل از چیپست اصلی PIC18F25K80 با تمامی فرامین AT و پروتکل‌های CAN, KWP2000, ISO9141 و J1850",
                        color = Color(0xFF64748B),
                        fontSize = 9.5.sp
                    )
                }
            }
        }
    }
}
