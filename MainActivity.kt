package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.obd.ObdPidConstants
import com.example.ui.DiagScreen
import com.example.ui.MainViewModel
import com.example.ui.components.ConnectionDialog
import com.example.ui.components.TopBarHeader
import com.example.ui.screens.ActuatorTestScreen
import com.example.ui.screens.EcuDetectionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveParametersScreen
import com.example.ui.screens.ObdModesScreen
import com.example.ui.screens.SensorGraphScreen
import com.example.ui.screens.SettingsAndBackupScreen
import com.example.ui.screens.TroubleCodesScreen
import com.example.ui.theme.DiagBackground
import com.example.ui.theme.LonakCyan
import com.example.ui.theme.LonakDiagTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LonakDiagTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: MainViewModel) {
    val connectionState by viewModel.connectionState.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val detectedEcu by viewModel.detectedEcu.collectAsState()
    val isDetectingEcu by viewModel.isDetectingEcu.collectAsState()
    val ecuDetectionProgress by viewModel.ecuDetectionProgress.collectAsState()
    val troubleCodes by viewModel.troubleCodes.collectAsState()
    val isScanningDtcs by viewModel.isScanningDtcs.collectAsState()
    val dtcScanStatus by viewModel.dtcScanStatus.collectAsState()
    val allSensors by viewModel.allSensors.collectAsState()
    val liveParamPage by viewModel.liveParamPage.collectAsState()
    val paramSearchQuery by viewModel.paramSearchQuery.collectAsState()
    val actuators by viewModel.actuators.collectAsState()
    val actuatorPage by viewModel.actuatorPage.collectAsState()
    val graphChannels by viewModel.graphChannels.collectAsState()
    val isGraphPaused by viewModel.isGraphPaused.collectAsState()
    val uiNotice by viewModel.uiNotice.collectAsState()

    var showConnectionDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Request permissions for Bluetooth if needed
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permissions handled
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    LaunchedEffect(uiNotice) {
        uiNotice?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.setNotice(null)
        }
    }

    // Quick Live readings for top banner
    val isConnected = connectionState.status == com.example.data.obd.ConnectionStatus.CONNECTED
    val rpmSensor = allSensors.find { it.pid == ObdPidConstants.PID_ENGINE_RPM }
    val coolantSensor = allSensors.find { it.pid == ObdPidConstants.PID_COOLANT_TEMP }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DiagBackground),
        containerColor = DiagBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBarHeader(
                connectionState = connectionState,
                onOpenConnectionDialog = { showConnectionDialog = true }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
        ) {
            when (currentScreen) {
                DiagScreen.HOME -> {
                    HomeScreen(
                        isConnected = isConnected,
                        detectedEcu = detectedEcu,
                        dtcCount = troubleCodes.size,
                        rpmValue = rpmSensor?.displayValue ?: "--",
                        coolantValue = coolantSensor?.displayValue ?: "--",
                        batteryVoltage = connectionState.batteryVoltage,
                        onOpenConnection = { showConnectionDialog = true },
                        onNavigate = { viewModel.navigateTo(it) },
                        onExportPdf = {
                            viewModel.exportReportToPdf { file ->
                                if (file != null) viewModel.shareReportFile(file)
                            }
                        }
                    )
                }

                DiagScreen.ECU_IDENTIFY -> {
                    EcuDetectionScreen(
                        isConnected = isConnected,
                        detectedEcu = detectedEcu,
                        isDetecting = isDetectingEcu,
                        progressText = ecuDetectionProgress,
                        onDetectClick = { viewModel.detectEcu() },
                        onSelectEcu = { viewModel.selectSpecificEcu(it) },
                        onBack = { viewModel.navigateTo(DiagScreen.HOME) }
                    )
                }

                DiagScreen.TROUBLE_CODES -> {
                    TroubleCodesScreen(
                        troubleCodes = troubleCodes,
                        isScanning = isScanningDtcs,
                        statusMessage = dtcScanStatus,
                        onScanClick = { viewModel.scanFaultCodes() },
                        onClearClick = { viewModel.clearFaultCodes() },
                        onBack = { viewModel.navigateTo(DiagScreen.HOME) }
                    )
                }

                DiagScreen.LIVE_PARAMETERS -> {
                    LiveParametersScreen(
                        isConnected = isConnected,
                        allSensors = allSensors,
                        currentPage = liveParamPage,
                        searchQuery = paramSearchQuery,
                        settings = settings,
                        onPageChange = { viewModel.setLiveParamPage(it) },
                        onSearchChange = { viewModel.setParamSearchQuery(it) },
                        onToggleSelectSensor = { viewModel.toggleSensorSelection(it) },
                        onSelectAllSensors = { viewModel.resetSensorSelection(it) },
                        onBack = { viewModel.navigateTo(DiagScreen.HOME) }
                    )
                }

                DiagScreen.SENSOR_GRAPHS -> {
                    SensorGraphScreen(
                        isConnected = isConnected,
                        graphChannels = graphChannels,
                        allSensors = allSensors,
                        isPaused = isGraphPaused,
                        onTogglePause = { viewModel.toggleGraphPause() },
                        onClearHistory = { viewModel.clearGraphHistory() },
                        onAddChannel = { sensor, colorHex -> viewModel.addGraphChannel(sensor, colorHex) },
                        onRemoveChannel = { viewModel.removeGraphChannel(it) },
                        onBack = { viewModel.navigateTo(DiagScreen.HOME) }
                    )
                }

                DiagScreen.ACTUATOR_TEST -> {
                    ActuatorTestScreen(
                        actuators = actuators,
                        currentPage = actuatorPage,
                        onPageChange = { viewModel.setActuatorPage(it) },
                        onRunTest = { viewModel.runActuatorTest(it) },
                        onBack = { viewModel.navigateTo(DiagScreen.HOME) }
                    )
                }

                DiagScreen.OBD_MODES -> {
                    ObdModesScreen(
                        terminalLogsFlow = viewModel.terminalLogs,
                        onSendTerminalCommand = { viewModel.sendTerminalCommand(it) },
                        onBack = { viewModel.navigateTo(DiagScreen.HOME) }
                    )
                }

                DiagScreen.SETTINGS_BACKUP -> {
                    SettingsAndBackupScreen(
                        currentSettings = settings,
                        onSaveSettings = { viewModel.updateSettings(it) },
                        onExportJson = { viewModel.exportObdLibraryJson() },
                        onImportJson = { viewModel.importObdLibraryJson(it) },
                        onResetToDefaults = { viewModel.resetObdLibraryToDefaults() },
                        onBack = { viewModel.navigateTo(DiagScreen.HOME) }
                    )
                }
            }
        }

        if (showConnectionDialog) {
            ConnectionDialog(
                connectionState = connectionState,
                bondedDevices = viewModel.getBondedBluetoothDevices(),
                onConnectWifi = { ip, port -> viewModel.connectWifi(ip, port) },
                onConnectBluetooth = { mac, name -> viewModel.connectBluetooth(mac, name) },
                onConnectSimulation = { viewModel.connectSimulation() },
                onDisconnect = { viewModel.disconnect() },
                onDismiss = { showConnectionDialog = false }
            )
        }
    }
}
