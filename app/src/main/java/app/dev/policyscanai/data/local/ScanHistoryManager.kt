package app.dev.policyscanai.data.local

import android.content.Context
import app.dev.policyscanai.data.model.ScanHistoryItem
import com.google.gson.Gson

class ScanHistoryManager(private val context: Context) {

    private val prefs = context.getSharedPreferences(
        "policy_scan_history", Context.MODE_PRIVATE
    )

    fun getAllScans(): List<ScanHistoryItem> {
        val json = prefs.getString("scans", null)
            ?: return emptyList()
        return try {
            Gson().fromJson(json, Array<ScanHistoryItem>::class.java)
                .toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveScan(scan: ScanHistoryItem) {
        val current = getAllScans().toMutableList()
        current.add(0, scan)
        prefs.edit()
            .putString("scans", Gson().toJson(current))
            .apply()
    }

    fun deleteScan(id: String): List<ScanHistoryItem> {
        val updated = getAllScans()
            .filter { it.id != id }
        prefs.edit()
            .putString("scans", Gson().toJson(updated))
            .apply()
        return updated
    }

    fun getScanById(id: String): ScanHistoryItem? {
        return getAllScans().find { it.id == id }
    }

    fun clearAll() {
        prefs.edit().remove("scans").apply()
    }
}
