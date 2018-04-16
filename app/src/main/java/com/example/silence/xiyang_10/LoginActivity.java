package com.example.silence.xiyang_10;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stephentuso.welcome.WelcomeHelper;

/**
 * Created by Silence on 2018/3/16.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edt_username;
    private EditText edt_password;
    private Button go;
    private CardView cv;
    private FloatingActionButton fab;
    WelcomeHelper welcomeScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        //setListener();
        go.setOnClickListener(this);
        fab.setOnClickListener(this);
        welcomeScreen = new WelcomeHelper(this, MyWelcomeActivity.class);
        welcomeScreen.show(savedInstanceState);
    }

    private void initView() {
        edt_username = findViewById(R.id.et_username);
        edt_password = findViewById(R.id.et_password);
        go = findViewById(R.id.bt_go);
        fab = findViewById(R.id.fab);
    }

    @Override
    public void onClick(View v) {
        String username=edt_username.getText().toString();
        String password=edt_password.getText().toString();

        switch(v.getId()){
            case R.id.bt_go:
                doget(username,password);
                break;
            case R.id.fab:
                reget();
        }
    }
    private void doget(final String username, final String password) {


        new Thread(new Runnable() {

            @Override
            public void run() {
                Explode explode = new Explode();
                explode.setDuration(500);

                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);
                final String state=NetUilts.loginOfGet(username, password);
                //if(state == null)


                runOnUiThread(new Runnable() {//执行任务在主线程中
                    @Override
                    public void run() {//就是在主线程中操作
                        if(state.equals("登录成功")){
                            Toast.makeText(LoginActivity.this, state, Toast.LENGTH_SHORT).show();
                            ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);
                            Intent i2 = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(i2, oc2.toBundle());
                        }else{
                            Toast.makeText(LoginActivity.this, "账号或密码错误!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();

    }

    private void reget() {

        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, fab, fab.getTransitionName());
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class), options.toBundle());
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        fab.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }
}
