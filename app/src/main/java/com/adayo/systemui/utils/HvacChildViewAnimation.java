package com.adayo.systemui.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;

public class HvacChildViewAnimation {


    //隐藏动画
    public static void performFadeOutAnimation(View view) {
        if (view.getVisibility()==View.GONE) return;
        // 创建 ObjectAnimator 对象，设置透明度动画
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        animator.setDuration(300); // 设置动画持续时间，单位毫秒
        animator.start(); // 启动动画

        // 在动画结束时执行的操作，这里是隐藏视图
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
            }
        });
    }

    //显示动画
    public static void fadeInView(View view) {
        if (view.getVisibility()==View.VISIBLE) return;
        // 使用 ViewPropertyAnimator 设置透明度动画
        view.animate()
                .alpha(1f) // 最终透明度为1
                .setDuration(500) // 设置动画持续时间，单位毫秒
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        // 在动画开始时执行的操作，这里是设置View可见
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        // 动画结束时可以添加一些额外的处理
                    }
                })
                .start(); // 启动动画
    }
}
