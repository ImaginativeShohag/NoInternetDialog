package am.appwise.components.ni

/**
 * Created by robertlevonyan on 11/21/17.
 */
interface ConnectionCallback {
    open fun hasActiveConnection(hasActiveConnection: Boolean)
}