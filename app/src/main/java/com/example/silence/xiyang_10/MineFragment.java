package com.example.silence.xiyang_10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sackcentury.shinebuttonlib.ShineButton;

/**
 * Created by Silence on 2018/3/16.
 * 这是“我的”模块的碎片类
 */

public class MineFragment extends Fragment {
    private int count;
    public MineFragment(){
        count = 0;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_mine, container, false);// 将布局加载到碎片实例中
    }
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState){
        Button button_change = (Button) getView().findViewById(R.id.change_touxiang);
        final RoundImageButton roundImageView = (RoundImageButton) getView().findViewById(R.id.round_touxiang);
        roundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(),"你点击了头像",Toast.LENGTH_SHORT).show();
            }
        });
        button_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count == 0){
                    roundImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_changetouxiang));
                    count++;
                }else if(count == 1){
                    roundImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_touxiang));
                    count++;
                }else if(count == 2){
                    roundImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_touxiang2));
                    count = 0;
                }

            }
        });

    }
    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final BaseActivity activity = (BaseActivity) getActivity();
        ShineButton shineButton = (ShineButton) getView().findViewById(R.id.shine_button);
        shineButton.init(activity);
        shineButton.setChecked(true);
    }
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);

        final float roundPx = pixels;

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;

    }
}
