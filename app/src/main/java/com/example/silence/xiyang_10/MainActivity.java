package com.example.silence.xiyang_10;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.View;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.example.silence.xiyang_10.runtimepermissions.PermissionsManager;
import com.example.silence.xiyang_10.runtimepermissions.PermissionsResultAction;
import com.stephentuso.welcome.WelcomeHelper;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Style;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

/**
 * Created by Silence on 2018/3/15.
 * 这是主活动
 */

public class MainActivity extends BaseActivity implements BottomNavigation.OnMenuItemSelectionListener{

    public Uri sketchUri;
    FragmentManager mFragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);// 设置布局文件

        // 隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {//权限通过了
            }

            @Override
            public void onDenied(String permission) {//权限拒绝了

            }
        });

        // 补间动画
        Explode explode = new Explode();
        explode.setDuration(400);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);

        initializeBottomNavigation(savedInstanceState);// 初始化底部导航栏
        initializeUI(savedInstanceState);// 初始化界面
        mFragmentManager = getSupportFragmentManager();
    }

    public void onBackPressed() {

        Fragment f;
        // SketchFragment
        f = checkFragmentInstance("Sketch", SketchFragment.class);
        if (f != null) {
            ((SketchFragment) f).save();


            // Removes forced portrait orientation for this fragment
            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            mFragmentManager.popBackStack();
            return;
        }
    }
    private Fragment checkFragmentInstance(String tag, Object instanceClass) {
        Fragment result = null;
        if (mFragmentManager != null) {
            Fragment fragment = mFragmentManager.findFragmentByTag(tag);
            if (instanceClass.equals(fragment.getClass())) {
                result = fragment;
            }
        }
        return result;
    }


    private void initializeUI(Bundle savedInstanceState) {
        final CustomViewPager viewPager = getViewPager();// 获取视图页面
       if (null != viewPager) {

            getBottomNavigation().setOnMenuChangedListener(new BottomNavigation.OnMenuChangedListener() {
                @Override
                public void onMenuChanged(final BottomNavigation parent) {
                    viewPager.setScanScroll(false);// 禁止左右滑动切换页面
                    viewPager.setAdapter(new ViewPagerAdapter(MainActivity.this, getBottomNavigation().getMenuItemCount()));
                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(
                                final int position, final float positionOffset, final int positionOffsetPixels) {
                        }

                        @Override
                        public void onPageSelected(final int position) {// 底部导航图标随页面切换而改变，禁止滑动下无效
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

            // 获取当前position的碎片视图
            if (null != getViewPager()) {
                getViewPager().setCurrentItem(position);
            }
        }
    }

    @Override
    public void onMenuItemReselect(@IdRes final int itemId, final int position, final boolean fromUser) {

        if (fromUser&&position==0) {
           MainPageFragment fragment = (MainPageFragment) getSupportFragmentManager().findFragmentByTag(
                    "android:switcher:"+R.id.ViewPager01+":0");// 通过manager获取碎片实例
            if (null != fragment) {
                fragment.scrollToTop();
            }
        }

    }

    public  class ViewPagerAdapter extends FragmentPagerAdapter {

        private final int mCount;

        public ViewPagerAdapter(final AppCompatActivity activity, int count) {
            super(activity.getSupportFragmentManager());
            this.mCount = count;
        }

        @Override
        public Fragment getItem(final int position) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            if (position == 0)
                return new MainPageFragment();
            else if(position == 3)
                return new MineFragment();
            else if(position == 2){
                SketchFragment sketchFragment = new SketchFragment();
                fragmentTransaction.add(sketchFragment, "Sketch").commit();
                return new SketchFragment();
            }

            else
                return new MyEditClass();
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }

}
