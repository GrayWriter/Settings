package com.graymonkey.demo.settings.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;


import com.graymonkey.demo.settings.R;

import java.util.HashMap;

/**
 * Created by neal on 4/4/18.
 * 支持全屏宽度的Dialog
 */

public class UnlockScienceDialog extends Dialog implements View.OnClickListener{
    private View.OnClickListener mPostiveListener;
    private View.OnClickListener mNegtiveListener;
    public UnlockScienceDialog(@NonNull Context context) {
        this(context,R.style.customDialogTheme);
    }

    public UnlockScienceDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public UnlockScienceDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_unlock_science);
        setCancelable(false);
        findViewById(R.id.tv_next).setOnClickListener(this);
        findViewById(R.id.tv_unlock).setOnClickListener(this);
        //设置窗口大小宽度全屏,UI设计风格与google风格不一致,与主题无关
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {

    }

    public void setPositiveButtonClickListener(View.OnClickListener listener){
            mPostiveListener = listener;
    }

    public void setNegtiveButtonClickListener(View.OnClickListener listener){
           mNegtiveListener = listener;
    }
}
