package com.flknlabs.accelerometerandgyroscopeexample


import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var mSensorManager : SensorManager
    private lateinit var mAccelerometer : Sensor
    private lateinit var mGyroscope : Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        mSensorManager.registerListener(accelerometerSensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager.registerListener(gyroscopeSensorListener, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(accelerometerSensorListener)
        mSensorManager.unregisterListener(gyroscopeSensorListener)
    }

    private val accelerometerSensorListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            acceleration.text = "Aceleracion X: ${sensorEvent.values[0]}\nAceleracion Y: ${sensorEvent.values[1]}\nAceleracion Z: ${sensorEvent.values[2]}"
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }

    private val gyroscopeSensorListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            if(sensorEvent.values[2] > 0.5f) { // anticlockwise
                background.setBackgroundColor(Color.BLUE)
            } else if(sensorEvent.values[2] < -0.5f) { // clockwise
                background.setBackgroundColor(Color.YELLOW)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }
}