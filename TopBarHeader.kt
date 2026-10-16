package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.obd.ConnectionState
import com.example.data.obd.ConnectionStatus
import com.example.data.obd.ConnectionType
import com.example.ui.theme.DiagAlertYellow
import com.example.ui.theme.DiagBorder
import com.example.ui.theme.DiagFaultRed
import com.example.ui.theme.EcuReadGreen
import com.example.ui.theme.LonakCyan
import com.example.ui.theme.LonakDarkNavy

@Composable
fun TopBarHeader(
    connectionState: ConnectionState,
    onOpenConnectionDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "led_pulse"
    )

    val (ledColor, statusText) = when (connectionState.status) {
        ConnectionStatus.CONNECTED -> EcuReadGreen to (if (connectionState.isSimulation) "شبیه‌ساز متصل" else "متصل")
        ConnectionStatus.CONNECTING -> DiagAlertYellow to "در حال اتصال..."
        ConnectionStatus.ERROR -> DiagFaultRed to "خطای اتصال"
        ConnectionStatus.DISCONNECTED -> Color(0xFF64748B) to "قطع"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LonakDarkNavy,
                        Color(0xFF0F172A)
                    )
                )
            )
            .statusBarsPadding()
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(LonakCyan.copy(alpha = 0.4f), Color(0xFF1E293B), LonakCyan.copy(alpha = 0.4f))
                ),
                shape = androidx.compose.ui.graphics.RectangleShape
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // LEFT TOP: Connection Menu Button with WiFi / Bluetooth to ELM327 v1.5
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B).copy(alpha = 0.8f))
                    .border(1.dp, LonakCyan.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .clickable { onOpenConnectionDialog() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("top_connection_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Pulsing LED Indicator
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .scale(if (connectionState.status == ConnectionStatus.CONNECTED) pulseScale else 1f)
                            .background(ledColor, CircleShape)
                    )

                    val connIcon = when (connectionState.type) {
                        ConnectionType.WIFI -> Icons.Default.Wifi
                        ConnectionType.BLUETOOTH -> Icons.Default.Bluetooth
                        ConnectionType.SIMULATION -> Icons.Default.FlashOn
                    }

                    Icon(
                        imageVector = connIcon,
                        contentDescription = "Connection Type",
                        tint = LonakCyan,
                        modifier = Modifier.size(16.dp)
                    )

                    Column {
                        Text(
                            text = "اتصال ELM327 v1.5",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = statusText,
                                color = ledColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (connectionState.status == ConnectionStatus.CONNECTED) {
                                Text(
                                    text = " • ${connectionState.batteryVoltage}",
                                    color = EcuReadGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // RIGHT TOP: "lonak diag" Logo & Brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.testTag("top_logo_section")
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Lonakdiag",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "پایانه دیاگ و عیب‌یابی",
                        color = LonakCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Sleek Diagnostic Hardware Logo Badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0284C7), LonakCyan)
                            )
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_lonak_logo),
                        contentDescription = "lonak diag logo",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                }
            }
        }
    }
}
