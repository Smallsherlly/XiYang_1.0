package com.example.silence.xiyang_10;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

/**
 * Created by Silence on 2018/3/8.
 */

public class MyWelcomeActivity extends WelcomeActivity {
    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.colorAccent)
                .page(new TitlePage(R.mipmap.hand3,
                        "Title")
                )
                .page(new BasicPage(R.drawable.ic_launcher_background,
                        "Header",
                        "More text.")
                        .background(R.color.colorPrimary)
                )
                .page(new BasicPage(R.drawable.ic_launcher_background,
                        "Lorem ipsum",
                        "dolor sit amet.")
                )
                .swipeToDismiss(true)
                .build();
    }
}
