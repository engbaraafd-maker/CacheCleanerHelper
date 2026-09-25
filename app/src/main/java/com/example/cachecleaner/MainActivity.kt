package com.example.cachecleaner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: AppListAdapter
    private var allApps: List<AppInfoItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val etSearch = findViewById<EditText>(R.id.etSearch)
        val btnClearOwnCache = findViewById<MaterialButton>(R.id.btnClearOwnCache)
        val btnStorageSettings = findViewById<MaterialButton>(R.id.btnStorageSettings)

        Thread {
            allApps = loadInstalledApps()
            runOnUiThread {
                adapter = AppListAdapter(allApps) { app -> openAppSettings(app.packageName) }
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = adapter
            }
        }.start()

        btnClearOwnCache.setOnClickListener {
            val deleted = cacheDir.deleteRecursively()
            Toast.makeText(
                this,
                if (deleted) "تم مسح كاش هذا التطبيق بنجاح" else "لا يوجد كاش لمسحه",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnStorageSettings.setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "تعذر فتح إعدادات التخزين على هذا الجهاز", Toast.LENGTH_SHORT).show()
            }
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim()?.lowercase() ?: ""
                val filtered = if (query.isEmpty()) allApps
                else allApps.filter { it.label.lowercase().contains(query) }
                if (::adapter.isInitialized) adapter.updateData(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadInstalledApps(): List<AppInfoItem> {
        val pm = packageManager
        val packages = pm.getInstalledApplications(0)
        return packages
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .map {
                AppInfoItem(
                    packageName = it.packageName,
                    label = pm.getApplicationLabel(it).toString(),
                    icon = pm.getApplicationIcon(it)
                )
            }
            .sortedBy { it.label.lowercase() }
    }

    private fun openAppSettings(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "تعذر فتح إعدادات هذا التطبيق", Toast.LENGTH_SHORT).show()
        }
    }
}
