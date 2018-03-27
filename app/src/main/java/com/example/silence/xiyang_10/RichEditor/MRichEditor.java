package com.example.silence.xiyang_10.RichEditor;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.silence.xiyang_10.R;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 简单富文本编辑器MRichEditor
 * Created by HDL on 2016/9/29.
 */
public class MRichEditor extends RelativeLayout {
    private static final String TAG = "MyRichEditor";//tag
    private static int contentSize = 16;//内容字体大小
    private static int contentColor = Color.GRAY;//内容字体颜色
    private static int titleSize = 18;//标题字体大小
    private static int titleColor = Color.BLACK;//标题字体颜色
    private TextView tvInsertImg;//插入图片按钮
    private TextView tvInsertPhoto;//插入拍照图片按钮
    private TextView tvPreview;//保存按钮
    private TextView tvInsertContent;//插入内容按钮
    private TextView tvInsertTitle;//插入标题按钮
    private RelativeLayout editor;//编辑器面板-----思想就是添加addview子控件
    private Activity mActivity;//用于启动拍照
    private Context context;//上下文对象
    private InputDialog dialog;//内容、标题输入框
    private Uri photoUri;//拍照图片的按钮
    private StringBuilder html;//生成的html文件
    private String baseImgUrl;//图片存放的路径
    private String htmlTitle = "";//html的标题
    private List<EditorBean> editorList = new CopyOnWriteArrayList<>();//内容列表[并发容器 防止异常(用arraylist可能会异常)]
    private List<String> imgPath;//图片路径集合
    private int imgQuality = 20;//保存图片的质量,默认为20%（即压缩率为80%）
    private TitleType titleType = TitleType.H3;//标题类型
    private String titleTypeStr = "h3";//标题类型的字符串


    public MRichEditor(Context context) {
        this(context, null);
    }

    public MRichEditor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MRichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        mActivity = (Activity) context;
        initView();//初始化视图
        initInputDialog();//初始化输入对话框
        initListener();//初始化监听器
    }

    /**
     * 初始化视图
     */
    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_custom_eidt, this);
        editor = (RelativeLayout) view.findViewById(R.id.et_custom_editor);
        tvInsertContent = (TextView) view.findViewById(R.id.tv_custom_edit_insert_content);
        tvInsertTitle = (TextView) view.findViewById(R.id.tv_custom_edit_insert_title);
        tvInsertImg = (TextView) view.findViewById(R.id.tv_custom_edit_insert_img);
        tvInsertPhoto = (TextView) view.findViewById(R.id.tv_custom_edit_insert_photo);
        tvPreview = (TextView) view.findViewById(R.id.tv_custom_edit_insert_preview);
    }


    /**
     * 初始化监听器
     */
    private void initListener() {
//        tvInsertImg.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                photoUri = TakePhotoUtils.getGalleryImg(mActivity, CamaraRequestCode.CAMARA_GET_IMG);//相册选择图片
//
//            }
//        });
        tvInsertPhoto.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View v) {
                photoUri = TakePhotoUtils.takePhotoAndroid6(mActivity);//拍照
            }
        });
        tvInsertContent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.show(ContentType.CONTENT);//弹出输入内容的对话框
            }
        });
        tvInsertTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show(ContentType.TITLE);//弹出输入标题的对话框
            }
        });

    }

    /**
     * 初始化输入对话框
     */
    private void initInputDialog() {
        dialog = new InputDialog(context);
        dialog.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (dialog.getType()) {
                    case CONTENT:
                        insertContent(contentSize, contentColor, ContentType.CONTENT);
                        break;
                    case TITLE:
                        insertContent(titleSize, titleColor, ContentType.TITLE);
                        break;
                    case IMG:
                        break;
                }
                dialog.dismiss();
                dialog.clearText();
            }
        });
        dialog.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog.clearText();
            }
        });
    }

    /**
     * 创建/拼接html字符串
     */
    public String createHtmlStr() {
        html = new StringBuilder("");//String拼接不出长字符，用可变长度的String
        //构造html文件
        html.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "\t<head>\n" +
                "\t\t<meta charset=\"UTF-8\">\n" +
                "\t\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=0.5, maximum-scale=2.0, user-scalable=yes\" />\n" +//这里可以使html自动铺满手机屏幕
                "\t\t<title>" + htmlTitle + "</title>\n" +
                "\t\t<style>\n" +
                "\t\t\tbody {\n" +
                "\t\t\t\twidth: 100%;\n" +
                "\t\t\t\theight: 100%;\n" +
                "\t\t\t\tmargin:0px;\n" +
                "\t\t\t}\n" +
                "\t\t\timg {\n" +
                "\t\t\t\tmax-width: 100%;\n" +//使图片刚好铺满屏幕，不超出屏幕
                "\t\t\t}" +
                "\t\t</style>\n" +
                "\t</head>\n" +
                "\n" +
                "\t<body>\t");
        //根据编辑器列表中的内容拼接相应的标签
        for (EditorBean editorBean : editorList) {
            switch (editorBean.getType()) {
                case CONTENT:
                    html.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;" + editorBean.getContent() + "</p>\n");//拼接内容，自动空两格
                    break;
                case TITLE:
                    html.append("<" + titleTypeStr + ">" + editorBean.getContent() + "</" + titleTypeStr + ">\n");//拼接标题---->默认h3
                    break;
                case IMG:
                    html.append("<center><img  src='" + baseImgUrl + "/" + TakePhotoUtils.getImageName(TakePhotoUtils.getImageAbsolutePath(mActivity, Uri.parse(editorBean.getContent()))) + "' /></center><br/>\n");//插入图片，图片的路径（服务器url）由调用这指定，图片名自动生成
                    break;
            }
        }
        html.append("</body>\n" +
                "</html>");

        return html.toString();
    }

    /**
     * 插入图片
     */
    public void insertImg() {
        insertImg(photoUri);
    }

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
        if (uri == null) {
            if (photoUri != null) {
                uri = photoUri;
            }
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
        final ImageView imageView = new ImageView(context);
        //处理长按事件（删除）
        imageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400);//为了便于预览格式，这里讲图片的高度固定为400dp了，不然图片太大整个页面都是图片，不便于浏览文字
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        /**
         * 下面是对图片进行压缩处理---并且统一复制到sdcard的takephoto文件夹
         */
        String filePath = TakePhotoUtils.getRealFilePathByUri(context, bitmapUri);//图片的真实路径
        try {
            filePath = TakePhotoUtils.saveFile(context, BitmapFactory.decodeFile(filePath), filePath, imgQuality);//压缩图片得到真实路径，imgQuality为图片的质量，按100制，默认图片质量20%（即压缩80%），现在主流手机使用20%最佳---平均下来150k左右
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

    /**
     * 插入内容,标题
     *
     * @param size
     * @param color
     * @param type
     */
    private void insertContent(int size, final int color, final ContentType type) {
        final long tag = System.currentTimeMillis();//使用当前的时间做标记----标记的作用就是要知道哪条是哪条,删除的时候好操作
        final MyText tvContent = new MyText(context);
        /**
         *初始化修改对话框--------之所以写在这里 是因为要对象序列化--局部有效原则----如果弄成全局的,那么tvContent永远是最新一个--删除就会出错哦
         */
        final InputDialog updateDialog = new InputDialog(context);
        updateDialog.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
                updateDialog.clearText();
            }
        });
        updateDialog.setPositiveButton("确定", new OnClickListener() {

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
                String befor = (type == ContentType.CONTENT) ? "    " : "";
                tvContent.setText(befor + content);//修改的是内容,就空两格
                for (EditorBean editorBean : editorList) {
                    if (editorBean.getTag() == tag) {
                        editorBean.setContent(content);
                        break;
                    }
                }
            }
        });

        tvContent.setTextSize(size);
        tvContent.setTextColor(color);
        /**
         * 单击就修改
         */
        tvContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.show(ContentType.CONTENT);
                updateDialog.setText(tvContent.getText().toString().replace("    ", ""));
            }
        });
        /**
         * 长按就删除
         */
        tvContent.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("删除");
                builder.setIcon(R.mipmap.delete);
                builder.setMessage("您确定要删除  " + tvContent.getText().toString() + "  吗?");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.removeView(tvContent);
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


        //内容就空两格
        String contentStr = (type == ContentType.CONTENT) ? "    " + dialog.getContent() : dialog.getContent();
        tvContent.setText(contentStr);
        editor.addView(tvContent);//添加到编辑器视图中
        editorList.add(new EditorBean(type, contentStr, tag));//添加到编辑器列表中
    }

    /**
     * 设置内容显示的大小,默认18
     *
     * @param size
     */
    public void setContentSize(int size) {
        this.contentSize = size;
    }

    /**
     * 设置标题显示的大小,默认22
     *
     * @param size
     */
    public void setTitleSize(int size) {
        this.titleSize = size;
    }

    /**
     * 设置内容的颜色,默认为灰色
     *
     * @param color
     */
    public void setContentColor(int color) {
        this.contentColor = color;
    }

    /**
     * 设置内容的颜色,默认为灰色
     *
     * @param color
     */
    public void setContentColor(String color) {
        this.contentColor = Color.parseColor(color);
    }

    /**
     * 设置内容的颜色,默认为黑色
     *
     * @param color
     */
    public void setTitleColor(int color) {
        this.titleColor = color;
    }

    /**
     * 设置内容的颜色,默认为黑色
     *
     * @param color
     */
    public void setTitleColor(String color) {
        this.titleColor = Color.parseColor(color);
    }

    /**
     * 对外提供修改dialog样式的接口
     *
     * @return
     */
    public InputDialog getDialog() {
        return dialog;
    }

    /**
     * 获取生成的html内容
     *
     * @return
     */
    public String getHtmlStr() {
        return html.toString();
    }

    /**
     * 获取html文件
     *
     * @param filePath
     * @return
     */
    public File getHtmlFile(String filePath) {
        File file = new File(filePath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(html.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 获取html的输入流
     *
     * @return
     */
    public InputStream getHtmlStream() {
        return new ByteArrayInputStream(html.toString().getBytes());
    }

    /**
     * 设置图片存放的路径
     *
     * @param baseImgUrl
     */
    public void setServerImgDir(String baseImgUrl) {
        this.baseImgUrl = baseImgUrl;
    }

    /**
     * 获取图片的路径,方便用户上传
     *
     * @return
     */
    public List<String> getImgPath() {
        imgPath = new CopyOnWriteArrayList<>();
        for (EditorBean editorBean : editorList) {
            if (editorBean.getType() == ContentType.IMG) {
                imgPath.add(TakePhotoUtils.getImageAbsolutePath(mActivity, Uri.parse(editorBean.getContent())));
            }
        }
        return imgPath;
    }

    /**
     * 设置html的标题
     *
     * @param htmlTitle
     */
    public void setHtmlTitle(String htmlTitle) {
        this.htmlTitle = htmlTitle;
    }

    /**
     * 设置保存按钮的文本
     *
     * @param text
     */
    public void setSaveBtnText(CharSequence text) {
        tvPreview.setText(text);
    }

    /**
     * 设置预览按钮的图片
     *
     * @param drawable
     */
    public void setPreviewBtnImageResource(Drawable drawable) {
        tvPreview.setCompoundDrawables(null, drawable, null, null);
    }

    /**
     * 设置预览按钮的点击事件
     *
     * @param clickListener
     */
    public void setOnPreviewBtnClickListener(OnClickListener clickListener) {
        tvPreview.setOnClickListener(clickListener);
    }

    /**
     * 设置预览按钮是否可见
     *
     * @param visibility
     */
    public void setPreviewBtnVisibility(int visibility) {
        tvPreview.setVisibility(visibility);
    }

    /**
     * 获取用户输入
     *
     * @return
     */
    public List<EditorBean> getEditorList() {
        return editorList;
    }

    /**
     * 设置拍摄图片的质量0-100;100表示原图
     *
     * @param imgQuality
     */
    public void setImgQuality(int imgQuality) {
        this.imgQuality = imgQuality;
    }

    /**
     * 标题类型(如，h1,h2,h3,h4,h5,h6)------默认h3
     *
     * @param titleType
     */
    public void setTitleType(TitleType titleType) {
        this.titleType = titleType;
        titleTypeStr = TitleTypeUtils.valueOf(titleType);
    }
}
