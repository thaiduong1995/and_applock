package com.example.myapplication.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Outline
import android.os.Build
import android.text.Html
import android.text.InputType
import android.text.Spanned
import android.util.Base64
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream

fun Activity.hideKeyboard(view: View) {
    val imm: InputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showSoftKeyboard(view: View) {
    if (view.requestFocus()) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun View.layoutInflater(): LayoutInflater = LayoutInflater.from(this.context)

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun TextView.string() = text.toString()

fun EditText.setMultiLineCapSentencesAndDoneAction() {
    imeOptions = EditorInfo.IME_ACTION_DONE
    setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
}

fun TextView.setMultiLineCapSentences() {
    setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE)
}

fun Int.percent(percent: Int): Int {
    return (this * percent) / 100
}

fun ViewGroup.inflate(@LayoutRes view: Int): View {
    return LayoutInflater.from(this.context).inflate(view, this, false)
}

fun View.inflate(@LayoutRes view: Int): View {
    return LayoutInflater.from(this.context).inflate(view, null, false)

}

fun Context.getLinearVerticalLayoutManager(
    reverseLayout: Boolean = false
): LinearLayoutManager {
    return LinearLayoutManager(this, LinearLayoutManager.VERTICAL, reverseLayout)
}

fun Context.getLinearHorizontalLayoutManager(
    reverseLayout: Boolean = false
): LinearLayoutManager {
    return LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, reverseLayout)
}

fun Context.getGirdLayoutManager(
    spanCount: Int = 3
): GridLayoutManager {
    return GridLayoutManager(this, spanCount)
}

fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
    val divider = DividerItemDecoration(
        this.context,
        DividerItemDecoration.VERTICAL
    )
    val drawable = ContextCompat.getDrawable(
        this.context,
        drawableRes
    )
    drawable?.let {
        divider.setDrawable(it)
        addItemDecoration(divider)
    }
}

fun Context.getDisplayMetrics(): DisplayMetrics {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return DisplayMetrics().apply {
        windowManager.defaultDisplay.getMetrics(this)
    }
}

fun Context.getDisplayWidth() = getDisplayMetrics().widthPixels

fun Context.getDisplayHeight() = getDisplayMetrics().heightPixels

fun View.setScaleWithDisplay(percent: Int) {
    val scaleWith = context.getDisplayWidth().percent(percent)
    layoutParams.width = scaleWith
}

fun View.setScaleHeightDisplay(percent: Int) {
    val scaledHeight = context.getDisplayHeight().percent(percent)
    layoutParams.height = scaledHeight
}

fun View.setScaleDisplay(percent: Int) {
    setScaleHeightDisplay(percent)
    setScaleWithDisplay(percent)
}

fun View.setSingleClick(action: (View) -> Unit) {
    setOnClickListener(Extensions.DebouncedOnClickListener(debounceDuration = 500) {
        action(it)
    })
}


fun Context.mGridLayoutManager(index: Int): GridLayoutManager {
    return GridLayoutManager(this, index)
}

fun RecyclerView.onLoadMore(callback: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val totalCountItem = (recyclerView.layoutManager as LinearLayoutManager).itemCount
            val visibleItemCount = (recyclerView.layoutManager as LinearLayoutManager).childCount
            val firstVisibleItem =
                (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if (dy > 0 && totalCountItem - visibleItemCount <= firstVisibleItem) {
                callback.invoke()
            }
        }
    })
}

fun bitmapToString(bitmap: Bitmap): String {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
    val b: ByteArray = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

fun stringToBitMap(encodedString: String?): Bitmap? {
    return try {
        val encodeByte: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
    } catch (e: java.lang.Exception) {
        e.message
        null
    }
}

fun View.setRadiusPx(radius: Int) {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            if (view != null) {
                outline?.setRoundRect(0, 0, view.width, view.height, radius.toFloat())
            }
        }
    }
    clipToOutline = true
}

fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) =
    this?.let { Toast.makeText(it, text, duration).show() }

fun fromHtml(source: String): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(source)
    }
}
//
////@JvmStatic
//fun adjustViewPager(context: Context, viewPager2: ViewPager2) {
//    val nextItemVisiblePx = context.resources.getDimension(R.dimen.viewpager_next_item_visible)
//    val currentItemHorizontalMarginPx =
//        context.resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
//    val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
//    val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
//        page.translationX = -pageTranslationX * position
//        // Next line scales the item's height. You can remove it if you don't want this effect
//        page.scaleY = 1 - (0.25f * kotlin.math.abs(position))
//        // If you want a fading effect uncomment the next line:
//        // page.alpha = 0.25f + (1 - abs(position))
//    }
//    viewPager2.setPageTransformer(pageTransformer)
//    val itemDecoration = HorizontalMarginItemDecoration(
//        context,
//        R.dimen.viewpager_current_item_horizontal_margin
//    )
//    viewPager2.addItemDecoration(itemDecoration)
//}
//<dimen name="viewpager_next_item_visible">26dp</dimen>
//<dimen name="viewpager_current_item_horizontal_margin">42dp</dimen>





