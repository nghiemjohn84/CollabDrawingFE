package com.example.collabdrawingfe;

import android.graphics.Path;

public class InputPath {

    public int color;
    public int strokeWidth;
    public Path path;

    public InputPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}
