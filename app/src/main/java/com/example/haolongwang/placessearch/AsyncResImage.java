package com.example.haolongwang.placessearch;

import android.graphics.Bitmap;
import android.view.View;

public interface AsyncResImage {
    void AsyncResImage(View view);
    void onImageReceived(Bitmap Data);
}
