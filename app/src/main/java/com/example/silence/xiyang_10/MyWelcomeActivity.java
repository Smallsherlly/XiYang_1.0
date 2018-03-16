package com.example.silence.xiyang_10;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

/**
 * Created by Silence on 2018/3/8.
 * 这是引导页的类
 */

public class MyWelcomeActivity extends WelcomeActivity {
    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.colorAccent)
                .page(new TitlePage(R.mipmap.hand3,
                        "全新的无纸手账体验")
                )
                .page(new BasicPage(R.drawable.date,
                        "日历式浏览，井然有序",
                        "梦想计划一览无余.")
                        .background(R.color.colorPrimary)
                )
                .page(new BasicPage(R.drawable.hudong,
                        "好友互动，欢乐加倍",
                        "互赞互爱~.")
                )
                .swipeToDismiss(true)
                .build();
    }
}
