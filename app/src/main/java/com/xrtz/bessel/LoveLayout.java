package com.xrtz.bessel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.view.View;
import android.view.animation.LinearInterpolator;
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
        Log.e("onMeasure","mHeight="+mHeight);
        Log.e("mWidth","mWidth="+mWidth);
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

    /**
     * 获取进入的动画
     * @param imageView
     * @param pointF
     * @return
     */
    private AnimatorSet getObjectAnimator(ImageView imageView,PointF pointF) {

        AnimatorSet set = getEnterAnimtor(imageView);

        ValueAnimator beisaier = getValueAnimation(imageView,pointF);

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(set);
        finalSet.playSequentially(set, beisaier);
        finalSet.setInterpolator(new LinearInterpolator());
        finalSet.setTarget(imageView);
        return finalSet;
    }
    /**
     * 进入的动画
     * @param target
     * @return
     */
    private AnimatorSet getEnterAnimtor(final View target) {

        ObjectAnimator alpha = ObjectAnimator.ofFloat(target, View.ALPHA, 0.2f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.2f, 1f);
        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(500);
        enter.setInterpolator(new LinearInterpolator());
        enter.playTogether(alpha, scaleX, scaleY);
        enter.setTarget(target);
        return enter;
    }


    /**
     * 贝塞尔动画
     * @param imageView
     * @param pointF
     * @return
     */
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
        valueAnimator.addUpdateListener(new BezierListenr(imageView));
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
        objectAnimator.addListener(new AnimEndListener(imageView));
        objectAnimator.start();
    }
    /**
     * 动画结束后，remove
     */
    private class AnimEndListener extends AnimatorListenerAdapter {
        private View target;

        public AnimEndListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
            removeView((target));
        }
    }
    //今天贝塞尔曲线的变换
    class BezierListenr implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        public BezierListenr(View target) {
            this.target = target;
        }
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //这里获取到贝塞尔曲线计算出来的的x y值 赋值给view 这样就能让爱心随着曲线走啦
            PointF pointF = (PointF) animation.getAnimatedValue();
            target.setX(pointF.x);
            target.setY(pointF.y);
            // 这里偷个懒,顺便做一个alpha动画,这样alpha渐变也完成啦
            target.setAlpha(1-animation.getAnimatedFraction());
        }
    }
}
