package com.aosika.phonelive.player_lhc_costom.libs.widght;

import android.content.Context;
import android.support.v4.view.LoopViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.aosika.phonelive.player_lhc_costom.libs.fragment.VerticalLoopFragment;

/**
 * Created by Administrator on 2017/12/15.
 */

public class VerticalLoopViewPager extends LoopViewPager {
    float startY = 0;
    private boolean isDatchToTop;//禁止往上滑
    private boolean isDatchToDown;//禁止往下滑

    public VerticalLoopViewPager(Context context) {
        super(context);
    }

    public VerticalLoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private MotionEvent swapTouchEvent(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();
        event.setLocation((event.getY() / height) * width, (event.getX() / width) * height);
        return event;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return super.onInterceptTouchEvent(swapTouchEvent(MotionEvent.obtain(event)));
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isDatchToTop){
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                startY = ev.getY();
            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                if (ev.getY() < startY) {//往上滑
                    return false;
                } else {//往下滑

                }
            }
        }else if(isDatchToDown){
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                startY = ev.getY();
            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                if (ev.getY() < startY) {//往上滑

                } else {//往下滑
                    return false;
                }
            }
        }
        return super.onTouchEvent(swapTouchEvent(MotionEvent.obtain(ev)));
    }


    /**
     * 禁止往上滑
     * @param datchToTop
     */
    public void setDatchToTop(boolean datchToTop) {
        isDatchToTop = datchToTop;
    }

    /**
     * 禁止往下滑
     * @param datchToDown
     */
    public void setDatchToDown(boolean datchToDown) {
        isDatchToDown = datchToDown;
    }
}


