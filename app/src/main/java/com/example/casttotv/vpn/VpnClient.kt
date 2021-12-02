package com.example.casttotv.vpn

import android.app.Activity
import android.os.Bundle
import com.example.casttotv.R
import android.widget.TextView
import android.widget.RadioButton
import android.content.SharedPreferences
import android.os.Build
import android.content.Intent
import android.widget.Toast
import android.content.pm.PackageInfo
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.lang.NumberFormatException
import java.util.*
import java.util.stream.Collectors

class VpnClient : AppCompatActivity() {
    interface Prefs {
        companion object {
            const val NAME = "connection"
            const val SERVER_ADDRESS = "server.address"
            const val SERVER_PORT = "server.port"
            const val SHARED_SECRET = "shared.secret"
            const val PROXY_HOSTNAME = "proxyhost"
            const val PROXY_PORT = "proxyport"
            const val ALLOW = "allow"
            const val PACKAGES = "packages"
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.form)
        val serverAddress = findViewById<TextView>(R.id.address)
        val serverPort = findViewById<TextView>(R.id.port)
        val sharedSecret = findViewById<TextView>(R.id.secret)
        val proxyHost = findViewById<TextView>(R.id.proxyhost)
        val proxyPort = findViewById<TextView>(R.id.proxyport)
        val allowed = findViewById<RadioButton>(R.id.allowed)
        val packages = findViewById<TextView>(R.id.packages)
        val prefs = getSharedPreferences(Prefs.NAME, MODE_PRIVATE)
        serverAddress.text = prefs.getString(Prefs.SERVER_ADDRESS, "")
        val serverPortPrefValue = prefs.getInt(Prefs.SERVER_PORT, 0)
        serverPort.text = if (serverPortPrefValue == 0) "" else serverPortPrefValue.toString()
        sharedSecret.text = prefs.getString(Prefs.SHARED_SECRET, "")
        proxyHost.text = prefs.getString(Prefs.PROXY_HOSTNAME, "")
        val proxyPortPrefValue = prefs.getInt(Prefs.PROXY_PORT, 0)
        proxyPort.text = if (proxyPortPrefValue == 0) "" else proxyPortPrefValue.toString()
        allowed.isChecked = prefs.getBoolean(Prefs.ALLOW, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            packages.text = java.lang.String.join(
                ", ", prefs.getStringSet(
                    Prefs.PACKAGES, emptySet()
                )
            )
        }
        findViewById<View>(R.id.connect).setOnClickListener { v: View? ->
            if (!checkProxyConfigs(proxyHost.text.toString(),
                    proxyPort.text.toString()
                )
            ) {
                return@setOnClickListener
            }
            var packageSet: Set<String>? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                packageSet = Arrays.stream(packages.text.toString().split(",").toTypedArray())
                    .map { obj: String -> obj.trim { it <= ' ' } }
                    .filter { s: String -> s.isNotEmpty() }
                    .collect(Collectors.toSet())
            }
            if (!checkPackages(packageSet)) {
                return@setOnClickListener
            }
            val serverPortNum: Int = try {
                serverPort.text.toString().toInt()
            } catch (e: NumberFormatException) {
                0
            }
            val proxyPortNum: Int = try {
                proxyPort.text.toString().toInt()
            } catch (e: NumberFormatException) {
                0
            }
            prefs.edit()
                .putString(Prefs.SERVER_ADDRESS, serverAddress.text.toString())
                .putInt(Prefs.SERVER_PORT, serverPortNum)
                .putString(Prefs.SHARED_SECRET, sharedSecret.text.toString())
                .putString(Prefs.PROXY_HOSTNAME, proxyHost.text.toString())
                .putInt(Prefs.PROXY_PORT, proxyPortNum)
                .putBoolean(Prefs.ALLOW, allowed.isChecked)
                .putStringSet(Prefs.PACKAGES, packageSet)
                .apply()
            val intent = android.net.VpnService.prepare(this@VpnClient)
            if (intent != null) {
                startService(serviceIntent.setAction(VpnService.ACTION_CONNECT))

//                startActivityForResult(intent, 0)
            } else {
//                onActivityResult(0, RESULT_OK,null)
                startService(serviceIntent.setAction(VpnService.ACTION_CONNECT))

            }
        }
        findViewById<View>(R.id.disconnect).setOnClickListener { v: View? ->
            startService(
                serviceIntent.setAction(VpnService.ACTION_DISCONNECT)
            )
        }
    }

    private fun checkProxyConfigs(proxyHost: String, proxyPort: String): Boolean {
        val hasIncompleteProxyConfigs = proxyHost.isEmpty() != proxyPort.isEmpty()
        if (hasIncompleteProxyConfigs) {
            Toast.makeText(this, R.string.incomplete_proxy_settings, Toast.LENGTH_SHORT).show()
        }
        return !hasIncompleteProxyConfigs
    }

    private fun checkPackages(packageNames: Set<String>?): Boolean {
        var hasCorrectPackageNames = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            hasCorrectPackageNames = packageNames!!.isEmpty() ||
                    packageManager.getInstalledPackages(0).stream()
                        .map { pi: PackageInfo -> pi.packageName }
                        .collect(Collectors.toSet())
                        .containsAll(packageNames)
        }
        if (!hasCorrectPackageNames) {
            Toast.makeText(this, R.string.unknown_package_names, Toast.LENGTH_SHORT).show()
        }
        return hasCorrectPackageNames
    }



    private val serviceIntent: Intent
        private get() = Intent(this, VpnService::class.java)
}