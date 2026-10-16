package com.example.data.obd

import com.example.data.model.ActuatorItem
import com.example.data.model.ActuatorTestType
import com.example.data.model.DiagnosticTroubleCode
import com.example.data.model.DtcStatus
import com.example.data.model.ObdSensor
import org.json.JSONArray
import org.json.JSONObject

/**
 * Standard OBD-II PIDs (Parameter IDs) in C/C++ style defines
 * as specified in the technical standard SAE J1979 / ISO 15031-5
 */
object ObdPidConstants {
    const val PID_ENGINE_LOAD = 0x04
    const val PID_COOLANT_TEMP = 0x05
    const val PID_SHORT_TERM_FUEL_TRIM_1 = 0x06
    const val PID_LONG_TERM_FUEL_TRIM_1 = 0x07
    const val PID_FUEL_PRESSURE = 0x0A
    const val PID_INTAKE_MAP = 0x0B
    const val PID_ENGINE_RPM = 0x0C
    const val PID_VEHICLE_SPEED = 0x0D
    const val PID_TIMING_ADVANCE = 0x0E
    const val PID_INTAKE_AIR_TEMP = 0x0F
    const val PID_MAF_AIR_FLOW = 0x10
    const val PID_THROTTLE_POSITION = 0x11
    const val PID_O2_SENSOR_1_VOLTAGE = 0x14
    const val PID_O2_SENSOR_2_VOLTAGE = 0x15
    const val PID_FUEL_LEVEL = 0x2F
    const val PID_EVAP_PURGE = 0x2E
    const val PID_BAROMETRIC_PRESSURE = 0x33
    const val PID_CATALYST_TEMP = 0x3C
    const val PID_CONTROL_MODULE_VOLTAGE = 0x42
    const val PID_ABSOLUTE_LOAD = 0x43
    const val PID_RELATIVE_THROTTLE_POS = 0x45
    const val PID_ACCEL_PEDAL_POS_D = 0x49
    const val PID_ACCEL_PEDAL_POS_E = 0x4A
    const val PID_COMMANDED_THROTTLE_ACTUATOR = 0x4C
    const val PID_FUEL_INJECTION_TIMING = 0x5D
    const val PID_ENGINE_FUEL_RATE = 0x5E

    // Proprietary / French-Iranian ECU PIDs (KWP2000 / CAN Local PIDs)
    const val PID_PROP_INJECTOR_1_PULSE = 0x101
    const val PID_PROP_INJECTOR_2_PULSE = 0x102
    const val PID_PROP_INJECTOR_3_PULSE = 0x103
    const val PID_PROP_INJECTOR_4_PULSE = 0x104
    const val PID_PROP_O2_HEATER = 0x105
    const val PID_PROP_FUEL_RAIL_AIR_TEMP = 0x106
    const val PID_PROP_STEPPER_MOTOR_STEPS = 0x107
    const val PID_PROP_COIL_CHARGE_TIME = 0x108
}

class ObdLibrary {

    companion object {
        val defaultSensors = listOf(
            ObdSensor(
                id = "o2_s1_volt",
                pid = ObdPidConstants.PID_O2_SENSOR_1_VOLTAGE,
                pidHex = "0x14",
                name = "O2 Sensor 1 Voltage (Upstream)",
                persianName = "ولتاژ سنسور اکسیژن بالا",
                unit = "mV",
                minStandard = 100f,
                maxStandard = 900f,
                standardDisplay = "100 - 900 mV (نوسانی)",
                category = "سیستم سوخت و آلایندگی",
                formulaDesc = "A * 0.005 V (سوئیچینگ سریع)"
            ),
            ObdSensor(
                id = "o2_s2_volt",
                pid = ObdPidConstants.PID_O2_SENSOR_2_VOLTAGE,
                pidHex = "0x15",
                name = "O2 Sensor 2 Voltage (Downstream)",
                persianName = "ولتاژ سنسور اکسیژن پایین",
                unit = "mV",
                minStandard = 400f,
                maxStandard = 750f,
                standardDisplay = "450 - 750 mV (ثابت)",
                category = "سیستم سوخت و آلایندگی",
                formulaDesc = "A * 0.005 V (پایش کاتالیزور)"
            ),
            ObdSensor(
                id = "o2_s1_out",
                pid = ObdPidConstants.PID_O2_SENSOR_1_VOLTAGE,
                pidHex = "0x14",
                name = "O2 Sensor 1 Output Status",
                persianName = "خروجی سنسور اکسیژن بالا",
                unit = "Lambda",
                minStandard = 0.95f,
                maxStandard = 1.05f,
                standardDisplay = "0.97 - 1.03 λ",
                category = "سیستم سوخت و آلایندگی",
                formulaDesc = "نسبت استوکیومتری سوخت"
            ),
            ObdSensor(
                id = "o2_s2_out",
                pid = ObdPidConstants.PID_O2_SENSOR_2_VOLTAGE,
                pidHex = "0x15",
                name = "O2 Sensor 2 Output Status",
                persianName = "خروجی سنسور اکسیژن پایین",
                unit = "Lambda",
                minStandard = 0.98f,
                maxStandard = 1.02f,
                standardDisplay = "0.99 - 1.01 λ",
                category = "سیستم سوخت و آلایندگی",
                formulaDesc = "راندمان عملکرد کاتالیزور"
            ),
            ObdSensor(
                id = "o2_heater",
                pid = ObdPidConstants.PID_PROP_O2_HEATER,
                pidHex = "0x105",
                name = "O2 Sensor Heater",
                persianName = "گرمکن سنسور اکسیژن",
                unit = "Ω",
                minStandard = 4.0f,
                maxStandard = 18.0f,
                standardDisplay = "6.0 - 15.0 Ω (فعال)",
                category = "سنسورها",
                formulaDesc = "مقاومت مدار گرمایش سنسور"
            ),
            ObdSensor(
                id = "inj_1",
                pid = ObdPidConstants.PID_PROP_INJECTOR_1_PULSE,
                pidHex = "0x101",
                name = "Injector 1 Pulse Width",
                persianName = "انژکتور یک",
                unit = "ms",
                minStandard = 2.5f,
                maxStandard = 4.5f,
                standardDisplay = "2.8 - 4.2 ms (در دور آرام)",
                category = "سوخت‌رسانی",
                formulaDesc = "زمان پاشش سیلندر ۱"
            ),
            ObdSensor(
                id = "inj_2",
                pid = ObdPidConstants.PID_PROP_INJECTOR_2_PULSE,
                pidHex = "0x102",
                name = "Injector 2 Pulse Width",
                persianName = "انژکتور دو",
                unit = "ms",
                minStandard = 2.5f,
                maxStandard = 4.5f,
                standardDisplay = "2.8 - 4.2 ms (در دور آرام)",
                category = "سوخت‌رسانی",
                formulaDesc = "زمان پاشش سیلندر ۲"
            ),
            ObdSensor(
                id = "inj_3",
                pid = ObdPidConstants.PID_PROP_INJECTOR_3_PULSE,
                pidHex = "0x103",
                name = "Injector 3 Pulse Width",
                persianName = "انژکتور سه",
                unit = "ms",
                minStandard = 2.5f,
                maxStandard = 4.5f,
                standardDisplay = "2.8 - 4.2 ms (در دور آرام)",
                category = "سوخت‌رسانی",
                formulaDesc = "زمان پاشش سیلندر ۳"
            ),
            ObdSensor(
                id = "inj_4",
                pid = ObdPidConstants.PID_PROP_INJECTOR_4_PULSE,
                pidHex = "0x104",
                name = "Injector 4 Pulse Width",
                persianName = "انژکتور چهار",
                unit = "ms",
                minStandard = 2.5f,
                maxStandard = 4.5f,
                standardDisplay = "2.8 - 4.2 ms (در دور آرام)",
                category = "سوخت‌رسانی",
                formulaDesc = "زمان پاشش سیلندر ۴"
            ),
            ObdSensor(
                id = "map_pressure",
                pid = ObdPidConstants.PID_INTAKE_MAP,
                pidHex = "0x0B",
                name = "Manifold Absolute Pressure (MAP)",
                persianName = "سنسور فشار منیفولد",
                unit = "mbar",
                minStandard = 280f,
                maxStandard = 420f,
                standardDisplay = "300 - 390 mbar (دور آرام)",
                category = "سنسورهای هوا",
                formulaDesc = "فشار مکش محفظه احتراق"
            ),
            // PAGE 2 (Items 11-20)
            ObdSensor(
                id = "iat_temp",
                pid = ObdPidConstants.PID_INTAKE_AIR_TEMP,
                pidHex = "0x0F",
                name = "Intake Air Temperature (IAT)",
                persianName = "سنسور دمای هوا",
                unit = "°C",
                minStandard = 15f,
                maxStandard = 60f,
                standardDisplay = "20 - 55 °C",
                category = "سنسورهای هوا",
                formulaDesc = "A - 40"
            ),
            ObdSensor(
                id = "throttle_elec",
                pid = ObdPidConstants.PID_THROTTLE_POSITION,
                pidHex = "0x11",
                name = "Electronic Throttle Valve",
                persianName = "دریچه برقی گاز",
                unit = "%",
                minStandard = 3.5f,
                maxStandard = 14f,
                standardDisplay = "5.0 - 12.0 % (درجا)",
                category = "ورودی هوا",
                formulaDesc = "(A * 100) / 255"
            ),
            ObdSensor(
                id = "coolant_temp",
                pid = ObdPidConstants.PID_COOLANT_TEMP,
                pidHex = "0x05",
                name = "Engine Coolant Temperature (ECT)",
                persianName = "دمای آب",
                unit = "°C",
                minStandard = 82f,
                maxStandard = 98f,
                standardDisplay = "85 - 94 °C",
                category = "سیستم خنک‌کننده",
                formulaDesc = "A - 40"
            ),
            ObdSensor(
                id = "engine_rpm",
                pid = ObdPidConstants.PID_ENGINE_RPM,
                pidHex = "0x0C",
                name = "Engine Speed (RPM)",
                persianName = "دور موتور",
                unit = "RPM",
                minStandard = 780f,
                maxStandard = 920f,
                standardDisplay = "820 - 880 RPM (دور آرام)",
                category = "موتور",
                formulaDesc = "((A * 256) + B) / 4"
            ),
            ObdSensor(
                id = "vehicle_speed",
                pid = ObdPidConstants.PID_VEHICLE_SPEED,
                pidHex = "0x0D",
                name = "Vehicle Speed",
                persianName = "سرعت",
                unit = "km/h",
                minStandard = 0f,
                maxStandard = 220f,
                standardDisplay = "0 - 180 km/h",
                category = "حرکت",
                formulaDesc = "A"
            ),
            ObdSensor(
                id = "battery_voltage",
                pid = ObdPidConstants.PID_CONTROL_MODULE_VOLTAGE,
                pidHex = "0x42",
                name = "Battery Voltage",
                persianName = "ولتاژ باتری",
                unit = "V",
                minStandard = 13.5f,
                maxStandard = 14.6f,
                standardDisplay = "13.8 - 14.4 V (دینام فعال)",
                category = "سیستم برق",
                formulaDesc = "((A * 256) + B) / 1000"
            ),
            ObdSensor(
                id = "inj_phase",
                pid = ObdPidConstants.PID_FUEL_INJECTION_TIMING,
                pidHex = "0x5D",
                name = "Sequential Fuel Injection Phase",
                persianName = "مرحله بندی پاشش سوخت",
                unit = "Deg",
                minStandard = 280f,
                maxStandard = 380f,
                standardDisplay = "310 - 360 Deg CA",
                category = "سوخت‌رسانی",
                formulaDesc = "زاویه میل‌لنگ نسبت به TDC"
            ),
            ObdSensor(
                id = "canister_purge",
                pid = ObdPidConstants.PID_EVAP_PURGE,
                pidHex = "0x2E",
                name = "Canister Purge Solenoid Valve",
                persianName = "شیربرقی کنیستر",
                unit = "%",
                minStandard = 0f,
                maxStandard = 100f,
                standardDisplay = "0 - 45 % (کنترل بخار بنزین)",
                category = "سیستم آلایندگی",
                formulaDesc = "(A * 100) / 255"
            ),
            ObdSensor(
                id = "fuel_rail_press",
                pid = ObdPidConstants.PID_FUEL_PRESSURE,
                pidHex = "0x0A",
                name = "Fuel Rail Pressure",
                persianName = "فشار ریل سوخت",
                unit = "bar",
                minStandard = 3.2f,
                maxStandard = 4.2f,
                standardDisplay = "3.5 - 3.9 bar",
                category = "سوخت‌رسانی",
                formulaDesc = "فشار پمپ بنزین و رگلاتور"
            ),
            ObdSensor(
                id = "fuel_rail_temp",
                pid = ObdPidConstants.PID_PROP_FUEL_RAIL_AIR_TEMP,
                pidHex = "0x106",
                name = "Fuel Rail Air Temperature",
                persianName = "دمای هوا ریل سوخت",
                unit = "°C",
                minStandard = 20f,
                maxStandard = 65f,
                standardDisplay = "25 - 55 °C",
                category = "سوخت‌رسانی",
                formulaDesc = "دمای محفظه ریل سوخت"
            ),
            // PAGE 3 (Items 21+)
            ObdSensor(
                id = "intake_manifold_press",
                pid = ObdPidConstants.PID_INTAKE_MAP,
                pidHex = "0x0B",
                name = "Intake Manifold Pressure",
                persianName = "فشار منیفولد ورودی",
                unit = "kPa",
                minStandard = 30f,
                maxStandard = 42f,
                standardDisplay = "32 - 40 kPa (خلاء مانیفولد)",
                category = "ورودی هوا",
                formulaDesc = "A"
            ),
            ObdSensor(
                id = "pedal_pos",
                pid = ObdPidConstants.PID_ACCEL_PEDAL_POS_D,
                pidHex = "0x49",
                name = "Accelerator Pedal Position",
                persianName = "سنسور پدال گاز",
                unit = "%",
                minStandard = 0f,
                maxStandard = 100f,
                standardDisplay = "0 % (رها شده)",
                category = "سیستم گاز",
                formulaDesc = "(A * 100) / 255"
            ),
            ObdSensor(
                id = "spark_advance",
                pid = ObdPidConstants.PID_TIMING_ADVANCE,
                pidHex = "0x0E",
                name = "Ignition Timing Advance",
                persianName = "آوانس جرقه",
                unit = "Deg",
                minStandard = 4f,
                maxStandard = 15f,
                standardDisplay = "6.0 - 12.0 Deg BTDC",
                category = "جرقه و احتراق",
                formulaDesc = "(A / 2) - 64"
            ),
            ObdSensor(
                id = "coil_charge_time",
                pid = ObdPidConstants.PID_PROP_COIL_CHARGE_TIME,
                pidHex = "0x108",
                name = "Ignition Coil Charge Time (Dwell)",
                persianName = "زمان شارژ کویل‌ها",
                unit = "ms",
                minStandard = 2.0f,
                maxStandard = 3.6f,
                standardDisplay = "2.2 - 3.2 ms",
                category = "جرقه و احتراق",
                formulaDesc = "شارژ القایی سیم‌پیچ کویل"
            ),
            ObdSensor(
                id = "stepper_pos",
                pid = ObdPidConstants.PID_PROP_STEPPER_MOTOR_STEPS,
                pidHex = "0x107",
                name = "Idle Air Stepper Motor Steps",
                persianName = "استپر موتور / موتور پله‌ای",
                unit = "Step",
                minStandard = 25f,
                maxStandard = 55f,
                standardDisplay = "30 - 45 Step (دور آرام)",
                category = "ورودی هوا",
                formulaDesc = "تنظیم هوای دور آرام"
            ),
            ObdSensor(
                id = "fuel_trim_short",
                pid = ObdPidConstants.PID_SHORT_TERM_FUEL_TRIM_1,
                pidHex = "0x06",
                name = "Short Term Fuel Trim",
                persianName = "درصد تصحیح کوتاه‌مدت سوخت",
                unit = "%",
                minStandard = -10f,
                maxStandard = 10f,
                standardDisplay = "-8.0 % تا +8.0 %",
                category = "سوخت‌رسانی",
                formulaDesc = "((A - 128) * 100) / 128"
            ),
            ObdSensor(
                id = "fuel_trim_long",
                pid = ObdPidConstants.PID_LONG_TERM_FUEL_TRIM_1,
                pidHex = "0x07",
                name = "Long Term Fuel Trim",
                persianName = "درصد تصحیح بلندمدت سوخت",
                unit = "%",
                minStandard = -10f,
                maxStandard = 10f,
                standardDisplay = "-6.0 % تا +6.0 %",
                category = "سوخت‌رسانی",
                formulaDesc = "((A - 128) * 100) / 128"
            ),
            ObdSensor(
                id = "engine_load",
                pid = ObdPidConstants.PID_ENGINE_LOAD,
                pidHex = "0x04",
                name = "Calculated Engine Load",
                persianName = "بار موتور",
                unit = "%",
                minStandard = 15f,
                maxStandard = 30f,
                standardDisplay = "18 - 26 % (درجا)",
                category = "موتور",
                formulaDesc = "(A * 100) / 255"
            ),
            ObdSensor(
                id = "baro_pressure",
                pid = ObdPidConstants.PID_BAROMETRIC_PRESSURE,
                pidHex = "0x33",
                name = "Barometric Atmospheric Pressure",
                persianName = "فشار اتمسفر محیط",
                unit = "kPa",
                minStandard = 85f,
                maxStandard = 103f,
                standardDisplay = "90 - 101 kPa (ارتفاع تهران)",
                category = "سنسورهای هوا",
                formulaDesc = "A"
            ),
            ObdSensor(
                id = "catalyst_temp",
                pid = ObdPidConstants.PID_CATALYST_TEMP,
                pidHex = "0x3C",
                name = "Catalytic Converter Temperature",
                persianName = "دمای کاتالیزور",
                unit = "°C",
                minStandard = 350f,
                maxStandard = 850f,
                standardDisplay = "420 - 750 °C",
                category = "سیستم سوخت و آلایندگی",
                formulaDesc = "((A * 256) + B) / 10 - 40"
            )
        )

        val defaultFaultCodes = listOf(
            DiagnosticTroubleCode(
                code = "P0130",
                persianTitle = "ایراد در مدار سنسور اکسیژن بالا (بانک ۱)",
                englishDescription = "O2 Sensor Circuit Malfunction (Bank 1 Sensor 1)",
                category = "سنسور اکسیژن",
                status = DtcStatus.PERMANENT,
                possibleCauses = "خرابی المنت یا مغزی سنسور اکسیژن بالا، اتصالی سیم‌کشی یا سوکت روغنی",
                freezeFrameData = mapOf("دور موتور" to "860 RPM", "دمای آب" to "88 °C", "سرعت" to "0 km/h")
            ),
            DiagnosticTroubleCode(
                code = "P0115",
                persianTitle = "ایراد در مدار سنسور دمای آب (ECT)",
                englishDescription = "Engine Coolant Temperature Circuit Malfunction",
                category = "سیستم خنک‌کننده",
                status = DtcStatus.PENDING,
                possibleCauses = "قطعی سیم فشنگی آب، خرابی سنسور کله‌سبز/آبی، عدم عملکرد ترموستات",
                freezeFrameData = mapOf("دور موتور" to "920 RPM", "دمای آب" to "-40 °C", "سرعت" to "15 km/h")
            ),
            DiagnosticTroubleCode(
                code = "P0105",
                persianTitle = "ایراد در سنسور فشار منیفولد (MAP) / مدار بار موتور",
                englishDescription = "Manifold Absolute Pressure/Barometric Pressure Circuit",
                category = "سنسورهای هوا",
                status = DtcStatus.PERMANENT,
                possibleCauses = "گرفتگی یا نشتی اورینگ سنسور مپ، نشتی مانیفولد، خرابی سنسور",
                freezeFrameData = mapOf("دور موتور" to "840 RPM", "فشار مپ" to "680 mbar", "سرعت" to "0 km/h")
            ),
            DiagnosticTroubleCode(
                code = "P0300",
                persianTitle = "احتراق ناقص تصادفی / چندگانه (میس‌فایر)",
                englishDescription = "Random/Multiple Cylinder Misfire Detected",
                category = "جرقه و احتراق",
                status = DtcStatus.PENDING,
                possibleCauses = "خرابی وایر و شمع‌ها، نیم‌سوز شدن کویل، کثیفی انژکتورها یا افت فشار بنزین",
                freezeFrameData = mapOf("دور موتور" to "2100 RPM", "بار موتور" to "48 %", "سرعت" to "55 km/h")
            ),
            DiagnosticTroubleCode(
                code = "P0443",
                persianTitle = "ایراد در مدار کنترل شیر برقی کنیستر (EVAP)",
                englishDescription = "Evaporative Emission System Purge Control Valve Circuit",
                category = "کنترل آلایندگی",
                status = DtcStatus.CONFIRMED,
                possibleCauses = "گیرپاژ شیربرقی کنیستر، سوختن بوبین، قطعی سوکت شیربرقی",
                freezeFrameData = mapOf("دور موتور" to "1400 RPM", "دمای آب" to "85 °C", "سرعت" to "40 km/h")
            ),
            DiagnosticTroubleCode(
                code = "P0505",
                persianTitle = "خطا در سیستم کنترل دور آرام موتور (استپر موتور)",
                englishDescription = "Idle Air Control System Malfunction",
                category = "دور آرام",
                status = DtcStatus.PENDING,
                possibleCauses = "کثیفی نشیمنگاه استپر در دریچه گاز، خرابی بوبین استپر موتور",
                freezeFrameData = mapOf("دور موتور" to "1250 RPM", "دمای آب" to "82 °C", "سرعت" to "0 km/h")
            ),
            DiagnosticTroubleCode(
                code = "P0201",
                persianTitle = "قطعی یا ایراد در مدار انژکتور سیلندر ۱",
                englishDescription = "Injector Circuit / Open - Cylinder 1",
                category = "انژکتورها",
                status = DtcStatus.PERMANENT,
                possibleCauses = "سوختن بوبین سوزن انژکتور شماره یک، قطعی سیم تحریک از رله دوبل یا ایسیو",
                freezeFrameData = mapOf("دور موتور" to "820 RPM", "زمان پاشش" to "0.0 ms", "سرعت" to "0 km/h")
            ),
            DiagnosticTroubleCode(
                code = "P0420",
                persianTitle = "کاهش راندمان مبدل کاتالیزور پایین‌تر از آستانه مجاز",
                englishDescription = "Catalyst System Efficiency Below Threshold (Bank 1)",
                category = "کنترل آلایندگی",
                status = DtcStatus.CONFIRMED,
                possibleCauses = "فرسودگی یا تخریب شبکه کاتالیست اگزوز، مصرف روغن، بنزین بی‌کیفیت",
                freezeFrameData = mapOf("دور موتور" to "2400 RPM", "سرعت" to "80 km/h", "دمای آب" to "90 °C")
            ),
            DiagnosticTroubleCode(
                code = "P0562",
                persianTitle = "افت ولتاژ سیستم برق خودرو و ایسیو",
                englishDescription = "System Voltage Low",
                category = "سیستم برق و باتری",
                status = DtcStatus.PENDING,
                possibleCauses = "ضعف دینام یا تسمه دینام، خرابی رگلاتور آفتامات، اتصالات باتری و بدنه ضعیف",
                freezeFrameData = mapOf("ولتاژ" to "11.2 V", "دور موتور" to "780 RPM", "سرعت" to "0 km/h")
            )
        )

        val defaultActuators = listOf(
            // PAGE 1: First 10 Actuators
            ActuatorItem(
                id = "fan_low",
                persianName = "رله فن خنک‌کننده دور کند",
                category = "سیستم خنک‌کننده",
                description = "تست فرمان ایسیو به رله فن دور کند در رادیاتور",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "فعال‌سازی ۳ ثانیه صدای چرخش دور کند فن"
            ),
            ActuatorItem(
                id = "fan_high",
                persianName = "رله فن خنک‌کننده دور تند",
                category = "سیستم خنک‌کننده",
                description = "تست فرمان ایسیو به رله فن دور تند در رادیاتور",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "فعال‌سازی ۳ ثانیه دور ماکزیمم فن"
            ),
            ActuatorItem(
                id = "fuel_pump",
                persianName = "رله پمپ بنزین (رله دوبل)",
                category = "سوخت‌رسانی",
                description = "تست راه‌اندازی رله پمپ بنزین و ایجاد فشار در ریل",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "شنیده شدن صدای ویز پمپ در باک"
            ),
            ActuatorItem(
                id = "inj_test_1",
                persianName = "تست انژکتور سیلندر ۱",
                category = "انژکتورها",
                description = "ارسال پالس ۵ هرتز به سوزن انژکتور ۱ جهت تست تیک‌تیک",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "صدای کلیک مکانیکی سوزن انژکتور"
            ),
            ActuatorItem(
                id = "inj_test_2",
                persianName = "تست انژکتور سیلندر ۲",
                category = "انژکتورها",
                description = "ارسال پالس ۵ هرتز به سوزن انژکتور ۲",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "صدای کلیک مکانیکی سوزن انژکتور"
            ),
            ActuatorItem(
                id = "inj_test_3",
                persianName = "تست انژکتور سیلندر ۳",
                category = "انژکتورها",
                description = "ارسال پالس ۵ هرتز به سوزن انژکتور ۳",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "صدای کلیک مکانیکی سوزن انژکتور"
            ),
            ActuatorItem(
                id = "inj_test_4",
                persianName = "تست انژکتور سیلندر ۴",
                category = "انژکتورها",
                description = "ارسال پالس ۵ هرتز به سوزن انژکتور ۴",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "صدای کلیک مکانیکی سوزن انژکتور"
            ),
            ActuatorItem(
                id = "canister_valve",
                persianName = "شیر برقی کنیستر (EVAP)",
                category = "سیستم آلایندگی",
                description = "باز و بسته کردن مکرر شیر برقی تخلیه بخارات باک",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "صدای ضربه زدن متوالی شیربرقی"
            ),
            ActuatorItem(
                id = "coil_1_4",
                persianName = "کویل جرقه سیلندر ۱ و ۴",
                category = "سیستم جرقه",
                description = "تست تحریک ترانزیستور خروجی کویل ۱ و ۴",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "ایجاد جرقه قوی بین الکترودها"
            ),
            ActuatorItem(
                id = "coil_2_3",
                persianName = "کویل جرقه سیلندر ۲ و ۳",
                category = "سیستم جرقه",
                description = "تست تحریک ترانزیستور خروجی کویل ۲ و ۳",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "ایجاد جرقه قوی بین الکترودها"
            ),

            // PAGE 2: Next 10 Actuators
            ActuatorItem(
                id = "ac_compressor",
                persianName = "رله کلاچ کمپرسور کولر",
                category = "سیستم تهویه",
                description = "درگیر کردن بوبین مگنت کلاچ کولر",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "صدای تق کلاچ کمپرسور کولر"
            ),
            ActuatorItem(
                id = "stepper_in_out",
                persianName = "موتور پله‌ای (استپر موتور)",
                category = "هوای دور آرام",
                description = "حرکت دادن شفت مخروطی استپر به جلو و عقب",
                testModeType = ActuatorTestType.STEP_INCREMENT,
                standardRef = "حرکت نرم شفت بدون گیرپاژ"
            ),
            ActuatorItem(
                id = "throttle_motor",
                persianName = "موتور دریچه گاز برقی",
                category = "هوای ورودی",
                description = "تست باز شدن پولکی دریچه گاز تا ۴۰ درصد و بازگشت با فنر",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "حرکت سریع پولکی دریچه گاز"
            ),
            ActuatorItem(
                id = "mil_lamp",
                persianName = "چراغ چک آمپر (MIL)",
                category = "نشانگرهای پشت آمپر",
                description = "روشن و خاموش کردن چراغ اخطار عیب‌یابی در صفحه کیلومتر",
                testModeType = ActuatorTestType.TOGGLE_ON_OFF,
                standardRef = "روشن شدن ال‌ای‌دی زرد/نارنجی چک"
            ),
            ActuatorItem(
                id = "o2_heater_test",
                persianName = "گرمکن سنسور اکسیژن بالا",
                category = "سنسورها",
                description = "برقراری جریان در المنت گرمایش سنسور اکسیژن",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "گرم شدن نوک سنسور و جریان ۱ تا ۲ آمپر"
            ),
            ActuatorItem(
                id = "battery_dyn_test",
                persianName = "تست افت ولتاژ باتری و دینام",
                category = "برق و باتری",
                description = "تست بارگیری مقطعی و اندازه‌گیری پاسخ ولتاژ آلترناتور",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "عدم افت ولتاژ به زیر ۱۲.۲ ولت"
            ),
            ActuatorItem(
                id = "abs_solenoid",
                persianName = "شیرهای برقی بلوک هیدرولیک ABS",
                category = "ترمز و پایداری",
                description = "پایش و فعال‌سازی شیرهای ورودی و خروجی چرخ‌ها",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "پالس هیدرولیک در پدال ترمز"
            ),
            ActuatorItem(
                id = "gearbox_solenoid",
                persianName = "شیربرقی‌های گیربکس اتوماتیک (AL4)",
                category = "جعبه دنده",
                description = "تست شیربرقی تنظیم فشار خط روغن و قفل کن توربین",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "فشار خط ۲.۶ تا ۱۱ بار"
            ),
            ActuatorItem(
                id = "temp_gauge",
                persianName = "عقربه دمای آب پشت آمپر",
                category = "نشانگرها",
                description = "انتقال سیگنال عقربه به موقعیت وسط (۹۰ درجه)",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "قرارگیری عقربه دقیقا روی ۹۰ درجه"
            ),
            ActuatorItem(
                id = "rpm_gauge",
                persianName = "عقربه دور موتور پشت آمپر",
                category = "نشانگرها",
                description = "ارسال پالس شبیه‌ساز ۳۰۰۰ دور به صفحه کیلومتر",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "قرارگیری عقربه روی ۳۰۰۰ RPM"
            ),

            // PAGE 3: Additional Actuators
            ActuatorItem(
                id = "thermostat_elec",
                persianName = "ترموستات برقی و شیر کنترل آب",
                category = "سیستم خنک‌کننده",
                description = "کنترل گرمایش المنتی ترموستات موتورهای پیشرفته",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "باز شدن مسیر رادیاتور در ۸۹ درجه"
            ),
            ActuatorItem(
                id = "variable_cam_vvt",
                persianName = "شیر برقی زمان‌بندی متغیر سوپاپ (VVT / CVVT)",
                category = "موتور",
                description = "اعمال فشار روغن به چرخ‌دنده متغیر میل‌سوپاپ هوا",
                testModeType = ActuatorTestType.PULSE_3S,
                standardRef = "تغییر زاویه ۱۰ تا ۳۵ درجه میل‌سوپاپ"
            )
        )
    }

    /**
     * Serializes current library to JSON format for file backup / export
     */
    fun exportToJson(
        sensors: List<ObdSensor>,
        dtcs: List<DiagnosticTroubleCode>
    ): String {
        val root = JSONObject()
        root.put("version", "1.5.0")
        root.put("app", "lonak diag OBDII Library")
        root.put("updatedAt", System.currentTimeMillis())

        val sensorsArr = JSONArray()
        for (s in sensors) {
            val sObj = JSONObject()
            sObj.put("id", s.id)
            sObj.put("pid", s.pid)
            sObj.put("pidHex", s.pidHex)
            sObj.put("name", s.name)
            sObj.put("persianName", s.persianName)
            sObj.put("unit", s.unit)
            sObj.put("minStandard", s.minStandard.toDouble())
            sObj.put("maxStandard", s.maxStandard.toDouble())
            sObj.put("standardDisplay", s.standardDisplay)
            sObj.put("category", s.category)
            sObj.put("formulaDesc", s.formulaDesc)
            sensorsArr.put(sObj)
        }
        root.put("sensors", sensorsArr)

        val dtcArr = JSONArray()
        for (d in dtcs) {
            val dObj = JSONObject()
            dObj.put("code", d.code)
            dObj.put("persianTitle", d.persianTitle)
            dObj.put("englishDescription", d.englishDescription)
            dObj.put("category", d.category)
            dObj.put("status", d.status.name)
            dObj.put("possibleCauses", d.possibleCauses)
            dtcArr.put(dObj)
        }
        root.put("dtcs", dtcArr)

        return root.toString(2)
    }

    /**
     * Imports library from JSON format
     */
    fun importFromJson(jsonString: String): Pair<List<ObdSensor>, List<DiagnosticTroubleCode>> {
        val root = JSONObject(jsonString)
        val sensorList = mutableListOf<ObdSensor>()
        val dtcList = mutableListOf<DiagnosticTroubleCode>()

        if (root.has("sensors")) {
            val sensorsArr = root.getJSONArray("sensors")
            for (i in 0 until sensorsArr.length()) {
                val sObj = sensorsArr.getJSONObject(i)
                sensorList.add(
                    ObdSensor(
                        id = sObj.optString("id", "sensor_$i"),
                        pid = sObj.optInt("pid", 0),
                        pidHex = sObj.optString("pidHex", "0x00"),
                        name = sObj.optString("name", "Unknown"),
                        persianName = sObj.optString("persianName", "سنسور"),
                        unit = sObj.optString("unit", ""),
                        minStandard = sObj.optDouble("minStandard", 0.0).toFloat(),
                        maxStandard = sObj.optDouble("maxStandard", 100.0).toFloat(),
                        standardDisplay = sObj.optString("standardDisplay", "-"),
                        currentValue = sObj.optDouble("minStandard", 0.0).toFloat(),
                        displayValue = "0",
                        isNormal = true,
                        category = sObj.optString("category", "موتور"),
                        formulaDesc = sObj.optString("formulaDesc", "")
                    )
                )
            }
        }

        if (root.has("dtcs")) {
            val dtcArr = root.getJSONArray("dtcs")
            for (i in 0 until dtcArr.length()) {
                val dObj = dtcArr.getJSONObject(i)
                val statusStr = dObj.optString("status", "PERMANENT")
                val status = try {
                    DtcStatus.valueOf(statusStr)
                } catch (e: Exception) {
                    DtcStatus.PERMANENT
                }

                dtcList.add(
                    DiagnosticTroubleCode(
                        code = dObj.optString("code", "P0000"),
                        persianTitle = dObj.optString("persianTitle", "خطای ناشناخته"),
                        englishDescription = dObj.optString("englishDescription", "Unknown"),
                        category = dObj.optString("category", "عمومی"),
                        status = status,
                        possibleCauses = dObj.optString("possibleCauses", "")
                    )
                )
            }
        }

        return Pair(
            if (sensorList.isNotEmpty()) sensorList else defaultSensors,
            if (dtcList.isNotEmpty()) dtcList else defaultFaultCodes
        )
    }
}
