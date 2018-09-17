package com.example.admin.gestureunlockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

/**
 * 手势绘制结果
 * Created by xiesuichao on 2018/4/19.
 */

public class GestureResultView extends View {

    private int normalRadius, normalCol, horizontalSpace, verticalSpace, checkedCol;
    private Paint normalPaint, checkedPaint;
    private List<GestureCircle> normalList = new ArrayList<>();
    private List<GestureCircle> mCheckedList = new ArrayList<>();


    public GestureResultView(Context context) {
        this(context, null);
    }

    public GestureResultView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureResultView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initData();
    }

    /**
     * 设置选中的点
     */
    public void setCheckedList(List<GestureCircle> checkedList) {
        if (checkedList.size() < 4)
            return;
        mCheckedList.clear();
        for (GestureCircle checkedCircle : checkedList) {
            for (GestureCircle normalCircle : normalList) {
                if (checkedCircle.getPosition() == normalCircle.getPosition()){
                    mCheckedList.add(normalCircle);
                }
            }
        }
        invalidate();
    }

    /**
     * 清除结果
     */
    public void clearResult(){
        mCheckedList.clear();
        invalidate();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GestureResultView);
            normalRadius = typedArray.getInteger(R.styleable.GestureResultView_grNormalRadius, -1);
            normalCol = typedArray.getColor(R.styleable.GestureResultView_grNormalCol, -1);
            horizontalSpace = typedArray.getInteger(R.styleable.GestureResultView_grHorizontalSpace, -1);
            verticalSpace = typedArray.getInteger(R.styleable.GestureResultView_grVerticalSpace, -1);
            checkedCol = typedArray.getColor(R.styleable.GestureResultView_grCheckedCol, -1);
            typedArray.recycle();
        }

        if (normalRadius == -1) {
            normalRadius = 6;
        }
        if (normalCol == -1) {
            normalCol = 0xffCACCD6;
        }
        if (horizontalSpace == -1) {
            horizontalSpace = 8;
        }
        if (verticalSpace == -1) {
            verticalSpace = horizontalSpace;
        }
        if (checkedCol == -1) {
            checkedCol = 0xffFF5442;
        }
    }

    private void initData() {
        normalPaint = new Paint();
        normalPaint.setColor(normalCol);
        normalPaint.setAntiAlias(true);
        normalPaint.setStyle(Paint.Style.FILL);

        checkedPaint = new Paint();
        checkedPaint.setColor(checkedCol);
        checkedPaint.setAntiAlias(true);
        checkedPaint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < 9; i++) {
            if (i < 3) {
                normalList.add(new GestureCircle(getPaddingLeft() + dp2px((i * 2 + 1) * normalRadius + i * horizontalSpace),
                        getPaddingTop() + dp2px(normalRadius),
                        i + 1, false));

            } else if (i < 6) {
                normalList.add(new GestureCircle(getPaddingLeft() + dp2px(((i - 3) * 2 + 1) * normalRadius + (i - 3) * horizontalSpace),
                        getPaddingTop() + dp2px(normalRadius * 3 + verticalSpace),
                        i + 1, false));

            } else {
                normalList.add(new GestureCircle(getPaddingLeft() + dp2px(((i - 6) * 2 + 1) * normalRadius + (i - 6) * horizontalSpace),
                        getPaddingTop() + dp2px(normalRadius * 5 + verticalSpace * 2),
                        i + 1, false));

            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int defaultWidth = dp2px(normalRadius * 6 + horizontalSpace * 2);
        int defaultHeight = dp2px(normalRadius * 6 + verticalSpace * 2);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize;
        int heightSize;
        if (widthMode == MeasureSpec.AT_MOST){
            widthSize = defaultWidth;
        }else {
            widthSize = MeasureSpec.getSize(widthMeasureSpec);
        }

        if (heightMode == MeasureSpec.AT_MOST){
            heightSize = defaultHeight;
        }else {
            heightSize = MeasureSpec.getSize(heightMeasureSpec);
        }

        setMeasuredDimension(widthSize, heightSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawNormalCircle(canvas);
        drawCheckedList(canvas);

    }

    private void drawNormalCircle(Canvas canvas) {
        for (GestureCircle circle : normalList) {
            canvas.drawCircle(circle.getX(), circle.getY(), dp2px(normalRadius), normalPaint);
        }
    }

    private void drawCheckedList(Canvas canvas) {
        if (!mCheckedList.isEmpty()){
            for (GestureCircle circle : mCheckedList) {
                canvas.drawCircle(circle.getX(), circle.getY(), dp2px(normalRadius), checkedPaint);
            }
        }
    }

    //根据手机的分辨率从 dp 的单位 转成为 px(像素)
    private int dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
