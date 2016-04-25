package com.guoxiaoxing.editor;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.guoxiaoxing.editor.utils.GLog;
import com.guoxiaoxing.editor.widget.BaseToolObject;
import com.guoxiaoxing.editor.widget.ToolStructureBuilder;

import java.util.List;

public class AppContext extends Application {

    private static Context mContext;

    public AppContext() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        AppConfigs.getInstance().editorFeatures = buildEditorTools();
        GLog.setLogMode(GLog.LogMode.VERBOSE);

        setupParameter();
        setupLibrary();
    }

    public static Context getAppContext() {
        return mContext;
    }

    private List<BaseToolObject> buildEditorTools() {
        ToolStructureBuilder tb = new ToolStructureBuilder(this);
        return tb.build();
    }

    private void setupParameter() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        AppConfigs.getInstance().deviceWidth = metrics.widthPixels;
        AppConfigs.getInstance().deviceHeight = metrics.heightPixels;
    }

    private void setupLibrary() {
        Fresco.initialize(this);
    }
}
