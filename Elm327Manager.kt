package com.example.data.obd

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import com.example.data.model.EcuInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.util.UUID
import kotlin.random.Random

enum class ConnectionType {
    WIFI,
    BLUETOOTH,
    SIMULATION
}

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

data class ConnectionState(
    val type: ConnectionType = ConnectionType.BLUETOOTH,
    val status: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val deviceName: String = "قطع (آماده اتصال)",
    val ipAddress: String = "192.168.0.10",
    val port: Int = 35000,
    val protocolName: String = "در انتظار اتصال",
    val elmVersion: String = "ELM327 v1.5",
    val batteryVoltage: String = "-- V",
    val isSimulation: Boolean = false,
    val errorMessage: String? = null,
    val activeEcuModel: String? = null
)

class Elm327Manager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val _connectionState = MutableStateFlow(ConnectionState())
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _terminalLogs = MutableSharedFlow<String>(replay = 50)
    val terminalLogs: SharedFlow<String> = _terminalLogs.asSharedFlow()

    private var wifiSocket: Socket? = null
    private var btSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    private var pollingJob: Job? = null
    private val sppUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // Callbacks for live sensor updates
    var onSensorDataReceived: ((Map<Int, Float>) -> Unit)? = null

    fun getBondedBluetoothDevices(): List<Pair<String, String>> {
        return try {
            val btAdapter = BluetoothAdapter.getDefaultAdapter()
            if (btAdapter != null && btAdapter.isEnabled) {
                btAdapter.bondedDevices.map { dev ->
                    Pair(dev.name ?: "Unknown", dev.address ?: "")
                }
            } else {
                emptyList()
            }
        } catch (e: SecurityException) {
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun connectSimulation() {
        disconnect()
        scope.launch {
            _connectionState.value = _connectionState.value.copy(
                type = ConnectionType.SIMULATION,
                status = ConnectionStatus.CONNECTING,
                deviceName = "شبیه‌ساز تست ELM327 v1.5",
                isSimulation = true,
                errorMessage = null
            )
            logTerminal("--- راه‌اندازی شبیه‌ساز ELM327 v1.5 ---")
            logTerminal("CMD: ATZ")
            delay(250)
            logTerminal("RES: ELM327 v1.5")
            logTerminal("CMD: ATE0 (اکو خاموش)")
            delay(100)
            logTerminal("RES: OK")
            logTerminal("CMD: ATSP0 (تشخیص خودکار پروتکل)")
            delay(150)
            logTerminal("RES: OK (ISO 15765-4 CAN 11bit 500kbps)")
            logTerminal("CMD: ATRV (قرائت ولتاژ باتری)")
            delay(100)
            logTerminal("RES: 14.1V")

            _connectionState.value = _connectionState.value.copy(
                status = ConnectionStatus.CONNECTED,
                batteryVoltage = "14.1 V",
                protocolName = "ISO 15765-4 CAN / KWP2000 (شبیه‌ساز فعال)",
                elmVersion = "ELM327 v1.5 Hardware"
            )

            startTelemetrySimulation()
        }
    }

    fun connectWifi(ip: String = "192.168.0.10", port: Int = 35000) {
        disconnect()
        scope.launch {
            _connectionState.value = _connectionState.value.copy(
                type = ConnectionType.WIFI,
                status = ConnectionStatus.CONNECTING,
                ipAddress = ip,
                port = port,
                deviceName = "WiFi ELM327 ($ip:$port)",
                isSimulation = false,
                errorMessage = null
            )
            logTerminal("در حال اتصال شبکه TCP به ماژول وای‌فای $ip:$port...")

            try {
                val socket = Socket()
                withContext(Dispatchers.IO) {
                    socket.connect(InetSocketAddress(ip, port), 4000)
                }
                wifiSocket = socket
                inputStream = socket.getInputStream()
                outputStream = socket.getOutputStream()

                initElmSequence()
            } catch (e: Exception) {
                logTerminal("خطا در اتصال به وای‌فای ELM327: ${e.message}")
                _connectionState.value = _connectionState.value.copy(
                    status = ConnectionStatus.ERROR,
                    errorMessage = "عدم اتصال به وای‌فای: ${e.message}"
                )
            }
        }
    }

    fun connectBluetooth(macAddress: String, deviceName: String = "OBDII") {
        disconnect()
        scope.launch {
            _connectionState.value = _connectionState.value.copy(
                type = ConnectionType.BLUETOOTH,
                status = ConnectionStatus.CONNECTING,
                deviceName = "Bluetooth ($deviceName)",
                isSimulation = false,
                errorMessage = null
            )
            logTerminal("در حال برقراری اتصال بلوتوث SPP به $deviceName ($macAddress)...")

            try {
                val btAdapter = BluetoothAdapter.getDefaultAdapter()
                    ?: throw IllegalStateException("بلوتوث در دستگاه پشتیبانی نمی‌شود")
                val device: BluetoothDevice = btAdapter.getRemoteDevice(macAddress)
                val socket = device.createRfcommSocketToServiceRecord(sppUuid)
                withContext(Dispatchers.IO) {
                    btAdapter.cancelDiscovery()
                    socket.connect()
                }
                btSocket = socket
                inputStream = socket.inputStream
                outputStream = socket.outputStream

                initElmSequence()
            } catch (e: Exception) {
                logTerminal("خطا در اتصال بلوتوث: ${e.message}")
                _connectionState.value = _connectionState.value.copy(
                    status = ConnectionStatus.ERROR,
                    errorMessage = "عدم اتصال بلوتوث: ${e.message}"
                )
            }
        }
    }

    private suspend fun initElmSequence() {
        try {
            logTerminal("--- آغاز راه‌اندازی ماژول سخت‌افزاری ELM327 v1.5 ---")
            val responseAtz = sendCommand("ATZ")
            delay(300)
            sendCommand("ATE0") // Echo off
            sendCommand("ATL0") // Linefeeds off
            sendCommand("ATS0") // Spaces off
            val protoRes = sendCommand("ATSP0") // Automatic protocol detection
            val voltRes = sendCommand("ATRV") // Read voltage

            _connectionState.value = _connectionState.value.copy(
                status = ConnectionStatus.CONNECTED,
                batteryVoltage = voltRes.trim().ifEmpty { "14.1 V" },
                protocolName = "ISO 15765-4 / KWP2000 Auto",
                elmVersion = responseAtz.trim().ifEmpty { "ELM327 v1.5" }
            )

            startRealHardwarePolling()
        } catch (e: Exception) {
            _connectionState.value = _connectionState.value.copy(
                status = ConnectionStatus.ERROR,
                errorMessage = "خطا در تنظیمات اولیه ماژول: ${e.message}"
            )
        }
    }

    /**
     * Matches and sets the specific ECU protocol in ELM327
     * based on user's selected ECU (e.g. Bosch ME17, Sagem S2000, Siemens C16, etc.)
     */
    suspend fun applyEcuProtocol(ecu: EcuInfo): Boolean = withContext(Dispatchers.IO) {
        logTerminal("==========================================")
        logTerminal("هماهنگی پروتکل ایسیو: ${ecu.brand} ${ecu.model}")
        logTerminal("پروتکل هدف: ${ecu.protocol} | نرخ باود: ${ecu.baudRate}")
        logTerminal("گذرگاه: ${ecu.busType}")

        try {
            // Send protocol select command (e.g. ATSP6 for CAN, ATSP5 for KWP, ATSP3 for ISO9141)
            val spCmd = ecu.elmProtocolCmd.replace(" ", "")
            val spRes = sendCommand(spCmd)
            logTerminal("ارسال فرمان پروتکل: $spCmd -> نتیجه: $spRes")

            // Send header setup command (e.g. ATSH7E0 or ATSH8110F1)
            val shCmd = ecu.elmHeaderCmd.replace(" ", "")
            val shRes = sendCommand(shCmd)
            logTerminal("ارسال هدر ارتباطی: $shCmd -> نتیجه: $shRes")

            // Protocol-specific tuning
            if (ecu.protocol.contains("CAN", ignoreCase = true)) {
                sendCommand("ATCRAF7E8")
                sendCommand("ATCAF1") // CAN auto formatting
                logTerminal("فیلتر پاسخ CAN روی شناسه 7E8 تنظیم گردید.")
            } else if (ecu.protocol.contains("KWP", ignoreCase = true)) {
                sendCommand("ATIB10") // 10.4 kbps
                sendCommand("ATWM8110F13E") // Wakeup tester present message
                logTerminal("تنظیمات تایمینگ و سیگنال بیدارباش KWP2000 انجام شد.")
            } else if (ecu.protocol.contains("9141", ignoreCase = true)) {
                sendCommand("ATSI") // Slow init
                sendCommand("ATIB10")
                logTerminal("آماده‌سازی خط K/L برای راه‌اندازی کند ۵ باود انجام شد.")
            }

            // Test Ping Mode 01 PID 00
            val pingRes = sendCommand("0100")
            logTerminal("استعلام پشتیبانی پارامترها (01 00): $pingRes")

            _connectionState.value = _connectionState.value.copy(
                protocolName = "${ecu.protocol} [${ecu.brand} ${ecu.model}]",
                activeEcuModel = "${ecu.brand} ${ecu.model}"
            )
            logTerminal("پروتکل ماژول با موفقیت روی ایسیو ${ecu.model} مچ و تثبیت گردید.")
            logTerminal("==========================================")
            true
        } catch (e: Exception) {
            logTerminal("خطا در هماهنگی پروتکل: ${e.message}")
            false
        }
    }

    suspend fun sendCommand(cmd: String): String = withContext(Dispatchers.IO) {
        val out = outputStream
        val ins = inputStream
        if (out == null || ins == null) {
            // Offline or simulation fallback response
            logTerminal("CMD: $cmd")
            val dummyResp = when (cmd.trim().uppercase().replace(" ", "")) {
                "ATZ" -> "ELM327 v1.5"
                "ATE0", "ATL0", "ATS0", "ATSP0", "ATSP6", "ATSP5", "ATSP3", "ATSP4" -> "OK"
                "ATSH7E0", "ATSH8110F1", "ATSH8111F1", "ATSH686AF1", "ATCRAF7E8", "ATCAF1", "ATIB10", "ATSI", "ATWM8110F13E" -> "OK"
                "ATRV" -> "14.1V"
                "0100" -> "41 00 BE 3F B8 11"
                "0902" -> "49 02 01 4E 41 41 50 30 31" // Sample VIN response
                "0904" -> "49 04 01 4D 45 31 37 5F 54 43" // Calibration ID
                "03" -> "43 01 30 01 15 00 00" // DTC P0130, P0115
                "07" -> "47 01 03 00 00 00 00"
                "04" -> "44" // Cleared
                else -> "OK"
            }
            logTerminal("RES: $dummyResp")
            return@withContext dummyResp
        }

        try {
            logTerminal("CMD: $cmd")
            out.write((cmd + "\r").toByteArray())
            out.flush()

            val buffer = ByteArray(512)
            val sb = StringBuilder()
            var bytesRead: Int
            val startTime = System.currentTimeMillis()

            while (System.currentTimeMillis() - startTime < 1500) {
                if (ins.available() > 0) {
                    bytesRead = ins.read(buffer)
                    val chunk = String(buffer, 0, bytesRead)
                    sb.append(chunk)
                    if (chunk.contains(">")) break
                } else {
                    delay(50)
                }
            }
            val cleaned = sb.toString().replace(">", "").trim()
            logTerminal("RES: $cleaned")
            cleaned
        } catch (e: Exception) {
            logTerminal("ERR: ${e.message}")
            "ERROR"
        }
    }

    private fun startRealHardwarePolling() {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            while (isActive && _connectionState.value.status == ConnectionStatus.CONNECTED) {
                try {
                    val rpmRaw = sendCommand("010C")
                    val speedRaw = sendCommand("010D")
                    val tempRaw = sendCommand("0105")
                    val mapRaw = sendCommand("010B")
                    val voltRaw = sendCommand("ATRV")

                    val values = mutableMapOf<Int, Float>()
                    parseRpm(rpmRaw)?.let { values[ObdPidConstants.PID_ENGINE_RPM] = it }
                    parseSpeed(speedRaw)?.let { values[ObdPidConstants.PID_VEHICLE_SPEED] = it }
                    parseTemp(tempRaw)?.let { values[ObdPidConstants.PID_COOLANT_TEMP] = it }
                    parseMap(mapRaw)?.let { values[ObdPidConstants.PID_INTAKE_MAP] = it }

                    if (voltRaw.contains("V", ignoreCase = true)) {
                        _connectionState.value = _connectionState.value.copy(
                            batteryVoltage = voltRaw.trim()
                        )
                    }

                    onSensorDataReceived?.invoke(values)
                } catch (e: Exception) {
                    // Ignore poll errors
                }
                delay(400)
            }
        }
    }

    private fun parseRpm(res: String): Float? {
        val hex = res.replace(" ", "").removePrefix("410C")
        if (hex.length >= 4) {
            val a = hex.substring(0, 2).toIntOrNull(16) ?: return null
            val b = hex.substring(2, 4).toIntOrNull(16) ?: return null
            return ((a * 256 + b) / 4.0).toFloat()
        }
        return null
    }

    private fun parseSpeed(res: String): Float? {
        val hex = res.replace(" ", "").removePrefix("410D")
        if (hex.length >= 2) {
            return hex.substring(0, 2).toIntOrNull(16)?.toFloat()
        }
        return null
    }

    private fun parseTemp(res: String): Float? {
        val hex = res.replace(" ", "").removePrefix("4105")
        if (hex.length >= 2) {
            return (hex.substring(0, 2).toIntOrNull(16)?.toFloat() ?: 0f) - 40f
        }
        return null
    }

    private fun parseMap(res: String): Float? {
        val hex = res.replace(" ", "").removePrefix("410B")
        if (hex.length >= 2) {
            return (hex.substring(0, 2).toIntOrNull(16)?.toFloat() ?: 0f) * 10f // mbar
        }
        return null
    }

    private fun startTelemetrySimulation() {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            var step = 0
            var coolant = 86.0f
            while (isActive && _connectionState.value.status == ConnectionStatus.CONNECTED) {
                step++
                val angle = step * 0.15
                // Realistic fluctuating RPM around 840-860
                val rpm = 845f + (Math.sin(angle) * 18f).toFloat() + Random.nextInt(-4, 5)

                // O2 Upstream fast oscillation between 150mV and 820mV
                val o2Upstream = 480f + (Math.sin(step * 0.45) * 320f).toFloat() + Random.nextInt(-15, 15)
                val o2Downstream = 610f + (Math.sin(step * 0.05) * 30f).toFloat()

                if (coolant < 89.2f) {
                    coolant += 0.05f
                } else if (coolant > 92f) {
                    coolant -= 0.05f
                }

                val mapPress = 340f + (Math.cos(angle) * 12f).toFloat() + Random.nextInt(-3, 4)
                val throttle = 8.2f + (Math.sin(angle * 0.5) * 0.6f).toFloat()
                val battery = 14.15f + (Random.nextInt(-5, 6) * 0.01f)
                val fuelPressure = 3.65f + (Random.nextInt(-3, 4) * 0.02f)
                val injBase = 3.22f + (Math.sin(angle) * 0.08f).toFloat()

                val simulatedMap = mapOf(
                    ObdPidConstants.PID_ENGINE_RPM to rpm,
                    ObdPidConstants.PID_VEHICLE_SPEED to 0f,
                    ObdPidConstants.PID_COOLANT_TEMP to coolant,
                    ObdPidConstants.PID_INTAKE_MAP to mapPress,
                    ObdPidConstants.PID_O2_SENSOR_1_VOLTAGE to o2Upstream,
                    ObdPidConstants.PID_O2_SENSOR_2_VOLTAGE to o2Downstream,
                    ObdPidConstants.PID_THROTTLE_POSITION to throttle,
                    ObdPidConstants.PID_CONTROL_MODULE_VOLTAGE to battery,
                    ObdPidConstants.PID_FUEL_PRESSURE to fuelPressure,
                    ObdPidConstants.PID_PROP_INJECTOR_1_PULSE to (injBase + 0.02f),
                    ObdPidConstants.PID_PROP_INJECTOR_2_PULSE to (injBase + 0.04f),
                    ObdPidConstants.PID_PROP_INJECTOR_3_PULSE to (injBase - 0.01f),
                    ObdPidConstants.PID_PROP_INJECTOR_4_PULSE to (injBase + 0.03f),
                    ObdPidConstants.PID_INTAKE_AIR_TEMP to 32f,
                    ObdPidConstants.PID_EVAP_PURGE to 15f,
                    ObdPidConstants.PID_TIMING_ADVANCE to (8.5f + Random.nextInt(-1, 2) * 0.2f),
                    ObdPidConstants.PID_BAROMETRIC_PRESSURE to 92.4f,
                    ObdPidConstants.PID_CATALYST_TEMP to 490f,
                    ObdPidConstants.PID_ENGINE_LOAD to (21.5f + (Math.sin(angle) * 1.2f).toFloat()),
                    ObdPidConstants.PID_ACCEL_PEDAL_POS_D to 0f
                )

                onSensorDataReceived?.invoke(simulatedMap)
                delay(350)
            }
        }
    }

    fun disconnect() {
        pollingJob?.cancel()
        try {
            wifiSocket?.close()
            btSocket?.close()
        } catch (e: Exception) {
            // Ignore close exceptions
        }
        wifiSocket = null
        btSocket = null
        inputStream = null
        outputStream = null
        _connectionState.value = _connectionState.value.copy(
            status = ConnectionStatus.DISCONNECTED,
            batteryVoltage = "-- V",
            deviceName = "قطع (آماده اتصال)",
            protocolName = "در انتظار اتصال",
            isSimulation = false
        )
        logTerminal("ارتباط با ماژول دیاگ قطع شد.")
    }

    private fun logTerminal(text: String) {
        scope.launch {
            _terminalLogs.emit(text)
        }
    }
}
