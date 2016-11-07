package com.xrtz.bessel;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by Administrator on 2016/11/3.
 */

public class LoveLayout extends RelativeLayout {
    Drawable[]drawables = new Drawable[5];
    LayoutParams params;
    //图片的宽和高
    int drawWidth;
    int drawHeight;

    //当前的Layout的宽高
    int mWidth;
    int mHeight;
    Random random = new Random();
    public LoveLayout(Context context) {
        this(context,null);
    }

    public LoveLayout(Context context, AttributeSet attrs) {
        this(context, attrs ,0);
    }

    public LoveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
    }

    public void init(){
        drawables[0] = getResources().getDrawable(R.mipmap.ss_heart1);
        drawables[1] = getResources().getDrawable(R.mipmap.ss_heart2);
        drawables[2] = getResources().getDrawable(R.mipmap.ss_heart3);
        drawables[3] = getResources().getDrawable(R.mipmap.ss_heart4);
        drawables[4] = getResources().getDrawable(R.mipmap.ss_heart5);

        drawWidth = drawables[0].getIntrinsicWidth();
        drawHeight = drawables[0].getIntrinsicHeight();
        params = new RelativeLayout.LayoutParams(drawWidth,drawHeight);
    }

    private AnimatorSet getObjectAnimator(ImageView imageView,PointF pointF) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView,"scaleX",0.2f,1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView,"scaleY",0.2f,1.0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView,"alpha",0.2f,1.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX,scaleY,alpha);
        animatorSet.setDuration(800);

        ValueAnimator beisaier = getValueAnimation(imageView,pointF);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animatorSet,beisaier);
        set.setDuration(3000);
        set.setTarget(imageView);
        return animatorSet;
    }

    private ValueAnimator getValueAnimation(final ImageView imageView, PointF pointF) {
        PointF pointF0 = new PointF();
        pointF0.x = pointF.x - drawWidth/2;
        pointF0.y = pointF.y - drawHeight/2;

        PointF pointF1 = getTogglePoint(1,pointF);
        PointF pointF2 = getTogglePoint(2,pointF);
        Log.e("getValueAnimation","pointF0.x="+pointF0.x+"   "+"pointF0.y="+pointF0.y);
        Log.e("getValueAnimation","pointF1.x="+pointF1.x+"   "+"pointF1.y="+pointF1.y);
        Log.e("getValueAnimation","pointF2.x="+pointF2.x+"   "+"pointF2.y="+pointF2.y);


        PointF pointF3 = new PointF(random.nextInt(mWidth),0);
        Log.e("getValueAnimation","pointF3.x="+pointF.x+"   "+"pointF3.y="+pointF.y);
        LoveValueEvalutor loveValueEvalutor = new LoveValueEvalutor(pointF1,pointF2);
        ValueAnimator valueAnimator = ValueAnimator.ofObject(loveValueEvalutor,pointF0,pointF3);
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PointF pointF1 = (PointF) valueAnimator.getAnimatedValue();
                imageView.setX(pointF1.x);
                imageView.setY(pointF1.y);
                imageView.setAlpha(1-valueAnimator.getAnimatedFraction());
            }
        });
        return valueAnimator;
    }
    private PointF getTogglePoint(int i,PointF pointF){
        PointF pointF1 = new PointF();
        pointF1.x = random.nextInt(mWidth);
        int reacHeight = (int) (pointF.y/2);
        if(i==1){
            pointF1.y = reacHeight + random.nextInt(reacHeight);
        }else if(i==2){
            pointF1.y = random.nextInt(reacHeight);
        }
        return pointF1;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            PointF pointF = new PointF();
            pointF.x = event.getX();
            pointF.y = event.getY();
            addLove(pointF);
        }

        return true;
    }

    private void addLove(PointF pointF) {
        params = new LayoutParams(drawWidth,drawHeight);
        params.leftMargin = (int) (pointF.x - drawWidth/2);
        params.topMargin = (int) (pointF.y - drawHeight/2);
        ImageView imageView = new ImageView(getContext());
        AnimatorSet objectAnimator = getObjectAnimator(imageView, pointF);
        imageView.setImageDrawable(drawables[random.nextInt(5)]);
        addView(imageView,params);
        objectAnimator.start();
    }

}
