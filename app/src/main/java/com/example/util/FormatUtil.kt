package com.example.util

import android.content.Context
import android.content.Intent
import java.util.Locale

object FormatUtil {

    private val SUFFIXES = arrayOf(
        "", "K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No", "Dc",
        "Ud", "Dd", "Td", "Qad", "Qid", "Sxd", "Spd", "Ocd", "Nod", "Vg", "Uvg"
    )

    fun formatNumber(value: Double): String {
        if (value < 0.0) return "0"
        if (value < 1000.0) {
            return String.format(Locale.US, "%.1f", value).replace(".0", "")
        }

        var num = value
        var index = 0
        while (num >= 1000.0 && index < SUFFIXES.size - 1) {
            num /= 1000.0
            index++
        }

        return String.format(Locale.US, "%.2f %s", num, SUFFIXES[index])
    }

    fun formatTime(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return when {
            h > 0 -> String.format(Locale.US, "%dh %dm %ds", h, m, s)
            m > 0 -> String.format(Locale.US, "%dm %ds", m, s)
            else -> String.format(Locale.US, "%ds", s)
        }
    }

    private val BLOCKED_WORDS = listOf(
        "idiota", "lixo", "merda", "bosta", "otario", "desgraca", "noob lixo",
        "morra", "imbecil", "porra", "caralho", "foder", "puta", "viado", "fdp"
    )

    fun filterToxicity(text: String): Pair<String, Boolean> {
        var clean = text
        var wasFiltered = false
        for (bad in BLOCKED_WORDS) {
            val regex = Regex("(?i)\\b$bad\\b")
            if (regex.containsMatchIn(clean)) {
                clean = regex.replace(clean, "***")
                wasFiltered = true
            }
        }
        return Pair(clean, wasFiltered)
    }

    fun shareAchievement(context: Context, achievementName: String, energy: String) {
        val message = "🚀 Conquistei '$achievementName' no AstroClicker: Idle RPG com $energy de Energia Cósmica! Você consegue me superar? Baixe agora e dispute o Ranking Global!"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Compartilhar Conquista Galáctica")
        context.startActivity(shareIntent)
    }

    fun shareDuelInvite(context: Context, playerMmr: Int, playerId: String) {
        val message = "⚔️ DESAFIO DE DUELO: Convidando você para uma batalha de velocidade de cliques no AstroClicker: Idle RPG! Meu MMR: $playerMmr. Use meu Código de Duelo: $playerId para conectar!"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Convidar Amigo para Duelo")
        context.startActivity(shareIntent)
    }
}
