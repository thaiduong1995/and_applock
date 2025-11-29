package  com.example.myapplication.ui.custom.passwordView.pin

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import com.example.myapplication.data.model.ThemeData
import com.example.myapplication.extention.dp2px
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.Utils

/**
 * Created by Thinhvh on 05/09/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
class IndicatorView : AppCompatImageView {

    private var indicatorDotNormal: Drawable? = null
    private var indicatorDotSelected: Drawable? = null
    private var themeId = Constants.DEFAULT_THEME

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        val buttonSize = context.dp2px(16f)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(buttonSize, buttonSize)
        layoutParams.setMargins(6, 0, 6, 0)
        this.layoutParams = layoutParams
    }

    fun setStateSelected(stateSelected: Boolean) {
        if (stateSelected) {
            setImageDrawable(indicatorDotSelected)
        } else {
            setImageDrawable(indicatorDotNormal)
        }
    }

    fun setThemeId(themeId: Int) {
        this.themeId = themeId
        indicatorDotNormal = Drawable.createFromStream(
            context.assets.open(
                Utils.getAssetPath(themeId).plus(ThemeData.PIN_INDICATOR_NORMAL)
            ), null
        )

        indicatorDotSelected = Drawable.createFromStream(
            context.assets.open(
                Utils.getAssetPath(themeId).plus(ThemeData.PIN_INDICATOR_SELECTED)
            ), null
        )

        setImageDrawable(indicatorDotNormal)
    }

    fun isClickEnable(isEnable: String) {

    }
}