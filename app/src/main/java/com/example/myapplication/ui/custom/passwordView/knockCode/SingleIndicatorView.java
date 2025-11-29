package com.example.myapplication.ui.custom.passwordView.knockCode;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.myapplication.R;
import com.example.myapplication.data.model.IndicatorType;
import com.example.myapplication.utils.Utils;

/**
 * Created by Thinhvh on 31/08/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
public class SingleIndicatorView extends RelativeLayout {

    private IndicatorType indicatorType = IndicatorType.SETUP;
    private View rootView;
    private Drawable drawable1, drawableEmpty;

    public SingleIndicatorView(Context context) {
        super(context);
        initialize(context, null);
    }

    public SingleIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attributeSet) {
        rootView = LayoutInflater.from(context).inflate(R.layout.view_single_indicator, this);
        setImageDrawable(drawableEmpty);
    }

    public void setImageDrawable(Drawable drawable) {
        ((ImageView) rootView.findViewById(R.id.indicator_click_image_view)).setImageDrawable(drawable);
    }

    public void setClicked(int which) {
        Drawable drawable = drawableEmpty;
        ImageView view = ((ImageView) rootView.findViewById(R.id.indicator_click_image_view));
        drawable = switch (which) {
            case 1 -> {
                view.setRotation(0);
                yield drawable1;
            }
            case 2 -> {
                view.setRotation(90);
                yield drawable1;
            }
            case 3 -> {
                view.setRotation(270);
                yield drawable1;
            }
            case 4 -> {
                view.setRotation(180);
                yield drawable1;
            }
            default -> drawable;
        };
        setImageDrawable(drawable);

        if (indicatorType == IndicatorType.UNLOCK) {
            if (which > 0) {
                this.animate().alpha(1f).setDuration(500).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        setVisibility(View.VISIBLE);
                        setAlpha(0f);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        startAnimationHideView();
                    }
                }).start();
            }
        }
    }

    private void startAnimationHideView() {
        this.animate().alpha(0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(View.GONE);
            }
        }).start();
    }

    public void setLayoutHeightWidth(int w, int h) {
        ViewGroup.LayoutParams p = (ViewGroup.MarginLayoutParams) getLayoutParams();
        p.height = h;
        p.width = w;
        requestLayout();
    }

    public void setColorFilter(int color) {
        ((ImageView) rootView.findViewById(R.id.indicator_click_image_view)).setColorFilter(color);
    }

    public void setThemeId(int themeId) {
        initDrawableWithTheme(themeId);
        setImageDrawable(drawableEmpty);
    }

    private void initDrawableWithTheme(int themeId) {
        try {
            drawable1 = Drawable.createFromStream(getContext().getAssets().open(Utils.INSTANCE.getAssetUri(themeId) + "knock_indicator_selected.png"), null);
            drawableEmpty = Drawable.createFromStream(getContext().getAssets().open(Utils.INSTANCE.getAssetUri(themeId) + "knock_indicator_empty.png"), null);
        } catch (Exception e) {

        }
    }

    public void setIndicatorType(IndicatorType indicatorType) {
        this.indicatorType = indicatorType;
    }
}
