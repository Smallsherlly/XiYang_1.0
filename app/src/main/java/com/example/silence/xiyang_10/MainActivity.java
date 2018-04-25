package com.example.silence.xiyang_10;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.example.silence.xiyang_10.db.DbHelper;
import com.example.silence.xiyang_10.runtimepermissions.PermissionsManager;
import com.example.silence.xiyang_10.runtimepermissions.PermissionsResultAction;
import com.stephentuso.welcome.WelcomeHelper;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Style;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

/**
 * Created by Silence on 2018/3/15.
 * 这是主活动
 */

public class MainActivity extends BaseActivity implements BottomNavigation.OnMenuItemSelectionListener,UCropFragmentCallback{
    public static Context mcontext;
    public Uri sketchUri;
    FragmentManager mFragmentManager;



    @Override
    public void loadingProgress(boolean showLoader) {

    }
    @Override
    public void onCropFinish(UCropFragment.UCropResult result) {

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public static Context getAppContext() {
        return MainActivity.mcontext;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteDatabase db = DbHelper.getInstance(this).getDatabase(true);
        DbHelper.getInstance(this).onCreate(db);

        //DbHelper.getInstance(this).onCreate(db);
        Toast.makeText(this,DbHelper.getInstance(this).getDatabaseName(),Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_main);// 设置布局文件
        mcontext = getApplicationContext();
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
        f = checkFragmentInstance(R.id.ViewPager01, SketchFragment.class);
        if (f != null) {
            ((SketchFragment) f).save();


            // Removes forced portrait orientation for this fragment
//            setRequestedOrientation(
//                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

            mFragmentManager.popBackStack();
            getViewPager().setCurrentItem(1);

            return;
        }else{
            super.onBackPressed();
        }

    }
    private Fragment checkFragmentInstance(int id, Object instanceClass) {
        Fragment result = null;
        Fragment fragment = null;
        if (mFragmentManager != null) {
          //  fragment =  getSupportFragmentManager().findFragmentById(id);// 通过manager获取碎片实例
            fragment =  getSupportFragmentManager().findFragmentByTag(
                    "android:switcher:"+R.id.ViewPager01+":"+getViewPager().getCurrentItem());// 通过manager获取碎片实例

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
                    viewPager.setAdapter(new ViewPagerAdapter(MainActivity.this, getBottomNavigation().getMenuItemCount()+1));
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
            Fragment f0= new MainPageFragment();
            Fragment f1= new MyEditClass();
            Fragment f2= new SearchViewFragment();
            Fragment f3= new MineFragment();
            if (position == 0){
                return f0;
            } else if(position == 3){
                return f3;
            } else if(position == 2){
                ((SearchViewFragment)f2).setOnSearchCallBackListener(new SearchViewFragment.searchCallBack() {
                    @Override
                    public void sendHandEdit(Long creation) {
                        getViewPager().setCurrentItem(1);
                        MyEditClass fragment = (MyEditClass) getSupportFragmentManager().findFragmentByTag(
                                "android:switcher:"+R.id.ViewPager01+":1");
                        fragment.show(creation);
                        fragment.insertNullImage();
                    }
                });
                return f2;
            }else if(position == 1)
                return  f1;
            else
                return new SketchFragment();
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }

}
