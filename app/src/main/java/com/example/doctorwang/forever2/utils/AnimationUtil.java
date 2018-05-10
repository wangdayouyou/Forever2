package com.example.doctorwang.forever2.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by zhangdonghai on 2017/11/2.
 */

public class AnimationUtil {

    public static void showView(View displayedView) {
        if (displayedView != null && displayedView.getVisibility() == View.GONE) {
            displayedView.setVisibility(View.VISIBLE);
        }
        byte translateDp = 40;
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator displayedViewFadeIn = ObjectAnimator.ofFloat(displayedView, "alpha", new float[]{0.0F, 1.0F});
        ObjectAnimator displayedViewTranslateIn = ObjectAnimator.ofFloat(displayedView, "translationY", new float[]{(float) dpToPx(displayedView.getContext(), (float) translateDp), 0.0F});
        set.playTogether(new Animator[]{displayedViewFadeIn, displayedViewTranslateIn});
        set.setDuration(800L);
        set.start();
    }

    private static int dpToPx(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((double) (dp * displayMetrics.density) + 0.5D);
    }
}
