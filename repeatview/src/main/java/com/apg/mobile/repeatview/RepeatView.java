package com.apg.mobile.repeatview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

// Created by X-tivity on 3/2/2017 AD.
public class RepeatView extends View {

    private static final int SCALE_TYPE_EXPAND = 0;
    private static final int SCALE_TYPE_SCALE_DOWN = 1;

    private BitmapDrawable bitmapDrawable;
    private int scaleType;
    private int mPreviewWidth;
    private int mPreviewHeight;

    public RepeatView(Context context) {
        super(context);
    }

    public RepeatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        readAttribute(context, attrs, 0);
    }

    public RepeatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttribute(context, attrs, defStyleAttr);
    }

    @TargetApi(23)
    public RepeatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        readAttribute(context, attrs, defStyleAttr);
    }

    private void readAttribute(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RepeatView);
        Drawable repeatDrawable = a.getDrawable(R.styleable.RepeatView_src);
        scaleType = a.getInt(R.styleable.RepeatView_scaleType, SCALE_TYPE_EXPAND);

        if (repeatDrawable == null)
            throw new NullPointerException("You have to set src first..");

        bitmapDrawable = drawableToBitmapDrawable(repeatDrawable);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int wm, hm;
        int viewHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int viewWidthMode = MeasureSpec.getMode(widthMeasureSpec);

        // 1: find user desired dimension
        int desiredWidth = measureDesiredSize(widthMeasureSpec, bitmapDrawable.getIntrinsicWidth());
        int desiredHeight = measureDesiredSize(heightMeasureSpec, bitmapDrawable.getIntrinsicHeight());

        // 2:expand_mode -> adjust view
        if (scaleType == SCALE_TYPE_EXPAND) {
            desiredHeight = updateSizeByExpandMode(desiredHeight, bitmapDrawable.getIntrinsicHeight());
            desiredWidth = updateSizeByExpandMode(desiredWidth, bitmapDrawable.getIntrinsicWidth());
        }
        // 2:scale_down_mode -> adjust image
        else if (scaleType == SCALE_TYPE_SCALE_DOWN) {

        }

        wm = MeasureSpec.makeMeasureSpec(desiredWidth, viewWidthMode);
        hm = MeasureSpec.makeMeasureSpec(desiredHeight, viewHeightMode);
        setMeasuredDimension(wm, hm);
    }

    @Override
    protected void onSizeChanged(int newW, int newH, int oldw, int oldh) {
        super.onSizeChanged(newW, newH, oldw, oldh);
        mPreviewWidth = newW;
        mPreviewHeight = newH;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        bitmapDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        bitmapDrawable.draw(canvas);
    }

    private int updateSizeByExpandMode(int desiredSize, int imageSize) {
        if (desiredSize > imageSize) {
            int item = (int) Math.ceil(desiredSize / (float) imageSize);
            return item * imageSize;
        } else {
            return imageSize;
        }
    }

    private int measureDesiredSize(int measureSpec, int imageSize) {

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            return size;
        } else if (mode == MeasureSpec.AT_MOST) {
            return Math.min(size, imageSize);
        } else {
            return imageSize;
        }
    }

    public BitmapDrawable drawableToBitmapDrawable(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {

            return (BitmapDrawable) drawable;
        } else if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {

            // Single color bitmap will be created of 1x1 pixel
            Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            return new BitmapDrawable(getResources(), bitmap);
        } else {

            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
            return new BitmapDrawable(getResources(), bitmap);
        }
    }
}
