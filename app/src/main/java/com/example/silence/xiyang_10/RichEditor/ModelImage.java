package com.example.silence.xiyang_10.RichEditor;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Silence on 2018/4/17.
 */

public class ModelImage extends AppCompatImageView {
    public ModelImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ModelImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ModelImage(Context context) {
        super(context);
        init();
    }
    public void init(){
//        EditorBean editorList = (EditorBean)(((RelativeLayout)getParent()).getTag());
//        Toast.makeText(getContext(),editorList.toString(),Toast.LENGTH_SHORT).show();
    }
}
