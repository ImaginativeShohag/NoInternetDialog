package am.appwise.components.ni

import android.content.Context
import android.os.AsyncTask

/**
 * Created by robertlevonyan on 11/21/17.
 */
internal class Ping : AsyncTask<Context, Void, Boolean>() {
    private var connectionCallback: ConnectionCallback? = null
    override fun doInBackground(vararg ctxs: Context): Boolean {
        return NoInternetUtils.isConnectedToInternet(ctxs[0]) && NoInternetUtils.hasActiveInternetConnection(ctxs[0])
    }

    override fun onPostExecute(aBoolean: Boolean) {
        super.onPostExecute(aBoolean)
//        if (connectionCallback != null) {
        connectionCallback?.hasActiveConnection(aBoolean)
//        }
    }

    fun setConnectionCallback(connectionCallback: ConnectionCallback) {
        this.connectionCallback = connectionCallback
    }
}