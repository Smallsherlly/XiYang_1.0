package com.example.silence.xiyang_10.RichEditor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.silence.xiyang_10.R;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Silence on 2018/3/21.
 */

public class DragScaleView extends AppCompatImageView {
    protected int screenWidth;
    protected int screenHeight;
    protected int lastX;
    protected int lastY;
    private int oriLeft;
    private int oriRight;
    private int oriTop;
    private int oriBottom;
    private int dragDirection;
    private static final int TOP = 0x15;
    private static final int LEFT = 0x16;
    private static final int BOTTOM = 0x17;
    private static final int RIGHT = 0x18;
    private static final int LEFT_TOP = 0x11;
    private static final int RIGHT_TOP = 0x12;
    private static final int LEFT_BOTTOM = 0x13;
    private static final int RIGHT_BOTTOM = 0x14;
    private static final int CENTER = 0x19;
    private int offset = 20;
    protected Paint paint = new Paint();
    private float scale;
    private float preScale = 1;// 默认前一次缩放比例为1
    /**
     * 初始化获取屏幕宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //获取SingleTouchView所在父布局的中心点
        ViewGroup mViewGroup = (ViewGroup) getParent();
        if(null != mViewGroup){
            screenWidth = mViewGroup.getWidth();
            screenHeight = mViewGroup.getHeight();
        }

    }
    protected void initScreenW_H() {

        //screenHeight = getResources().getDisplayMetrics().heightPixels - 40;

        //screenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    public DragScaleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initScreenW_H();
    }

    public DragScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initScreenW_H();
    }

    public DragScaleView(Context context) {
        super(context);
        initScreenW_H();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //getParent().requestDisallowInterceptTouchEvent(true);
                oriLeft = getLeft();
                oriRight = getRight();
                oriTop = getTop();
                oriBottom = getBottom();

                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                dragDirection = getDirection((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:

                //getParent().requestDisallowInterceptTouchEvent(true);


                int tempRawX = (int)event.getRawX();
                int tempRawY = (int)event.getRawY();

                int dx = tempRawX - lastX;
                int dy = tempRawY - lastY;
                lastX = tempRawX;
                lastY = tempRawY;

                switch (dragDirection) {

                    case RIGHT_BOTTOM:
                        right_bottom(dx,dy);
                        break;

                    case CENTER:
                       center( dx, dy);
                        break;
                }

                //把新的位置 oriLeft, oriTop, oriRight, oriBottom设置到控件，实现位置移动和大小变化。
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(oriRight - oriLeft, oriBottom - oriTop);
                lp.setMargins(oriLeft,oriTop,0,0);
                setLayoutParams(lp);
                break;
        }
        return super.onTouchEvent(event);
    }
    /**
     * 触摸点为中心->>移动
     */
    private void center(int dx, int dy) {
        int left = getLeft() + dx;
        int top = getTop() + dy;
        int right = getRight() + dx;
        int bottom = getBottom() + dy;
        if (left < 0) {
            left = 0;
            right = left + getWidth();
        }

        if (right > screenWidth ) {
            right = screenWidth ;
            left = right - getWidth();
        }
        if (top < 0) {
            top = 0;
            bottom = top + getHeight();
        }

        if (bottom > screenHeight ) {
            bottom = screenHeight ;
            top = bottom - getHeight();
        }

        oriLeft = left;
        oriTop = top;
        oriRight = right;
        oriBottom = bottom;
    }




    /**
     * 触摸点为右下边缘
     *
     * @param v
     * @param dy
     */
    private void right_bottom(int dx,int dy) {
        oriRight += dx;
        oriBottom += dy;

        if (oriBottom > screenHeight + offset) {
            oriBottom = screenHeight + offset;
        }
        if (oriBottom - oriTop - 2 * offset < 200) {
            oriBottom = 200 + oriTop + 2 * offset;
        }
        if (oriRight > screenWidth + offset) {
            oriRight = screenWidth + offset;
        }
        if (oriRight - oriLeft - 2 * offset < 200) {
            oriRight = oriLeft + 2 * offset + 200;
        }
    }


    /**
     * 获取触摸点flag
     *

     * @param x
     * @param y
     * @return
     */
    protected int getDirection(int x, int y) {
        int left = getLeft();
        int right = getRight();
        int bottom = getBottom();
        int top = getTop();
        if (x < 40 && y < 40) {
            return LEFT_TOP;
        }
        if (y < 40 && right - left - x < 40) {
            return RIGHT_TOP;
        }
        if (x < 40 && bottom - top - y < 40) {
            return LEFT_BOTTOM;
        }
        if (right - left - x < 60 && bottom - top - y < 60) {
            return RIGHT_BOTTOM;
        }
        if (x < 40) {
            return LEFT;
        }
        if (y < 40) {
            return TOP;
        }
        if (right - left - x < 40) {
            return RIGHT;
        }
        if (bottom - top - y < 40) {
            return BOTTOM;
        }
        return CENTER;
    }

    /**
     * 获取截取宽度
     *
     * @return
     */
    public int getCutWidth() {
        return getWidth() - 2 * offset;
    }

    /**
     * 获取截取高度
     *
     * @return
     */
    public int getCutHeight() {
        return getHeight() - 2 * offset;
    }
}

