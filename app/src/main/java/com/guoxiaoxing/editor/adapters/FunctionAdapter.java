package com.guoxiaoxing.editor.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guoxiaoxing.editor.AppConstant;
import com.guoxiaoxing.editor.R;
import com.guoxiaoxing.editor.activity.effect.EffectActivity;
import com.guoxiaoxing.editor.activity.paint.PaintActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author: guoxiaoxing
 * Email: guoxiaoxingv@163.com
 * Site: https://github.com/guoxiaoxing
 * Date: 16/4/22 上午11:13
 * Function: main function adapter
 * <p/>
 * Modification history:
 * Date                 Author              Version             Description
 * ------------------------------------------------------------------------------
 * 16/4/22 上午11:13      guoxiaoxing          1.0           main function adapter
 */
public class FunctionAdapter extends RecyclerView.Adapter<FunctionAdapter.ViewHolder> {

    private Activity mActivity;
    private String mSelectPhoto;
    private String mEditPhoto;

    public FunctionAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_function, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        switch (position) {
            //编辑
            case 0:
                holder.mTvFunction.setText("编辑");
                break;
            //增强
            case 1:
                holder.mTvFunction.setText("增强");
                break;
            //特效
            case 2:
                holder.mTvFunction.setText("特效");
                break;
            //边框
            case 3:
                holder.mTvFunction.setText("边框");
                break;
            //魔幻笔
            case 4:
                holder.mTvFunction.setText("魔幻笔");
                break;
            //马赛克
            case 5:
                holder.mTvFunction.setText("马赛克");
                break;
            //贴纸
            case 6:
                holder.mTvFunction.setText("贴纸");
                break;
            //文字
            case 7:
                holder.mTvFunction.setText("文字");
                break;
            //背景虚化
            case 8:
                holder.mTvFunction.setText("背景虚化");
                break;
            default:
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (holder.getAdapterPosition()) {
                    //编辑
                    case 0:
                        holder.mTvFunction.setText("编辑");
                        startEdit();
                        break;
                    //增强
                    case 1:
                        holder.mTvFunction.setText("增强");
                        break;
                    //特效
                    case 2:
                        holder.mTvFunction.setText("特效");
                        break;
                    //边框
                    case 3:
                        holder.mTvFunction.setText("边框");
                        break;
                    //魔幻笔
                    case 4:
                        holder.mTvFunction.setText("魔幻笔");
                        startPaint();
                        break;
                    //马赛克
                    case 5:
                        holder.mTvFunction.setText("马赛克");
                        break;
                    //贴纸
                    case 6:
                        holder.mTvFunction.setText("贴纸");
                        break;
                    //文字
                    case 7:
                        holder.mTvFunction.setText("文字");
                        break;
                    //背景虚化
                    case 8:
                        holder.mTvFunction.setText("背景虚化");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 9;
    }


    public void setPhoto(String selectPhoto, String editPhoto) {
        mSelectPhoto = selectPhoto;
        mEditPhoto = editPhoto;
    }

    private void startEdit() {
        Intent editIntent = new Intent(mActivity, EffectActivity.class);
        editIntent.putExtra(AppConstant.SELECTED_PHOTO, mSelectPhoto);
        editIntent.putExtra(AppConstant.OUTPUT_PATH, mEditPhoto);
        mActivity.startActivityForResult(editIntent, AppConstant.REQUEST_CODE_EDIT_ACTIVITY);
    }

    private void startPaint() {
        Intent paintIntent = new Intent(mActivity, PaintActivity.class);
        paintIntent.putExtra(AppConstant.SELECTED_PHOTO, mSelectPhoto);
        paintIntent.putExtra(AppConstant.OUTPUT_PATH, mEditPhoto);
        mActivity.startActivityForResult(paintIntent, AppConstant.REQUEST_CODE_EDIT_ACTIVITY);
    }

    private void startText() {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_function)
        TextView mTvFunction;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}  