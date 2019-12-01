package am.appwise.components.ni

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.provider.Settings
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by robertlevonyan on 11/20/17.
 */
object NoInternetUtils {
    fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null
    }

    fun hasActiveInternetConnection(context: Context): Boolean {
        try {
            val urlc = URL("https://www.google.com").openConnection() as HttpURLConnection
            urlc.setRequestProperty("User-Agent", "Test")
            urlc.setRequestProperty("Connection", "close")
            urlc.connectTimeout = 1500
            urlc.connect()
            return urlc.responseCode == 200
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    fun isConnectedToWifi(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return wifiInfo.isConnected
    }

    fun isConnectedToMobileNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return mobileInfo.isConnected
    }

    fun turnOn3g(context: Context) {
        context.startActivity(Intent(Settings.ACTION_SETTINGS))
    }

    fun turnOnWifi(context: Context) {
        val wifiManager = context.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager == null || isConnectedToWifi(context)) {
            context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            return
        }
        wifiManager.isWifiEnabled = true
    }

    fun isAirplaneModeOn(context: Context): Boolean {
        return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0
    }

    fun turnOffAirplaneMode(context: Context) {
        context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
    }

    fun getCurrentDate(): String? {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("MM-dd", Locale.getDefault())
        return df.format(c.time)
    }
}