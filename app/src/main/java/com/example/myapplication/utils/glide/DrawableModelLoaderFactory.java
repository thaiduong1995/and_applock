package com.example.myapplication.utils.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

public class DrawableModelLoaderFactory implements ModelLoaderFactory<String, Drawable> {

    private final Context mContext;

    DrawableModelLoaderFactory(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ModelLoader<String, Drawable> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new DrawableModelLoader(mContext);
    }

    @Override
    public void teardown() {
        // Empty Implementation.
    }
}
