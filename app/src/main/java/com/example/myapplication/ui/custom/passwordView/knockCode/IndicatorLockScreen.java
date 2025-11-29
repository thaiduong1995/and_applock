package com.example.myapplication.ui.custom.passwordView.knockCode;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.myapplication.R;
import com.example.myapplication.data.model.IndicatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thinhvh on 31/08/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
public class IndicatorLockScreen extends RelativeLayout {

    private IndicatorType indicatorType = IndicatorType.SETUP;

    public IndicatorLockScreen(Context context) {
        super(context);
        initialize(context, null);
    }

    public IndicatorLockScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    private List<SingleIndicatorView> indicators;
    private View rootView;
    private SingleIndicatorView vw1, vw2, vw3, vw4, vw5, vw6;
    private LinearLayout linearLayout;

    private int color = 0;

    private void initialize(Context context, AttributeSet attributeSet) {
        rootView = LayoutInflater.from(context).inflate(R.layout.knock_code_indicator_view, this);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutAddView);
        vw1 = (SingleIndicatorView) rootView.findViewById(R.id.inditactor_view_1);
        vw2 = (SingleIndicatorView) rootView.findViewById(R.id.inditactor_view_2);
        vw3 = (SingleIndicatorView) rootView.findViewById(R.id.inditactor_view_3);
        vw4 = (SingleIndicatorView) rootView.findViewById(R.id.inditactor_view_4);
        vw5 = (SingleIndicatorView) rootView.findViewById(R.id.inditactor_view_5);
        vw6 = (SingleIndicatorView) rootView.findViewById(R.id.inditactor_view_6);

        indicators = new ArrayList<>();
        indicators.add(vw1);
        indicators.add(vw2);
        indicators.add(vw3);
        indicators.add(vw4);
        indicators.add(vw5);
        indicators.add(vw6);
        setIndicatorType(indicatorType);
    }

    public void setColorFilter(int color) {
        this.color = color;
        vw1.setColorFilter(color);
        vw2.setColorFilter(color);
        vw3.setColorFilter(color);
        vw4.setColorFilter(color);
        vw5.setColorFilter(color);
        vw6.setColorFilter(color);
    }

    public void updateClicks(int[] clicks) {
        for (int i = 0; i < clicks.length; i++) {
            if (i < clicks.length - 1) {
                if (clicks[i + 1] == -1) {
                    indicators.get(i).setClicked(clicks[i]);
                }
            } else if (i == 5) {
                indicators.get(i).setClicked(clicks[i]);
            }
        }
    }

    int normalSize = 0;

    public void setHeightWidth(int w, int h) {
        normalSize = w;
        for (int i = 0; i < indicators.size(); i++) {
            indicators.get(i).setLayoutHeightWidth(w, h);
        }
        requestLayout();
    }

    public void setMargins(int width) {
        for (int i = 0; i < indicators.size(); i++) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) indicators.get(i).getLayoutParams();
            p.setMargins(width, width, width, width);
            indicators.get(i).requestLayout();
        }
    }

    public void setThemeId(int themeId) {
        for (SingleIndicatorView view : indicators) {
            view.setThemeId(themeId);
        }
    }

    public void clearPinCode() {

    }

    public void setIndicatorType(IndicatorType indicatorType) {
        this.indicatorType = indicatorType;
        indicators.forEach(indicator -> {
            indicator.setIndicatorType(indicatorType);
            if (indicatorType == IndicatorType.SETUP) {
                indicator.setAlpha(1f);
                indicator.setVisibility(View.VISIBLE);
            } else {
                indicator.setAlpha(0f);
                indicator.setVisibility(View.GONE);
            }
        });
    }
}


