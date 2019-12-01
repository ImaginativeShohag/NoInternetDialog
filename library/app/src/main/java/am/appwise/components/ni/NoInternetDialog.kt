package am.appwise.components.ni

import android.animation.*
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

/**
 * Created by robertlevonyan on 11/20/17.
 */
class NoInternetDialog private constructor(activity: Activity, bgGradientStart: Int, bgGradientCenter: Int, bgGradientEnd: Int,
                                           bgGradientOrientation: Int, bgGradientType: Int, dialogRadius: Float,
                                           titleTypeface: Typeface?, messageTypeface: Typeface?,
                                           buttonColor: Int, buttonTextColor: Int, buttonIconsColor: Int, wifiLoaderColor: Int,
                                           cancelable: Boolean) : Dialog(activity), View.OnClickListener, ConnectionListener, ConnectionCallback {
    @Retention(RetentionPolicy.RUNTIME)
    @IntDef(ORIENTATION_TOP_BOTTOM, ORIENTATION_BOTTOM_TOP, ORIENTATION_RIGHT_LEFT, ORIENTATION_LEFT_RIGHT, ORIENTATION_BL_TR, ORIENTATION_TR_BL, ORIENTATION_BR_TL, ORIENTATION_TL_BR)
    internal annotation class Orientation

    private var topGuide: Guideline? = null
    private var root: FrameLayout? = null
    private var close: AppCompatImageView? = null
    private var plane: AppCompatImageView? = null
    private var moon: AppCompatImageView? = null
    private var ghost: AppCompatImageView? = null
    private var tomb: AppCompatImageView? = null
    private var ground: AppCompatImageView? = null
    private var pumpkin: AppCompatImageView? = null
    private var wifiIndicator: AppCompatImageView? = null
    private var noInternet: AppCompatTextView? = null
    private var noInternetBody: AppCompatTextView? = null
    private var turnOn: AppCompatTextView? = null
    private var wifiOn: AppCompatButton? = null
    private var mobileOn: AppCompatButton? = null
    private var airplaneOff: AppCompatButton? = null
    private var wifiLoading: ProgressBar? = null
    private var bgGradientStart: Int
    private var bgGradientCenter: Int
    private var bgGradientEnd: Int
    private var bgGradientOrientation: Int
    private var bgGradientType: Int
    private var dialogRadius: Float
    private var titleTypeface: Typeface?
    private var messageTypeface: Typeface?
    private var buttonColor: Int
    private var buttonTextColor: Int
    private var buttonIconsColor: Int
    private var wifiLoaderColor: Int
    private var _cancelable: Boolean = false
    private var isHalloween: Boolean = false
    private var isWifiOn = false
    private var direction = 0
    private var wifiReceiver: WifiReceiver? = null
    private var networkStatusReceiver: NetworkStatusReceiver? = null
    private var wifiAnimator: ObjectAnimator? = null
    private var connectionCallback: ConnectionCallback? = null
    private val activity: Activity?
    private fun initReceivers(context: Context?) {
        wifiReceiver = WifiReceiver()
        context?.registerReceiver(wifiReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        networkStatusReceiver = NetworkStatusReceiver()
        context?.registerReceiver(networkStatusReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        wifiReceiver?.setConnectionListener(this)
        networkStatusReceiver?.setConnectionCallback(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_no_internet)
        initMainWindow()
        initView()
        initGuideLine()
        initBackground()
        initButtonStyle()
        initListeners()
        initHalloweenTheme()
        initAnimations()
        initTypefaces()
        initWifiLoading()
        initClose()
    }

    private fun initMainWindow() {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initView() {
        root = findViewById<FrameLayout?>(R.id.root)
        close = findViewById(R.id.close)
        moon = findViewById(R.id.moon)
        plane = findViewById(R.id.plane)
        ghost = findViewById(R.id.ghost)
        tomb = findViewById(R.id.tomb)
        ground = findViewById(R.id.ground)
        pumpkin = findViewById(R.id.pumpkin)
        wifiIndicator = findViewById(R.id.wifi_indicator)
        noInternet = findViewById(R.id.no_internet)
        noInternetBody = findViewById(R.id.no_internet_body)
        turnOn = findViewById(R.id.turn_on)
        wifiOn = findViewById(R.id.wifi_on)
        mobileOn = findViewById(R.id.mobile_on)
        airplaneOff = findViewById(R.id.airplane_off)
        wifiLoading = findViewById<ProgressBar?>(R.id.wifi_loading)
        topGuide = findViewById(R.id.top_guide)
    }

    private fun initGuideLine() {
        val lp = topGuide?.getLayoutParams() as ConstraintLayout.LayoutParams
        lp.guidePercent = if (isHalloween) 0.34f else 0.3f
        topGuide?.setLayoutParams(lp)
    }

    private fun initBackground() {
        val orientation = getOrientation()
        val drawable = GradientDrawable(orientation, intArrayOf(bgGradientStart, bgGradientCenter, bgGradientEnd))
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.cornerRadius = dialogRadius
        when (bgGradientType) {
            GRADIENT_RADIAL -> drawable.gradientType = GradientDrawable.RADIAL_GRADIENT
            GRADIENT_SWEEP -> drawable.gradientType = GradientDrawable.SWEEP_GRADIENT
            else -> drawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        }
        if (isHalloween) {
            drawable.gradientType = GradientDrawable.RADIAL_GRADIENT
            drawable.gradientRadius = context.resources.getDimensionPixelSize(R.dimen.dialog_height) / 2.toFloat()
        } else {
            drawable.gradientType = GradientDrawable.LINEAR_GRADIENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            root?.setBackground(drawable)
        } else {
            root?.setBackgroundDrawable(drawable)
        }
    }

    private fun initButtonStyle() {
        wifiOn.getBackground().mutate().setColorFilter(buttonColor, PorterDuff.Mode.SRC_IN)
        mobileOn.getBackground().mutate().setColorFilter(buttonColor, PorterDuff.Mode.SRC_IN)
        airplaneOff.getBackground().mutate().setColorFilter(buttonColor, PorterDuff.Mode.SRC_IN)
        wifiOn.setTextColor(buttonTextColor)
        mobileOn.setTextColor(buttonTextColor)
        airplaneOff.setTextColor(buttonTextColor)
        val wifi = ContextCompat.getDrawable(context, R.drawable.ic_wifi_white)
        val mobileData = ContextCompat.getDrawable(context, R.drawable.ic_4g_white)
        val airplane = ContextCompat.getDrawable(context, R.drawable.ic_airplane_off)
        wifi.mutate().setColorFilter(buttonIconsColor, PorterDuff.Mode.SRC_ATOP)
        mobileData.mutate().setColorFilter(buttonIconsColor, PorterDuff.Mode.SRC_ATOP)
        airplane.mutate().setColorFilter(buttonIconsColor, PorterDuff.Mode.SRC_ATOP)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wifiOn.setCompoundDrawablesRelativeWithIntrinsicBounds(wifi, null, null, null)
            mobileOn.setCompoundDrawablesRelativeWithIntrinsicBounds(mobileData, null, null, null)
            airplaneOff.setCompoundDrawablesRelativeWithIntrinsicBounds(airplane, null, null, null)
        } else {
            wifiOn.setCompoundDrawablesWithIntrinsicBounds(wifi, null, null, null)
            mobileOn.setCompoundDrawablesWithIntrinsicBounds(mobileData, null, null, null)
            airplaneOff.setCompoundDrawablesWithIntrinsicBounds(airplane, null, null, null)
        }
    }

    private fun getOrientation(): GradientDrawable.Orientation? {
        val orientation: GradientDrawable.Orientation
        orientation = when (bgGradientOrientation) {
            ORIENTATION_BOTTOM_TOP -> GradientDrawable.Orientation.BOTTOM_TOP
            ORIENTATION_RIGHT_LEFT -> GradientDrawable.Orientation.RIGHT_LEFT
            ORIENTATION_LEFT_RIGHT -> GradientDrawable.Orientation.LEFT_RIGHT
            ORIENTATION_BL_TR -> GradientDrawable.Orientation.BL_TR
            ORIENTATION_TR_BL -> GradientDrawable.Orientation.TR_BL
            ORIENTATION_BR_TL -> GradientDrawable.Orientation.BR_TL
            ORIENTATION_TL_BR -> GradientDrawable.Orientation.TL_BR
            else -> GradientDrawable.Orientation.TOP_BOTTOM
        }
        return orientation
    }

    private fun initListeners() {
        close.setOnClickListener(this)
        wifiOn.setOnClickListener(this)
        mobileOn.setOnClickListener(this)
        airplaneOff.setOnClickListener(this)
    }

    private fun initHalloweenTheme() {
        if (!isHalloween) {
            return
        }
        val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.ground)
        val dr = RoundedBitmapDrawableFactory.create(context.resources, bmp)
        dr.cornerRadius = dialogRadius
        dr.setAntiAlias(true)
        ground.setBackgroundDrawable(dr)
        plane.setImageResource(R.drawable.witch)
        tomb.setImageResource(R.drawable.tomb_hw)
        moon.setVisibility(View.VISIBLE)
        ground.setVisibility(View.VISIBLE)
        pumpkin.setVisibility(View.VISIBLE)
        wifiOn.getBackground().mutate().setColorFilter(ContextCompat.getColor(context, R.color.colorNoInternetGradCenterH), PorterDuff.Mode.SRC_IN)
        mobileOn.getBackground().mutate().setColorFilter(ContextCompat.getColor(context, R.color.colorNoInternetGradCenterH), PorterDuff.Mode.SRC_IN)
        airplaneOff.getBackground().mutate().setColorFilter(ContextCompat.getColor(context, R.color.colorNoInternetGradCenterH), PorterDuff.Mode.SRC_IN)
    }

    private fun initAnimations() {
        direction = animateDirection()
        val ghostXAnimator = ObjectAnimator.ofFloat(ghost, "translationX", 1f, GHOST_X_ANIMATION_VALUE * direction)
        val ghostYAnimator = ObjectAnimator.ofFloat(ghost, "translationY", 1f, GHOST_Y_ANIMATION_VALUE)
        val scaleXGhostAnimator = ObjectAnimator.ofFloat(ghost, "scaleX", 1f, GHOST_SCALE_ANIMATION_VALUE)
        val scaleYGhostAnimator = ObjectAnimator.ofFloat(ghost, "scaleY", 1f, GHOST_SCALE_ANIMATION_VALUE)
        val ghostXAnimatorReverse = ObjectAnimator.ofFloat(ghost, "translationX", GHOST_X_ANIMATION_VALUE * direction, 1f)
        val ghostYAnimatorReverse = ObjectAnimator.ofFloat(ghost, "translationY", GHOST_Y_ANIMATION_VALUE, 1f)
        val scaleXGhostAnimatorReverse = ObjectAnimator.ofFloat(ghost, "scaleX", GHOST_SCALE_ANIMATION_VALUE, 1f)
        val scaleYGhostAnimatorReverse = ObjectAnimator.ofFloat(ghost, "scaleY", GHOST_SCALE_ANIMATION_VALUE, 1f)
        val ghostSet = AnimatorSet()
        ghostSet.playTogether(ghostXAnimator, ghostYAnimator, scaleXGhostAnimator, scaleYGhostAnimator)
        ghostSet.duration = ANIMATION_DURATION
        ghostSet.startDelay = ANIMATION_DELAY
        ghostSet.interpolator = DecelerateInterpolator()
        val ghostSetReverse = AnimatorSet()
        ghostSetReverse.playTogether(ghostXAnimatorReverse, ghostYAnimatorReverse, scaleXGhostAnimatorReverse, scaleYGhostAnimatorReverse)
        ghostSetReverse.duration = ANIMATION_DURATION
        ghostSetReverse.startDelay = ANIMATION_DELAY
        ghostSetReverse.interpolator = DecelerateInterpolator()
        ghostSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                ghostXAnimatorReverse.setFloatValues(GHOST_X_ANIMATION_VALUE * direction, 1f)
                ghostSetReverse.start()
            }
        })
        ghostSetReverse.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                direction = animateDirection()
                ghostXAnimator.setFloatValues(1f, GHOST_X_ANIMATION_VALUE * direction)
                ghostSet.start()
            }
        })
        ghostSet.start()
        startFlight()
    }

    private fun startFlight() {
        if (!NoInternetUtils.isAirplaneModeOn(context)) {
            plane.setVisibility(View.GONE)
            return
        }
        plane.setVisibility(View.VISIBLE)
        noInternetBody.setText(R.string.airplane_on)
        turnOn.setText(R.string.turn_off)
        airplaneOff.setVisibility(View.VISIBLE)
        wifiOn.setVisibility(View.INVISIBLE)
        mobileOn.setVisibility(View.INVISIBLE)
        val flightThere = ObjectAnimator.ofFloat(plane, "translationX", FLIGHT_THERE_START, FLIGHT_THERE_END)
        val flightBack = ObjectAnimator.ofFloat(plane, "translationX", FLIGHT_BACK_START, FLIGHT_BACK_END)
        flightThere.duration = FLIGHT_DURATION
        flightBack.duration = FLIGHT_DURATION
        flightThere.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                plane.setRotationY(180f)
                flightBack.start()
            }
        })
        flightBack.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                plane.setRotationY(0f)
                flightThere.start()
            }
        })
        flightThere.start()
    }

    private fun initTypefaces() {
        if (titleTypeface != null) {
            noInternet.setTypeface(titleTypeface)
        }
        if (messageTypeface != null) {
            noInternetBody.setTypeface(messageTypeface)
        }
    }

    private fun initWifiLoading() {
        wifiLoading.getIndeterminateDrawable().setColorFilter(wifiLoaderColor, PorterDuff.Mode.SRC_IN)
        ViewCompat.setElevation(wifiLoading, 10f)
    }

    private fun initClose() {
        setCancelable(_cancelable)
        close.setVisibility(if (_cancelable) View.VISIBLE else View.GONE)
    }

    override fun onClick(view: View?) {
        val id = view.getId()
        if (id == R.id.close) {
            dismiss()
        } else if (id == R.id.wifi_on) {
            turnOnWifiWithAnimation()
        } else if (id == R.id.mobile_on) {
            NoInternetUtils.turnOn3g(context)
            dismiss()
        } else if (id == R.id.airplane_off) {
            NoInternetUtils.turnOffAirplaneMode(context)
            dismiss()
        }
    }

    private fun turnOnWifiWithAnimation() {
        val widthAnimator = ValueAnimator.ofFloat(
                context.resources.getDimensionPixelSize(R.dimen.button_width).toFloat(),
                context.resources.getDimensionPixelSize(R.dimen.button_width) + 10.toFloat(),
                context.resources.getDimensionPixelSize(R.dimen.button_size2).toFloat())
        widthAnimator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            val lpWifi = wifiOn.getLayoutParams() as RelativeLayout.LayoutParams
            lpWifi.width = value as Int
            wifiOn.setLayoutParams(lpWifi)
        }
        val translateXAnimatorWifi = ObjectAnimator.ofFloat(wifiOn, "translationX", 1f, 110f)
        val translateYAnimatorWifi = ObjectAnimator.ofFloat(wifiOn, "translationY", 1f, 0f)
        val translateXAnimatorLoading = ObjectAnimator.ofFloat(wifiLoading, "translationX", 1f, 104f)
        val translateYAnimatorLoading = ObjectAnimator.ofFloat(wifiLoading, "translationY", 1f, -10f)
        val textSizeAnimator = ValueAnimator.ofFloat(13f, 0f)
        textSizeAnimator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            wifiOn.setTextSize(value)
        }
        translateXAnimatorWifi.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                wifiLoading.setVisibility(View.VISIBLE)
            }
        })
        val set = AnimatorSet()
        set.playTogether(widthAnimator, translateXAnimatorWifi, translateYAnimatorWifi, translateXAnimatorLoading, translateYAnimatorLoading, textSizeAnimator)
        set.duration = 400
        set.start()
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                NoInternetUtils.turnOnWifi(context)
                animateWifi()
            }
        })
    }

    private fun animateWifi() {
        wifiAnimator = ObjectAnimator.ofFloat(wifiIndicator, "alpha", 0f, 0.5f)
        wifiAnimator.setDuration(1500)
        wifiAnimator.setRepeatMode(ValueAnimator.RESTART)
        wifiAnimator.setRepeatCount(ValueAnimator.INFINITE)
        wifiAnimator.start()
    }

    private fun animateDirection(): Int {
        val r = Random()
        return r.nextInt(2)
    }

    override fun onWifiTurnedOn() {
        if (wifiAnimator != null && wifiAnimator.isStarted()) {
            wifiAnimator.cancel()
            wifiIndicator.setImageResource(R.drawable.ic_wifi)
            wifiIndicator.setAlpha(0.5f)
            isWifiOn = true
            context.unregisterReceiver(wifiReceiver)
        }
    }

    override fun onWifiTurnedOff() {}
    override fun hasActiveConnection(hasActiveConnection: Boolean) {
        if (connectionCallback != null) connectionCallback.hasActiveConnection(hasActiveConnection)
        if (!hasActiveConnection) {
            showDialog()
        } else {
            dismiss()
        }
    }

    override fun show() {
        if (!activity.isFinishing()) {
            super.show()
            startFlight()
        }
    }

    fun showDialog() {
        val ping = Ping()
        ping.setConnectionCallback { hasActiveConnection ->
            if (!hasActiveConnection) {
                show()
            }
        }
        ping.execute(context)
    }

    override fun dismiss() {
        reset()
        super.dismiss()
    }

    private fun reset() {
        if (airplaneOff != null) {
            airplaneOff.setVisibility(View.GONE)
        }
        if (wifiOn != null) {
            wifiOn.setVisibility(View.VISIBLE)
            val wifiParams = wifiOn.getLayoutParams() as RelativeLayout.LayoutParams
            wifiParams.width = context.resources.getDimensionPixelSize(R.dimen.button_width)
            wifiOn.setLayoutParams(wifiParams)
            wifiOn.setTextSize(13f)
            wifiOn.setTranslationX(1f)
            wifiOn.setTranslationY(1f)
        }
        if (mobileOn != null) {
            mobileOn.setVisibility(View.VISIBLE)
        }
        if (turnOn != null) {
            turnOn.setText(R.string.turn_on)
        }
        if (wifiLoading != null) {
            wifiLoading.setTranslationX(1f)
            wifiLoading.setVisibility(View.INVISIBLE)
        }
        if (ghost != null) {
            ghost.setTranslationY(1f)
        }
    }

    fun onDestroy() {
        try {
            context.unregisterReceiver(networkStatusReceiver)
        } catch (e: Exception) {
        }
        try {
            context.unregisterReceiver(wifiReceiver)
        } catch (e: Exception) {
        }
    }

    fun setConnectionCallback(connectionCallback: ConnectionCallback?) {
        this.connectionCallback = connectionCallback
    }

    class Builder {
        private var activity: Activity?
        private var bgGradientStart = 0
        private var bgGradientCenter = 0
        private var bgGradientEnd = 0
        private var bgGradientOrientation = 0
        private var bgGradientType = 0
        private var dialogRadius = 0f
        private var titleTypeface: Typeface? = null
        private var messageTypeface: Typeface? = null
        private var buttonColor = 0
        private var buttonTextColor = 0
        private var buttonIconsColor = 0
        private var wifiLoaderColor = 0
        private var connectionCallback: ConnectionCallback? = null
        private var cancelable = false

        constructor(activity: Activity?) {
            this.activity = activity
        }

        constructor(fragment: Fragment?) {
            activity = fragment.getActivity()
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        constructor(fragment: android.app.Fragment?) {
            activity = fragment.getActivity()
        }

        fun setBgGradientStart(@ColorInt bgGradientStart: Int): Builder? {
            this.bgGradientStart = bgGradientStart
            return this
        }

        fun setBgGradientCenter(@ColorInt bgGradientCenter: Int): Builder? {
            this.bgGradientCenter = bgGradientCenter
            return this
        }

        fun setBgGradientEnd(@ColorInt bgGradientEnd: Int): Builder? {
            this.bgGradientEnd = bgGradientEnd
            return this
        }

        fun setBgGradientOrientation(@Orientation bgGradientOrientation: Int): Builder? {
            this.bgGradientOrientation = bgGradientOrientation
            return this
        }

        fun setBgGradientType(bgGradientType: Int): Builder? {
            this.bgGradientType = bgGradientType
            return this
        }

        fun setDialogRadius(dialogRadius: Float): Builder? {
            this.dialogRadius = dialogRadius
            return this
        }

        fun setDialogRadius(@DimenRes dialogRadiusDimen: Int): Builder? {
            dialogRadius = activity.getResources().getDimensionPixelSize(dialogRadiusDimen).toFloat()
            return this
        }

        fun setTitleTypeface(titleTypeface: Typeface?): Builder? {
            this.titleTypeface = titleTypeface
            return this
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        fun setTitleTypeface(titleTypefaceId: Int): Builder? {
            titleTypeface = activity.getResources().getFont(titleTypefaceId)
            return this
        }

        fun setMessageTypeface(messageTypeface: Typeface?): Builder? {
            this.messageTypeface = messageTypeface
            return this
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        fun setMessageTypeface(messageTypefaceId: Int): Builder? {
            messageTypeface = activity.getResources().getFont(messageTypefaceId)
            return this
        }

        fun setButtonColor(buttonColor: Int): Builder? {
            this.buttonColor = buttonColor
            return this
        }

        fun setButtonTextColor(buttonTextColor: Int): Builder? {
            this.buttonTextColor = buttonTextColor
            return this
        }

        fun setButtonIconsColor(buttonIconsColor: Int): Builder? {
            this.buttonIconsColor = buttonIconsColor
            return this
        }

        fun setWifiLoaderColor(wifiLoaderColor: Int): Builder? {
            this.wifiLoaderColor = wifiLoaderColor
            return this
        }

        fun setConnectionCallback(callback: ConnectionCallback?): Builder? {
            connectionCallback = callback
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder? {
            this.cancelable = cancelable
            return this
        }

        fun build(): NoInternetDialog? {
            val dialog = NoInternetDialog(activity, bgGradientStart, bgGradientCenter, bgGradientEnd,
                    bgGradientOrientation, bgGradientType, dialogRadius, titleTypeface, messageTypeface,
                    buttonColor, buttonTextColor, buttonIconsColor, wifiLoaderColor, cancelable)
            dialog.setConnectionCallback(connectionCallback)
            return dialog
        }
    }

    companion object {
        const val GRADIENT_LINEAR = 0
        const val GRADIENT_RADIAL = 1
        const val GRADIENT_SWEEP = 2
        const val NO_RADIUS = -1f
        private const val RADIUS = 12f
        private const val GHOST_X_ANIMATION_VALUE = 320f
        private const val GHOST_Y_ANIMATION_VALUE = -100f
        private const val GHOST_SCALE_ANIMATION_VALUE = 1.3f
        private const val ANIMATION_DURATION: Long = 1500
        private const val ANIMATION_DELAY: Long = 800
        private const val FLIGHT_THERE_START = -200f
        private const val FLIGHT_THERE_END = 1300f
        private const val FLIGHT_BACK_START = 1000f
        private const val FLIGHT_BACK_END = -400f
        private const val FLIGHT_DURATION: Long = 2500
        const val ORIENTATION_TOP_BOTTOM = 10
        const val ORIENTATION_BOTTOM_TOP = 11
        const val ORIENTATION_RIGHT_LEFT = 12
        const val ORIENTATION_LEFT_RIGHT = 13
        const val ORIENTATION_BL_TR = 14
        const val ORIENTATION_TR_BL = 15
        const val ORIENTATION_BR_TL = 16
        const val ORIENTATION_TL_BR = 17
    }

    init {

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        this.activity = activity

        isHalloween = NoInternetUtils.getCurrentDate() == "10-31"

        this.bgGradientStart = if (bgGradientStart == 0) if (isHalloween) ContextCompat.getColor(context, R.color.colorNoInternetGradStartH) else ContextCompat.getColor(context, R.color.colorNoInternetGradStart) else bgGradientStart
        this.bgGradientCenter = if (bgGradientCenter == 0) if (isHalloween) ContextCompat.getColor(context, R.color.colorNoInternetGradCenterH) else ContextCompat.getColor(context, R.color.colorNoInternetGradCenter) else bgGradientCenter
        this.bgGradientEnd = if (bgGradientEnd == 0) if (isHalloween) ContextCompat.getColor(context, R.color.colorNoInternetGradEndH) else ContextCompat.getColor(context, R.color.colorNoInternetGradEnd) else bgGradientEnd
        this.bgGradientOrientation = if (bgGradientOrientation < 10 || bgGradientOrientation > 17) ORIENTATION_TOP_BOTTOM else bgGradientOrientation
        this.bgGradientType = if (bgGradientType <= 0 || bgGradientType > 2) GRADIENT_LINEAR else bgGradientType
        this.dialogRadius = if (dialogRadius == 0f) RADIUS else dialogRadius

        if (dialogRadius == NO_RADIUS) {
            this.dialogRadius = 0f
        }

        this.titleTypeface = titleTypeface
        this.messageTypeface = messageTypeface
        this.buttonColor = if (buttonColor == 0) if (isHalloween) ContextCompat.getColor(context, R.color.colorNoInternetGradCenterH) else ContextCompat.getColor(context, R.color.colorAccent) else buttonColor
        this.buttonTextColor = if (buttonTextColor == 0) ContextCompat.getColor(context, R.color.colorWhite) else buttonTextColor
        this.buttonIconsColor = if (buttonIconsColor == 0) ContextCompat.getColor(context, R.color.colorWhite) else buttonIconsColor
        this.wifiLoaderColor = if (wifiLoaderColor == 0) ContextCompat.getColor(context, R.color.colorWhite) else wifiLoaderColor
        this._cancelable = cancelable

        initReceivers(activity)
    }
}