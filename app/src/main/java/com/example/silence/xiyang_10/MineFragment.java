package com.example.silence.xiyang_10;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.silence.xiyang_10.db.DbHelper;
import com.example.silence.xiyang_10.models.HandEdit;
import com.example.silence.xiyang_10.models.StorageHelper;

import com.example.silence.xiyang_10.myhttputils.FailedMsgUtils;
import com.example.silence.xiyang_10.myhttputils.MyHttpUtils;
import com.example.silence.xiyang_10.myhttputils.bean.CommCallback;
import com.example.silence.xiyang_10.myhttputils.bean.HttpBody;
import com.example.silence.xiyang_10.myhttputils.bean.StringCallBack;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.yalantis.ucrop.UCrop;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

/**
 * Created by Silence on 2018/3/16.
 * 这是“我的”模块的碎片类
 */

public class MineFragment extends Fragment {
    private int count;
    private String urlpath;//图片路径
    private RoundImageButton test_button;
    public MineFragment(){
        count = 0;
    }
    private View myview;
    private String username;
    private Button et_username;
    private Button et_qqnum;
    private Button et_phonenum;
    private Button synchronize;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initUI();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return myview;// 将布局加载到碎片实例中
    }
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState){

    }

    public void initUI(){
        Intent message = getActivity().getIntent();
        username = message.getStringExtra("username");
        myview = getActivity().getLayoutInflater().inflate(R.layout.fragment_mine,null);
        synchronize = (Button) myview.findViewById(R.id.synchronize);
        synchronize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadMult();
            }
        });
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("上传中，请稍后");

        et_username = (Button) myview.findViewById(R.id.username);
        et_qqnum = (Button) myview.findViewById(R.id.qqnum);
        et_phonenum = (Button) myview.findViewById(R.id.phonenum);
        et_username.setText("昵称："+message.getStringExtra("username"));
        et_qqnum.setText("QQ："+message.getStringExtra("qqnum"));
        et_phonenum.setText("手机："+message.getStringExtra("phonenum"));
        Intent intent = getActivity().getIntent();
        String username = intent.getStringExtra("username");

        test_button = (RoundImageButton) myview.findViewById(R.id.round_touxiang);
        new Thread(new Runnable() {

            @Override
            public void run() {
                final String state=NetUilts.iconPathGet(username);

                getActivity().runOnUiThread(new Runnable() {//执行任务在主线程中
                    @Override
                    public void run() {//就是在主线程中操作
                        if(!state.equals("获取头像失败")&&state.equals(null)){
                            test_button.setImageURI(Uri.parse(state));
                        }
                        Log.i("iconpath",state);

                    }
                });
            }
        }).start();
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(getContext())
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem("相册",
                                ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {

                                    @Override
                                    public void onClick(int which) {

                                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                                        intent.setDataAndType(
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                "image/*");
                                        startActivityForResult(intent, 1);
                                    }
                                })
                        .addSheetItem("拍照",
                                ActionSheetDialog.SheetItemColor.Blue,
                                new ActionSheetDialog.OnSheetItemClickListener() {

                                    @Override
                                    public void onClick(int which) {
                                        Intent intent = new Intent(
                                                MediaStore.ACTION_IMAGE_CAPTURE);
                                        //下面这句指定调用相机拍照后的照片存储的路径
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                                                .fromFile(new File(Environment
                                                        .getExternalStorageDirectory(),
                                                        "xiaoma.jpg")));
                                        startActivityForResult(intent, 2);
                                    }
                                }).show();
            }
        });


    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
                // 如果是直接从相册获取
                case 1:
                    File file = StorageHelper.createNewAttachmentFile((MainActivity)getActivity(),username, ".png");

                    UCrop.of(data.getData(), Uri.fromFile(file)).start(getContext(),MineFragment.this);
                    break;
                // 如果是调用相机拍照时
                case 2:
                    File file2 = StorageHelper.createNewAttachmentFile(getActivity(), username,".png");
                    UCrop.of(data.getData(), Uri.fromFile(file2)).start(getActivity());
                    break;
                // 取得裁剪后的图片
                case 69:
                    Uri resultUri = UCrop.getOutput(data);
                    test_button.setImageURI(resultUri);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            final String state=NetUilts.iconPathPost(username,resultUri.getPath());

                            getActivity().runOnUiThread(new Runnable() {//执行任务在主线程中
                                @Override
                                public void run() {//就是在主线程中操作
                                    Toast.makeText(getActivity(),state,Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }).start();
                    break;
                default:
                    break;

            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            //图片路径
            urlpath = FileUtilcll.saveFile(getActivity(), "temphead.jpg", photo);
            System.out.println("----------路径----------" + urlpath);
            test_button.setImageBitmap(photo);
        }
    }

    //圆角裁剪函数，待分析
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);

        final float roundPx = pixels;

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;

    }

    public void outputFiles(String path,MyHttpUtils utils){
        File root = new File(path);
        recyclemethod(root,utils);
    }

    public void recyclemethod(File path,MyHttpUtils utils){
        if(path.exists()) {
            File[] files = path.listFiles();
            for (File f : files) {
                if (f.isFile()) {
                    utils.addFile(f);
                } else if (f.isDirectory()) {
                    recyclemethod(f, utils);
                }
            }
        }
    }
    /**
     * 云端服务器数据同步
     *上传并且下载
     *
     */
    public void onUploadMult() {
        Intent intent = getActivity().getIntent();
        String username = intent.getStringExtra("username");
        mProgressDialog.show();
        MyHttpUtils utils = MyHttpUtils.build();
        utils.uploadUrl("http://119.23.206.213:80/Login/upload");
        utils.addParam("username",username);
        Log.d("usernameMine",username);
        String path = getActivity().getExternalFilesDir(null)+"/userdata/"+username;
        File root = new File(path);
        if(root.exists()){
            recyclemethod(root,utils);
            utils.onExecuteUpLoad(new CommCallback() {
                @Override
                public void onComplete() {
                    mProgressDialog.dismiss();
                    ToastUtils.showToast(getActivity(), "上传完成");
                }

                @Override
                public void onSucceed(Object o) {

                }

                @Override
                public void onFailed(Throwable throwable) {
                    ToastUtils.showToast(getActivity(), FailedMsgUtils.getErrMsgStr(throwable));
                }
            });
        }

    }

    /**
     * 开始下载按钮单击事件
     *
     *
     */
    public void onDownload() {

        HttpBody body = new HttpBody();
        body.setUrl("http://119.23.206.213:80/Login/data/userdata/"+username)
                .setConnTimeOut(6000)
                .setFileSaveDir(getActivity().getExternalFilesDir(null)+"/userdata/"+username)
                .setReadTimeOut(5 * 60 * 1000);

        MyHttpUtils.build()
                .setHttpBody(body)
                .onExecuteDwonload(new CommCallback() {

                    @Override
                    public void onSucceed(Object o) {
                        ToastUtils.showToast(getActivity(), "下载完成");
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        ToastUtils.showToast(getActivity(), FailedMsgUtils.getErrMsgStr(throwable));
                    }

                    @Override
                    public void onDownloading(long total, long current) {
                        System.out.println(total + "-------" + current);
                        //tvProgress.setText(new DecimalFormat("######0.00").format(((double) current / total) * 100) + "%");//保留两位小数
                    }
                });
    }

 
}
