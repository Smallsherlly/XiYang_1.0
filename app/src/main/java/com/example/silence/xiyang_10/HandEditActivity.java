package com.example.silence.xiyang_10;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avocarrot.json2view.DynamicView;
import com.cuiweiyou.numberpickerdialog.NumberPickerDialog;
import com.example.silence.xiyang_10.RichEditor.ContentType;
import com.example.silence.xiyang_10.RichEditor.DragScaleView;
import com.example.silence.xiyang_10.RichEditor.EditorBean;
import com.example.silence.xiyang_10.RichEditor.InputDialog;
import com.example.silence.xiyang_10.RichEditor.MyText;
import com.example.silence.xiyang_10.RichEditor.TakePhotoUtils;
import com.example.silence.xiyang_10.db.DbHelper;
import com.example.silence.xiyang_10.models.HandEdit;
import com.example.silence.xiyang_10.models.StorageHelper;
import com.larswerkman.lobsterpicker.LobsterPicker;
import com.larswerkman.lobsterpicker.sliders.LobsterShadeSlider;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Silence on 2018/4/25.
 */

public class HandEditActivity extends AppCompatActivity {

    private EditText title;
    private TextView insertImg;
    private TextView insertContent;
    private TextView insertSketch;
    private TextView preView;
    private ViewGroup parent;
    private ImageView save;
    private InputDialog dialog;//内容、标题输入框
    private RelativeLayout sampleview;
    private RelativeLayout sampleview2;
    private int imgQuality = 10;//保存图片的质量,默认为20%（即压缩率为80%）
    private List<EditorBean> editorList = new ArrayList<>();//内容列表[并发容器 防止异常(用arraylist可能会异常)]

    private Uri attachmentUri;
    private static int contentSize = 16;//内容字体大小
    private static int contentColor = Color.GRAY;//内容字体颜色
    private long firstTime;
    private int count = 0;
    private TimerTask task;
    private long interval = 500;
    private Handler handler_text;
    private Handler handler_img;
    private Timer delayTimer;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_handedit);
        initUI();
        initInputDialog();//初始化输入对话框
        initListener();//初始化监听器
    }

    public void initUI(){
        // 隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        title = (EditText) findViewById(R.id.et_main_title);
        parent = (ViewGroup) findViewById(R.id.et_custom_editor);
        save = (ImageView) findViewById(R.id.save);
        insertImg = (TextView) findViewById(R.id.tv_custom_edit_insert_img);
        insertContent = (TextView) findViewById(R.id.tv_custom_edit_insert_content);
        insertSketch = (TextView) findViewById(R.id.tv_custom_edit_change_content);
        preView = (TextView) findViewById(R.id.tv_custom_edit_insert_preview);

        preView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preView();
            }
        });
        Intent intent = getIntent();
        if(intent.getStringExtra("Creation")!=null){
            HandEdit hand = DbHelper.getInstance().getHandEdit(Long.valueOf(intent.getStringExtra("Creation")));
            Log.i("creation",intent.getStringExtra("Creation")+hand.getJson_path());
            File json_file = new File(hand.getJson_path());
            jsonCompile(json_file);
        }else{
            jsonCompile(null);
            parent.addView(sampleview);
        }




        reloadBean();
    }

    public void preView(){
        parent.removeView(sampleview);
        jsonCompile(null);
        parent.addView(sampleview);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
                // 如果是直接从相册获取
                case 1:
                    File file = StorageHelper.createNewAttachmentFile(this, ".png");

                    UCrop.of(data.getData(), Uri.fromFile(file)).start(this);
                    break;
                // 如果是调用相机拍照时
                case 2:
                    Toast.makeText(this,"takephoto",Toast.LENGTH_SHORT).show();
                    File temp = new File(Environment.getExternalStorageDirectory()
                            + "/xiaoma.jpg");
                    UCrop.of(data.getData(), Uri.fromFile(new File(getCacheDir(), "pp.png"))).start(this);
                    break;
                case 3:
                    Log.i("Sketch_comback","come here");
                    insertImg((Uri)data.getParcelableExtra("URI"));
                    break;
                case 69:
                    Uri resultUri = UCrop.getOutput(data);
                    insertImg(resultUri);
                    break;
                default:
                    break;

            }

        }
    }

    private String readFile(File file, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = new FileInputStream(file);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line;
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null) isr.close();
                if (fIn != null) fIn.close();
                if (input != null) input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }

    private String readFile(String fileName, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets().open(fileName);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line;
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null) isr.close();
                if (fIn != null) fIn.close();
                if (input != null) input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }

    public  void reloadBean(){
        editorList.clear();
        List<EditorBean> bean = (List<EditorBean>) (JsonCreater.StringToEditorbean(sampleview.getTag().toString()));
        EditorBean mybean = bean.get(0);
        if(bean!=null){
            for (EditorBean editorBean : bean) {
                switch (editorBean.getType()) {
                    case IMG:
                        insertImg(editorBean);
                        break;
                    case CONTENT:
                        insertContent(editorBean,ContentType.CONTENT);
                        break;
                }

            }
        }
    }

    private void initListener() {
        insertContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.show(ContentType.CONTENT);//弹出输入内容的对话框
            }
        });
        insertImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(HandEditActivity.this)
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
        insertSketch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeSketch();
            }
        });

    }
    public File insertNull(){
        final long tag = System.currentTimeMillis();//使用当前的时间做标记----标记的作用就是要知道哪条是哪条,删除的时候好操作
        final MyText tvContent = new MyText(this);
        tvContent.setTag(Long.toString(tag));


        String contentStr = null;
        tvContent.setText(contentStr);
        sampleview.addView(tvContent);//添加到编辑器视图中
        sampleview.removeView(tvContent);
        editorList.add(new EditorBean(ContentType.CONTENT, contentStr, tag));//添加到编辑器列表中
        removeEditorBeanByTag(tag);
        File f = saveJson();
        return f;
    }
    public File saveJson(){
        Intent tent = getIntent();
        String username = tent.getStringExtra("username");
        File file2 = StorageHelper.createNewAttachmentFile(this, username,".json");
        try {
            FileOutputStream outputStream = new FileOutputStream(file2);
            outputStream.write(JsonCreater.createJsonStr(sampleview,editorList).getBytes());
            outputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return file2;
    }

    public void jsonCompile(@Nullable File f){
        JSONObject jsonObject;

        try {
            //jsonObject = new JSONObject(readFile(f,getActivity()));
            if(f == null){
                jsonObject = new JSONObject(readFile("sample.json",this));
            }else{
                jsonObject = new JSONObject(readFile(f,this));
                parent.removeView(sampleview);

                sampleview = (RelativeLayout) DynamicView.createView(this, jsonObject,parent);
                sampleview.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));

                Log.d("welcome","I'm here");
                parent.addView(sampleview);
                reloadBean();
                return;
            }



        } catch (JSONException je) {
            je.printStackTrace();
            jsonObject = null;
        }

        if (jsonObject != null) {

            /* create dynamic view and return the view with the holder class attached as tag */
            sampleview = (RelativeLayout)DynamicView.createView(this, jsonObject,parent);
            /* get the view with id "testClick" and attach the onClickListener */
            //((SampleViewHolder) sampleView.getTag()).clickableView.setOnClickListener(this);

            /* add Layout Parameters in just created view and set as the contentView of the activity */
            sampleview.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
            //setContentView(sampleView);

        } else {
            Log.e("Json2View", "Could not load valid json file");
            Toast.makeText(this,"it's null",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化输入对话框
     */
    private void initInputDialog() {
        dialog = new InputDialog(this);
        dialog.getEdit().setTextColor(contentColor);
        dialog.getEdit().setTextSize(contentSize);
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (dialog.getType()) {
                    case CONTENT:
                        insertContent(ContentType.CONTENT);
                        break;
//                    case TITLE:
//                        insertContent(titleSize, titleColor, ContentType.TITLE);
//                        break;
                    case IMG:
                        break;
                }
                dialog.dismiss();
                dialog.clearText();
            }
        });

        dialog.setColorButton("颜色", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View colorView = inflater.inflate(R.layout.dialog_color, null);
                final LobsterPicker lobsterPicker = (LobsterPicker) colorView.findViewById(R.id.lobsterPicker);
                LobsterShadeSlider shadeSlider = (LobsterShadeSlider) colorView.findViewById(R.id.shadeSlider);

                lobsterPicker.addDecorator(shadeSlider);
                lobsterPicker.setColorHistoryEnabled(true);
                lobsterPicker.setHistory(contentColor);
                lobsterPicker.setColor(contentColor);

                new AlertDialog.Builder(HandEditActivity.this)
                        .setView(colorView)
                        .setTitle("Choose Color")
                        .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                contentColor = lobsterPicker.getColor();
                                dialog.getEdit().setTextColor(contentColor);
                            }
                        })
                        .setNegativeButton("CLOSE", null)
                        .show();
            }

        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog.clearText();
            }
        });
        dialog.setSizeButton("字号", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NumberPickerDialog(
                        HandEditActivity.this,
                        new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                contentSize = newVal;
                                dialog.getEdit().setTextSize(contentSize);
                            }
                        },
                        60, // 最大值
                        10, // 最小值
                        20) // 默认值
                        .setCurrentValue(contentSize) // 更新默认值
                        .show();
            }
        });
    }

    private void insertContent(final ContentType type) {
        final long tag = System.currentTimeMillis();//使用当前的时间做标记----标记的作用就是要知道哪条是哪条,删除的时候好操作
        final MyText tvContent = new MyText(this);
        tvContent.setTag(Long.toString(tag));

        /**
         *初始化修改对话框--------之所以写在这里 是因为要对象序列化--局部有效原则----如果弄成全局的,那么tvContent永远是最新一个--删除就会出错哦
         */
        final InputDialog updateDialog = new InputDialog(this);
        updateDialog.getEdit().setTextColor(contentColor);
        updateDialog.getEdit().setTextSize(contentSize);
        updateDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
                updateDialog.clearText();
            }
        });

        updateDialog.setColorButton("颜色", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                View colorView = inflater.inflate(R.layout.dialog_color, null);
                final LobsterPicker lobsterPicker = (LobsterPicker) colorView.findViewById(R.id.lobsterPicker);
                LobsterShadeSlider shadeSlider = (LobsterShadeSlider) colorView.findViewById(R.id.shadeSlider);

                lobsterPicker.addDecorator(shadeSlider);
                lobsterPicker.setColorHistoryEnabled(true);
                lobsterPicker.setHistory(contentColor);
                lobsterPicker.setColor(contentColor);

                new AlertDialog.Builder(HandEditActivity.this)
                        .setView(colorView)
                        .setTitle("Choose Color")
                        .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                contentColor = lobsterPicker.getColor();
                                updateDialog.getEdit().setTextColor(contentColor);
                                tvContent.setTextColor(contentColor);
                            }
                        })
                        .setNegativeButton("CLOSE", null)
                        .show();
            }
        });

        updateDialog.setSizeButton("字号", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NumberPickerDialog(
                        HandEditActivity.this,
                        new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                contentSize = newVal;
                                updateDialog.getEdit().setTextSize(contentSize);
                                tvContent.setTextSize(contentSize);
                            }
                        },
                        60, // 最大值
                        10, // 最小值
                        20) // 默认值
                        .setCurrentValue(contentSize) // 更新默认值
                        .show();
            }
        });

        updateDialog.setPositiveButton("确定", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = updateDialog.getContent();
                updateDialog.clearText();
                updateDialog.dismiss();
                if (TextUtils.isEmpty(content)) {
                    sampleview.removeView(tvContent);
                    removeEditorBeanByTag(tag);
                    return;
                }
                //String befor = (type == ContentType.CONTENT) ? "    " : "";
                tvContent.setText(content);//修改的是内容,就空两格
                for (EditorBean editorBean : editorList) {
                    if (editorBean.getTag() == tag) {
                        editorBean.setContent(content);
                        break;
                    }
                }
            }
        });

        tvContent.setTextSize(contentSize);
        tvContent.setTextColor(contentColor);


        /**
         * 长按就删除
         */
        tvContent.setOnClickListener(new View.OnClickListener() {
            int left = tvContent.getLeft();
            int top = tvContent.getTop();
            @Override
            public void onClick(View v) {
                long secondTime = System.currentTimeMillis();
                // 判断每次点击的事件间隔是否符合连击的有效范围
                // 不符合时，有可能是连击的开始，否则就仅仅是单击
                if (secondTime - firstTime <= interval) {
                    ++count;
                } else {
                    count = 1;
                }
                // 延迟，用于判断用户的点击操作是否结束
                delay(0,tag,left,top);
                left = tvContent.getLeft();
                top = tvContent.getTop();
                firstTime = secondTime;

            }
        });
        // 点击事件结束后的事件处理
        handler_text = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                TextView content = parent.findViewWithTag(msg.obj);
                if (count == 1) {
                    if(Math.abs(content.getLeft()-msg.arg1)<5&&Math.abs(content.getTop()-msg.arg2)<5) {// 允许单击抖动
                        updateDialog.show(ContentType.CONTENT);
                        updateDialog.setText(content.getText().toString().replace("    ", ""));
                    }
                } else if (count > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HandEditActivity.this);
                    builder.setTitle("删除");
                    builder.setIcon(R.mipmap.delete);
                    builder.setMessage("您确定要删除  " + content.getText().toString() + "  吗?");
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sampleview.removeView(content);
                            removeEditorBeanByTag(tag);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                }
                delayTimer.cancel();
                count = 0;
                super.handleMessage(msg);
            }

        };


        //内容就空两格
        String contentStr = dialog.getContent();
        tvContent.setText(contentStr);
        sampleview.addView(tvContent);//添加到编辑器视图中
        editorList.add(new EditorBean(type, contentStr, tag));//添加到编辑器列表中
    }
    private void insertContent(EditorBean bean, final ContentType type) {
        String tag = Long.toString(bean.getTag());
        final MyText tvContent = sampleview.findViewWithTag(tag);
        contentColor = tvContent.getCurrentTextColor();
        contentSize = (int)tvContent.getTextSize()/2;

        /**
         *初始化修改对话框--------之所以写在这里 是因为要对象序列化--局部有效原则----如果弄成全局的,那么tvContent永远是最新一个--删除就会出错哦
         */
        final InputDialog updateDialog = new InputDialog(this);
        updateDialog.getEdit().setTextColor(contentColor);
        updateDialog.getEdit().setTextSize(contentSize);
        updateDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
                updateDialog.clearText();
            }
        });

        updateDialog.setColorButton("颜色", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                View colorView = inflater.inflate(R.layout.dialog_color, null);
                final LobsterPicker lobsterPicker = (LobsterPicker) colorView.findViewById(R.id.lobsterPicker);
                LobsterShadeSlider shadeSlider = (LobsterShadeSlider) colorView.findViewById(R.id.shadeSlider);

                lobsterPicker.addDecorator(shadeSlider);
                lobsterPicker.setColorHistoryEnabled(true);
                lobsterPicker.setHistory(contentColor);
                lobsterPicker.setColor(contentColor);

                new AlertDialog.Builder(HandEditActivity.this)
                        .setView(colorView)
                        .setTitle("Choose Color")
                        .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                contentColor = lobsterPicker.getColor();
                                updateDialog.getEdit().setTextColor(contentColor);
                                tvContent.setTextColor(contentColor);
                            }
                        })
                        .setNegativeButton("CLOSE", null)
                        .show();
            }
        });

        updateDialog.setSizeButton("字号", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NumberPickerDialog(
                        HandEditActivity.this,
                        new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                contentSize = newVal;
                                updateDialog.getEdit().setTextSize(contentSize);
                                tvContent.setTextSize(contentSize);
                            }
                        },
                        60, // 最大值
                        10, // 最小值
                        20) // 默认值
                        .setCurrentValue(contentSize) // 更新默认值
                        .show();
            }
        });

        updateDialog.setPositiveButton("确定", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = updateDialog.getContent();
                updateDialog.clearText();
                updateDialog.dismiss();
                if (TextUtils.isEmpty(content)) {
                    sampleview.removeView(tvContent);
                    removeEditorBeanByTag(Long.valueOf(tag));
                    return;
                }
                //String befor = (type == ContentType.CONTENT) ? "    " : "";
                tvContent.setText(content);//修改的是内容,就空两格
                for (EditorBean editorBean : editorList) {
                    if (editorBean.getTag() == Long.valueOf(tag)) {
                        editorBean.setContent(content);
                        break;
                    }
                }
            }
        });



        /**
         * 长按就删除
         */
        tvContent.setOnClickListener(new View.OnClickListener() {
            int left = tvContent.getLeft();
            int top = tvContent.getTop();
            @Override
            public void onClick(View v) {

                long secondTime = System.currentTimeMillis();
                // 判断每次点击的事件间隔是否符合连击的有效范围
                // 不符合时，有可能是连击的开始，否则就仅仅是单击
                if (secondTime - firstTime <= interval) {
                    ++count;
                } else {
                    count = 1;
                }
                // 延迟，用于判断用户的点击操作是否结束
                delay(0,Long.valueOf(tag),left,top);
                left = tvContent.getLeft();
                top = tvContent.getTop();
                firstTime = secondTime;

            }
        });
        // 点击事件结束后的事件处理
        handler_text = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                MyText content = parent.findViewWithTag(msg.obj);
                if (count == 1) {
                    if(Math.abs(content.getLeft()-msg.arg1)<5&&Math.abs(content.getTop()-msg.arg2)<5) {// 允许单击抖动
                        updateDialog.show(ContentType.CONTENT);
                        updateDialog.setText(content.getText().toString().replace("    ", ""));
                    }
                } else if (count > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HandEditActivity.this);
                    builder.setTitle("删除");
                    builder.setIcon(R.mipmap.delete);
                    builder.setMessage("您确定要删除  " + content.getText().toString() + "  吗?");
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sampleview.removeView(content);
                            removeEditorBeanByTag(Long.valueOf(tag));
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                }
                delayTimer.cancel();
                count = 0;
                super.handleMessage(msg);
            }

        };

        editorList.add(bean);//添加到编辑器列表中
    }
    public void insertImg(EditorBean bean) {
        final long tag = bean.getTag();//使用当前时间的毫秒值来标记当前内容，便于删除列表中的记录
        final DragScaleView imageView = sampleview.findViewWithTag(Long.toString(bean.getTag()));
        final int left= imageView.getLeft();
        final int top = imageView.getTop();
        Log.i("position",String.valueOf(left)+"+"+String.valueOf(top));
        //处理点击事件（删除）
        imageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                final int left= imageView.getLeft();
                final int top = imageView.getTop();
                Log.i("position",String.valueOf(left)+"+"+String.valueOf(top));
                //imageView.requestFocus();
                long secondTime = System.currentTimeMillis();
                // 判断每次点击的事件间隔是否符合连击的有效范围
                // 不符合时，有可能是连击的开始，否则就仅仅是单击
                if (secondTime - firstTime <= interval) {
                    ++count;
                } else {
                    count = 1;
                }
                // 延迟，用于判断用户的点击操作是否结束
                delay(1,tag,left,top);

                firstTime = secondTime;

            }
        });

        // 点击事件结束后的事件处理
        handler_img = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ImageView imageV = sampleview.findViewWithTag(msg.obj);
                if (count == 1) {final int left= imageView.getLeft();
                    final int top = imageView.getTop();
                    Log.i("positionM",String.valueOf(left)+"+"+String.valueOf(top));
                    if(Math.abs(imageV.getLeft()-msg.arg1)<5&&Math.abs(imageV.getTop()-msg.arg2)<5) {// 允许单击抖动
                        if(imageV.getBackground()!=null){
                            imageV.setBackgroundResource(0);
                        }else{
                            //Bitmap map = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.shape_gray_square_bg);
                            Drawable draw = getResources().getDrawable(R.drawable.shape_gray_square_bg);
                            imageV.setBackground(draw);
                            imageV.setPadding(1,1,1,1);
                        }
                    }
                } else if (count > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HandEditActivity.this);
                    builder.setTitle("删除");
                    builder.setIcon(R.mipmap.delete);
                    builder.setMessage("您确定要删除这张图片吗?");
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sampleview.removeView(imageV);//移除图片
                            removeEditorBeanByTag(tag);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                }
                delayTimer.cancel();
                count = 0;
                super.handleMessage(msg);
            }
        };

       imageView.setFocusable(true);
       imageView.setClickable(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        editorList.add(bean);//添加到列表中
    }
    public void insertImg(Uri bitmapUri) {
        final long tag = System.currentTimeMillis();//使用当前时间的毫秒值来标记当前内容，便于删除列表中的记录
        final DragScaleView imageView = new DragScaleView(this);
        imageView.setTag(Long.toString(tag));
        //处理点击事件（删除）
        imageView.setOnClickListener(new View.OnClickListener(){
            int left= imageView.getLeft();
            int top = imageView.getTop();

            @Override
            public void onClick(View v){

                //imageView.requestFocus();
                long secondTime = System.currentTimeMillis();
                // 判断每次点击的事件间隔是否符合连击的有效范围
                // 不符合时，有可能是连击的开始，否则就仅仅是单击
                if (secondTime - firstTime <= interval) {
                    ++count;
                } else {
                    count = 1;
                }
                // 延迟，用于判断用户的点击操作是否结束
                delay(1,tag,left,top);
                left= imageView.getLeft();
                top = imageView.getTop();
                firstTime = secondTime;

            }
        });

        // 点击事件结束后的事件处理
        handler_img = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ImageView imageV = parent.findViewWithTag(msg.obj);
                if (count == 1) {
                    if(Math.abs(imageV.getLeft()-msg.arg1)<5&&Math.abs(imageV.getTop()-msg.arg2)<5) {// 允许单击抖动
                        if(imageV.getBackground()!=null){
                            imageV.setBackgroundResource(0);
                        }else{
                            //Bitmap map = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.shape_gray_square_bg);
                            Drawable draw = getResources().getDrawable(R.drawable.shape_gray_square_bg);
                            imageV.setBackground(draw);
                            imageV.setPadding(1,1,1,1);
                        }
                    }
                } else if (count > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HandEditActivity.this);
                    builder.setTitle("删除");
                    builder.setIcon(R.mipmap.delete);
                    builder.setMessage("您确定要删除这张图片吗?");
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sampleview.removeView(imageV);//移除图片
                            removeEditorBeanByTag(tag);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                }
                delayTimer.cancel();
                count = 0;
                super.handleMessage(msg);
            }
        };

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);//为了便于预览格式，这里讲图片的高度固定为400dp了，不然图片太大整个页面都是图片，不便于浏览文字
        imageView.setLayoutParams(params);
        imageView.setFocusable(true);
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
        sampleview.addView(imageView);//添加到编辑器中
        editorList.add(new EditorBean(ContentType.IMG, Uri.fromFile(new File(filePath)).toString(), tag));//添加到列表中
    }
    private void takeSketch() {
        File f = StorageHelper.createNewAttachmentFile(this, ".png");
        if (f == null) {
            Toast.makeText(this,"wrong path",Toast.LENGTH_SHORT).show();
            return;
        }

        attachmentUri = Uri.fromFile(f);


        // Fragments replacing
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Bundle b = new Bundle();
        b.putParcelable(MediaStore.EXTRA_OUTPUT, attachmentUri);
        Intent intent = new Intent(this,SketchActivity.class);
        intent.putExtras(b);
        startActivityForResult(intent,3);

    }
// 延迟时间是连击的时间间隔有效范围
    private void delay(int type,long index,int left,int top) {
    if (task != null)
        task.cancel();

    task = new TimerTask() {
        @Override
        public void run() {

            Message message = new Message();
            message.arg1 = left;
            message.arg2 = top;
            message.obj = Long.toString(index);
            // message.what = 0;
            if(type == 1){
                handler_img.sendMessage(message);
            }else if(type == 0){
                handler_text.sendMessage(message);
            }




        }
    };
    delayTimer = new Timer();
    delayTimer.schedule(task, interval);
}

    private void removeEditorBeanByTag(long tag) {
    for (EditorBean editorBean : editorList) {
        if (editorBean.getTag() == tag) {
            editorList.remove(editorBean);
            break;
        }
    }
}
}
