/*
 * Copyright (c) 2016.
 * chinaume@163.com
 */

package com.goav.toggle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

import com.goav.toggle.R.*;

public class ToggleButton extends View {


    private boolean isOpen;

    private float left, top, right, bottom;
    private float mRadius, mBottomRadius;
    private final int DEFAULT_STROKE = 3;
    private final long DEFAULT_DURATION = 300L;
    private float mCenterY, mCenterX;
    private Point mClosePoint, mArcPoint, mOpenPoint;

    private Paint mBackPaint, mButtonPaint, mTextPaint;
    private int mButtonColor = Color.WHITE;
    private int mBorderColor = Color.GRAY;
    private int mOpenColor = Color.GREEN;
    private int mCloseColor = mButtonColor;
    private int mOpenTextColor = Color.WHITE;
    private int mCloseTextColor = Color.BLACK;
    private float mBorderWidth = DEFAULT_STROKE;
    private float mTextSize;
    private CharSequence mOpenText, mCloseText;
    private boolean enable = true;
    private ToggleButtonStateChangeListener mListener = ToggleButtonStateChangeListener.DEFAULT;
    private ValueAnimator mAnimator;
    private boolean isCancle;

    public ToggleButton(Context context) {
        this(context, null);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initAnimator();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    public void initPaint(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, context.getResources().getDisplayMetrics());


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToggleButton, defStyleAttr, defStyleRes);
        final int count = a.getIndexCount();
        for (int index = 0; index < count; index++) {
            int i = a.getIndex(index);

            //not swith
            if (i == styleable.ToggleButton_toggle_color) {
                mButtonColor = a.getColor(i, mButtonColor);

            } else if (i == styleable.ToggleButton_toggle_border_color) {
                mBorderColor = a.getColor(i, mBorderColor);

            } else if (i == styleable.ToggleButton_toggle_border_width) {
                mBorderWidth = a.getDimension(i, mBorderWidth);

            } else if (i == styleable.ToggleButton_toggle_open_color) {
                mOpenColor = a.getColor(i, mOpenColor);

            } else if (i == styleable.ToggleButton_toggle_close_color) {
                mCloseColor = a.getColor(i, mCloseColor);

            } else if (i == styleable.ToggleButton_toggle_open_text) {
                mOpenText = a.getText(i);

            } else if (i == styleable.ToggleButton_toggle_close_text) {
                mCloseText = a.getText(i);

            } else if (i == styleable.ToggleButton_toggle_enable) {
                enable = a.getBoolean(i, true);

            } else if (i == styleable.ToggleButton_toggle_open_textColor) {
                mOpenTextColor = a.getColor(i, mOpenTextColor);

            } else if (i == styleable.ToggleButton_toggle_close_textColor) {
                mCloseTextColor = a.getColor(i, mCloseTextColor);

            } else if (i == styleable.ToggleButton_toggle_textSize) {
                mTextSize = a.getDimension(i, mTextSize);

            }
        }
        a.recycle();

        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setDither(true);
        setToggleColor(mButtonColor);

        mBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackPaint.setDither(true);
        mBackPaint.setAntiAlias(true);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setDither(true);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
//        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);

        setBackgroundColor(Color.TRANSPARENT);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mClosePoint = mArcPoint = mOpenPoint = new Point();
        setPadding(DEFAULT_STROKE, DEFAULT_STROKE, DEFAULT_STROKE, DEFAULT_STROKE);


    }

    public void addToggleButtonStateChangeListener(ToggleButtonStateChangeListener mListener) {
        this.mListener = mListener;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
        setToggleColor(enable ? mButtonColor : ColorUtil.setAlpha(mButtonColor, 40));
        invalidate();
    }

    public void setmTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
        mTextPaint.setTextSize(mTextSize);
    }

    public void setToggleColor(int color) {
        mButtonPaint.setColor(color);// TODO: 17/1/19 15:43
        mButtonPaint.setShadowLayer(mBorderWidth, 0, 0, Color.GRAY);// TODO: 17/1/19 15:43
    }

    public void setmCloseText(CharSequence mCloseText) {
        this.mCloseText = mCloseText;
    }

    public void setmOpenText(CharSequence mOpenText) {
        this.mOpenText = mOpenText;
    }

    public void setmBorderWidth(float mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
    }

    public void setmCloseColor(int mCloseColor) {
        this.mCloseColor = mCloseColor;
    }

    public void setmOpenColor(int mOpenColor) {
        this.mOpenColor = mOpenColor;
    }

    public void setmBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
    }

    public void toggle() {
        toggle(!isOpen);
    }

    /**
     * @param isOpen true open default false
     */
    public void toggle(boolean isOpen) {
        if (isOpen == this.isOpen) return;

        if (isOpen) {
            open();
        } else {
            close();
        }
        this.isOpen = enable ? isOpen : this.isOpen;
    }

    public void open() {
        open(DEFAULT_DURATION);
    }

    public void close() {
        close(DEFAULT_DURATION);
    }

    public void open(long duration) {
        if (!enable) return;
        float lastRight = right - mRadius;
        startAnimation(lastRight, duration, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                changeWithShow((float) animation.getAnimatedValue(), 1.f - animation.getAnimatedFraction());
                postInvalidate();
            }
        });
    }


    public void close(long duration) {
        if (!enable) return;
        float lastRight = left + mRadius;
        startAnimation(lastRight, duration, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                changeWithShow((float) animation.getAnimatedValue(), animation.getAnimatedFraction());
                postInvalidate();
            }
        });
    }

    private void changeWithShow(float mCenterX, float mScale) {//1...0 open   0...1 close
        this.mCenterX = mCenterX;
        invalidateRect(mScale);

        float left = mCenterX - mBottomRadius;
        float right = mCenterX - mBorderWidth + (mClosePoint.r - mCenterX) * mScale;
        float top = mCenterY - mBottomRadius * mScale;
        float bottom = mCenterY + mBottomRadius * mScale;
        invalidateArc(left, top, right, bottom);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        left = getPaddingLeft();
        right = w - getPaddingRight();
        top = getPaddingTop();
        bottom = h - getPaddingBottom();

        //半径
        mRadius = (bottom - top) * 0.5f;
        mBottomRadius = mRadius - mBorderWidth;
        //center
        mCenterX = left + mRadius;
        mCenterY = top + mRadius;

        mClosePoint = new Point(left, top, right, bottom);
        if (!isOpen) {
            invalidateRect(1.0f);
            invalidateArc(mCenterX - mBottomRadius, mCenterY - mBottomRadius, right - mBorderWidth, mCenterY + mBottomRadius);
        }
    }

    private void invalidateArc(float l, float t, float r, float b) {
        mArcPoint = new Point(l, t, r, b);
    }


    private void invalidateRect(float mScale) {
        float scaleXY = mBorderWidth * mScale;
        mOpenPoint = new Point(left + scaleXY, top + scaleXY, right - scaleXY, bottom - scaleXY);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mBackPaint.setColor(mBorderColor);// TODO: 17/1/19 16:06
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setStrokeWidth(mBorderWidth);
        drawRoundRect(canvas, mClosePoint, mRadius, mBackPaint);//绘制关闭边框

        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setColor(mOpenColor);// TODO: 17/1/19 16:06

        drawRoundRect(canvas, mOpenPoint, mRadius, mBackPaint);//绘制开启
        canvas.save();

        mBackPaint.setColor(mCloseColor);
        mBackPaint.setStyle(Paint.Style.FILL);
        drawRoundRect(canvas, mArcPoint, mRadius, mBackPaint);//绘制关闭动画
        drawToggleButton(canvas);//绘制指示器
        drawToggleTextView(canvas);

        canvas.restore();
    }

    private void drawToggleTextView(Canvas canvas) {
        Rect rect = new Rect();
        String text;
        int color;
        Paint.Align align;
        float left = mBorderWidth;
        if (isOpen && !TextUtils.isEmpty(mOpenText)) {
            text = mOpenText.toString();
            color = mOpenTextColor;
            align = Paint.Align.LEFT;
            mTextPaint.getTextBounds(text, 0, text.length(), rect);
        } else if (!isOpen && !TextUtils.isEmpty(mCloseText)) {
            text = mCloseText.toString();
            color = mCloseTextColor;
            align = Paint.Align.LEFT;
        } else {
            text = null;
            return;
        }

        initTextDraw(rect, text, align, color);
        if (isOpen) {
            left += (int) (mCenterX - mRadius + left - rect.width()) >> 1;
        } else {
            left += (int) (mCenterX + mRadius + right - rect.width()) >> 1;
        }


        canvas.drawText(text, left, mCenterY + (mTextPaint.descent() - mTextPaint.ascent()) / 2.f - mTextPaint.descent(), mTextPaint);
    }

    private void initTextDraw(Rect rect, String text, Paint.Align align, int Textcolor) {
        mTextPaint.setTextAlign(align);
        mTextPaint.getTextBounds(text, 0, text.length(), rect);
        mTextPaint.setColor(Textcolor);
    }

    private void drawToggleButtonOffset(Canvas canvas) {
//        mBackPaint.setColor(Color.YELLOW);
//        canvas.drawArc(new RectF(mArcLeft, top, mArcRight, bottom), 90, 180, true, mBackPaint);
    }

    private void drawToggleButton(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mBottomRadius, mButtonPaint);
        mBackPaint.setColor(Color.GRAY);
        mBackPaint.setStrokeWidth(1);
        mBackPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mCenterX, mCenterY, mBottomRadius, mBackPaint);
    }

    private void drawRoundRect(Canvas canvas, Point point, float radius, Paint paint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(point.l, point.t, point.r, point.b, radius, radius, paint);
        } else {
            canvas.drawRoundRect(point.toRectF(), radius, radius, paint);// TODO: 17/1/19 15:55
        }
    }


    private void initAnimator() {
        final boolean oldIsOpen = isOpen;
        mAnimator = ValueAnimator.ofFloat();
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isCancle) {
                    isCancle = !isCancle;
                    return;
                }
                mListener.onStateChange(ToggleButton.this, isOpen);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isCancle = true;
            }
        });
    }

    private void startAnimation(float lastRight, long duration, ValueAnimator.AnimatorUpdateListener listener) {
//        ValueAnimator right = ValueAnimator.ofFloat(this.mCenterX, lastRight);
        if (mAnimator == null) {
            initAnimator();
        }

        if (mAnimator.isStarted()) {
            mAnimator.cancel();
        }
        mAnimator.setFloatValues(this.mCenterX, lastRight);
        mAnimator.addUpdateListener(listener);
        mAnimator.setDuration(duration);
        mAnimator.start();
    }

    private static class ColorUtil {

        public static int setAlpha(int color, int alpha) {
            int oldAlpha = Color.alpha(color);
            int red = Color.red(color);
            int bule = Color.blue(color);
            int green = Color.green(color);
            return Color.argb(alpha, red, green, bule);
        }

    }

    private class Point {
        float l, t, r, b, s;

        public Point() {
        }

        public Point(float l, float t, float r, float b) {
            this.l = l;
            this.t = t;
            this.r = r;
            this.b = b;
        }


        public RectF toRectF() {
            return new RectF(l, t, r, b);
        }
    }


    public interface ToggleButtonStateChangeListener {

        /**
         * @param button this
         * @param isOpen if true has open else fase
         */
        void onStateChange(ToggleButton button, boolean isOpen);

        ToggleButtonStateChangeListener DEFAULT = new ToggleButtonStateChangeListener() {
            @Override
            public void onStateChange(ToggleButton button, boolean isOpen) {
                Log.d("toggleButton", isOpen + "");
            }
        };
    }
}
