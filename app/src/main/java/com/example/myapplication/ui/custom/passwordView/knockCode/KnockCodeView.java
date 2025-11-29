package com.example.myapplication.ui.custom.passwordView.knockCode;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.ImageViewCompat;

import com.example.myapplication.R;
import com.example.myapplication.data.model.CustomTheme;
import com.example.myapplication.ui.custom.passwordView.InputPasswordListener;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.utils.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Thinhvh on 30/08/2022.
 * Phone: 0398477967
 * Email: thinhvh.fpt@gmail.com
 */
public class KnockCodeView extends LinearLayout implements View.OnClickListener {

    public KnockCodeView(Context context) {
        super(context);
        initialize(context, null);
    }

    public KnockCodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }


    private InputPasswordListener inputPasswordListener;
    public int[] clicksSquence = new int[6];
    private View rootView;
    private CardView card1, card2, card3, card4;
    private List<CardView> cardViewsList;
    private ImageView lineCenter;
    private boolean isAnimating = false;
    private int passwordLength = Constants.MIN_PASSWORD_LENGTH;

    private void initialize(Context context, AttributeSet attributeSet) {
        rootView = LayoutInflater.from(context).inflate(R.layout.activity_knock_code_view, this);
        card1 = rootView.findViewById(R.id.cardview_1);
        card2 = rootView.findViewById(R.id.cardview_2);
        card3 = rootView.findViewById(R.id.cardview_3);
        card4 = rootView.findViewById(R.id.cardview_4);

        lineCenter = rootView.findViewById(R.id.knock_view_line_center);

        cardViewsList = new ArrayList<>();
        cardViewsList.add(card1);
        cardViewsList.add(card2);
        cardViewsList.add(card3);
        cardViewsList.add(card4);


        for (CardView crd : cardViewsList) {
            crd.setOnClickListener(this);
            crd.setPreventCornerOverlap(false);
            crd.setCardElevation(0);

        }
        for (int i = 0; i < clicksSquence.length; i++) {
            clicksSquence[i] = -1;
        }

        if (attributeSet != null) {
            final TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SetLockKnockCode, 0, 0);

            setLinesDrawable(typedArray.getColor(R.styleable.SetLockKnockCode_knock_line_drawable, R.drawable.ic_knock_code_full));
            setButtonsColor();
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.cardview_1) {
            addClickToSquence(1);

        } else if (i == R.id.cardview_2) {
            addClickToSquence(2);

        } else if (i == R.id.cardview_3) {
            addClickToSquence(3);

        } else if (i == R.id.cardview_4) {
            addClickToSquence(4);
        }
    }


    private void addClickToSquence(int number) {
        if (getClicksCount() < clicksSquence.length) {
            try {
                clicksSquence[getClicksCount()] = number;
            } catch (IndexOutOfBoundsException e) {
            }
        }
        clickDetected(clicksSquence);
    }

    IndicatorLockScreen indicator;

    public void setIndicator(IndicatorLockScreen indicator) {
        this.indicator = indicator;
    }

    public void clickDetected(final int[] clicks) {
        indicator.updateClicks(clicks);
        if (inputPasswordListener != null) {
            inputPasswordListener.onInputting();
        }
        if (isEnoughValue(clicksSquence)) {
            if (inputPasswordListener != null) {
                inputPasswordListener.onInputComplete(new Gson().toJson(clicksSquence));
            }
            return;
        }
        if (getClicksCount() == 1) {
            if (inputPasswordListener != null) {
                inputPasswordListener.onStartInput();
            }
        }
    }

    private boolean isEnoughValue(int[] clicks) {
        int currentPasswordLength = 0;
        for (int i = 0; i < clicks.length; i++) {
            if (clicks[i] != -1) {
                currentPasswordLength = i + 1;
            }
        }
        return currentPasswordLength >= passwordLength;
    }

    public void clearClicks() {
        if (isAnimating) return;
        Arrays.fill(clicksSquence, -1);

        isAnimating = true;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                indicator.updateClicks(clicksSquence);
                isAnimating = false;
            }
        }, 300);
    }

    public void setColorFilter(int color) {
        lineCenter.setColorFilter(color);
    }

    public boolean isEntryIsTrue(int[] clicks) {
        for (int i = 0; i < clicks.length; i++) {
            if (clicks[i] != clicksSquence[i]) {
                return false;
            }
        }
        return true;
    }

    public int getClicksCount() {
        for (int i = 0; i < clicksSquence.length; i++) {
            if (clicksSquence[i] == -1) return i;
        }
        return clicksSquence.length;
    }

    public void setPasswordLength(int passwordLength) {
        this.passwordLength = passwordLength;
    }

    public void setInputPasswordListener(InputPasswordListener detector) {
        this.inputPasswordListener = detector;
    }

    public void setButtonsColor() {
        for (CardView crd : cardViewsList) {
            crd.setCardBackgroundColor(Color.TRANSPARENT);
        }
    }

    public void setLinesDrawable(int resId) {
        lineCenter.setImageResource(resId);
    }

    public void setThemeId(int themeId) {
        if (indicator != null) {
            indicator.setThemeId(themeId);
        }

        String filePath = Utils.INSTANCE.getAssetUri(themeId) + "knock_bg.png";
        try {
            lineCenter.setImageDrawable(Drawable.createFromStream(getContext().getAssets().open(filePath), null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCustomTheme(CustomTheme customTheme) {
        if (customTheme != null) {
            ImageViewCompat.setImageTintList(lineCenter, ColorStateList.valueOf(customTheme.getKnockColor()));
            indicator.setColorFilter(customTheme.getKnockColor());
        }
    }
}
