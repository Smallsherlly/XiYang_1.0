package com.example.silence.xiyang_10;

/**
 * Created by Silence on 2018/3/15.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import butterknife.BindView;


/**
 * Created by Silence on 2018/3/14.
 * 这是新建手账的碎片类
 */

public class HandEditFragment2 extends Fragment {
    CoordinatorLayout mCoordinatorLayout;
    ViewGroup mRoot;
    private SharedPreferences prefs;
    @BindView(R.id.reminder_layout)
    LinearLayout reminder_layout;

    public HandEditFragment2(){

    }


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_handedit, container, false);// 将布局加载到碎片实例中
    }
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }// @Nullable  意味着可以传入null值
    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final BaseActivity activity = (BaseActivity) getActivity();
        prefs = activity.prefs;
        mRoot = (ViewGroup) activity.findViewById(R.id.CoordinatorLayout01);

        if (mRoot instanceof CoordinatorLayout) {// 判断mRoot是否属于Coordinatorlayout实例
            mCoordinatorLayout = (CoordinatorLayout) mRoot;
        }

    }

}

