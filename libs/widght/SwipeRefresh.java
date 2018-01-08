package com.aosika.phonelive.player_lhc_costom.libs.widght;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.aosika.phonelive.R;
import com.aosika.phonelive.player_lhc_costom.constant.Constant;
import com.aosika.phonelive.player_lhc_costom.libs.fragment.VerticalLoopFragment;
import com.aosika.phonelive.utils.ToastUtil;
import com.aosika.phonelive.utils.WordUtil;

/**
 * Created by mengyunfeng on 17/12/16.
 */

public class SwipeRefresh extends SwipeRefreshLayout {



    public SwipeRefresh(Context context) {
        super(context);
    }

    public SwipeRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        /*if(VerticalLoopFragment.mCurrentP==0){
            return true;
        }else if(VerticalLoopFragment.mIsLoading){
            ToastUtil.show(WordUtil.getString(R.string.loading));
            return true;
        }else{
            return super.onInterceptTouchEvent(event);
        }
        return false;

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        /*if(Constant.mCurrentP==0){
            return false;
        }else{
            return super.onTouchEvent(ev);
        }*/
        /*return super.onTouchEvent(ev);*/

        return false;
    }
}
