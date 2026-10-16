package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EcuInfo
import com.example.ui.DiagScreen
import com.example.ui.theme.DiagAlertYellow
import com.example.ui.theme.DiagBorder
import com.example.ui.theme.DiagFaultRed
import com.example.ui.theme.DiagSurface
import com.example.ui.theme.DiagSurfaceElevated
import com.example.ui.theme.EcuReadGreen
import com.example.ui.theme.LonakBlue
import com.example.ui.theme.LonakCyan
import com.example.ui.theme.StandardRefBlue

data class MainMenuItem(
    val id: DiagScreen,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val tag: String
)

@Composable
fun HomeScreen(
    isConnected: Boolean,
    detectedEcu: EcuInfo?,
    dtcCount: Int,
    rpmValue: String,
    coolantValue: String,
    batteryVoltage: String,
    onOpenConnection: () -> Unit,
    onNavigate: (DiagScreen) -> Unit,
    onExportPdf: () -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems = listOf(
        MainMenuItem(
            id = DiagScreen.ECU_IDENTIFY,
            title = "شناسایی ECU",
            subtitle = "شناسایی هوشمند انواع بوش، زیمنس، والئو، ساژم",
            icon = Icons.Default.DirectionsCar,
            accentColor = LonakCyan,
            tag = "btn_ecu_identify"
        ),
        MainMenuItem(
            id = DiagScreen.TROUBLE_CODES,
            title = "عیب‌یابی و خطاها",
            subtitle = "خواندن خطاهای دائم و موقت و پاکسازی خطا",
            icon = Icons.Default.ErrorOutline,
            accentColor = DiagFaultRed,
            tag = "btn_trouble_codes"
        ),
        MainMenuItem(
            id = DiagScreen.LIVE_PARAMETERS,
            title = "پارامترهای زنده",
            subtitle = "سنسورها، ولتاژها و مقادیر استاندارد (۱۰ در صفحه)",
            icon = Icons.Default.Speed,
            accentColor = EcuReadGreen,
            tag = "btn_live_parameters"
        ),
        MainMenuItem(
            id = DiagScreen.SENSOR_GRAPHS,
            title = "منحنی گرافیکی سنسورها",
            subtitle = "اسیلوسکوپ چندکاناله همزمان با امکان ادیت",
            icon = Icons.Default.ShowChart,
            accentColor = StandardRefBlue,
            tag = "btn_sensor_graphs"
        ),
        MainMenuItem(
            id = DiagScreen.ACTUATOR_TEST,
            title = "تست عملگرها",
            subtitle = "تست رله فن، پمپ بنزین، انژکتور، کویل و استپر",
            icon = Icons.Default.Build,
            accentColor = DiagAlertYellow,
            tag = "btn_actuator_test"
        ),
        MainMenuItem(
            id = DiagScreen.OBD_MODES,
            title = "پروتکل‌ها و ۱۰ مد OBD",
            subtitle = "بررسی مدهای ۱ تا ۱۰ و ترمینال فرامین مستقیم AT",
            icon = Icons.Default.Terminal,
            accentColor = Color(0xFFA855F7),
            tag = "btn_obd_modes"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // DISCONNECTED WARNING BANNER (Shows when not connected to ELM327)
        if (!isConnected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1B18))
                    .border(1.5.dp, DiagAlertYellow, RoundedCornerShape(12.dp))
                    .clickable { onOpenConnection() }
                    .padding(12.dp)
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
                            tint = DiagAlertYellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ماژول دیاگ متصل نیست (قطع)",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "پارامترها تا زمان اتصال به ELM327 مقداردهی نمی‌شوند. برای خواندن پارامترها متصل شوید.",
                                color = Color(0xFFFEF08A),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = onOpenConnection,
                        colors = ButtonDefaults.buttonColors(containerColor = LonakBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Wifi, null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("اتصال", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // TOP VEHICLE STATUS BANNER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(DiagSurfaceElevated, Color(0xFF1E293B))
                    )
                )
                .border(1.dp, DiagBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ایسیو شناسایی شده:",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        Text(
                            text = if (isConnected && detectedEcu != null) {
                                "${detectedEcu.brand} ${detectedEcu.model}"
                            } else {
                                "در انتظار اتصال به دیاگ"
                            },
                            color = if (isConnected) LonakCyan else Color(0xFF64748B),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isConnected && detectedEcu != null) {
                                detectedEcu.vehicleModel
                            } else {
                                "ماژول ELM327 متصل نیست"
                            },
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )
                    }

                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (!isConnected) Color(0xFF334155).copy(alpha = 0.5f)
                                else if (dtcCount > 0) DiagFaultRed.copy(alpha = 0.2f)
                                else EcuReadGreen.copy(alpha = 0.2f)
                            )
                            .border(
                                1.dp,
                                if (!isConnected) Color(0xFF64748B)
                                else if (dtcCount > 0) DiagFaultRed
                                else EcuReadGreen,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (!isConnected) Icons.Default.PowerSettingsNew else Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = if (!isConnected) Color(0xFF94A3B8) else if (dtcCount > 0) DiagFaultRed else EcuReadGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (!isConnected) {
                                    "قطع (Off-line)"
                                } else if (dtcCount > 0) {
                                    "$dtcCount خطای ثبت شده"
                                } else {
                                    "بدون خطا (سالم)"
                                },
                                color = if (!isConnected) Color(0xFF94A3B8) else if (dtcCount > 0) DiagFaultRed else EcuReadGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Quick Telemetry Pills (Live RPM, Coolant, Voltage)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // RPM
                    QuickTelemetryCard(
                        title = "دور موتور",
                        value = if (isConnected) rpmValue else "--",
                        unit = if (isConnected) "RPM" else "",
                        color = if (isConnected) EcuReadGreen else Color(0xFF64748B),
                        modifier = Modifier.weight(1f)
                    )
                    // Coolant
                    QuickTelemetryCard(
                        title = "دمای آب",
                        value = if (isConnected) coolantValue else "--",
                        unit = if (isConnected) "°C" else "",
                        color = if (isConnected) StandardRefBlue else Color(0xFF64748B),
                        modifier = Modifier.weight(1f)
                    )
                    // Battery
                    QuickTelemetryCard(
                        title = "ولتاژ باتری",
                        value = if (isConnected) batteryVoltage else "-- V",
                        unit = "",
                        color = if (isConnected) LonakCyan else Color(0xFF64748B),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section Title
        Text(
            text = "منوی اصلی دستگاه دیاگ lonak",
            color = Color(0xFFE2E8F0),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        // Main Menu Grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (item in menuItems) {
                MainMenuItemCard(
                    item = item,
                    onClick = { onNavigate(item.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Action Buttons: PDF Export & Settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onExportPdf,
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("btn_export_pdf"),
                colors = ButtonDefaults.buttonColors(containerColor = LonakBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("خروجی PDF گزارش", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = { onNavigate(DiagScreen.SETTINGS_BACKUP) },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("btn_settings_backup"),
                colors = ButtonDefaults.buttonColors(containerColor = DiagSurfaceElevated),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = LonakCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تنظیمات و آپدیت", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun QuickTelemetryCard(
    title: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0A0F1D))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(title, color = Color(0xFF94A3B8), fontSize = 10.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    color = color,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(unit, color = color.copy(alpha = 0.7f), fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
fun MainMenuItemCard(
    item: MainMenuItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DiagSurface)
            .border(1.dp, item.accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag(item.tag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tactical Icon Box
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(item.accentColor.copy(alpha = 0.15f))
                        .border(1.dp, item.accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = item.accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.subtitle,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }

            // Arrow pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ورود >",
                    color = item.accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
