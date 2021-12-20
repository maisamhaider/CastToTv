package com.example.casttotv.ui.fragments

import android.app.Activity
import android.content.*
import android.net.VpnService
import android.os.Bundle
import android.os.RemoteException
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListUpdateCallback
import com.bumptech.glide.Glide
import com.example.casttotv.R
import com.example.casttotv.databinding.FragmentVpnBinding
import com.example.casttotv.openvpn.CheckInternetConnection
import com.example.casttotv.openvpn.SharedPreference
import com.example.casttotv.openvpn.Utils
import com.example.casttotv.openvpn.adapter.ServerListRVAdapter
import com.example.casttotv.openvpn.interfaces.ChangeServer
import com.example.casttotv.openvpn.interfaces.NavItemClickListener
import com.example.casttotv.openvpn.model.Server
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.OpenVPNService.getStatus
import de.blinkt.openvpn.core.OpenVPNService.setDefaultStatus
import de.blinkt.openvpn.core.OpenVPNThread
import de.blinkt.openvpn.core.OpenVPNThread.stop
import de.blinkt.openvpn.core.VpnStatus
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.util.ArrayList

class
VpnFragment : Fragment(), View.OnClickListener, NavItemClickListener {

    private var serverLists: ArrayList<Server>? = null
    private var serverListRVAdapter: ServerListRVAdapter? = null

    private var server: Server? = null
    private var connection: CheckInternetConnection? = null

    var vpnStart = false
    private var preference: SharedPreference? = null


    private var _binding: FragmentVpnBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVpnBinding.inflate(inflater, container, false)
        initializeAll()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vpnBtn.setOnClickListener(this)
        binding.mButtonVpnList.setOnClickListener {
            binding.recyclerView.visibility = View.VISIBLE
            if (serverLists != null) {
                serverListRVAdapter = ServerListRVAdapter(serverLists, requireContext(), this)
                binding.recyclerView.adapter = serverListRVAdapter
            }

        }

        // Checking is vpn already running or not

        // Checking is vpn already running or not
        isServiceRunning()
        VpnStatus.initLogCache(requireActivity().cacheDir)
    }

    private fun initializeAll() {
        preference = SharedPreference(context)
        server = preference!!.server

        // Update current selected server icon
        updateCurrentServerIcon(server!!.flagUrl)
        connection = CheckInternetConnection()


        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        serverLists = serverList

    }

    companion object {
        @JvmStatic
        fun newInstance() = VpnFragment()
    }

    private val serverList: ArrayList<Server>
        private get() {
            val servers = ArrayList<Server>()
            servers.add(
                Server(
                    "Japan",
                    Utils.getImgURL(R.drawable.japan),
                    "japan.ovpn",
                    "vpn",
                    "vpn"
                )
            )


            return servers
        }

    /**
     * @param v: click listener view
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.vpnBtn ->                 // Vpn is running, user would like to disconnect current connection.
                if (vpnStart) {
                    confirmDisconnect()
                } else {
                    prepareVpn()
                }
        }
    }

    /**
     * Show show disconnect confirm dialog
     */
    private fun confirmDisconnect() {
        val builder = AlertDialog.Builder(
            requireActivity()
        )
        builder.setMessage(requireActivity().getString(R.string.connection_close_confirm))
        builder.setPositiveButton(
            requireActivity().getString(R.string.yes)
        ) { dialog, id -> stopVpn() }
        builder.setNegativeButton(
            requireActivity().getString(R.string.no)
        ) { dialog, id ->
            // User cancelled the dialog
        }

        // Create the AlertDialog
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Prepare for vpn connect with required permission
     */
    private fun prepareVpn() {
        if (!vpnStart) {
            if (getInternetStatus()) {

                // Checking permission for network monitor
                val intent = VpnService.prepare(context)
                if (intent != null) {
                    intentLauncher.launch(intent)
                } else startVpn() //have already permission

                // Update confection status
                status("connecting")
            } else {

                // No internet connection available
                showToast("you have no internet connection !!")
            }
        } else if (stopVpn()) {

            // VPN is stopped, show a Toast message.
            showToast("Disconnect Successfully")
        }
    }

    /**
     * Stop vpn
     * @return boolean: VPN status
     */
    private fun stopVpn(): Boolean {
        try {
            stop()
            status("connect")
            vpnStart = false
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    var intentLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startVpn()
        } else {
            showToast("Permission Deny !! ")
        }
    }

    /**
     * Internet connection status.
     */
    private fun getInternetStatus(): Boolean {
        return connection!!.netCheck(context)
    }

    /**
     * Get service status
     */
    private fun isServiceRunning() {
        setStatus(getStatus())
    }

    /**
     * Start the VPN
     */
    private fun startVpn() {
        try {
            // .ovpn file
            val conf = requireActivity().assets.open(server!!.ovpn)
            val isr = InputStreamReader(conf)
            val br = BufferedReader(isr)
            var config = ""
            var line: String?
            while (true) {
                line = br.readLine()
                if (line == null) break
                config += """
                $line
                
                """.trimIndent()
            }
            br.readLine()
            OpenVpnApi.startVpn(
                context,
                config,
                server!!.country,
                server!!.ovpnUserName,
                server!!.ovpnUserPassword
            )

            // Update log
            binding.logTv.text = "Connecting..."
            vpnStart = true
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Status change with corresponding vpn connection status
     * @param connectionState
     */
    fun setStatus(connectionState: String?) {
        if (connectionState != null) when (connectionState) {
            "DISCONNECTED" -> {
                status("connect")
                vpnStart = false
                setDefaultStatus()
                binding.logTv.text = ""
            }
            "CONNECTED" -> {
                vpnStart = true // it will use after restart this activity
                status("connected")
                binding.logTv.text = ""
            }
            "WAIT" -> binding.logTv.text = "waiting for server connection!!"
            "AUTH" -> binding.logTv.text = "server authenticating!!"
            "RECONNECTING" -> {
                status("connecting")
                binding.logTv.text = "Reconnecting..."
            }
            "NONETWORK" -> binding.logTv.text = "No network connection"
        }
    }

    /**
     * Change button background color and text
     * @param status: VPN current status
     */
    fun status(status: String) {
        if (status == "connect") {
            binding.vpnBtn.text = requireContext().getString(R.string.connect)
        } else if (status == "connecting") {
            binding.vpnBtn.text = requireContext().getString(R.string.connecting)
        } else if (status == "connected") {
            binding.vpnBtn.text = requireContext().getString(R.string.disconnect)
        } else if (status == "tryDifferentServer") {
            binding.vpnBtn.setBackgroundResource(R.drawable.button_connected)
            binding.vpnBtn.text = "Try Different\nServer"
        } else if (status == "loading") {
            binding.vpnBtn.setBackgroundResource(R.drawable.button)
            binding.vpnBtn.text = "Loading Server.."
        } else if (status == "invalidDevice") {
            binding.vpnBtn.setBackgroundResource(R.drawable.button_connected)
            binding.vpnBtn.text = "Invalid Device"
        } else if (status == "authenticationCheck") {
            binding.vpnBtn.setBackgroundResource(R.drawable.button_connecting)
            binding.vpnBtn.text = "Authentication \n Checking..."
        }
    }

    /**
     * Receive broadcast message
     */
    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                setStatus(intent.getStringExtra("state"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                var duration = intent.getStringExtra("duration")
                var lastPacketReceive = intent.getStringExtra("lastPacketReceive")
                var byteIn = intent.getStringExtra("byteIn")
                var byteOut = intent.getStringExtra("byteOut")
                if (duration == null) duration = "00:00:00"
                if (lastPacketReceive == null) lastPacketReceive = "0"
                if (byteIn == null) byteIn = " "
                if (byteOut == null) byteOut = " "
                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Update status UI
     * @param duration: running time
     * @param lastPacketReceive: last packet receive time
     * @param byteIn: incoming data
     * @param byteOut: outgoing data
     */
    fun updateConnectionStatus(
        duration: String,
        lastPacketReceive: String,
        byteIn: String,
        byteOut: String
    ) {
        binding.durationTv.text = "Duration: $duration"
        binding.lastPacketReceiveTv.text = "Packet Received: $lastPacketReceive second ago"
        binding.byteInTv.text = "Bytes In: $byteIn"
        binding.byteOutTv.text = "Bytes Out: $byteOut"
    }

    /**
     * Show toast message
     * @param message: toast message
     */
    fun showToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * VPN server country icon change
     * @param serverIcon: icon URL
     */
    private fun updateCurrentServerIcon(serverIcon: String?) {
        Glide.with(requireContext())
            .load(serverIcon)
            .into(binding.selectedServerIcon)
    }


    override fun onResume() {
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcastReceiver, IntentFilter("connectionState"))
        if (server == null) {
            server = preference!!.server
        }
        super.onResume()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver)
        super.onPause()
    }

    /**
     * Save current selected server on local shared preference
     */
    override fun onStop() {
        if (server != null) {
            preference!!.saveServer(server)
        }
        super.onStop()
    }

    override fun clickedItem(index: Int) {
//        preference!!.saveServer(serverList[index])

        server = serverList[index]
        updateCurrentServerIcon(server!!.flagUrl)

        // Stop previous connection
        if (vpnStart) {
            stopVpn()
        }
        prepareVpn()
        binding.recyclerView.visibility = View.GONE
    }


}