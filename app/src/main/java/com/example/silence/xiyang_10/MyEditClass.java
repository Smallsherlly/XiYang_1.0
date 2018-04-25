package com.example.silence.xiyang_10;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avocarrot.json2view.DynamicView;
import com.avocarrot.json2view.DynamicViewId;
import com.cuiweiyou.numberpickerdialog.NumberPickerDialog;
import com.example.silence.xiyang_10.RichEditor.ContentType;
import com.example.silence.xiyang_10.RichEditor.DragScaleView;
import com.example.silence.xiyang_10.RichEditor.EditorBean;
import com.example.silence.xiyang_10.RichEditor.InputDialog;
import com.example.silence.xiyang_10.RichEditor.MRichEditor;
import com.example.silence.xiyang_10.RichEditor.ModelImage;
import com.example.silence.xiyang_10.RichEditor.MyText;
import com.example.silence.xiyang_10.RichEditor.TakePhotoUtils;
import com.example.silence.xiyang_10.db.DbHelper;
import com.example.silence.xiyang_10.models.Attachment;
import com.example.silence.xiyang_10.models.HandEdit;
import com.example.silence.xiyang_10.models.ONStyle;
import com.example.silence.xiyang_10.models.StorageHelper;
import com.kizitonwose.colorpreference.ColorDialog;
import com.kizitonwose.colorpreference.ColorPreference;
import com.kizitonwose.colorpreference.ColorShape;
import com.larswerkman.lobsterpicker.LobsterPicker;
import com.larswerkman.lobsterpicker.sliders.LobsterShadeSlider;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

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
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.Inflater;

import butterknife.BindView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Silence on 2018/3/27.
 */

public class MyEditClass extends Fragment implements ColorDialog.OnColorSelectedListener{
    CoordinatorLayout mCoordinatorLayout;
    ViewGroup mRoot;
    private int imgQuality = 20;//保存图片的质量,默认为20%（即压缩率为80%）
    private List<EditorBean> editorList = new ArrayList<>();//内容列表[并发容器 防止异常(用arraylist可能会异常)]
    private RelativeLayout parent;//编辑器
    private RelativeLayout editor;
    private String urlpath;//图片路径
    private DragScaleView dragScaleView;
    private TextView tvInsertImg;//插入图片按钮
    private long firstTime;
    private int count = 0;
    private TimerTask task;
    private long interval = 500;
    private Handler handler_text;
    private Handler handler_img;
    private Timer delayTimer;
    private InputDialog dialog;//内容、标题输入框
    private static int contentSize = 16;//内容字体大小
    private static int contentColor = Color.GRAY;//内容字体颜色
    private static int titleSize = 18;//标题字体大小
    private static int titleColor = Color.BLACK;//标题字体颜色
    private TextView tvInsertContent;//插入内容按钮
    private TextView tvInsertTitle;//插入标题按钮
    private TextView tvchangecolor;//插入标题按钮
    private SharedPreferences prefs;
    private EditText title;
    @BindView(R.id.reminder_layout)
    LinearLayout reminder_layout;
    private float scale;
    private float preScale = 1;// 默认前一次缩放比例为1
    private int pos_left;
    private int pos_top;
    private int img_pos_left;
    private int img_pos_top;
    private Uri attachmentUri;
    private View view;
    private HandEdit cur_handedit;

    private RelativeLayout sampleview;
    private RelativeLayout re;

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




    @Override
    public void onColorSelected(int newColor, String tag) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        init();// 在onCreate中初始化，可以防止fragment销毁后恢复时在onCreateView中重新初始化，导致视图丢失
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Uri uri=((MainActivity)getActivity()).sketchUri;
        if(uri!=null){
            insertImg(uri);
            ((MainActivity)getActivity()).sketchUri = null;
        }
        return view;
        // inflater.inflate(R.layout.fragment_handedit, container, false);// 将布局加载到碎片实例中
    }
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }// @Nullable  意味着可以传入null值
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

    public void jsonCompile(@Nullable File f){
        JSONObject jsonObject;

        try {
            //jsonObject = new JSONObject(readFile(f,getActivity()));
            if(f == null){
                jsonObject = new JSONObject(readFile("sample.json",((MainActivity)getActivity())));
            }else{
                jsonObject = new JSONObject(readFile(f,((MainActivity)getActivity())));
                parent.removeView(sampleview);

                sampleview = (RelativeLayout)DynamicView.createView(getActivity(), jsonObject);
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
            sampleview = (RelativeLayout)DynamicView.createView(getActivity(), jsonObject,parent);
            /* get the view with id "testClick" and attach the onClickListener */
            //((SampleViewHolder) sampleView.getTag()).clickableView.setOnClickListener(this);

            /* add Layout Parameters in just created view and set as the contentView of the activity */
            sampleview.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
            //setContentView(sampleView);

        } else {
            Log.e("Json2View", "Could not load valid json file");
            Toast.makeText(getActivity(),"it's null",Toast.LENGTH_SHORT).show();
        }
    }

    public void show(Long creation){
        cur_handedit.setCreation(creation);
        HandEdit hand = DbHelper.getInstance().getHandEdit(creation);
        Log.i("creation",Long.toString(creation)+hand.getJson_path());
        File json_file = new File(hand.getJson_path());
        //File test_F = new File("/storage/emulated/0/Android/data/com.example.silence.xiyang_10/files/userdata/asdf/20180424_120140_468.json");
        title.setText(hand.getTitle());
        jsonCompile(json_file);
    }

    public void init() {
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_handedit,null);
        title = (EditText) view.findViewById(R.id.et_main_title);
        mRoot = (ViewGroup) view.findViewById(R.id.CoordinatorLayout01);
        tvInsertImg = (TextView) view.findViewById(R.id.tv_custom_edit_insert_img);
        parent = (RelativeLayout)view.findViewById(R.id.et_custom_editor);
        cur_handedit = new HandEdit();
        String nul = null;
        cur_handedit.setCreation(nul);
        ImageView save = (ImageView) view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"you click save ",Toast.LENGTH_SHORT).show();
                //insertContent(contentColor,contentSize,ContentType.CONTENT);
                File cur_file = insertNull();
                Intent tent = getActivity().getIntent();
                String username = tent.getStringExtra("username");
                final long tag = System.currentTimeMillis();
                HandEdit handedit = new HandEdit();
                handedit.setTitle(title.getText().toString());
                handedit.setAuthor(username);
                handedit.setJson_path(cur_file.getPath());

                if(cur_handedit.getCreation()!=null){
                    handedit.setCreation(cur_handedit.getCreation());
                }

                for(EditorBean bean:editorList){
                    int flag = 0;
                    switch (bean.getType()){
                        case IMG:
                            flag = 1;
                            String cover_path = bean.getContent();
                            handedit.setCover_path(cover_path);
                            Log.i("cover_path",cover_path);
                            break;
                    }
                    if(flag == 1)
                        break;
                }

                DbHelper.getInstance(getContext()).updateHandEdit(handedit,true);
            }
        });
       jsonCompile(null);

        parent.addView(sampleview);
        //reloadBean();


//        editor = (RelativeLayout) view.findViewById(R.id.et_custom_editor);
        tvInsertContent = (TextView) view.findViewById(R.id.tv_custom_edit_insert_content);
        tvchangecolor = (TextView) view.findViewById(R.id.tv_custom_edit_change_content);

        initInputDialog();//初始化输入对话框
        initListener();//初始化监听器
        if (mRoot instanceof CoordinatorLayout) {// 判断mRoot是否属于Coordinatorlayout实例
            mCoordinatorLayout = (CoordinatorLayout) mRoot;
        }
        tvInsertImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(getActivity())
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

    public  void reloadBean(){
        editorList.clear();
        List<EditorBean> bean = (List<EditorBean>) (JsonCreater.StringToEditorbean(sampleview.getTag().toString(),view));
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

    private void takeSketch() {
        MainActivity mainActivity = (MainActivity)getActivity();
        File f = StorageHelper.createNewAttachmentFile(mainActivity, ".png");
        if (f == null) {
            //mainActivity.showMessage("error", ONStyle.ALERT);
            Toast.makeText(getContext(),"wrong path",Toast.LENGTH_SHORT).show();
            return;
        }

        attachmentUri = Uri.fromFile(f);
        // Forces portrait orientation to this fragment only
        //mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Fragments replacing
        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        //mainActivity.animateTransition(transaction, mainActivity.TRANSITION_HORIZONTAL);
        //SketchFragment mSketchFragment = new SketchFragment();
        Bundle b = new Bundle();
        b.putParcelable(MediaStore.EXTRA_OUTPUT, attachmentUri);
//        if (attachment != null) {
//            b.putParcelable("base", attachment.getUri());
//        }
//        mSketchFragment.setArguments(b);
//        transaction.replace(R.id.ViewPager01,mSketchFragment,"Sketch")
//                .addToBackStack("Sketch").commit();
        ((MainActivity) getActivity()).getViewPager().setCurrentItem(4);
        SketchFragment fragment = (SketchFragment) getActivity().getSupportFragmentManager().findFragmentByTag(
                "android:switcher:"+R.id.ViewPager01+":4");// 通过manager获取碎片实例
//        if(fragment!=null)
           fragment.setArguments(b);

    }


    /**
     * 初始化监听器
     */
    private void initListener() {
        tvInsertContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.show(ContentType.CONTENT);//弹出输入内容的对话框
            }
        });
//        tvInsertTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.show(ContentType.TITLE);//弹出输入标题的对话框
//            }
//        });
        tvchangecolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeSketch();
            }
        });

    }

    /**
     * 初始化输入对话框
     */
    private void initInputDialog() {
        dialog = new InputDialog(getContext());
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
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View colorView = inflater.inflate(R.layout.dialog_color, null);
                        final LobsterPicker lobsterPicker = (LobsterPicker) colorView.findViewById(R.id.lobsterPicker);
                        LobsterShadeSlider shadeSlider = (LobsterShadeSlider) colorView.findViewById(R.id.shadeSlider);

                        lobsterPicker.addDecorator(shadeSlider);
                        lobsterPicker.setColorHistoryEnabled(true);
                        lobsterPicker.setHistory(contentColor);
                        lobsterPicker.setColor(contentColor);

                        new AlertDialog.Builder(getContext())
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
                        getContext(),
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

    public void insertNullImage(){
        final long tag = System.currentTimeMillis();//使用当前的时间做标记----标记的作用就是要知道哪条是哪条,删除的时候好操作
        final DragScaleView image = new DragScaleView(getContext());
        image.setTag(Long.toString(tag));

        image.setImageResource(R.drawable.circle);
        String contentStr = "";
        sampleview.addView(image);//添加到编辑器视图中
        sampleview.removeView(image);
        editorList.add(new EditorBean(ContentType.CONTENT, contentStr, tag));//添加到编辑器列表中
        removeEditorBeanByTag(tag);

    }
    public File insertNull(){
        final long tag = System.currentTimeMillis();//使用当前的时间做标记----标记的作用就是要知道哪条是哪条,删除的时候好操作
        final MyText tvContent = new MyText(getContext());
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
    /**
     * 插入内容,标题
     *
     *
     * @param bean
     * @param type
     */

    private void insertContent(EditorBean bean, final ContentType type) {
        String tag = Long.toString(bean.getTag());
        final MyText tvContent = sampleview.findViewWithTag(tag);
        contentColor = tvContent.getCurrentTextColor();
        contentSize = (int)tvContent.getTextSize()/2;

        /**
         *初始化修改对话框--------之所以写在这里 是因为要对象序列化--局部有效原则----如果弄成全局的,那么tvContent永远是最新一个--删除就会出错哦
         */
        final InputDialog updateDialog = new InputDialog(getContext());
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

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View colorView = inflater.inflate(R.layout.dialog_color, null);
                final LobsterPicker lobsterPicker = (LobsterPicker) colorView.findViewById(R.id.lobsterPicker);
                LobsterShadeSlider shadeSlider = (LobsterShadeSlider) colorView.findViewById(R.id.shadeSlider);

                lobsterPicker.addDecorator(shadeSlider);
                lobsterPicker.setColorHistoryEnabled(true);
                lobsterPicker.setHistory(contentColor);
                lobsterPicker.setColor(contentColor);

                new AlertDialog.Builder(getContext())
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
                        getContext(),
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
                    editor.removeView(tvContent);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
    private void insertContent(final ContentType type) {
        final long tag = System.currentTimeMillis();//使用当前的时间做标记----标记的作用就是要知道哪条是哪条,删除的时候好操作
        final MyText tvContent = new MyText(getContext());
        tvContent.setTag(Long.toString(tag));

        /**
         *初始化修改对话框--------之所以写在这里 是因为要对象序列化--局部有效原则----如果弄成全局的,那么tvContent永远是最新一个--删除就会出错哦
         */
        final InputDialog updateDialog = new InputDialog(getContext());
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

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View colorView = inflater.inflate(R.layout.dialog_color, null);
                final LobsterPicker lobsterPicker = (LobsterPicker) colorView.findViewById(R.id.lobsterPicker);
                LobsterShadeSlider shadeSlider = (LobsterShadeSlider) colorView.findViewById(R.id.shadeSlider);

                lobsterPicker.addDecorator(shadeSlider);
                lobsterPicker.setColorHistoryEnabled(true);
                lobsterPicker.setHistory(contentColor);
                lobsterPicker.setColor(contentColor);

                new AlertDialog.Builder(getContext())
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
                        getContext(),
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
                    editor.removeView(tvContent);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("删除");
                    builder.setIcon(R.mipmap.delete);
                    builder.setMessage("您确定要删除  " + content.getText().toString() + "  吗?");
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parent.removeView(content);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
                // 如果是直接从相册获取
                case 1:
                    File file = StorageHelper.createNewAttachmentFile((MainActivity)getContext(), ".png");

                    UCrop.of(data.getData(), Uri.fromFile(file)).start(getContext(),MyEditClass.this);
                    break;
                // 如果是调用相机拍照时
                case 2:
                    Toast.makeText(getActivity(),"takephoto",Toast.LENGTH_SHORT).show();
                    File temp = new File(Environment.getExternalStorageDirectory()
                            + "/xiaoma.jpg");
                    UCrop.of(data.getData(), Uri.fromFile(new File(getActivity().getCacheDir(), "pp.png"))).start(getContext(),MyEditClass.this);
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
    // 保存当前的json文件
    public File saveJson(){
        Intent tent = getActivity().getIntent();
        String username = tent.getStringExtra("username");
        File file2 = StorageHelper.createNewAttachmentFile((MainActivity)getActivity(), username,".json");
        try {
            FileOutputStream outputStream = new FileOutputStream(file2);
            outputStream.write(JsonCreater.createJsonStr(sampleview,editorList).getBytes());
            outputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return file2;
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
    public void insertImg(EditorBean bean) {
        final long tag = bean.getTag();//使用当前时间的毫秒值来标记当前内容，便于删除列表中的记录
        final DragScaleView imageView = sampleview.findViewWithTag(Long.toString(bean.getTag()));

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
                            Drawable draw = getContext().getResources().getDrawable(R.drawable.shape_gray_square_bg);
                            imageV.setBackground(draw);
                            imageV.setPadding(1,1,1,1);
                        }
                    }
                } else if (count > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

//        imageView.setFocusable(true);
        imageView.setClickable(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        editorList.add(bean);//添加到列表中
    }
    /**
     * 通过uri插入图片---带(长按即可)删除功能（不能修改--删除再添加即可）
     *
     * @param bitmapUri
     */

    public void insertImg(Uri bitmapUri) {
        final long tag = System.currentTimeMillis();//使用当前时间的毫秒值来标记当前内容，便于删除列表中的记录
        final DragScaleView imageView = new DragScaleView(getActivity());
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
                            Drawable draw = getContext().getResources().getDrawable(R.drawable.shape_gray_square_bg);
                            imageV.setBackground(draw);
                            imageV.setPadding(1,1,1,1);
                        }
                    }
                } else if (count > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("删除");
                    builder.setIcon(R.mipmap.delete);
                    builder.setMessage("您确定要删除这张图片吗?");
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.removeView(imageV);//移除图片
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
        String filePath = TakePhotoUtils.getRealFilePathByUri(getActivity(), bitmapUri);//图片的真实路径
        try {
            filePath = TakePhotoUtils.saveFile(getActivity(), BitmapFactory.decodeFile(filePath), filePath, imgQuality);//压缩图片得到真实路径，imgQuality为图片的质量，按100制，默认图片质量20%（即压缩80%），现在主流手机使用20%最佳---平均下来150k左右
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
