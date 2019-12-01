package org.imaginativeworld.sampleapp

import am.appwise.components.ni.NoInternetDialog
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private var noInternetDialog: NoInternetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        noInternetDialog = NoInternetDialog.Builder(this@MainActivity).build()
    }

    override fun onPause() {
        super.onPause()

        noInternetDialog?.onDestroy()

    }
}
