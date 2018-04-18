package com.example.silence.xiyang_10.RichEditor;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cuiweiyou.numberpickerdialog.NumberPickerDialog;
import com.example.silence.xiyang_10.MainActivity;
import com.example.silence.xiyang_10.MyEditClass;
import com.example.silence.xiyang_10.R;
import com.larswerkman.lobsterpicker.LobsterPicker;
import com.larswerkman.lobsterpicker.sliders.LobsterShadeSlider;

/**
 * Created by Silence on 2018/4/17.
 */

public class ModelText extends MyText {
    private static int contentSize;//内容字体大小
    private static int contentColor = Color.GRAY;//内容字体颜色

    public ModelText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ModelText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ModelText(Context context) {
        super(context);
    }
//    private void init(){
//        contentColor = getCurrentTextColor();
//        contentSize = (int)getTextSize();
//        final InputDialog updateDialog = new InputDialog(getContext());
//        updateDialog.getEdit().setTextColor(getCurrentTextColor());
//        updateDialog.getEdit().setTextSize(getTextSize());
//        updateDialog.setNegativeButton("取消", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateDialog.dismiss();
//                updateDialog.clearText();
//            }
//        });
//
//        updateDialog.setColorButton("颜色", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                LayoutInflater inflater = ((MainActivity)getContext()).getLayoutInflater();
//                View colorView = inflater.inflate(R.layout.dialog_color, null);
//                final LobsterPicker lobsterPicker = (LobsterPicker) colorView.findViewById(R.id.lobsterPicker);
//                LobsterShadeSlider shadeSlider = (LobsterShadeSlider) colorView.findViewById(R.id.shadeSlider);
//
//                lobsterPicker.addDecorator(shadeSlider);
//                lobsterPicker.setColorHistoryEnabled(true);
//                lobsterPicker.setHistory(contentColor);
//                lobsterPicker.setColor(contentColor);
//
//                new AlertDialog.Builder(getContext())
//                        .setView(colorView)
//                        .setTitle("Choose Color")
//                        .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                contentColor = lobsterPicker.getColor();
//                                updateDialog.getEdit().setTextColor(contentColor);
//                                setTextColor(contentColor);
//                            }
//                        })
//                        .setNegativeButton("CLOSE", null)
//                        .show();
//            }
//        });
//
//        updateDialog.setSizeButton("字号", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new NumberPickerDialog(
//                        getContext(),
//                        new NumberPicker.OnValueChangeListener() {
//                            @Override
//                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                                contentSize = newVal;
//                                updateDialog.getEdit().setTextSize(contentSize);
//                                setTextSize(contentSize);
//                            }
//                        },
//                        60, // 最大值
//                        10, // 最小值
//                        20) // 默认值
//                        .setCurrentValue(contentSize) // 更新默认值
//                        .show();
//            }
//        });
//
//        updateDialog.setPositiveButton("确定", new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                String content = updateDialog.getContent();
//                updateDialog.clearText();
//                updateDialog.dismiss();
//                if (TextUtils.isEmpty(content)) {
//                    ((RelativeLayout)getParent()).removeView(ModelText.this);
//                    removeEditorBeanByTag(tag);
//                    ((RelativeLayout)getParent()).getTag();
//                    return;
//                }
//                //String befor = (type == ContentType.CONTENT) ? "    " : "";
//                tvContent.setText(content);//修改的是内容,就空两格
//                for (EditorBean editorBean : editorList) {
//                    if (editorBean.getTag() == tag) {
//                        editorBean.setContent(content);
//                        break;
//                    }
//                }
//            }
//        });
//
//        tvContent.setTextSize(contentSize);
//        tvContent.setTextColor(contentColor);
//
//
//        /**
//         * 长按就删除
//         */
//        tvContent.setOnClickListener(new View.OnClickListener() {
//            int left = tvContent.getLeft();
//            int top = tvContent.getTop();
//            @Override
//            public void onClick(View v) {
//                long secondTime = System.currentTimeMillis();
//                // 判断每次点击的事件间隔是否符合连击的有效范围
//                // 不符合时，有可能是连击的开始，否则就仅仅是单击
//                if (secondTime - firstTime <= interval) {
//                    ++count;
//                } else {
//                    count = 1;
//                }
//                // 延迟，用于判断用户的点击操作是否结束
//                delay(0,tag,left,top);
//                left = tvContent.getLeft();
//                top = tvContent.getTop();
//                firstTime = secondTime;
//
//            }
//        });
//        // 点击事件结束后的事件处理
//        handler_text = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                TextView content = parent.findViewWithTag(msg.obj);
//                if (count == 1) {
//                    if(Math.abs(content.getLeft()-msg.arg1)<5&&Math.abs(content.getTop()-msg.arg2)<5) {// 允许单击抖动
//                        updateDialog.show(ContentType.CONTENT);
//                        updateDialog.setText(content.getText().toString().replace("    ", ""));
//                    }
//                } else if (count > 1) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                    builder.setTitle("删除");
//                    builder.setIcon(R.mipmap.delete);
//                    builder.setMessage("您确定要删除  " + content.getText().toString() + "  吗?");
//                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            editor.removeView(content);
//                            removeEditorBeanByTag(tag);
//                        }
//                    });
//                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });
//                    builder.create().show();
//                }
//                delayTimer.cancel();
//                count = 0;
//                super.handleMessage(msg);
//            }
//
//        };
//
//    }
}
