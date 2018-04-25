package com.example.silence.xiyang_10;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.silence.xiyang_10.models.OnDrawChangedListener;
import com.example.silence.xiyang_10.models.SketchView;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.feio.android.checklistview.interfaces.Constants;
import it.feio.android.checklistview.utils.AlphaManager;

/**
 * Created by Silence on 2018/4/25.
 */

public class SketchActivity extends AppCompatActivity implements OnDrawChangedListener {


    ImageView stroke;
    ImageView eraser;
    SketchView mSketchView;
    ImageView undo;
    ImageView redo;
    ImageView erase;
    private int seekBarStrokeProgress, seekBarEraserProgress;
    private View popupLayout, popupEraserLayout;
    private ImageView strokeImageView, eraserImageView;
    private int size;
    private ColorPicker mColorPicker;
    private int oldColor;




    @SuppressWarnings("unchecked")

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sketch);
        //getMainActivity().setNavigationOnClickListener(v -> getActivity().onBackPressed());
        mSketchView = (SketchView) findViewById(R.id.drawing);
        mSketchView.setOnDrawChangedListener(this);
        stroke = (ImageView) findViewById(R.id.sketch_stroke);
        eraser = (ImageView) findViewById(R.id.sketch_eraser);
        undo = (ImageView) findViewById(R.id.sketch_undo);
        redo = (ImageView) findViewById(R.id.sketch_redo);
        erase = (ImageView) findViewById(R.id.sketch_erase);

        // Show the Up button in the action bar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            // getMainActivity().getSupportActionBar().setTitle(R.string.title_activity_sketch);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        stroke.setOnClickListener(v -> {
            if (mSketchView.getMode() == SketchView.STROKE) {
                showPopup(v, SketchView.STROKE);
            } else {
                mSketchView.setMode(SketchView.STROKE);
                AlphaManager.setAlpha(eraser, 0.4f);
                AlphaManager.setAlpha(stroke, 1f);
            }
        });

        AlphaManager.setAlpha(eraser, 0.4f);
        eraser.setOnClickListener(v -> {
            if (mSketchView.getMode() == SketchView.ERASER) {
                showPopup(v, SketchView.ERASER);
            } else {
                mSketchView.setMode(SketchView.ERASER);
                AlphaManager.setAlpha(stroke, 0.4f);
                AlphaManager.setAlpha(eraser, 1f);
            }
        });

        undo.setOnClickListener(v -> mSketchView.undo());

        redo.setOnClickListener(v -> mSketchView.redo());

        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForErase();
            }

            private void askForErase() {
                new MaterialDialog.Builder(SketchActivity.this)
                        .content("erase_sketch")
                        .positiveText("confirm")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                mSketchView.erase();
                            }
                        })
                        .build().show();
            }
        });


        // Inflate the popup_layout.xml
        LayoutInflater inflater = getLayoutInflater();
        popupLayout = inflater.inflate(R.layout.popup_sketch_stroke, null);
        // And the one for eraser
        LayoutInflater inflaterEraser = (LayoutInflater) getLayoutInflater();
        popupEraserLayout = inflaterEraser.inflate(R.layout.popup_sketch_eraser, null);

        // Actual stroke shape size is retrieved
        strokeImageView = (ImageView) popupLayout.findViewById(R.id.stroke_circle);
        final Drawable circleDrawable = getResources().getDrawable(R.drawable.circle);
        size = circleDrawable.getIntrinsicWidth();
        // Actual eraser shape size is retrieved
        eraserImageView = (ImageView) popupEraserLayout.findViewById(R.id.stroke_circle);
        size = circleDrawable.getIntrinsicWidth();

        setSeekbarProgress(SketchView.DEFAULT_STROKE_SIZE, SketchView.STROKE);
        setSeekbarProgress(SketchView.DEFAULT_ERASER_SIZE, SketchView.ERASER);

        // Stroke color picker initialization and event managing
        mColorPicker = (ColorPicker) popupLayout.findViewById(R.id.stroke_color_picker);
        mColorPicker.addSVBar((SVBar) popupLayout.findViewById(R.id.svbar));
        mColorPicker.addOpacityBar((OpacityBar) popupLayout.findViewById(R.id.opacitybar));
        mColorPicker.setOnColorChangedListener(mSketchView::setStrokeColor);
        mColorPicker.setColor(mSketchView.getStrokeColor());
        mColorPicker.setOldCenterColor(mSketchView.getStrokeColor());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                save();
               onBackPressed();
                break;
            default:
                Log.e(Constants.TAG, "Wrong element choosen: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }


    public void save() {
        Bitmap bitmap = mSketchView.getBitmap();
        if (bitmap != null) {

            try {
                Intent intent = getIntent();

                Uri uri = intent.getExtras().getParcelable(MediaStore.EXTRA_OUTPUT);
                File bitmapFile = new File(uri.getPath());
                FileOutputStream out = new FileOutputStream(bitmapFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();

                if (bitmapFile.exists()) {
                    Intent back = new Intent();
                    back.putExtra("URI",uri);
                    setResult(RESULT_OK,back);
                } else {
                    //getActivity().showMessage(R.string.error, ONStyle.ALERT);
                    Toast.makeText(this,"uri is null",Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e(Constants.TAG, "Error writing sketch image data", e);
            }
        }
    }


    // The method that displays the popup.
    private void showPopup(View anchor, final int eraserOrStroke) {

        boolean isErasing = eraserOrStroke == SketchView.ERASER;

        oldColor = mColorPicker.getColor();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Creating the PopupWindow
        PopupWindow popup = new PopupWindow(this);
        popup.setContentView(isErasing ? popupEraserLayout : popupLayout);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.setOnDismissListener(() -> {
            if (mColorPicker.getColor() != oldColor)
                mColorPicker.setOldCenterColor(oldColor);
        });

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets (transformed
        // dp to pixel to support multiple screen sizes)
        popup.showAsDropDown(anchor);

        // Stroke size seekbar initialization and event managing
        SeekBar mSeekBar;
        mSeekBar = (SeekBar) (isErasing ? popupEraserLayout
                .findViewById(R.id.stroke_seekbar) : popupLayout
                .findViewById(R.id.stroke_seekbar));
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // When the seekbar is moved a new size is calculated and the new shape
                // is positioned centrally into the ImageView
                setSeekbarProgress(progress, eraserOrStroke);
            }
        });
        int progress = isErasing ? seekBarEraserProgress : seekBarStrokeProgress;
        mSeekBar.setProgress(progress);
    }


    protected void setSeekbarProgress(int progress, int eraserOrStroke) {
        int calcProgress = progress > 1 ? progress : 1;

        int newSize = Math.round((size / 100f) * calcProgress);
        int offset = (size - newSize) / 2;
        Log.v(Constants.TAG, "Stroke size " + newSize + " (" + calcProgress + "%)");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(newSize, newSize);
        lp.setMargins(offset, offset, offset, offset);
        if (eraserOrStroke == SketchView.STROKE) {
            strokeImageView.setLayoutParams(lp);
            seekBarStrokeProgress = progress;
        } else {
            eraserImageView.setLayoutParams(lp);
            seekBarEraserProgress = progress;
        }

        mSketchView.setSize(newSize, eraserOrStroke);
    }


    @Override
    public void onDrawChanged() {
        // Undo
        if (mSketchView.getPaths().size() > 0)
            AlphaManager.setAlpha(undo, 1f);
        else
            AlphaManager.setAlpha(undo, 0.4f);
        // Redo
        if (mSketchView.getUndoneCount() > 0)
            AlphaManager.setAlpha(redo, 1f);
        else
            AlphaManager.setAlpha(redo, 0.4f);
    }



}
