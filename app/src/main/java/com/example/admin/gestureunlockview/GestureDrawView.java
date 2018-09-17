package com.example.admin.gestureunlockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

/**
 * 手势绘制
 * Created by xiesuichao on 2018/4/18.
 */

public class GestureDrawView extends View {

    private int outerRadius, outerHorizontalSpace, outerVerticalSpace, outerCheckedCol, innerRadius,
            innerNormalCol, innerCheckedCol, lineCol, lineWidth;
    private Paint outerCheckedPaint, innerNormalPaint, innerCheckedPaint, linePaint;
    private Path linePath, movePath;
    private boolean isUnlocking = false;
    private OnGestureDrawListener drawListener;
    private OnGestureErrorListener errorListener;
    private List<GestureCircle> pointList = new ArrayList<>();
    private List<GestureCircle> checkedList = new ArrayList<>();
    private Handler errorHandler;
    private Runnable errorRunnable;

    public GestureDrawView(Context context) {
        this(context, null);
    }

    public GestureDrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initData();
    }

    public interface OnGestureDrawListener {
        void gestureDraw(List<GestureCircle> checkedList);
    }

    public interface OnGestureErrorListener{
        void gestureError();
    }

    /**
     * 手势绘制监听
     */
    public void setOnGestureDrawListener(OnGestureDrawListener drawListener) {
        this.drawListener = drawListener;
    }

    /**
     * 手势绘制错误监听
     */
    public void setOnGestureErrorListener(OnGestureErrorListener errorListener){
        this.errorListener = errorListener;
    }

    /**
     * 清空runnable，回收handler
     */
    public void cancelHandler(){
        errorHandler.removeCallbacks(errorRunnable);
        errorHandler = null;
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GestureDrawView);
            outerRadius = typedArray.getInteger(R.styleable.GestureDrawView_gdOuterRadius, -1);
            outerCheckedCol = typedArray.getColor(R.styleable.GestureDrawView_gdOuterCheckCol, -1);
            outerHorizontalSpace = typedArray.getColor(R.styleable.GestureDrawView_gdOuterHorizontalSpace, -1);
            outerVerticalSpace = typedArray.getColor(R.styleable.GestureDrawView_gdOuterVerticalSpace, -1);
            innerRadius = typedArray.getInteger(R.styleable.GestureDrawView_gdInnerRadius, -1);
            innerNormalCol = typedArray.getColor(R.styleable.GestureDrawView_gdInnerNormalCol, -1);
            innerCheckedCol = typedArray.getColor(R.styleable.GestureDrawView_gdInnerCheckedCol, -1);
            lineCol = typedArray.getColor(R.styleable.GestureDrawView_gdLineCol, -1);
            lineWidth = typedArray.getColor(R.styleable.GestureDrawView_gdLineWidth, -1);
            typedArray.recycle();
        }

        if (outerRadius == -1) {
            outerRadius = 30;
        }
        if (outerCheckedCol == -1) {
            outerCheckedCol = 0x66FF5442;
        }
        if (outerHorizontalSpace == -1) {
            outerHorizontalSpace = 40;
        }
        if (outerVerticalSpace == -1) {
            outerVerticalSpace = outerHorizontalSpace;
        }
        if (innerRadius == -1) {
            innerRadius = 10;
        }
        if (innerNormalCol == -1) {
            innerNormalCol = 0xffCACCD6;
        }
        if (innerCheckedCol == -1) {
            innerCheckedCol = 0xffFF5442;
        }
        if (lineCol == -1) {
            lineCol = innerCheckedCol;
        }
        if (lineWidth == -1) {
            lineWidth = 3;
        }
    }

    private void initData() {
        innerNormalPaint = new Paint();
        innerNormalPaint.setColor(innerNormalCol);
        innerNormalPaint.setAntiAlias(true);
        innerNormalPaint.setStyle(Paint.Style.FILL);

        innerCheckedPaint = new Paint();
        innerCheckedPaint.setAntiAlias(true);
        innerCheckedPaint.setStyle(Paint.Style.FILL);
        innerCheckedPaint.setColor(innerCheckedCol);

        outerCheckedPaint = new Paint();
        outerCheckedPaint.setAntiAlias(true);
        outerCheckedPaint.setStyle(Paint.Style.FILL);
        outerCheckedPaint.setColor(outerCheckedCol);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(dp2px(lineWidth));
        linePaint.setColor(lineCol);

        linePath = new Path();
        movePath = new Path();

        for (int i = 0; i < 9; i++) {
            if (i < 3) {
                pointList.add(new GestureCircle(getPaddingLeft() + dp2px((1 + i * 2) * outerRadius + i * outerHorizontalSpace),
                        getPaddingTop() + dp2px(outerRadius),
                        i + 1, false));

            } else if (i < 6) {
                pointList.add(new GestureCircle(getPaddingLeft() + dp2px(((i - 3) * 2 + 1) * outerRadius + (i - 3) * outerHorizontalSpace),
                        getPaddingTop() + dp2px(outerRadius * 3 + outerVerticalSpace),
                        i + 1, false));

            } else {
                pointList.add(new GestureCircle(getPaddingLeft() + dp2px(((i - 6) * 2 + 1) * outerRadius + (i - 6) * outerHorizontalSpace),
                        getPaddingTop() + dp2px(outerRadius * 5 + outerVerticalSpace * 2),
                        i + 1, false));
            }
        }

        errorHandler = new Handler();
        errorRunnable = new Runnable() {
            @Override
            public void run() {
                reset();
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int defaultWidth = dp2px(outerRadius * 6 + outerHorizontalSpace * 2);
        int defaultHeight = dp2px(outerRadius * 6 + outerVerticalSpace * 2);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize;
        int heightSize;
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = defaultWidth;
        } else {
            widthSize = MeasureSpec.getSize(widthMeasureSpec);
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = defaultHeight;
        } else {
            heightSize = MeasureSpec.getSize(heightMeasureSpec);
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawInnerUncheckedCircle(canvas);
        drawChecked(canvas);

    }

    //未选中内圆
    private void drawInnerUncheckedCircle(Canvas canvas) {
        for (GestureCircle circle : pointList) {
            canvas.drawCircle(circle.getX(), circle.getY(), dp2px(innerRadius), innerNormalPaint);
        }
    }

    //选中状态的内外圆和连线
    private void drawChecked(Canvas canvas) {
        canvas.drawPath(linePath, linePaint);
        canvas.drawPath(movePath, linePaint);
        if (isUnlocking) {
            for (GestureCircle point : checkedList) {
                canvas.drawCircle(point.getX(), point.getY(), dp2px(outerRadius), outerCheckedPaint);
                canvas.drawCircle(point.getX(), point.getY(), dp2px(innerRadius), innerCheckedPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float downX = event.getX();
                float downY = event.getY();
                GestureCircle downCircle = getCircleRange(downX, downY);
                if (downCircle != null) {
                    checkedList.add(downCircle);
                    linePath.moveTo(downCircle.getX(), downCircle.getY());
                    movePath.moveTo(downCircle.getX(), downCircle.getY());
                    isUnlocking = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isUnlocking) {
                    float endX = event.getX();
                    float endY = event.getY();
                    GestureCircle endPoint = getCircleRange(endX, endY);
                    GestureCircle lastPoint = checkedList.get(checkedList.size() - 1);
                    movePath.reset();
                    movePath.moveTo(lastPoint.getX(), lastPoint.getY());
                    movePath.lineTo(endX, endY);
                    if (endPoint != null && !checkedList.contains(endPoint)) {
                        if (endPoint.getPosition() != lastPoint.getPosition()) {
                            checkedList.add(endPoint);
                        }
                        linePath.lineTo(endPoint.getX(), endPoint.getY());
                        linePath.moveTo(endPoint.getX(), endPoint.getY());
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                movePath.reset();
                if (drawListener != null) {
                    drawListener.gestureDraw(checkedList);
                }
                if (checkedList.size() < 4) {
                    resetDelay();
                    if (errorListener != null){
                        errorListener.gestureError();
                    }
                } else {
                    reset();
                }
                break;
        }
        invalidate();
        return true;
    }

    private GestureCircle getCircleRange(float x, float y) {
        for (GestureCircle point : pointList) {
            if (Math.abs(point.getX() - x) <= dp2px(30) && Math.abs(point.getY() - y) <= dp2px(30)) {
                return point;
            }
        }
        return null;
    }

    private void reset() {
        isUnlocking = false;
        linePath.reset();
        checkedList.clear();
        invalidate();
    }

    private void resetDelay() {
        errorHandler.postDelayed(errorRunnable, 300);
    }

    //根据手机的分辨率从 dp 的单位 转成为 px(像素)
    private int dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
