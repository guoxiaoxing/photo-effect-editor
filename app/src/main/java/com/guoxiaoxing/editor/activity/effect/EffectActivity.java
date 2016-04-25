package com.guoxiaoxing.editor.activity.effect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.guoxiaoxing.editor.AppConfigs;
import com.guoxiaoxing.editor.AppConstant;
import com.guoxiaoxing.editor.R;
import com.guoxiaoxing.editor.activity.BaseActivity;
import com.guoxiaoxing.editor.adapters.EditorListToolsAdapter;
import com.guoxiaoxing.editor.asynctasks.DoTransformationAsyncTask;
import com.guoxiaoxing.editor.asynctasks.EditorSaveResultAsyncTask;
import com.guoxiaoxing.editor.asynctasks.OpenBitmapAsyncTask;
import com.guoxiaoxing.editor.process.effect.AbstractOptimizeTransformation;
import com.guoxiaoxing.editor.process.effect.ITransformation;
import com.guoxiaoxing.editor.utils.AsyncTaskUtils;
import com.guoxiaoxing.editor.utils.GLog;
import com.guoxiaoxing.editor.utils.StorageUtils;
import com.guoxiaoxing.editor.utils.ViewUtils;
import com.guoxiaoxing.editor.widget.BaseToolObject;
import com.guoxiaoxing.editor.widget.EffectToolObject;
import com.guoxiaoxing.editor.widget.OptimizeToolObject;
import com.guoxiaoxing.editor.widget.TouchImageView;

import java.util.List;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.AdapterView.OnItemClickListener;
import it.sephiroth.android.library.widget.HListView;

/**
 * Author: guoxiaoxing
 * Email: guoxiaoxingv@163.com
 * Site: https://github.com/guoxiaoxing
 * Date: 16/4/22 上午11:01
 * Function: photo editor
 *
 * Modification history:
 * Date                 Author              Version             Description
 * --------------------------------------------------------------------------
 * 16/4/22 上午11:01      guoxiaoxing          1.0              photo editor
 */
public class EffectActivity extends BaseActivity {


    private String mSelectPhoto;
    private String outputPath;
    private TextView btnApply;
    private TextView btnBack;
    private TouchImageView imgPreview;

    private ProgressDialog processDialog;
    private OpenBitmapAsyncTask openTask;
    private Bitmap bmThumb;
    private Bitmap bmSource;
    private Bitmap bmProcessed;
    private boolean canUndo;
    private ITransformation transformation;
    private DoTransformationAsyncTask transTask;

    private ViewFlipper toolContainer;
    private ViewGroup toolContentView;
    private ViewGroup toolView;
    private HListView listBaseTools;
    private HListView listEffectTools;
    private SeekBar toolSeekBar;
    private List<BaseToolObject> currentChildrenTools;
    private EditorState editorState;

    private static final int BASE_TOOL_PANEL = 0;
    private static final int EFFECT_TOOL_PANEL = 1;
    private static final int OPTIMIZE_TOOL_PANEL = 2;

    private enum EditorState {
        APPLYABLE, DONEABLE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_editor);

        handleIntent();
        setupView();

        openTask = new OpenBitmapAsyncTask.Builder(this, mSelectPhoto)
                .setProgress(processDialog)
                .setImageView(imgPreview)
                .setBitmapOpenedListener(new OpenBitmapAsyncTask.OnBitmapOpenedListener() {
                    @Override
                    public void onBitmapOpened(Bitmap bm) {
                        EffectActivity.this.bmSource = bm;
                    }
                })
                .build();
        AsyncTaskUtils.executeTask(openTask);

        //Open Thumb
        OpenBitmapAsyncTask loadThumbTask = new OpenBitmapAsyncTask.Builder(this, mSelectPhoto)
                .setRequestSize(150, 150)
                .setBitmapOpenedListener(new OpenBitmapAsyncTask.OnBitmapOpenedListener() {
                    @Override
                    public void onBitmapOpened(Bitmap bm) {
                        bmThumb = bm;
                        renderTools();
                    }
                }).build();
        AsyncTaskUtils.executeTask(loadThumbTask);
    }

    @Override
    public void onBackPressed() {
        if (toolContainer.getDisplayedChild() == BASE_TOOL_PANEL) {
            super.onBackPressed();
        } else {
            btnApply.setText(getString(R.string.editor_btn_done_text));
            editorState = EditorState.DONEABLE;
            toolContainer.setDisplayedChild(BASE_TOOL_PANEL);
            if (transTask != null && !transTask.isCancelled()) {
                transTask.cancel(true);
            }
            currentChildrenTools = AppConfigs.getInstance().editorFeatures;
            imgPreview.setImageBitmap(bmSource);
        }
    }


    public Bitmap getThumbnail() {
        return bmThumb;
    }

    public ITransformation getTransformation() {
        return this.transformation;
    }

    private DoTransformationAsyncTask.OnPerformedListener onPerformedListener = new DoTransformationAsyncTask.OnPerformedListener() {
        @Override
        public void onPerformed(Bitmap bm) {
            if (bmProcessed != null && !bmProcessed.isRecycled()) {
                bmProcessed.recycle();
                bmProcessed = null;
            }
            bmProcessed = bm;
            imgPreview.setImageBitmap(bmProcessed);
            editorState = EditorState.APPLYABLE;
            btnApply.setText(getString(R.string.editor_apply_button_text));
        }
    };

    public void doTransform(ITransformation transform) {

        this.transformation = transform;
        if (bmSource == null) {
            transTask = new DoTransformationAsyncTask(mSelectPhoto, transform, processDialog, onPerformedListener);
            transTask.execute();
        } else {
            transTask = new DoTransformationAsyncTask(bmSource, transform, processDialog, onPerformedListener);
            transTask.execute();
        }
    }

    private void handleIntent(){
        Intent receiveIntent = getIntent();
        String receiveAction = receiveIntent.getAction();
        String receiveType = receiveIntent.getType();
        Bundle data = getIntent().getExtras();
        if (receiveAction != null && receiveAction.equals(Intent.ACTION_SEND)) {
            if (receiveType.startsWith("image/*") || receiveType.startsWith("*/*")) {
                if (data != null && data.containsKey(Intent.EXTRA_STREAM)) {
                    Uri receiveUri = (Uri) data.get(Intent.EXTRA_STREAM);
                    mSelectPhoto = StorageUtils.getPathFromUri(receiveUri, this);
                }
            }
        } else if (receiveAction != null
                && (receiveAction.equals(Intent.ACTION_VIEW)
                || receiveAction.equals(Intent.ACTION_EDIT)
                || receiveAction.equals(Intent.ACTION_PICK))) {
            Uri receiveUri = receiveIntent.getData();
            if (receiveUri != null) {
                mSelectPhoto = StorageUtils.getPathFromUri(receiveUri, this);
            }
        } else {
            mSelectPhoto = data.getString(AppConstant.SELECTED_PHOTO);
        }

        Intent dataIntent = getIntent();
        outputPath = dataIntent.getStringExtra(AppConstant.OUTPUT_PATH);
    }

    private void setupView() {
        toolContainer = (ViewFlipper) findViewById(R.id.controller);
        toolView = (ViewGroup) toolContainer.findViewById(R.id.tool_view);
        toolContentView = (ViewGroup) findViewById(R.id.tool_detail);
        toolContainer.setDisplayedChild(0);
        imgPreview = (TouchImageView) findViewById(R.id.imgPreview);
        btnApply = (TextView) findViewById(R.id.btnApply);
        btnApply.setOnClickListener(onClick);

        btnBack = (TextView) findViewById(R.id.btnBack);
        ViewUtils.setupFont(this, (ViewGroup) findViewById(R.id.header), AppConfigs.getInstance().TYPE_FACE);
        btnBack.setOnClickListener(onClick);
        processDialog = new ProgressDialog(this);
        processDialog.setMessage(getString(R.string.editor_process_dialog_message));
        processDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        processDialog.setIndeterminate(true);
    }

    private void renderTools() {
        //Hiển thị HorizontalListView, ListView này gồm các BaseToolObject Level 1
        toolContainer.setDisplayedChild(0);
        listBaseTools = (HListView) toolView.findViewById(R.id.tool_listView);
        currentChildrenTools = AppConfigs.getInstance().editorFeatures;
        EditorListToolsAdapter listToolsAdapter = new EditorListToolsAdapter(this, AppConfigs.getInstance().editorFeatures);
        listBaseTools.setAdapter(listToolsAdapter);
        listBaseTools.setOnItemClickListener(toolItemClick);

        listEffectTools = (HListView) findViewById(R.id.content_listView);
        listEffectTools.setOnItemClickListener(toolItemClick);

        toolSeekBar = (SeekBar) findViewById(R.id.toolSeekBar);
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnApply:
                    if (toolContainer.getDisplayedChild() != BASE_TOOL_PANEL && editorState.equals(EditorState.APPLYABLE)) {
                        //APPLY PRESSED
                        btnApply.setText(getString(R.string.editor_btn_done_text));
                        toolContainer.setDisplayedChild(BASE_TOOL_PANEL);
                        currentChildrenTools = AppConfigs.getInstance().editorFeatures;
                        editorState = EditorState.DONEABLE;
                        if (bmSource != null && !bmSource.isRecycled() && bmProcessed != null) {
                            bmSource.recycle();
                            bmSource = null;
                        }
                        if (bmProcessed != null) {
                            bmSource = bmProcessed.copy(bmProcessed.getConfig(), true);
                            transformation = null;
                        }
                    } else {
                        GLog.v("EDITOR", "DONEABLE");
                        //DONE PRESSED
                        processDialog.setMessage(getString(R.string.editor_saving_progress_dialog));
                        EditorSaveResultAsyncTask saveResultAsyncTask = new EditorSaveResultAsyncTask(EffectActivity.this,
                                bmSource, outputPath, new EditorSaveResultAsyncTask.OnSaveCompletedListener() {
                            @Override
                            public void onSaveCompleted() {
                                processDialog.dismiss();
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(AppConstant.OUTPUT_PATH, outputPath);
                                if (EffectActivity.this.getParent() == null) {
                                    setResult(RESULT_OK, resultIntent);
                                } else {
                                    getParent().setResult(RESULT_OK, resultIntent);
                                }
                                EffectActivity.this.finish();
                            }
                        }, processDialog);
                        saveResultAsyncTask.execute();
                    }
                    break;

                case R.id.btnBack:
                    AlertDialog confirm = new AlertDialog.Builder(EffectActivity.this)
                            .setMessage("Are you sure to close editor?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    EffectActivity.this.finish();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    confirm.show();
                    break;
            }
        }
    };

    private OnItemClickListener toolItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BaseToolObject clickedToolObject = currentChildrenTools.get(i);
            if (clickedToolObject instanceof EffectToolObject) {
                if (clickedToolObject.transform != null) {
                    doTransform(clickedToolObject.transform);
                }
            } else if (clickedToolObject instanceof OptimizeToolObject) {
                toolContainer.setDisplayedChild(OPTIMIZE_TOOL_PANEL);
                AbstractOptimizeTransformation optimizeTransformation = (AbstractOptimizeTransformation) clickedToolObject.transform;
                optimizeTransformation.setupTransformation(toolSeekBar, bmSource, transTask, onPerformedListener);
                //HIDE PROGRESS DIALOG
                // optimizeTransformation.setProgressDialog(processDialog);
            } else {
                toolContainer.setDisplayedChild(EFFECT_TOOL_PANEL);
                currentChildrenTools = clickedToolObject.childrenTools;
                EditorListToolsAdapter adapter = new EditorListToolsAdapter(EffectActivity.this, currentChildrenTools);
                listEffectTools.setAdapter(adapter);
                btnApply.setText(getString(R.string.editor_apply_button_text));
                editorState = EditorState.APPLYABLE;
            }
        }
    };

    @Override
    protected void onStop() {
        if (openTask != null && !openTask.isCancelled()) {
            openTask.cancel(true);
        }
        super.onStop();
    }


}
