package com.flknlabs.fingerprintexample

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


@RequiresApi(Build.VERSION_CODES.M)
class FingerprintHandler(private val context: Context): FingerprintManager.AuthenticationCallback() {

    fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject?) {
        val cancellationSignal = CancellationSignal()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.USE_FINGERPRINT
            ) != PackageManager.PERMISSION_GRANTED
        ) { return }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }


    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        update("Error de Autenticación de huellas dactilares\n$errString", false)
    }


    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        update("Ayuda de Autenticación de huellas dactilares\n$helpString", false)
    }


    override fun onAuthenticationFailed() {
        update("Fallo al autenticar con la huella dactilar.", false)
    }


    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        update("Éxito al autenticar con la huella dactilar.", true)
    }


    fun update(e: String?, success: Boolean) {
        val textView = (context as Activity?)!!.findViewById<View>(R.id.errorText) as TextView
        textView.text = e
        if (success) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.successText))
        }
    }
}