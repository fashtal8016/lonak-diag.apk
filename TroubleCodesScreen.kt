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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiagnosticTroubleCode
import com.example.data.model.DtcStatus
import com.example.ui.theme.DiagAlertYellow
import com.example.ui.theme.DiagBorder
import com.example.ui.theme.DiagFaultRed
import com.example.ui.theme.DiagSurface
import com.example.ui.theme.DiagSurfaceElevated
import com.example.ui.theme.EcuReadGreen
import com.example.ui.theme.LonakBlue
import com.example.ui.theme.LonakCyan
import com.example.ui.theme.StandardRefBlue

@Composable
fun TroubleCodesScreen(
    troubleCodes: List<DiagnosticTroubleCode>,
    isScanning: Boolean,
    statusMessage: String,
    onScanClick: () -> Unit,
    onClearClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedTab by remember { mutableIntStateOf(0) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val filteredCodes = when (selectedTab) {
        1 -> troubleCodes.filter { it.status == DtcStatus.PERMANENT }
        2 -> troubleCodes.filter { it.status == DtcStatus.PENDING }
        else -> troubleCodes
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text("پاک کردن تمامی کدهای خطا (Mode 04)", fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                Text(
                    "آیا از پاک کردن خطاهای دائم و موقت ایسیو و خاموش کردن چراغ چک اطمینان دارید؟ توصیه می‌شود قبل از پاکسازی، سوئیچ خودرو باز و موتور خاموش باشد.",
                    color = Color(0xFFCBD5E1)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearConfirmDialog = false
                        onClearClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DiagFaultRed)
                ) {
                    Text("بله، پاک کن", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("انصراف", color = LonakCyan)
                }
            },
            containerColor = DiagSurfaceElevated
        )
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
                Text(
                    text = "عیب‌یابی و خواندن کدهای خطا (DTC)",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Action Buttons Row (Scan & Clear)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onScanClick,
                enabled = !isScanning,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_scan_dtcs"),
                colors = ButtonDefaults.buttonColors(containerColor = LonakBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isScanning) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("در حال اسکن...", fontSize = 12.sp)
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("اسکن مجدد خطاها", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Button(
                onClick = { showClearConfirmDialog = true },
                enabled = !isScanning && troubleCodes.isNotEmpty(),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_clear_dtcs"),
                colors = ButtonDefaults.buttonColors(containerColor = DiagFaultRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("پاک کردن خطاها (04)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // Status banner
        if (statusMessage.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    .border(1.dp, DiagBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(text = statusMessage, color = LonakCyan, fontSize = 11.5.sp)
            }
        }

        // Filter Tabs
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
                text = { Text("همه خطاها (${troubleCodes.size})", fontSize = 11.5.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "خطاهای دائم (${troubleCodes.count { it.status == DtcStatus.PERMANENT }})",
                        fontSize = 11.5.sp
                    )
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        "خطاهای موقت (${troubleCodes.count { it.status == DtcStatus.PENDING }})",
                        fontSize = 11.5.sp
                    )
                }
            )
        }

        // DTC Cards List
        if (filteredCodes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(DiagSurfaceElevated, RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EcuReadGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "هیچ کد خطایی در این بخش یافت نشد",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "سیستم مدیریت موتور در وضعیت عادی و پایدار کار می‌کند.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCodes) { dtc ->
                    DtcItemCard(dtc = dtc)
                }
            }
        }
    }
}

@Composable
fun DtcItemCard(dtc: DiagnosticTroubleCode) {
    val isPerm = dtc.status == DtcStatus.PERMANENT
    val statusColor = if (isPerm) DiagFaultRed else DiagAlertYellow

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DiagSurface)
            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(12.dp)
            .testTag("dtc_card_${dtc.code}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.2f))
                            .border(1.dp, statusColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = dtc.code,
                            color = statusColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dtc.category,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = dtc.status.persianTitle,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Persian Title (or Code if not recognized)
            Text(
                text = dtc.persianTitle.ifEmpty { dtc.code },
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            // English Technical description
            Text(
                text = dtc.englishDescription,
                color = StandardRefBlue,
                fontSize = 11.sp
            )

            // Possible causes
            if (dtc.possibleCauses.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0A0F1D), RoundedCornerShape(6.dp))
                        .padding(6.dp)
                ) {
                    Text(
                        text = "علت احتمالی: ${dtc.possibleCauses}",
                        color = Color(0xFFCBD5E1),
                        fontSize = 10.5.sp
                    )
                }
            }

            // Freeze Frame
            if (dtc.freezeFrameData.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for ((k, v) in dtc.freezeFrameData) {
                        Text(
                            text = "$k: $v",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
