package com.example.saro.pianokeyboard.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by Saro on 9/8/2015.
 */
public class StableHorizontalScrollView extends HorizontalScrollView {
    private boolean enabled;

    public StableHorizontalScrollView(Context context) {
        super(context);
        this.enabled = true;
    }

    public StableHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

}
