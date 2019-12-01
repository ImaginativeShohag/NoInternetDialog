package am.appwise.components.ni

/**
 * Created by robertlevonyan on 11/21/17.
 */
internal interface ConnectionListener {
    open fun onWifiTurnedOn()
    open fun onWifiTurnedOff()
}