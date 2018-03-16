package com.example.silence.xiyang_10;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Silence on 2018/3/16.
 */

public class MineFragment extends Fragment {
    public MineFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_mine, container, false);// 将布局加载到碎片实例中
    }
}
