package com.ginuo.leweather;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;



//启动页（欢迎页），而非主页面
public class LauncherActivity extends Activity {
    private TextView countDown;
    private MyCountDownTimer downTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_layout);
        //状态栏设为透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        /*
        一、DecorView为整个Window界面的最顶层View。
        二、DecorView只有一个子元素为LinearLayout。代表整个Window界面，包含通知栏，标题栏，内容显示栏三块区域。
        三、LinearLayout里有两个FrameLayout子元素。
        下面这句是为了实现沉浸式状态栏，隐藏状态栏，让启动动画能占满全屏
         */
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        initView();      //获取各组件对象

        downTimer = new MyCountDownTimer(4000, 1000);
        downTimer.start();  //启动倒计时器(异步的，非UI线程中)

    }



    private void initView() {
        countDown= (TextView) findViewById(R.id.countDown);
        // 需要在布局填充完成后才能获取到View的尺寸
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    //当全局布局状态发生变化或者在View tree中的views的可见性发生变化时回调
                    @Override
                    public void onGlobalLayout() {
                        // 需要移除监听，否则会重复触发
                        getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }


    /**
     * 继承CountDownTimer (倒计时器)
     * 重写onTick，onFinish方法
     */
    private class MyCountDownTimer extends CountDownTimer{
        /**
         * millisInFuture
         *     表示以毫秒为单位 倒计时的总数
         *
         *     例如 millisInFuture=1000 表示1秒
         * countDownInterval
         *     表示 间隔 多少微秒 调用一次 onTick 方法
         *
         *     例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         *
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisInFuture) {
            //更新启动页面的倒计时标签
            countDown.setText("倒计时 "+millisInFuture/1000);
        }

        @Override
        public void onFinish() {
            countDown.setText("正在跳转...");
            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(intent);
            LauncherActivity.this.finish();       //记得销毁当前Activity
        }
    }
}
