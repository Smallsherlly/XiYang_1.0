package com.example.silence.xiyang_10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageView;


/**
 * Created by Silence on 2018/3/16.
 */

public class RoundImageView extends AppCompatImageView {

    public RoundImageView(Context context) {
        super(context);
    }
    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public RoundImageView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        Path clipPath = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        clipPath.addRoundRect(new RectF(0, 0, w, h), 120.0f, 120.0f, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }

}
