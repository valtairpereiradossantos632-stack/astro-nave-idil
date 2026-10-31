package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.anticheat.AntiCheatEngine
import com.example.data.model.GameDefinitions
import com.example.util.FormatUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AstroClicker: Idle RPG", appName)
  }

  @Test
  fun `test format number`() {
    assertEquals("100", FormatUtil.formatNumber(100.0))
    assertEquals("1.50 K", FormatUtil.formatNumber(1500.0))
    assertEquals("2.00 M", FormatUtil.formatNumber(2000000.0))
  }

  @Test
  fun `test anti cheat checksum verification`() {
    val data = "1000:5:1:1200:50"
    val hash = AntiCheatEngine.generateChecksum(data)
    assertTrue(AntiCheatEngine.verifyChecksum(data, hash))
  }

  @Test
  fun `test toxicity filter`() {
    val (clean, wasFiltered) = FormatUtil.filterToxicity("Voce e um idiota no jogo")
    assertTrue(wasFiltered)
    assertTrue(clean.contains("***"))
  }

  @Test
  fun `test generator cost increase`() {
    val probe = GameDefinitions.DEFAULT_GENERATORS.first()
    val baseCost = probe.currentCost(1.0)
    val leveled = probe.copy(level = 10)
    assertTrue(leveled.currentCost(1.0) > baseCost)
  }
}
