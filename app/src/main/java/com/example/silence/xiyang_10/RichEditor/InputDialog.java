package com.example.silence.xiyang_10.RichEditor;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.silence.xiyang_10.R;


/**
 * 自定义输入对话框
 * Created by HDL on 2016/9/30.
 */
public class InputDialog {
    private ContentType type;//类型
    private Context context;
    private AlertDialog dialog;
    private TextView tvOk;
    private TextView tvCancle;
    private EditText etContent;
    private TextView tvcolor;

    public InputDialog(Context context) {
        this.context = context;
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_editor, null);
        tvCancle = (TextView) view.findViewById(R.id.tv_dialog_editor_cancel);
        tvOk = (TextView) view.findViewById(R.id.tv_dialog_editor_ok);
        etContent = (EditText) view.findViewById(R.id.et_dialog_editor_content);
        tvcolor = (TextView) view.findViewById(R.id.tv_dialog_editor_color);
        builder.setView(view);
        dialog = builder.create();
    }

    /**
     * 设置确定按钮
     *
     * @param ok
     * @param onClickListener
     */
    public void setPositiveButton(CharSequence ok, View.OnClickListener onClickListener) {
        tvOk.setText(ok);
        tvOk.setOnClickListener(onClickListener);
    }

    public void setColorButton(CharSequence color, View.OnClickListener onClickListener){
        tvcolor.setText(color);
        tvcolor.setOnClickListener(onClickListener);
    }
    /**
     * 设置取消按钮
     *
     * @param cancle
     * @param onClickListener
     */
    public void setNegativeButton(String cancle, View.OnClickListener onClickListener) {
        tvCancle.setText(cancle);
        tvCancle.setOnClickListener(onClickListener);
    }

    /**
     * 获取输入的内容
     *
     * @return
     */
    public String getContent() {
        return etContent.getText().toString();
    }
    public EditText getEdit(){return etContent;}

    /**
     * 设置输入框的提示内容
     *
     * @param hint
     */
    public void setHint(CharSequence hint) {
        etContent.setHint(hint);
    }

    /**
     * 设置输入框的内容
     *
     * @param text
     */
    public void setText(CharSequence text) {
        etContent.setText(text);
        etContent.setSelection(text.length());
    }

    /**
     * 设置输入框显示的行数
     *
     * @param lines
     */
    public void setLines(int lines) {
        etContent.setLines(lines);
    }

    /**
     * 清空输入框的内容
     */
    public void clearText() {
        etContent.setText("");
    }


    /**
     * 显示对话框
     */
    public void show(ContentType type) {
        this.type = type;
        dialog.show();
    }

    public ContentType getType() {
        return type;
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        dialog.dismiss();
    }
}
