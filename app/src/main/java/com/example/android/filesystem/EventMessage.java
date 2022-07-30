package com.example.android.filesystem;

import android.graphics.Bitmap;

public class EventMessage {
    private Bitmap bitmap = null;

    public EventMessage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
