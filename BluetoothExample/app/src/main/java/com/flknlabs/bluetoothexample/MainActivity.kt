package com.flknlabs.bluetoothexample

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

@RequiresApi(Build.VERSION_CODES.S)
class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothManager: BluetoothManager

    private lateinit var pairedDevices: Set<BluetoothDevice>
    private val BLT_PERMISSION_CODE = 1000
    private val REQUEST_ENABLE_BT = 1

    val list = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissionGranted = requestBTPermission()
        if (permissionGranted) {
            bluetoothManager = getSystemService(BluetoothManager::class.java)
            bluetoothAdapter = bluetoothManager.adapter

            registerReceiver(enableReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))

            if (!bluetoothAdapter.isEnabled) {
                /*val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)*/
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) !=
                    PackageManager.PERMISSION_GRANTED) { return }
                bluetoothAdapter.enable()
                cbEnable.isChecked = true
            } else {
                cbEnable.isChecked = true
            }

            adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, list)
            lvDevices.adapter = adapter

            cbEnable.setOnCheckedChangeListener { compoundButton, b ->
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) !=
                    PackageManager.PERMISSION_GRANTED) {
                            return@setOnCheckedChangeListener
                }

                if (compoundButton.isChecked) bluetoothAdapter.enable()
                else bluetoothAdapter.disable()
            }

            cbVisible.setOnCheckedChangeListener { compoundButton, b ->
                if (compoundButton.isChecked) {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                    startActivityForResult(enableBtIntent, 0)
                }
            }

            btnRegistered.setOnClickListener {
                list.clear()
                adapter.notifyDataSetChanged()
                doRegisteredList()
            }

            btnSearch.setOnClickListener {
                list.clear()
                adapter.notifyDataSetChanged()

                registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) { return@setOnClickListener }
                bluetoothAdapter.startDiscovery()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(enableReceiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { return }
                    list.add(device!!.name)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private val enableReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {}
    }



    private fun requestBTPermission(): Boolean {
        var permissionGranted = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val bltNotGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED
            val bltAdminNotGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_DENIED
            val bltConnectNotGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED
            val bltAdvertiseNotGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_DENIED

            if (bltNotGranted || bltAdminNotGranted || bltConnectNotGranted || bltAdvertiseNotGranted){
                val permission = arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                )
                requestPermissions(permission, BLT_PERMISSION_CODE)
            }
            else{
                permissionGranted = true
            }
        }
        else{
            permissionGranted = true
        }

        return permissionGranted
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BLT_PERMISSION_CODE) {
            if (grantResults.size == 4 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                grantResults[3] == PackageManager.PERMISSION_GRANTED){


            }
            else{
                showAlert("Location permission was denied. Unable to track location.")
            }
        }
    }

    private fun doRegisteredList() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) { return }
        pairedDevices = bluetoothAdapter.bondedDevices

        pairedDevices.forEach {
            list.add(it.name)
        }

        adapter.notifyDataSetChanged()
    }



    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        val dialog = builder.create()
        dialog.show()
    }
}