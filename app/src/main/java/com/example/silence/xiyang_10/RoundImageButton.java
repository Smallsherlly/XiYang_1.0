package com.example.silence.xiyang_10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageView;


/**
 * Created by Silence on 2018/3/16.
 * 这是一个圆角图片类
 */

public class RoundImageButton extends AppCompatImageButton {

    public RoundImageButton(Context context) {
        super(context);
    }
    public RoundImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public RoundImageButton(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }







    @Override
    protected void onDraw(Canvas canvas) {// 绘制背景的画布
        Path clipPath = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        clipPath.addRoundRect(new RectF(0, 0, w, h), 150.0f, 150.0f, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }

}
