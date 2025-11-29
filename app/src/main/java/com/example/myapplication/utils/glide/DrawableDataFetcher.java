package com.example.myapplication.utils.glide;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

public class DrawableDataFetcher implements DataFetcher<Drawable> {

    private final String packageName;
    private final Context mContext;

    DrawableDataFetcher(Context context, String name) {
        packageName = name;
        mContext = context;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Drawable> callback) {
        final Drawable icon;
        try {
            String pkgName = this.packageName.replace("pkg:", "");
            icon = mContext.getPackageManager().getApplicationIcon(pkgName);
            callback.onDataReady(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        // Empty Implementation
    }

    @Override
    public void cancel() {
        // Empty Implementation
    }

    @NonNull
    @Override
    public Class<Drawable> getDataClass() {
        return Drawable.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
