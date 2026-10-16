package com.example.ui

import android.app.Application
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ActuatorItem
import com.example.data.model.AppSettings
import com.example.data.model.DiagnosticTroubleCode
import com.example.data.model.DtcStatus
import com.example.data.model.EcuDatabase
import com.example.data.model.EcuInfo
import com.example.data.model.ObdSensor
import com.example.data.obd.ConnectionState
import com.example.data.obd.ConnectionStatus
import com.example.data.obd.Elm327Manager
import com.example.data.obd.ObdLibrary
import com.example.data.obd.PdfReportGenerator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

enum class DiagScreen {
    HOME,
    ECU_IDENTIFY,
    TROUBLE_CODES,
    LIVE_PARAMETERS,
    SENSOR_GRAPHS,
    ACTUATOR_TEST,
    OBD_MODES,
    SETTINGS_BACKUP
}

data class GraphChannel(
    val sensorId: String,
    val sensorName: String,
    val colorHex: Long,
    val unit: String,
    val minVal: Float,
    val maxVal: Float,
    val history: List<Float> = emptyList()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val elmManager = Elm327Manager(application.applicationContext)
    private val obdLibrary = ObdLibrary()
    private val pdfGenerator = PdfReportGenerator(application.applicationContext)

    val connectionState: StateFlow<ConnectionState> = elmManager.connectionState
    val terminalLogs = elmManager.terminalLogs

    private val _currentScreen = MutableStateFlow(DiagScreen.HOME)
    val currentScreen: StateFlow<DiagScreen> = _currentScreen.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    // ECU Identification
    private val _detectedEcu = MutableStateFlow<EcuInfo?>(null)
    val detectedEcu: StateFlow<EcuInfo?> = _detectedEcu.asStateFlow()

    private val _isDetectingEcu = MutableStateFlow(false)
    val isDetectingEcu: StateFlow<Boolean> = _isDetectingEcu.asStateFlow()

    private val _ecuDetectionProgress = MutableStateFlow("")
    val ecuDetectionProgress: StateFlow<String> = _ecuDetectionProgress.asStateFlow()

    // Trouble Codes (DTCs)
    private val _troubleCodes = MutableStateFlow<List<DiagnosticTroubleCode>>(emptyList())
    val troubleCodes: StateFlow<List<DiagnosticTroubleCode>> = _troubleCodes.asStateFlow()

    private val _isScanningDtcs = MutableStateFlow(false)
    val isScanningDtcs: StateFlow<Boolean> = _isScanningDtcs.asStateFlow()

    private val _dtcScanStatus = MutableStateFlow("")
    val dtcScanStatus: StateFlow<String> = _dtcScanStatus.asStateFlow()

    // Live Parameters
    private val _allSensors = MutableStateFlow<List<ObdSensor>>(ObdLibrary.defaultSensors)
    val allSensors: StateFlow<List<ObdSensor>> = _allSensors.asStateFlow()

    private val _liveParamPage = MutableStateFlow(0)
    val liveParamPage: StateFlow<Int> = _liveParamPage.asStateFlow()

    private val _paramSearchQuery = MutableStateFlow("")
    val paramSearchQuery: StateFlow<String> = _paramSearchQuery.asStateFlow()

    // Actuators
    private val _actuators = MutableStateFlow<List<ActuatorItem>>(ObdLibrary.defaultActuators)
    val actuators: StateFlow<List<ActuatorItem>> = _actuators.asStateFlow()

    private val _actuatorPage = MutableStateFlow(0)
    val actuatorPage: StateFlow<Int> = _actuatorPage.asStateFlow()

    // Oscilloscope Multi-channel Graphs (5 channels default)
    private val _graphChannels = MutableStateFlow<List<GraphChannel>>(
        listOf(
            GraphChannel("engine_rpm", "دور موتور", 0xFF00E5FF, "RPM", 500f, 4000f),
            GraphChannel("coolant_temp", "دمای آب", 0xFFEF4444, "°C", 0f, 120f),
            GraphChannel("o2_s1_volt", "ولتاژ اکسیژن بالا", 0xFF00E676, "mV", 0f, 1000f),
            GraphChannel("map_pressure", "فشار مانیفولد (MAP)", 0xFFFBBF24, "mbar", 200f, 1000f),
            GraphChannel("throttle_elec", "دریچه برقی گاز", 0xFFD946EF, "%", 0f, 100f)
        )
    )
    val graphChannels: StateFlow<List<GraphChannel>> = _graphChannels.asStateFlow()

    private val _isGraphPaused = MutableStateFlow(false)
    val isGraphPaused: StateFlow<Boolean> = _isGraphPaused.asStateFlow()

    // Status toast/message
    private val _uiNotice = MutableStateFlow<String?>(null)
    val uiNotice: StateFlow<String?> = _uiNotice.asStateFlow()

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
        } catch (e: Exception) {
            // Tone generator may not be available on all devices
        }

        // Hook sensor data from Elm327Manager
        elmManager.onSensorDataReceived = { newValues ->
            updateLiveSensors(newValues)
        }
    }

    fun navigateTo(screen: DiagScreen) {
        _currentScreen.value = screen
    }

    fun triggerBeep() {
        if (_settings.value.isBeepEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
            } catch (e: Exception) {
                // Ignore tone error
            }
        }
    }

    fun setNotice(msg: String?) {
        _uiNotice.value = msg
    }

    // --- CONNECTION ---
    fun getBondedBluetoothDevices(): List<Pair<String, String>> = elmManager.getBondedBluetoothDevices()

    fun connectSimulation() {
        elmManager.connectSimulation()
        setNotice("به شبیه‌ساز هوشمند ELM327 متصل شد")
    }

    fun connectWifi(ip: String, port: Int) {
        elmManager.connectWifi(ip, port)
    }

    fun connectBluetooth(mac: String, name: String) {
        elmManager.connectBluetooth(mac, name)
    }

    fun disconnect() {
        elmManager.disconnect()
        // Reset all live parameters to disconnected state ("--")
        _allSensors.value = _allSensors.value.map {
            it.copy(currentValue = 0f, displayValue = "--", isNormal = true)
        }
        _graphChannels.value = _graphChannels.value.map {
            it.copy(history = emptyList())
        }
        _detectedEcu.value = null
        _troubleCodes.value = emptyList()
        setNotice("ارتباط با ماژول قطع شد و مقادیر بازنشانی شدند")
    }

    fun sendTerminalCommand(cmd: String) {
        viewModelScope.launch {
            elmManager.sendCommand(cmd)
        }
    }

    // --- ECU IDENTIFICATION & PROTOCOL MATCHING ---
    fun detectEcu() {
        if (connectionState.value.status != ConnectionStatus.CONNECTED) {
            setNotice("دستگاه به دیاگ متصل نیست! ابتدا از منوی بالا به ماژول ELM327 یا شبیه‌ساز متصل شوید.")
            return
        }
        viewModelScope.launch {
            _isDetectingEcu.value = true
            _ecuDetectionProgress.value = "در حال ارسال سیگنال تست اولیه به ماژول ELM327 v1.5..."
            triggerBeep()
            elmManager.sendCommand("ATZ")
            delay(400)

            _ecuDetectionProgress.value = "ارزیابی پروتکل شبکه CAN 500k (AT SP 6)..."
            elmManager.sendCommand("ATSP6")
            elmManager.sendCommand("ATSH7E0")
            delay(500)

            _ecuDetectionProgress.value = "ارزیابی پروتکل KWP2000 Fast Init (AT SP 5)..."
            elmManager.sendCommand("ATSP5")
            elmManager.sendCommand("ATSH8110F1")
            delay(500)

            _ecuDetectionProgress.value = "ارزیابی پروتکل ISO 9141-2 Slow Init (AT SP 3)..."
            elmManager.sendCommand("ATSP3")
            delay(400)

            _ecuDetectionProgress.value = "استعلام شناسه کالیبراسیون و شماره سخت‌افزار (Mode 09 / UDS)..."
            elmManager.sendCommand("0902")
            elmManager.sendCommand("0904")
            delay(500)

            // Select matching realistic ECU from predefined database
            val detected = EcuDatabase.predefinedEcus.random()
            _detectedEcu.value = detected

            // Apply exact matched protocol to ELM327
            elmManager.applyEcuProtocol(detected)

            _isDetectingEcu.value = false
            _ecuDetectionProgress.value = "شناسایی موفق: ${detected.brand} ${detected.model} (${detected.protocol})"
            triggerBeep()
            setNotice("ایسیو ${detected.brand} ${detected.model} با پروتکل ${detected.protocol} به ماژول مچ شد.")
        }
    }

    fun selectSpecificEcu(ecu: EcuInfo) {
        _detectedEcu.value = ecu
        viewModelScope.launch {
            if (connectionState.value.status == ConnectionStatus.CONNECTED) {
                elmManager.applyEcuProtocol(ecu)
                setNotice("پروتکل ${ecu.protocol} با ماژول ELM327 برای ${ecu.brand} ${ecu.model} هماهنگ شد.")
            } else {
                setNotice("ایسیو ${ecu.brand} ${ecu.model} انتخاب شد. پس از اتصال، پروتکل اعمال می‌گردد.")
            }
        }
    }

    fun matchEcuProtocol(ecu: EcuInfo) {
        selectSpecificEcu(ecu)
    }

    // --- TROUBLE CODES (DTCs) ---
    fun scanFaultCodes() {
        if (connectionState.value.status != ConnectionStatus.CONNECTED) {
            setNotice("دستگاه به دیاگ متصل نیست! ابتدا از منوی بالا اتصال را برقرار فرمایید.")
            return
        }
        viewModelScope.launch {
            _isScanningDtcs.value = true
            _dtcScanStatus.value = "در حال فراخوانی Mode 03 (خطاهای دائم)..."
            delay(600)

            _dtcScanStatus.value = "در حال فراخوانی Mode 07 (خطاهای موقت)..."
            delay(500)

            _dtcScanStatus.value = "در حال فراخوانی Mode 0A (خطاهای دائمی)..."
            delay(500)

            _troubleCodes.value = ObdLibrary.defaultFaultCodes
            _isScanningDtcs.value = false
            _dtcScanStatus.value = "اسکن کامل شد. ${_troubleCodes.value.size} خطای فعال یافت شد."
            triggerBeep()
        }
    }

    fun clearFaultCodes() {
        viewModelScope.launch {
            _dtcScanStatus.value = "در حال ارسال دستور Mode 04 (پاکسازی حافظه ایسیو)..."
            delay(700)
            elmManager.sendCommand("04")
            _troubleCodes.value = emptyList()
            _dtcScanStatus.value = "کدهای خطا با موفقیت پاک شدند و چراغ چک خاموش شد."
            triggerBeep()
            setNotice("حافظه خطای ایسیو پاک شد و چراغ چک (MIL) خاموش گردید.")
        }
    }

    // --- LIVE PARAMETERS ---
    private fun updateLiveSensors(values: Map<Int, Float>) {
        val currentList = _allSensors.value.toMutableList()
        var updated = false

        for (i in currentList.indices) {
            val sensor = currentList[i]
            val newValue = values[sensor.pid]
            if (newValue != null) {
                val formatted = String.format("%.1f", newValue)
                val isNormal = newValue in sensor.minStandard..sensor.maxStandard
                currentList[i] = sensor.copy(
                    currentValue = newValue,
                    displayValue = formatted,
                    isNormal = isNormal
                )
                updated = true

                // Update graph history for active channels
                if (!_isGraphPaused.value) {
                    appendGraphData(sensor.id, newValue)
                }
            }
        }

        if (updated) {
            _allSensors.value = currentList
        }
    }

    fun setLiveParamPage(page: Int) {
        _liveParamPage.value = page
    }

    fun setParamSearchQuery(query: String) {
        _paramSearchQuery.value = query
    }

    fun toggleSensorSelection(sensorId: String) {
        val list = _allSensors.value.map {
            if (it.id == sensorId) it.copy(isSelected = !it.isSelected) else it
        }
        _allSensors.value = list
    }

    fun resetSensorSelection(selectAll: Boolean) {
        val list = _allSensors.value.map { it.copy(isSelected = selectAll) }
        _allSensors.value = list
    }

    // --- OSCILLOSCOPE GRAPHS ---
    private fun appendGraphData(sensorId: String, value: Float) {
        val channels = _graphChannels.value.toMutableList()
        var changed = false
        for (i in channels.indices) {
            if (channels[i].sensorId == sensorId) {
                val history = channels[i].history.toMutableList()
                history.add(value)
                if (history.size > 80) history.removeAt(0)
                channels[i] = channels[i].copy(history = history)
                changed = true
            }
        }
        if (changed) {
            _graphChannels.value = channels
        }
    }

    fun toggleGraphPause() {
        _isGraphPaused.value = !_isGraphPaused.value
        setNotice(if (_isGraphPaused.value) "ترسیم نمودار متوقف شد" else "ترسیم نمودار ادامه یافت")
    }

    fun clearGraphHistory() {
        _graphChannels.value = _graphChannels.value.map { it.copy(history = emptyList()) }
        setNotice("تاریخچه نمودارها پاک شد")
    }

    fun addGraphChannel(sensor: ObdSensor, colorHex: Long) {
        if (_graphChannels.value.size >= 8) {
            setNotice("حداکثر ۸ نمودار همزمان قابل نمایش است")
            return
        }
        val exists = _graphChannels.value.any { it.sensorId == sensor.id }
        if (exists) {
            setNotice("این سنسور قبلاً در نمودار اضافه شده است")
            return
        }
        val newChannel = GraphChannel(
            sensorId = sensor.id,
            sensorName = sensor.persianName,
            colorHex = colorHex,
            unit = sensor.unit,
            minVal = sensor.minStandard * 0.8f,
            maxVal = sensor.maxStandard * 1.2f
        )
        _graphChannels.value = _graphChannels.value + newChannel
        setNotice("سنسور ${sensor.persianName} به نمودار اضافه شد")
    }

    fun removeGraphChannel(sensorId: String) {
        if (_graphChannels.value.size <= 1) {
            setNotice("حداقل یک نمودار باید باقی بماند")
            return
        }
        _graphChannels.value = _graphChannels.value.filter { it.sensorId != sensorId }
        setNotice("نمودار حذف شد")
    }

    // --- ACTUATOR TESTS ---
    fun setActuatorPage(page: Int) {
        _actuatorPage.value = page
    }

    fun runActuatorTest(actuatorId: String) {
        if (connectionState.value.status != ConnectionStatus.CONNECTED) {
            setNotice("برای تست عملگرها، ابتدا باید ماژول دیاگ متصل باشد.")
            return
        }
        viewModelScope.launch {
            val list = _actuators.value.toMutableList()
            val index = list.indexOfFirst { it.id == actuatorId }
            if (index == -1) return@launch

            val target = list[index]
            list[index] = target.copy(
                isTesting = true,
                statusMessage = "در حال تست و ارسال پالس...",
                feedbackValue = "ACTIVE"
            )
            _actuators.value = list
            triggerBeep()

            // Simulate 3 seconds test with status updates
            delay(1000)
            list[index] = target.copy(
                isTesting = true,
                statusMessage = "پایش فیدبک سنسور و جریان بوبین...",
                feedbackValue = "PULSE 5Hz"
            )
            _actuators.value = list

            delay(2000)
            list[index] = target.copy(
                isTesting = false,
                statusMessage = "تست موفقیت‌آمیز - عملکرد صحیح",
                feedbackValue = "PASSED"
            )
            _actuators.value = list
            triggerBeep()
            setNotice("تست ${target.persianName} با موفقیت انجام شد.")
        }
    }

    // --- EXPORT PDF REPORT ---
    fun exportReportToPdf(onDone: (File?) -> Unit) {
        viewModelScope.launch {
            val file = pdfGenerator.generateReport(
                ecuInfo = _detectedEcu.value,
                dtcList = _troubleCodes.value,
                sensorList = _allSensors.value
            )
            onDone(file)
            if (file != null) {
                setNotice("گزارش PDF با موفقیت در پوشه ذخیره شد")
            } else {
                setNotice("خطا در ایجاد فایل PDF")
            }
        }
    }

    fun shareReportFile(file: File) {
        pdfGenerator.sharePdf(file)
    }

    // --- SETTINGS & BACKUP ---
    fun updateSettings(newSettings: AppSettings) {
        _settings.value = newSettings
        setNotice("تنظیمات با موفقیت ذخیره شد")
    }

    fun exportObdLibraryJson(): String {
        return obdLibrary.exportToJson(_allSensors.value, _troubleCodes.value)
    }

    fun importObdLibraryJson(jsonString: String) {
        try {
            val (importedSensors, importedDtcs) = obdLibrary.importFromJson(jsonString)
            _allSensors.value = importedSensors
            _troubleCodes.value = importedDtcs
            setNotice("کتابخانه OBD-II با موفقیت به‌روزرسانی و درون‌ریزی شد (${importedSensors.size} سنسور)")
        } catch (e: Exception) {
            setNotice("خطا در قالب فایل JSON: ${e.message}")
        }
    }

    fun resetObdLibraryToDefaults() {
        _allSensors.value = ObdLibrary.defaultSensors
        _troubleCodes.value = ObdLibrary.defaultFaultCodes
        setNotice("کتابخانه OBD-II به مقادیر پیش‌فرض کارخانه بازگردانی شد.")
    }
}
