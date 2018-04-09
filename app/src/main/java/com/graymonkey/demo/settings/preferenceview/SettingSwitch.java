package com.graymonkey.demo.settings.preferenceview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Switch;

import java.lang.reflect.Field;

/**
 * Created by neal on 1/29/18.
 * 原生Switch控件会将tracker宽度拉伸为thumb宽度的2倍
 * 所以需要反射防止拉伸
 */

public class SettingSwitch extends Switch {
    public SettingSwitch(Context context) {
        super(context);
    }

    public SettingSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //TODO 反射耗性能，可参考原生控件的实现，自己写一个Switch控件
        try {
            //反射后Switch控件的整体宽度依旧是thumb的2倍，mSwitchWidth指的是滑块的轨迹宽度
            Field mSwitchWidth = Switch.class.getDeclaredField("mSwitchWidth");
            Field mTracker = Switch.class.getDeclaredField("mTrackDrawable");
            mTracker.setAccessible(true);
            mSwitchWidth.setAccessible(true);
            Drawable trackDrawable = (Drawable) mTracker.get(this);
            int trackWidth = trackDrawable.getIntrinsicWidth();
            mSwitchWidth.setInt(this,trackWidth);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
