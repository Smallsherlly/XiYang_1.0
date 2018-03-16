package com.example.silence.xiyang_10;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sackcentury.shinebuttonlib.ShineButton;

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
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState){
        Button button_change = (Button) getView().findViewById(R.id.change_touxiang);
        final RoundImageView roundImageView = (RoundImageView) getView().findViewById(R.id.round_touxiang);
        button_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_changetouxiang));
            }
        });

    }
    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final BaseActivity activity = (BaseActivity) getActivity();
        ShineButton shineButton = (ShineButton) getView().findViewById(R.id.shine_button);
        shineButton.init(activity);
    }
}
