package com.cem.admodule.manager

import android.content.Context
import android.content.res.TypedArray
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.cem.admodule.R
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd

open class CustomNativeView : FrameLayout {
    private var templateType = 0
    var primaryView: TextView? = null
    var secondaryView: TextView? = null
    var ratingBar: RatingBar? = null
    var tertiaryView: TextView? = null
    var iconView: ImageView? = null
    var callToActionView: TextView? = null
    var mediaView: MediaView? = null
    var background: View? = null

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ){
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        val attributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.CustomNativeView, 0, 0)
        templateType = getTemplateType(attributes)
        attributes.recycle()
        initLayout(context, templateType)
        initIds()
    }

    open fun initLayout(context: Context, layoutRes: Int = R.layout.admob_native_ad_view) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(layoutRes, this)
    }

    private fun getTemplateType(attributes: TypedArray): Int {
        return attributes.getResourceId(
            R.styleable.CustomNativeView_ad_view_layout, R.layout.admob_native_ad_view
        )
    }

    private fun initIds() {
        primaryView = findViewById<TextView>(R.id.primary)
        secondaryView = findViewById<TextView>(R.id.secondary)
        tertiaryView = findViewById<TextView>(R.id.body)
        ratingBar = findViewById<RatingBar>(R.id.rating_bar)
        if (ratingBar != null) ratingBar!!.isEnabled = false
        callToActionView = findViewById<TextView>(R.id.cta)
        iconView = findViewById<ImageView>(R.id.icon)
        background = findViewById<View>(R.id.background)
        mediaView = findViewById<MediaView>(R.id.media_view)
    }

    fun adHasOnlyStore(nativeAd: NativeAd?): Boolean {
        val store = nativeAd?.store
        val advertiser = nativeAd?.advertiser
        return !TextUtils.isEmpty(store) && TextUtils.isEmpty(advertiser)
    }

    fun setTemplateType(int: Int) {
        templateType = int
        invalidate()
    }
}