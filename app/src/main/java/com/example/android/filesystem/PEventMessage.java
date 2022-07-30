package com.example.android.filesystem;

import android.graphics.Bitmap;

public class PEventMessage {
    private Bitmap bitmap = null;

    public PEventMessage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
