package com.graymonkey.demo.settings.preferenceview;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.Switch;


/**
 * Created by neal on 1/29/18.
 * Like SwitchPreference
 */

public class SwitchSettingItemView extends SettingItemView {
    private Listener mListener = new Listener();

    private class Listener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!callChangeListener(isChecked)) {
                // Listener didn't like it, change it back.
                // CompoundButton will make sure we don't recurse.
                buttonView.setChecked(!isChecked);
                return;
            }

            //persist
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mConte);
            String key = getKey();
            sp.edit().putBoolean(key,isChecked).apply();

        }
    }
    public SwitchSettingItemView(@NonNull Context context) {
        this(context,null,0);
    }

    public SwitchSettingItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwitchSettingItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View checkableView = findViewById(R.id.widget_switch);
        if (checkableView != null && checkableView instanceof Checkable) {
            if (checkableView instanceof Switch) {
                final Switch switchView = (Switch) checkableView;
                switchView.setOnCheckedChangeListener(null);
            }

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            String key = getKey();

            boolean checked = sp.getBoolean(key,(boolean)mDefaultValue);
            ((Checkable) checkableView).setChecked(checked);

            if (checkableView instanceof Switch) {
                final Switch switchView = (Switch) checkableView;
                switchView.setOnCheckedChangeListener(mListener);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getBoolean(index,false);
    }
}
