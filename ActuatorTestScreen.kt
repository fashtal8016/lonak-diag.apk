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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActuatorItem
import com.example.ui.theme.DiagAlertYellow
import com.example.ui.theme.DiagBorder
import com.example.ui.theme.DiagSurface
import com.example.ui.theme.DiagSurfaceElevated
import com.example.ui.theme.EcuReadGreen
import com.example.ui.theme.LonakBlue
import com.example.ui.theme.LonakCyan
import com.example.ui.theme.StandardRefBlue

@Composable
fun ActuatorTestScreen(
    actuators: List<ActuatorItem>,
    currentPage: Int,
    onPageChange: (Int) -> Unit,
    onRunTest: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val pageSize = 10
    val totalPages = maxOf(1, (actuators.size + pageSize - 1) / pageSize)
    val safePage = currentPage.coerceIn(0, totalPages - 1)
    val startIndex = safePage * pageSize
    val pageItems = actuators.drop(startIndex).take(pageSize)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Navigation
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
                        text = "تست عملگرها (Actuator & Component Testing)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "فرمان مستقیم به رله‌ها، انژکتور، پمپ و شیربرقی‌ها",
                        color = DiagAlertYellow,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Safety Caution Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DiagAlertYellow.copy(alpha = 0.12f))
                .border(1.dp, DiagAlertYellow.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = DiagAlertYellow,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "شرایط تست: سوئیچ باز، موتور خاموش، باتری بالای ۱۲ ولت. در زمان تست انژکتور از استارت خودداری شود.",
                    color = Color(0xFFFEF08A),
                    fontSize = 10.5.sp
                )
            }
        }

        // Actuators List (10 per page)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(pageItems) { item ->
                ActuatorCard(item = item, onRunTest = { onRunTest(item.id) })
            }
        }

        // Pagination Bar (۱۰ پارامتر در هر صفحه)
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
                    modifier = Modifier.height(34.dp).testTag("btn_prev_actuator_page")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("صفحه قبل", fontSize = 11.sp)
                }

                Text(
                    text = "صفحه ${safePage + 1} از $totalPages (کل: ${actuators.size} عملگر)",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { if (safePage < totalPages - 1) onPageChange(safePage + 1) },
                    enabled = safePage < totalPages - 1,
                    colors = ButtonDefaults.buttonColors(containerColor = LonakBlue),
                    modifier = Modifier.height(34.dp).testTag("btn_next_actuator_page")
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
fun ActuatorCard(
    item: ActuatorItem,
    onRunTest: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DiagSurface)
            .border(
                1.dp,
                if (item.isTesting) LonakCyan else DiagBorder,
                RoundedCornerShape(8.dp)
            )
            .padding(10.dp)
            .testTag("actuator_${item.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.persianName,
                        color = Color.White,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.category,
                        color = StandardRefBlue,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = item.description,
                    color = Color(0xFF94A3B8),
                    fontSize = 10.5.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "وضعیت: ${item.statusMessage}",
                        color = if (item.feedbackValue == "PASSED") EcuReadGreen else if (item.isTesting) LonakCyan else Color(0xFFCBD5E1),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (item.standardRef.isNotEmpty()) {
                        Text(
                            text = " • ${item.standardRef}",
                            color = Color(0xFF64748B),
                            fontSize = 9.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Test Button
            Button(
                onClick = onRunTest,
                enabled = !item.isTesting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (item.feedbackValue == "PASSED") Color(0xFF059669) else LonakBlue
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                if (item.isTesting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else if (item.feedbackValue == "PASSED") {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تست شد", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تست ۳ ثانیه", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
