package com.graymonkey.demo.settings.preferenceview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by neal on 1/28/18.
 * 功能类似ListPreference
 */

public class ListSettingItemView extends SettingItemView implements DialogInterface.OnClickListener{
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;

    public ListSettingItemView(@NonNull Context context) {
        this(context,null,0);
    }

    public ListSettingItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ListSettingItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(
                attrs, com.android.internal.R.styleable.ListPreference);
        mEntries = a.getTextArray(com.android.internal.R.styleable.ListPreference_entries);
        mEntryValues = a.getTextArray(com.android.internal.R.styleable.ListPreference_entryValues);
        a.recycle();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String key = getKey();
        int checkedItem = sp.getInt(key,0);
        CharSequence summary = mEntryValues[checkedItem];
        if(!TextUtils.isEmpty(summary)){
            mSummaryView.setText(summary);
            mSummaryView.setVisibility(VISIBLE);
        }

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSingleChoiceDialog(view);
            }
        });
    }

    private void showSingleChoiceDialog(View view) {
        AlertDialog.Builder builder=new AlertDialog.Builder(view.getContext());
        builder.setTitle(mTitleView.getText());
        builder.setNegativeButton(android.R.string.cancel,null);

        /**
         * 设置内容区域为单选列表项
         */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String key = getKey();
        int checkedItem = sp.getInt(key,0);

        builder.setSingleChoiceItems(mEntryValues, checkedItem,this);
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        CharSequence summary = mEntryValues[i];
        if (callChangeListener(summary)){
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = sp.edit();
            String key = getKey();
            editor.putInt(key,i);
            editor.apply();


            if(!TextUtils.isEmpty(summary)){
                mSummaryView.setText(summary);
                mSummaryView.setVisibility(VISIBLE);
            }
        }


        dialogInterface.dismiss();


    }

}
