package com.example.casttotv.vpn

import android.app.Notification
import android.os.ParcelFileDescriptor
import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.RequiresApi
import android.os.Build
import android.widget.Toast
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.core.util.Pair
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import android.R


class VpnService : android.net.VpnService(), Handler.Callback {
    private var mHandler: Handler? = null

    private class Connection(thread: Thread?, pfd: ParcelFileDescriptor?) :
        Pair<Thread?, ParcelFileDescriptor>(thread, pfd)

    private val mConnectingThread = AtomicReference<Thread?>()
    private val mConnection = AtomicReference<Connection?>()
    private val mNextConnectionId = AtomicInteger(1)
    private var mConfigureIntent: PendingIntent? = null
    override fun onCreate() {
        // The handler is only used to show messages.
        if (mHandler == null) {
            mHandler = Handler(this)
        }
        // Create the intent to "configure" the connection (just start ToyVpnClient).
        mConfigureIntent = PendingIntent.getActivity(
            this, 0, Intent(this, VpnClient::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return if (ACTION_DISCONNECT == intent.action) {
            disconnect()
            START_NOT_STICKY
        } else {
            connect()
            START_STICKY
        }
    }

    override fun onDestroy() {
        disconnect()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun handleMessage(message: Message): Boolean {
        Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show()
//        if (message.what != com.example.casttotv.R.string.disconnected) {
//            updateForegroundNotification(message.what)
//        }
        return true
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun connect() {
        // Become a foreground service. Background services can be VPN services too, but they can
        // be killed by background check before getting a chance to receive onRevoke().
//        updateForegroundNotification(com.example.casttotv.R.string.connecting)
//        mHandler!!.sendEmptyMessage(com.example.casttotv.R.string.connecting)
        // Extract information from the shared preferences.
        val prefs = getSharedPreferences(VpnClient.Prefs.NAME, MODE_PRIVATE)
        val server = prefs.getString(VpnClient.Prefs.SERVER_ADDRESS, "")
        val secret = prefs.getString(VpnClient.Prefs.SHARED_SECRET, "")!!.toByteArray()
        val allow = prefs.getBoolean(VpnClient.Prefs.ALLOW, true)
        val packages = prefs.getStringSet(VpnClient.Prefs.PACKAGES, emptySet())
        val port = prefs.getInt(VpnClient.Prefs.SERVER_PORT, 0)
        val proxyHost = prefs.getString(VpnClient.Prefs.PROXY_HOSTNAME, "")
        val proxyPort = prefs.getInt(VpnClient.Prefs.PROXY_PORT, 0)
        startConnection(
            VpnConnection(
                this, mNextConnectionId.getAndIncrement(), server!!, port, secret,
                proxyHost, proxyPort, allow, packages!!
            )
        )
    }

    private fun startConnection(connection: VpnConnection) {
        // Replace any existing connecting thread with the  new one.
        val thread = Thread(connection, "ToyVpnThread")
        setConnectingThread(thread)
        // Handler to mark as connected once onEstablish is called.
        connection.setConfigureIntent(mConfigureIntent)
        connection.setOnEstablishListener(object : VpnConnection.OnEstablishListener {
            override fun onEstablish(tunInterface: ParcelFileDescriptor?) {
//                mHandler!!.sendEmptyMessage(com.example.casttotv.R.string.connected)
                mConnectingThread.compareAndSet(thread, null)
                setConnection(Connection(thread, tunInterface))
            }

        })

        thread.start()

    }

    private fun setConnectingThread(thread: Thread?) {
        val oldThread = mConnectingThread.getAndSet(thread)
        oldThread?.interrupt()
    }

    private fun setConnection(connection: Connection?) {
        val oldConnection = mConnection.getAndSet(connection)
        if (oldConnection != null) {
            try {
                oldConnection.first!!.interrupt()
                oldConnection.second!!.close()
            } catch (e: IOException) {
                Log.e(TAG, "Closing VPN interface", e)
            }
        }
    }

    private fun disconnect() {
//        mHandler!!.sendEmptyMessage(com.example.casttotv.R.string.disconnected)
        setConnectingThread(null)
        setConnection(null)
        stopForeground(true)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun updateForegroundNotification(message: Int) {
        val NOTIFICATION_CHANNEL_ID = "Vpn"
        val mNotificationManager = getSystemService(
            NOTIFICATION_SERVICE
        ) as NotificationManager
        mNotificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
        startForeground(
            1, Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(com.example.casttotv.R.drawable.ic_vpn)
                .setContentText(getString(message))
                .setContentIntent(mConfigureIntent)
                .build()
        )
    }

    companion object {
        private val TAG = VpnService::class.java.simpleName
        const val ACTION_CONNECT = "com.example.android.toyvpn.START"
        const val ACTION_DISCONNECT = "com.example.android.toyvpn.STOP"
    }
}