package com.example

import com.example.data.obd.ObdLibrary
import com.example.data.obd.ObdPidConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {
    @Test
    fun testObdLibraryDefaults() {
        val sensors = ObdLibrary.defaultSensors
        assertTrue(sensors.isNotEmpty())
        assertTrue(sensors.any { it.pid == ObdPidConstants.PID_ENGINE_RPM })
        assertTrue(sensors.any { it.pid == ObdPidConstants.PID_COOLANT_TEMP })
        assertTrue(sensors.any { it.pid == ObdPidConstants.PID_VEHICLE_SPEED })
    }

    @Test
    fun testJsonExportImport() {
        val library = ObdLibrary()
        val originalSensors = ObdLibrary.defaultSensors
        val originalDtcs = ObdLibrary.defaultFaultCodes

        val json = library.exportToJson(originalSensors, originalDtcs)
        assertTrue(json.contains("lonak diag OBDII Library"))

        val (importedSensors, importedDtcs) = library.importFromJson(json)
        assertEquals(originalSensors.size, importedSensors.size)
        assertEquals(originalDtcs.size, importedDtcs.size)
    }

    @Test
    fun testEcuDatabaseContainsRequestedModels() {
        val ecus = com.example.data.model.EcuDatabase.predefinedEcus
        val models = ecus.map { it.model.lowercase() }
        
        // Bosch
        assertTrue(models.any { it.contains("me17") })
        assertTrue(models.any { it.contains("me7.9.7") })

        // Sagem
        assertTrue(models.any { it.contains("al96") || it.contains("sl96") })
        assertTrue(models.any { it.contains("s2000.10") })
        assertTrue(models.any { it.contains("s2000.11") })
        assertTrue(models.any { it.contains("s2000.3f") })
        assertTrue(models.any { it.contains("s2000.pl4") })
        assertTrue(models.any { it.contains("s2000.jcl") })
        assertTrue(models.any { it.contains("s3000") })

        // Valeo
        assertTrue(models.any { it.contains("j35") })

        // Siemens
        assertTrue(models.any { it.contains("c11") })
        assertTrue(models.any { it.contains("c12") })
        assertTrue(models.any { it.contains("c13") })
        assertTrue(models.any { it.contains("c14") })
        assertTrue(models.any { it.contains("c15") })
        assertTrue(models.any { it.contains("c16") })
        assertTrue(models.any { it.contains("cr3") })

        // Ezu
        assertTrue(models.any { it.contains("ezu") })
    }
}
