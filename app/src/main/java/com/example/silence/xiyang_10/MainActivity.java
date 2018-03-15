package com.example.silence.xiyang_10;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.stephentuso.welcome.WelcomeHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

/**
 * Created by Silence on 2018/3/15.
 */

public class MainActivity extends BaseActivity implements BottomNavigation.OnMenuItemSelectionListener {


    WelcomeHelper welcomeScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);// 设置布局文件

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }// 隐藏标题栏
        initializeBottomNavigation(savedInstanceState);// 初始化底部导航栏
        initializeUI(savedInstanceState);// 初始化界面
        welcomeScreen = new WelcomeHelper(this, MyWelcomeActivity.class);
        welcomeScreen.show(savedInstanceState);


    }


    private void initializeUI(Bundle savedInstanceState) {
        final ViewPager viewPager = getViewPager();// 获取视图页面
        if (null != viewPager) {

            getBottomNavigation().setOnMenuChangedListener(new BottomNavigation.OnMenuChangedListener() {
                @Override
                public void onMenuChanged(final BottomNavigation parent) {

                    viewPager.setAdapter(new ViewPagerAdapter(MainActivity.this, parent.getMenuItemCount()));
                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(
                                final int position, final float positionOffset, final int positionOffsetPixels) {
                        }

                        @Override
                        public void onPageSelected(final int position) {
                            if (getBottomNavigation().getSelectedIndex() != position) {
                                getBottomNavigation().setSelectedIndex(position, false);
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(final int state) {
                        }
                    });
                }
            });

        }
    }

    private void initializeBottomNavigation(Bundle savedInstanceState) {
        if (null == savedInstanceState) {
            getBottomNavigation().setDefaultSelectedIndex(0);// 设置底部导航栏默认高亮位置
        }
    }

    @Override
    public void onMenuItemSelect(final int itemId, final int position, final boolean fromUser) {
        if (fromUser) {
            getBottomNavigation().getBadgeProvider().remove(itemId);
            if (null != getViewPager()) {
                getViewPager().setCurrentItem(position);// 获取当前position的碎片视图
            }
        }
    }

    @Override
    public void onMenuItemReselect(@IdRes final int itemId, final int position, final boolean fromUser) {

        if (fromUser&&position==0) {
           MainActivityFragment fragment = (MainActivityFragment) getSupportFragmentManager().findFragmentByTag(
                    "android:switcher:"+R.id.ViewPager01+":0");// 通过manager获取碎片实例
            if (null != fragment) {
                fragment.scrollToTop();
            }
        }

    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final int mCount;

        public ViewPagerAdapter(final AppCompatActivity activity, int count) {
            super(activity.getSupportFragmentManager());
            this.mCount = count;
        }

        @Override
        public Fragment getItem(final int position) {
            if (position == 0)
                return new MainActivityFragment();
            else
                return new MainActivityFragment2();
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }
}
