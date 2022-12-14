package com.flknlabs.ambientalsensorsexample

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var mSensorManager : SensorManager
    private var mLight : Sensor? = null
    private var mPressure : Sensor? = null
    private var mHumidity : Sensor? = null
    private var mAmbientTemperature : Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        mHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
        mAmbientTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        if (mLight == null) {
            Toast.makeText(this, "no hay sensor de luz", Toast.LENGTH_SHORT).show()
        } else {
            mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL)
        }

        if (mPressure == null) {
            Toast.makeText(this, "no hay sensor de presión", Toast.LENGTH_SHORT).show()
        } else {
            mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL)
        }

        if (mHumidity == null) {
            Toast.makeText(this, "no hay sensor de humedad", Toast.LENGTH_SHORT).show()
        } else {
            mSensorManager.registerListener(this, mHumidity, SensorManager.SENSOR_DELAY_NORMAL)
        }

        if (mAmbientTemperature == null) {
            Toast.makeText(this, "no hay sensor de temperatura", Toast.LENGTH_SHORT).show()
        } else {
            mSensorManager.registerListener(this, mAmbientTemperature, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        var txt = ""
        when (event!!.sensor.type) {
            Sensor.TYPE_LIGHT -> {
                txt += """Luminosidad              
${event.values[0]} Lux


"""
                luminosidad.text = txt
            }
            Sensor.TYPE_PRESSURE -> {
                txt += """Presion
${event.values[0]} mBar 


"""
                presion.text = txt
            }
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                txt += """Temperatura
                
${event.values[0]} ºC

"""
                temperatura.text = txt
            }
            Sensor.TYPE_RELATIVE_HUMIDITY -> {
                txt += """Humedad relativa 
${event.values[0]} %

"""
                humedad.text = txt
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
}
