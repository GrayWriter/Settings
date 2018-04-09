package com.graymonkey.demo.settings.preferenceview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tct.launcher.R;

/**
 * Created by neal on 1/27/18.
 * 用法与Preference组件相同,不过全部是View
 */

public class SettingItemView extends FrameLayout {
    protected ImageView mIconView;
    protected ImageView mIconArrow;
    protected TextView mTitleView;
    protected TextView mSummaryView;
    private int mLayoutResId = R.layout.custom_preference;
    private int mWidgetLayoutResId;
    protected Object mDefaultValue;
    private ImageView mUnreadMsg;
    private AnimatorSet mAnimatorSet;
    //波纹圆半径
    private float mRadius;
    private float mMaxRadius;
    private Paint mPaint;
    private boolean isAnimating =false;
    //View 的Id无法保证唯一性，删除id，再重新添加id，再打包则id存在变更的可能
    private String mKey;
    private int mIconResId;
    private CharSequence mTitle;
    private int mTitleRes;
    private CharSequence mSummary;
    private OnPreferenceChangeListener mOnChangeListener;
    /**动画的重复次数*/
    private int mRepeatCount =2;
    /**动画已经重复的次数*/
    private int mAnimateCount;
    public SettingItemView(@NonNull Context context) {
        this(context,null,0);
    }

    public SettingItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs,com.android.internal.R.styleable.Preference);
        for (int i = a.getIndexCount() - 1; i >= 0; i--) {
            int attr = a.getIndex(i);
            switch (attr) {
                case com.android.internal.R.styleable.Preference_icon:
                    mIconResId = a.getResourceId(com.android.internal.R.styleable.Preference_icon,0);
                    break;
                case com.android.internal.R.styleable.Preference_key:
                    mKey = a.getString(attr);
                    break;
                case com.android.internal.R.styleable.Preference_title:
                    mTitleRes = a.getResourceId(attr, 0);
                    mTitle = a.getText(attr);
                    break;
                case com.android.internal.R.styleable.Preference_summary:
                    mSummary = a.getText(attr);
                    break;
                case com.android.internal.R.styleable.Preference_layout:
                    mLayoutResId = a.getResourceId(attr, mLayoutResId);
                    break;
                case com.android.internal.R.styleable.Preference_widgetLayout:
                    mWidgetLayoutResId = a.getResourceId(attr, mWidgetLayoutResId);
                    break;
                case com.android.internal.R.styleable.Preference_defaultValue:
                    mDefaultValue = onGetDefaultValue(a,attr);
                    break;
                default:
            }
        }
        a.recycle();

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(mLayoutResId,this);
        mIconView = (ImageView) findViewById(android.R.id.icon);
        mTitleView = (TextView) findViewById(android.R.id.title);
        mSummaryView = (TextView) findViewById(android.R.id.summary);
        mIconArrow = (ImageView) findViewById(R.id.icon_arrow);
        mUnreadMsg = (ImageView) findViewById(R.id.unread_msg) ;

        mIconView.setImageResource(mIconResId);
        mTitleView.setText(mTitle);
        if (!TextUtils.isEmpty(mSummary)){
        mSummaryView.setText(mSummary);
        } else {
            mSummaryView.setVisibility(GONE);
        }

        if (mWidgetLayoutResId != 0){
            mIconArrow.setVisibility(GONE);
            LinearLayout widgetFrame =(LinearLayout) findViewById(android.R.id.widget_frame);
            inflater.inflate(mWidgetLayoutResId,widgetFrame);
            widgetFrame.setVisibility(VISIBLE);
        }



    }

    private void initPaint(){
        if (mPaint != null){
            return;
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.GRAY);
    }


    private void initAnimator(){
        if (mAnimatorSet != null){
            return;
        }
        //动画默认持续时间是300ms
        ValueAnimator alphaAnimator = ValueAnimator.ofInt(0,mPaint.getAlpha());
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int alpha = (int)valueAnimator.getAnimatedValue();
                mPaint.setAlpha(alpha);
                invalidate();
            }
        });

        ValueAnimator alphaDismiss = ValueAnimator.ofInt(mPaint.getAlpha(),0);
        alphaDismiss.setDuration(1000);
        alphaDismiss.setStartDelay(700);
        alphaDismiss.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int alpha = (int)valueAnimator.getAnimatedValue();
                mPaint.setAlpha(alpha);
                invalidate();
            }
        });

        //圆的内接矩形
        mMaxRadius=(float) Math.sqrt(getHeight()*getHeight()+0.3*getWidth()*0.3*getWidth());
        ValueAnimator radiusAnimator = ValueAnimator.ofFloat(0,mMaxRadius);
        radiusAnimator.setDuration(1000);
        radiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mRadius = (float)valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(radiusAnimator).with(alphaDismiss).after(alphaAnimator);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimating = true;
                mAnimateCount++;
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating =false;
                mRadius =0;
                //重复2次动画
                if (mAnimateCount >= mRepeatCount){
                    mAnimateCount =0;
                } else {
                    startRippleAnimation();
                }
            }
        });
    }

    /**
     * 设置波纹动画的重复次数，默认为2次
     * @param repeatCount
     */
    public void setRepeatCount(int repeatCount){
        mRepeatCount = repeatCount;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        if (isAnimating){
            canvas.drawColor(mPaint.getColor());
            canvas.drawCircle(getWidth()/2,getHeight()/2,mRadius,mPaint);
        }
        super.onDraw(canvas);

    }

    public void startRippleAnimation(){
        if (isAnimating){
            return;
        }
        initPaint();
        initAnimator();
        mAnimatorSet.start();
    }

    public void cancelRippleAnimation(){
        mAnimatorSet.cancel();
    }

    /**
     * 设置波纹颜色
     * @param color
     */
    public void setRippleColor(int color){
        initPaint();
        mPaint.setColor(color);
    }

    /**
     * 是否显示未读的消息，右上角的小红点
     * @param show
     */
    public void showUnreadMsg(boolean show){
        if (show){
            mUnreadMsg.setVisibility(VISIBLE);
        }else {
            mUnreadMsg.setVisibility(GONE);
        }
    }

    /**
     * Call this method after the user changes the preference, but before the
     * internal state is set. This allows the client to ignore the user value.
     *
     * @param newValue The new value of this Preference.
     * @return True if the user value should be set as the preference
     *         value (and persisted).
     */
    protected boolean callChangeListener(Object newValue) {
        return mOnChangeListener == null || mOnChangeListener.onPreferenceChange(this, newValue);
    }
    public interface OnPreferenceChangeListener {
        /**
         * Called when a Preference has been changed by the user. This is
         * called before the state of the Preference is about to be updated and
         * before the state is persisted.
         *
         * @param view The changed SettingItemView.
         * @param newValue The new value of the Preference.
         * @return True to update the state of the Preference with the new value.
         */
        boolean onPreferenceChange(SettingItemView view, Object newValue);
    }
    public void setOnPreferenceChangeListener(OnPreferenceChangeListener listener){
        mOnChangeListener = listener;
    }
    public String getKey(){
        if (TextUtils.isEmpty(mKey)){
            throw new RuntimeException("android:key can not be null or empty");
        }
        return mKey;
    }

    /**
     * Called when a Preference is being inflated and the default value
     * attribute needs to be read. Since different Preference types have
     * different value types, the subclass should get and return the default
     * value which will be its value type.
     * <p>
     * For example, if the value type is String, the body of the method would
     * proxy to {@link TypedArray#getString(int)}.
     *
     * @param a The set of attributes.
     * @param index The index of the default value attribute.
     * @return The default value of this preference type.
     */
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return null;
    }

}


