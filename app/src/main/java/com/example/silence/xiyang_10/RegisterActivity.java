package com.example.silence.xiyang_10;

/**
 * Created by Silence on 2018/3/16.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    private EditText edt_username;
    private EditText edt_password;
    private EditText edt_qqnum;
    private EditText edt_phonenum;
    private EditText edt_repassword;
    private FloatingActionButton fab;
    private Button go;
    private CardView cvAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ShowEnterAnimation();
        initView();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=edt_username.getText().toString();
                String password=edt_password.getText().toString();
                String qqnum = edt_qqnum.getText().toString();
                String phonenum = edt_phonenum.getText().toString();
                String icon_path = null;
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        final String state=NetUilts.registerOfPost(username, password,qqnum,phonenum,icon_path);

                        runOnUiThread(new Runnable() {//执行任务在主线程中
                            @Override
                            public void run() {//就是在主线程中操作

                                if(state.equals("注册成功")){
                                    animateRevealClose();
                                }
                                Toast.makeText(RegisterActivity.this, state, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void initView() {
        edt_username = findViewById(R.id.et_username);
        edt_password = findViewById(R.id.et_password);
        edt_repassword = findViewById(R.id.et_repeatpassword);
        edt_qqnum = findViewById(R.id.et_qqnumber);
        edt_phonenum = findViewById(R.id.et_phonenumber);
        cvAdd = findViewById(R.id.cv_add);
        fab = findViewById(R.id.fab);
        go = findViewById(R.id.bt_go);
    }

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }
}
