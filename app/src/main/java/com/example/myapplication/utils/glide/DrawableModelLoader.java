package com.example.myapplication.utils.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

class DrawableModelLoader implements ModelLoader<String, Drawable> {

    private final Context mContext;

    DrawableModelLoader(Context context) {
        mContext = context;
    }

    @Nullable
    @Override
    public LoadData<Drawable> buildLoadData(@NonNull String packageName, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(packageName),
                new DrawableDataFetcher(mContext, packageName));
    }

    @Override
    public boolean handles(@NonNull String model) {
        return model.startsWith("pkg:");
    }
}