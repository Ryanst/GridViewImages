package com.example.zhengjt.gridviewimages;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by zhengjt on 15/9/19.
 */
public class BitmapCache implements ImageLoader.ImageCache {
    private final LruCache<String,Bitmap> mCache;

    public BitmapCache() {
        int maxSize = 4 * 512 * 512;
        mCache = new LruCache<String,Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getHeight() * value .getRowBytes();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String s) {
        return null;
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {

    }
}
