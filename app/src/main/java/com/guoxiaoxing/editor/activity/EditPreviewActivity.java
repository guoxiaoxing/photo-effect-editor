package com.guoxiaoxing.editor.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.guoxiaoxing.editor.AppConfigs;
import com.guoxiaoxing.editor.AppConstants;
import com.guoxiaoxing.editor.AppContext;
import com.guoxiaoxing.editor.R;
import com.guoxiaoxing.editor.adapters.FunctionAdapter;
import com.guoxiaoxing.editor.asynctasks.OpenBitmapAsyncTask;
import com.guoxiaoxing.editor.dialogs.SavePhotoDialog;
import com.guoxiaoxing.editor.dialogs.ShareIntentsDialog;
import com.guoxiaoxing.editor.utils.GLog;
import com.guoxiaoxing.editor.utils.StorageUtils;
import com.guoxiaoxing.editor.widget.TouchImageView;

import java.io.File;
import java.io.IOException;

public class EditPreviewActivity extends Activity {

    private String mSelectPhoto;
    private String mEditPhoto;
    private Uri selected_uri;

    private Uri output_uri;
    private File saveLocation;

    private TouchImageView prv_Image;
    private ProgressDialog progress;
    private OpenBitmapAsyncTask openTask;

    private RecyclerView mRvFunction;
    private Context mContext;

    private FunctionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = AppContext.getAppContext();
        // Request Fullscreen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.edit_preview_activity);
        setupView();

        // Receive
        Intent receiveIntent = getIntent();
        String receiveAction = receiveIntent.getAction();
        String receiveType = receiveIntent.getType();
        if (receiveAction != null && receiveAction.equals(Intent.ACTION_SEND)) {
            if (receiveType.startsWith("image/")
                    || receiveType.startsWith("*/*")) {
                Bundle receiveExtras = receiveIntent.getExtras();
                if (receiveExtras != null
                        && receiveExtras.containsKey(Intent.EXTRA_STREAM)) {
                    Uri receiveUri = (Uri) receiveExtras
                            .get(Intent.EXTRA_STREAM);
                    mSelectPhoto = StorageUtils.getPathFromUri(receiveUri,
                            this);
                }
            }
        } else if (receiveAction != null
                && (receiveAction.equals(Intent.ACTION_VIEW)
                || receiveAction.equals(Intent.ACTION_EDIT) || receiveAction
                .equals(Intent.ACTION_PICK))) {

            Uri receiveUri = receiveIntent.getData();
            if (receiveUri != null) {
                mSelectPhoto = StorageUtils.getPathFromUri(receiveUri, this);
            }
        } else {
            Bundle bundle = getIntent().getExtras();
            mSelectPhoto = bundle.getString(AppConstants.SELECTED_PHOTO);
        }
        if (!mSelectPhoto.equals(null) && !mSelectPhoto.equals("")) {
            File select_f = new File(mSelectPhoto);
            selected_uri = Uri.fromFile(select_f);
            mEditPhoto = StorageUtils.getCacheDirectory().getPath() + "/"
                    + select_f.getName();
            File edited_f = new File(mEditPhoto);

            output_uri = Uri.fromFile(edited_f);
            openTask = new OpenBitmapAsyncTask.Builder(this, mSelectPhoto)
                    .setImageView(prv_Image)
                    .setRequestSize(AppConfigs.getInstance().deviceWidth, AppConfigs.getInstance().deviceHeight)
                    .setProgress(progress)
                    .build();
            openTask.execute();
            GLog.v("Selected", mSelectPhoto);
        }

        adapter.setPhoto(mSelectPhoto, mEditPhoto);
    }

    private void setupView() {
        prv_Image = (TouchImageView) findViewById(R.id.imgv_preview);
        mRvFunction = (RecyclerView) findViewById(R.id.rv_function);

        //prv_Image.setDisplayType(DisplayType.FIT_TO_SCREEN);
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(
                R.string.loading_dialog_text));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        adapter = new FunctionAdapter(EditPreviewActivity.this);
        LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mRvFunction.setHasFixedSize(true);
        mRvFunction.setLayoutManager(manager);
        mRvFunction.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.REQUEST_CODE_EDIT_ACTIVITY:
                    openTask = new OpenBitmapAsyncTask.Builder(this, mEditPhoto)
                            .setImageView(prv_Image)
                            .setRequestSize(AppConfigs.getInstance().deviceWidth, AppConfigs.getInstance().deviceHeight)
                            .setProgress(progress)
                            .build();
                    openTask.execute();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        GLog.v("EDIT PREVIEW", "ONDESTROY");
        Drawable drawable = prv_Image.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bmDr = (BitmapDrawable) drawable;
            bmDr.getBitmap().recycle();
        }

        // Cancel load image task
        if (openTask != null && !openTask.isCancelled()) {
            openTask.cancel(true);
        }

        super.onDestroy();
    }

    private void save() {
        File editedFile = new File(mEditPhoto);
        if (!editedFile.exists()) {
            try {
                StorageUtils.copyFile(new File(mEditPhoto), editedFile);
            } catch (IOException e) {
                Toast.makeText(EditPreviewActivity.this, "An error has occurred, please try again!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        progress.setMessage(getString(R.string.saving_dialog_text));
        SavePhotoDialog savePhotoDialog = new SavePhotoDialog(EditPreviewActivity.this, mEditPhoto, progress);
        savePhotoDialog.show();
    }

    private void share() {
        String sharePath = null;
        File edited_f = new File(mEditPhoto);
        if (edited_f.exists()) {
            sharePath = mEditPhoto;
        } else {
            sharePath = mSelectPhoto;
        }

        ShareIntentsDialog share = new ShareIntentsDialog.Builder(this)
                .setDialogTitle(
                        getResources().getString(R.string.sharedialog_title))
                .setImagePath(sharePath).build();

        share.show();
    }
}