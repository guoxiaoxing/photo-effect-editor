package com.guoxiaoxing.editor.activity.paint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;

import com.guoxiaoxing.editor.AppConstant;
import com.guoxiaoxing.editor.R;
import com.guoxiaoxing.editor.activity.BaseActivity;
import com.guoxiaoxing.editor.process.paint.PaintInfo;
import com.guoxiaoxing.editor.process.paint.PaintView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PaintActivity extends BaseActivity {

    private static final int PAINT_WIDTH = 30;

    private String mSelectPhoto;

    @Bind(R.id.pv_canvas)
    PaintView mPaintView;

    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/DrawAPicture";
    private File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        ButterKnife.bind(this);
        handleIntent();
        setupView();
        setupData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        mSelectPhoto = intent.getStringExtra(AppConstant.SELECTED_PHOTO);
    }

    private void setupView() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int windowWidth = displayMetrics.widthPixels;

        PaintInfo.PaintWidth = PAINT_WIDTH;
        PaintInfo.PaintColor = Color.WHITE;
        mPaintView.setPenWidth(PAINT_WIDTH);
        mPaintView.setColor(Color.WHITE);

        save();
    }

    private void setupData() {
        if (TextUtils.isEmpty(mSelectPhoto)) {
            return;
        }
        Bitmap selectBitmap = BitmapFactory.decodeFile(mSelectPhoto);
        mPaintView.setBackgroundBitmap(selectBitmap);
    }

    private void save() {
        dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
            Log.e(PATH, dir.mkdirs() + "");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                moveTaskToBack(true);
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                // redo
                mPaintView.ReDoORUndo(true);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                // undo
                mPaintView.ReDoORUndo(false);
                break;
            case KeyEvent.KEYCODE_BACK:
                super.onKeyDown(keyCode, event);
                break;
        }
        return true;
    }
}
