package com.example.data.anticheat

import java.security.MessageDigest

object AntiCheatEngine {
    private const val CHECKSUM_SALT = "AstroClicker_FairPlay_Security_v1"
    private const val MAX_ALLOWED_CPS = 25
    private val tapTimestamps = mutableListOf<Long>()

    data class AntiCheatStatus(
        val isVerified: Boolean = true,
        val isAnomalyDetected: Boolean = false,
        val anomalyMessage: String = "",
        val currentCps: Int = 0
    )

    /**
     * Checks tap rate. Returns true if tap is legitimate (human capability),
     * or false if macro/auto-clicker anomaly is detected.
     */
    @Synchronized
    fun validateTap(): Pair<Boolean, AntiCheatStatus> {
        val now = System.currentTimeMillis()
        tapTimestamps.add(now)

        // Keep taps from the last 1000ms
        tapTimestamps.removeAll { now - it > 1000L }
        val cps = tapTimestamps.size

        if (cps > MAX_ALLOWED_CPS) {
            val status = AntiCheatStatus(
                isVerified = false,
                isAnomalyDetected = true,
                anomalyMessage = "Aviso Anti-Macro: Taxa de $cps cliques/s excede o limite humano! Limitador ativo.",
                currentCps = cps
            )
            return Pair(false, status)
        }

        val status = AntiCheatStatus(
            isVerified = true,
            isAnomalyDetected = false,
            anomalyMessage = "",
            currentCps = cps
        )
        return Pair(true, status)
    }

    /**
     * Validates that the device clock was not manipulated backwards
     */
    fun validateTimestamp(lastTimestamp: Long, currentTimestamp: Long): Pair<Boolean, String> {
        if (currentTimestamp < lastTimestamp - 5000L) { // allow 5s slight skew
            return Pair(false, "Alerta Anti-Cheat: Relógio do sistema retrocedido! Bônus offline pausado.")
        }
        val maxAllowedFuture = 1000L * 60 * 60 * 24 * 30 // 30 days
        if (currentTimestamp - lastTimestamp > maxAllowedFuture) {
            return Pair(false, "Alerta Anti-Cheat: Salto temporal inconsistente detectado.")
        }
        return Pair(true, "")
    }

    /**
     * Generates a tamper-proof SHA-256 signature for save exports
     */
    fun generateChecksum(data: String): String {
        val input = "$data:$CHECKSUM_SALT"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }.take(16)
    }

    /**
     * Validates data with its checksum
     */
    fun verifyChecksum(data: String, expectedChecksum: String): Boolean {
        return generateChecksum(data).equals(expectedChecksum, ignoreCase = true)
    }
}
