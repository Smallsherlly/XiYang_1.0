package com.example.silence.xiyang_10;

import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

/**
 * Created by Silence on 2018/3/12.
 */

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigation.OnMenuItemSelectionListener {
    private ViewPager mViewPager;
    private SystemBarTintManager mSystemBarTint;
    private BottomNavigation mBottomNavigation;


    public ViewPager getViewPager() {
        return mViewPager;
    }

    public BottomNavigation getBottomNavigation() {
        if (null == mBottomNavigation) {
            mBottomNavigation = (BottomNavigation) findViewById(R.id.BottomNavigation);
        }
        return mBottomNavigation;
    }
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mViewPager = (ViewPager) findViewById(R.id.ViewPager01);
        mBottomNavigation = (BottomNavigation) findViewById(R.id.BottomNavigation);
        if (null != mBottomNavigation) {
            Typeface typeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
            mBottomNavigation.setOnMenuItemClickListener(this);
            mBottomNavigation.setDefaultTypeface(typeface);
        }
    }




    public SystemBarTintManager getSystemBarTint() {
        if (null == mSystemBarTint) {
            mSystemBarTint = new SystemBarTintManager(this);
        }
        return mSystemBarTint;
    }
    public int getNavigationBarHeight() {
        return getSystemBarTint().getConfig().getNavigationBarHeight();
    }
}
