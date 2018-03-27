package com.example.silence.xiyang_10;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.silence.xiyang_10.RichEditor.ContentType;
import com.example.silence.xiyang_10.RichEditor.DragScaleView;
import com.example.silence.xiyang_10.RichEditor.EditorBean;
import com.example.silence.xiyang_10.RichEditor.MRichEditor;
import com.example.silence.xiyang_10.RichEditor.TakePhotoUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;

/**
 * Created by Silence on 2018/3/27.
 */

public class MyEditClass extends AppCompatActivity {
    CoordinatorLayout mCoordinatorLayout;
    ViewGroup mRoot;
    private int imgQuality = 20;//保存图片的质量,默认为20%（即压缩率为80%）
    private List<EditorBean> editorList = new CopyOnWriteArrayList<>();//内容列表[并发容器 防止异常(用arraylist可能会异常)]
    private RelativeLayout editor;//编辑器
    private String urlpath;//图片路径
    private DragScaleView dragScaleView;
    private TextView tvInsertImg;//插入图片按钮
    private SharedPreferences prefs;
    @BindView(R.id.reminder_layout)
    LinearLayout reminder_layout;





    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_handedit);
        mRoot = (ViewGroup) findViewById(R.id.CoordinatorLayout01);
        dragScaleView = new DragScaleView(this);
        tvInsertImg = (TextView) findViewById(R.id.tv_custom_edit_insert_img);
        editor = (RelativeLayout) findViewById(R.id.et_custom_editor);


        // 隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (mRoot instanceof CoordinatorLayout) {// 判断mRoot是否属于Coordinatorlayout实例
            mCoordinatorLayout = (CoordinatorLayout) mRoot;
        }
        tvInsertImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(MyEditClass.this)
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
                // 如果是直接从相册获取
                case 1:
                    startPhotoZoom(data.getData());
                    insertImg(data.getData());
                    break;
                // 如果是调用相机拍照时
                case 2:
                    File temp = new File(Environment.getExternalStorageDirectory()
                            + "/xiaoma.jpg");
                    startPhotoZoom(Uri.fromFile(temp));
                    insertImg(data);
                    break;
                // 取得裁剪后的图片
                case 3:
                    if (data != null) {
                        setPicToView(data);
                    }
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
            urlpath = FileUtilcll.saveFile(this, "temphead.jpg", photo);
            System.out.println("----------路径----------" + urlpath);
//            dragScaleView.setImageBitmap(photo);
//            dragScaleView.setBackgroundColor(getResources().getColor(R.color.blue));
//            dragScaleView.setClickable(true);
//            dragScaleView.setScaleType(ImageView.ScaleType.FIT_XY);
//            editor.addView(dragScaleView);
        }
    }
    /**
     * 插入图片
     */
//    public void insertImg() {
//        insertImg(photoUri);
//    }

    /**
     * 插入图片
     *
     * @param data onactivityresult返回的data------拍照完成之后调用这个
     * @desc 用于适配部分手机 onactivityresult返回为空的情况
     */
    public void insertImg(Intent data) {
        Uri uri = null;
        if (data != null && data.getData() != null) {
            uri = data.getData();

        }
        insertImg(uri);
    }

    /**
     * 通过uri插入图片---带(长按即可)删除功能（不能修改--删除再添加即可）
     *
     * @param bitmapUri
     */

    public void insertImg(Uri bitmapUri) {
        final long tag = System.currentTimeMillis();//使用当前时间的毫秒值来标记当前内容，便于删除列表中的记录
        final DragScaleView imageView = new DragScaleView(this);
        //处理长按事件（删除）
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyEditClass.this);
                builder.setTitle("删除");
                builder.setIcon(R.mipmap.delete);
                builder.setMessage("您确定要删除这张图片吗?");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.removeView(imageView);//移除图片
                        removeEditorBeanByTag(tag);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
                return true;
            }
        });
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400);//为了便于预览格式，这里讲图片的高度固定为400dp了，不然图片太大整个页面都是图片，不便于浏览文字
        //imageView.setLayoutParams(params);
        imageView.setClickable(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        /**
         * 下面是对图片进行压缩处理---并且统一复制到sdcard的takephoto文件夹
         */
        String filePath = TakePhotoUtils.getRealFilePathByUri(this, bitmapUri);//图片的真实路径
        try {
            filePath = TakePhotoUtils.saveFile(this, BitmapFactory.decodeFile(filePath), filePath, imgQuality);//压缩图片得到真实路径，imgQuality为图片的质量，按100制，默认图片质量20%（即压缩80%），现在主流手机使用20%最佳---平均下来150k左右
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * imageview对大图显示效果不好，这里对原图进行压缩
         */
        Bitmap images = BitmapFactory.decodeFile(filePath, TakePhotoUtils.getOptions(filePath, 4));//压缩图片的大小，按4倍来压缩
        imageView.setImageBitmap(images);
        editor.addView(imageView);//添加到编辑器中
        editorList.add(new EditorBean(ContentType.IMG, Uri.fromFile(new File(filePath)).toString(), tag));//添加到列表中
    }

    /**
     * 移除对象---按tag
     *
     * @param tag
     */
    private void removeEditorBeanByTag(long tag) {
        for (EditorBean editorBean : editorList) {
            if (editorBean.getTag() == tag) {
                editorList.remove(editorBean);
                break;
            }
        }
    }
}
