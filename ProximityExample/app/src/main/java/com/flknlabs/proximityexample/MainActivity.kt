package com.flknlabs.proximityexample

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var mSensorManager : SensorManager
    private lateinit var mProximity : Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        mSensorManager.registerListener(this, mProximity, 2 * 1000 * 1000)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if(sensorEvent.values[0] < mProximity.maximumRange) {
            // Detected something nearby
            background.setBackgroundColor(Color.RED)
        } else {
            // Nothing is nearby
            background.setBackgroundColor(Color.GREEN)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }


}