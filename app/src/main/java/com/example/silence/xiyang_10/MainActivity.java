package com.example.silence.xiyang_10;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.util.Log;
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
import com.example.silence.xiyang_10.RichEditor.DragScaleView;
import com.example.silence.xiyang_10.RichEditor.EditorBean;
import com.example.silence.xiyang_10.db.DbHelper;
import com.example.silence.xiyang_10.models.HandEdit;
import com.example.silence.xiyang_10.myhttputils.FailedMsgUtils;
import com.example.silence.xiyang_10.myhttputils.MyHttpUtils;
import com.example.silence.xiyang_10.myhttputils.bean.CommCallback;
import com.example.silence.xiyang_10.myhttputils.bean.HttpBody;
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
    String username;
    private ProgressDialog mProgressDialog;

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

    public void dataStringManage(String data){
        String[] parts = data.split(";");
        for(int i=0; i<parts.length; i++){
            String[] fields = parts[i].split(",");
            HandEdit handEdit = new HandEdit();
            handEdit.setCreation(fields[0]);
            handEdit.setLastModification(fields[1]);
            handEdit.setZan_number(Long.valueOf(fields[2]));
            handEdit.setAuthor(fields[3]);
            handEdit.setJson_path(fields[4]);
            handEdit.setCover_path(fields[5]);
            handEdit.setTitle(fields[6]);
            handEdit.setContent(fields[7]);
            handEdit.setArchived(Integer.valueOf(fields[8]));
            handEdit.setTrashed(Integer.valueOf(fields[9]));
            File imgsrc = new File(fields[4]);
            if(!imgsrc.exists()){
                int index = fields[4].indexOf("2018");
                String filename = fields[4].substring(index);
                onDownload(filename,null,null);
            }
            imgsrc = new File(fields[5]);
            if(!imgsrc.exists()){
                int index = fields[5].indexOf("2018");
                String filename = fields[5].substring(index);
                onDownload(filename,null,null);
            }
            DbHelper.getInstance().updateHandEdit(handEdit,false);
        }

    }

    public void onDownload(String filename, @Nullable DragScaleView view, @Nullable EditorBean bean) {
        mProgressDialog.show();
        HttpBody body = new HttpBody();
        body.setUrl("http://119.23.206.213:80/Login/upload/userdata/"+username+"/"+filename)
                .setConnTimeOut(6000)
                .setFileSaveDir(MainActivity.this.getExternalFilesDir(null)+"/userdata/"+username)
                .setReadTimeOut(5 * 60 * 1000);

        MyHttpUtils.build()
                .setHttpBody(body)
                .onExecuteDwonload(new CommCallback() {

                    @Override
                    public void onSucceed(Object o) {
                        mProgressDialog.dismiss();
                       // view.setImageURI(Uri.parse(bean.getContent()));
                        // ToastUtils.showToast(HandEditActivity.this, "下载完成");
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        ToastUtils.showToast(MainActivity.this, FailedMsgUtils.getErrMsgStr(throwable));
                    }

                    @Override
                    public void onDownloading(long total, long current) {
                        System.out.println(total + "-------" + current);
                        //tvProgress.setText(new DecimalFormat("######0.00").format(((double) current / total) * 100) + "%");//保留两位小数
                    }
                });
    }
    public static Context getAppContext() {
        return MainActivity.mcontext;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteDatabase db = DbHelper.getInstance(this).getDatabase(true);
        DbHelper.getInstance(this).onCreate(db);
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("正从云端下载文件，请稍候");
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        //检查本地是否有用户数据，如没有，则尝试从服务器同步
       if(DbHelper.getInstance().getAllHandEdits(username,false).size() == 0){

        new Thread(new Runnable() {

            @Override
            public void run() {
                final String state= NetUilts.getDatabase(username);

                runOnUiThread(new Runnable() {//执行任务在主线程中
                    @Override
                    public void run() {//就是在主线程中操作
                       // Toast.makeText(MainActivity.this, state, Toast.LENGTH_SHORT).show();
                        Log.i("data",state);
                        dataStringManage(state);
                    }
                });
            }
        }).start();
       }


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
        AlertDialog.Builder adialog = new AlertDialog.Builder(MainActivity.this);
        adialog.setTitle("确定返回到登录界面？");
        adialog.setCancelable(true);
        adialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.super.onBackPressed();
            }
        });
        adialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        adialog.show();

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
            Fragment f1= new SearchViewFragment();
            Fragment f2= new MineFragment();
            if (position == 0){
                return f0;
            }else if(position == 1){
                return f1;
            }else if(position == 2)
                return  f2;
            else
                return new SketchFragment();
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }

}
