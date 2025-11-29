package  com.example.myapplication.extention

import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.os.Build
import android.os.SystemClock
import android.text.Html
import android.view.View
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun EditText.showKeyBoard(context: Context) {
    val imm: InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun EditText.hideKeyBoard(context: Context) {
    val imm: InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInputFromWindow(rootView.windowToken, 0, 0)

}

fun View.setOnSingleClickListener(listener: (View) -> Unit) {
    setOnClickListener(object : OnSingleClickListener() {
        override fun onSingleClick(v: View) {
            listener(v)
        }
    })
}

fun TextView.setTextHtml(content: String) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(content)
    }
}

fun View.setShakeAnimation(listener: AnimatorListenerAdapter?) {
    val animator = ObjectAnimator
        .ofFloat(this, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        .setDuration(500)
    animator.addListener(listener)
    animator.start()
}

abstract class OnSingleClickListener : View.OnClickListener {
    companion object {
        private const val MIN_CLICK_INTERVAL: Long = 500
    }

    private var mLastClickTime: Long = 0
    abstract fun onSingleClick(v: View)
    override fun onClick(v: View) {
        val currentClickTime: Long = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime
        mLastClickTime = currentClickTime

        if (elapsedTime <= MIN_CLICK_INTERVAL) return
        onSingleClick(v)
    }
}

fun View.loadBitmap(): Bitmap {
    val b = Bitmap.createBitmap(
        measuredWidth,
        measuredHeight,
        Bitmap.Config.ARGB_8888
    )
    val c = Canvas(b)
    layout(0, 0, measuredWidth, measuredHeight)
    draw(c)
    return b
}

fun View.setRadius(radius: Int) {
    val mRadius = context.dp2px(radius.toFloat())
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(0, 0, view!!.width, view.height, mRadius.toFloat())
        }
    }
    clipToOutline = true
}

fun ViewPager2.addOnPageChangeCallback(onPageSelected: (Int) -> Unit) {
    this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            onPageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {}
    })
}

fun Animation.setAnimationListener(onFinished: () -> Unit) {
    this.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {
        }

        override fun onAnimationEnd(p0: Animation?) {
            onFinished.invoke()
        }

        override fun onAnimationRepeat(p0: Animation?) {
        }

    })
}

fun View.addOnGlobalLayoutCallback(onGlobalLayout: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object :
        ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            onGlobalLayout.invoke()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}

fun SeekBar.setOnSeekBarChangeCallback(onSeekBarChangeListener: (SeekBar, Int, Boolean) -> Unit) {
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onSeekBarChangeListener.invoke(seekBar!!, progress, fromUser)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    })
}

fun RecyclerView.setupTouchHelper(adapter: RecyclerView.Adapter<*>) {
    val simpleItemTouchCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
//            if (direction == ItemTouchHelper.LEFT) {
//                adapter.notifyItemChanged(position)
//            } else if (direction == ItemTouchHelper.RIGHT) {
//                adapter.notifyItemChanged(position)
//            }
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3
                    val paint = Paint()
                    if (dX < 0) {
                        val icon = BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.ic_delete_circle_red
                        )
                        val margin = (dX / 5 - width) / 2
                        val iconDest = RectF(
                            itemView.right.toFloat() + margin,
                            itemView.top.toFloat() + width,
                            itemView.right.toFloat() + (margin + width),
                            itemView.bottom.toFloat() - width
                        )
                        canvas.drawBitmap(icon, null, iconDest, paint)
                    }
                } else {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                }
                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX / 5,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
    ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(this)
}

fun ConstraintLayout.selected(boolean: Boolean) {
    if (!boolean) {
        setBackgroundResource(R.drawable.bg_stroke_1_color_gray_radius_100)
    } else {
        setBackgroundResource(R.drawable.bg_stroke_1_color_blue_radius_100)
    }
}

fun View.enable() {
    isClickable = true
    background = ContextCompat.getDrawable(context, R.drawable.bg_button_gradient)
}

fun View.disable() {
    isClickable = false
    background = ContextCompat.getDrawable(context, R.drawable.bg_button_disable)
}
