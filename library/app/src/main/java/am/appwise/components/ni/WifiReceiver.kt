package am.appwise.components.ni

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

/**
 * Created by robertlevonyan on 11/21/17.
 */
internal class WifiReceiver : BroadcastReceiver() {
    private var connectionListener: ConnectionListener? = null
    override fun onReceive(context: Context, intent: Intent) {
        val conMan = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMan.activeNetworkInfo
        if (netInfo != null && netInfo.type == ConnectivityManager.TYPE_WIFI) {
//            if (connectionListener != null) {
            connectionListener?.onWifiTurnedOn()
//            }
        } else {
//            if (connectionListener != null) {
            connectionListener?.onWifiTurnedOff()
//            }
        }
    }

    fun setConnectionListener(connectionListener: ConnectionListener?) {
        this.connectionListener = connectionListener
    }
}