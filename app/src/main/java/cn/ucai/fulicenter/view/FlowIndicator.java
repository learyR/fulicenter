package cn.ucai.fulicenter.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import cn.ucai.fulicenter.R;

/**
 * Created by Administrator on 2016/9/29.
 */
public class FlowIndicator extends View{
    int mCount;//指示器的实心圆总数
    int mFocus;//焦点的索引，从0开始
    int mRadius;//实心圆的半径
    int mSpace;//实心圆之间的距离
    int mNormalColor;//非焦点的颜色
    int mFocusColor;//焦点的颜色

    Paint mPaint;


    public FlowIndicator(Context context) {

        super(context);
    }

    public FlowIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取布局中定义的那些自定义的属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlowIndicator);

        mCount = array.getInt(R.styleable.FlowIndicator_count, 5);
        mFocus = array.getInt(R.styleable.FlowIndicator_focus, 0);
        mSpace = array.getDimensionPixelSize(R.styleable.FlowIndicator_space, 5);
        mRadius = array.getDimensionPixelOffset(R.styleable.FlowIndicator_r, 10);
        mNormalColor = array.getColor(R.styleable.FlowIndicator_normal_color, 0x000);
        mFocusColor = array.getColor(R.styleable.FlowIndicator_focus_color, 0xff0000);

        array.recycle();
        mPaint = new Paint();
    }

    public int getFocus() {
        return mFocus;
    }

    public void setFocus(int focus) {
        this.mFocus = focus;
//        requestLayout();
        invalidate();
    }

    public int getmCount() {
        return mCount;
    }

    public void setmCount(int count) {
        this.mCount = count;
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int result = size;
        if (mode != MeasureSpec.EXACTLY) {
            size = getPaddingTop() + getPaddingBottom() + mRadius * 2;
            result = Math.min(result, size);
        }
        return result;
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        int result = size;
        if (mode != MeasureSpec.EXACTLY) {
            size = getPaddingLeft() + getPaddingRight() + 2 * mRadius * mCount + mSpace * (mCount - 1);
            result = Math.min(result, size);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        //计算出第一个实心圆的起始横坐标
        int leftSpace = getPaddingLeft() + (getWidth() - mCount * 2 * mRadius - mSpace * (mCount - 1)) / 2;
        for (int i=0;i<mCount;i++) {
            //计算每个实心圆的横坐标
            int x = leftSpace + i * (2 * mRadius + mSpace);
            //计算每个实心圆的颜色
            int color = i == mFocus ? mFocusColor : mNormalColor;
            mPaint.setColor(color);
            canvas.drawCircle(x + mRadius, mRadius, mRadius, mPaint);
        }
    }
}
