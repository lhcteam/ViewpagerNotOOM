package com.aosika.phonelive.player_lhc_costom.libs.widght;

import android.content.Context;
import android.support.v4.view.LoopViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/12/15.
 */

public class VerticalViewPagerNoScroll extends fr.castorflex.android.verticalviewpager.VerticalViewPager {
    float startY = 0;
    private int mState;//状态  0：可上下滑动 1：禁止向下滑动 2：禁止向上滑动 -1：禁止滑动

    public static final int STATE_SCROLL=0;
    public static final int STATE_NODOWN=1;
    public static final int STATE_NOTOP=2;
    public static final int STATE_NOALL=-1;

    public VerticalViewPagerNoScroll(Context context) {
        super(context);
    }

    public VerticalViewPagerNoScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // return false;//可行,不拦截事件,
        // return true;//不行,孩子无法处理事件
        //return super.onInterceptTouchEvent(ev);//不行,会有细微移动

        if(mState==STATE_SCROLL){
            return super.onInterceptTouchEvent(ev);
        }else if(mState==STATE_NOTOP){
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                startY = ev.getY();

            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                if (ev.getY() < startY) {//往上滑
                    return false;
                } else {//往下滑
                }
            }
            return super.onInterceptTouchEvent(ev);
        }else if(mState==STATE_NODOWN){
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                startY = ev.getY();
            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                if (ev.getY() < startY) {//往上滑
                } else {//往下滑
                    return false;
                }
            }
            return super.onInterceptTouchEvent(ev);
        }else{
            return false;
        }
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent ev) {
        System.out.println(">>>>>><<<<<<<"+ev.getY()+"-----"+ev.getX());
        if(mState==STATE_SCROLL){
            return super.onTouchEvent(ev);
        }else if(mState==STATE_NODOWN){
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                startY = ev.getY();

            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                if (ev.getY() < startY) {//往上滑
                    return false;
                } else {//往下滑
                }
            }
            return super.onTouchEvent(ev);
        }else if(mState==STATE_NOTOP){
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                startY = ev.getY();
            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                if (ev.getY() < startY) {//往上滑
                } else {//往下滑
                    return false;
                }
            }
            return super.onTouchEvent(ev);
        }else{
            return false;
        }
    }*/

    public void setState(int state) {
        mState = state;
    }
}


