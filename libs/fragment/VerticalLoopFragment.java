package com.aosika.phonelive.player_lhc_costom.libs.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.alibaba.fastjson.JSON;
import com.aosika.phonelive.R;
import com.aosika.phonelive.bean.VideoBean;
import com.aosika.phonelive.http.HttpCallback;
import com.aosika.phonelive.http.HttpUtil;
import com.aosika.phonelive.player_lhc_costom.bean.Videos;
import com.aosika.phonelive.player_lhc_costom.fragment.VideoLoopPlayerFragment;
import com.aosika.phonelive.player_lhc_costom.libs.adapter.VerticalAdapter;
import com.aosika.phonelive.player_lhc_costom.libs.tansformer.DefaultTransformer;
import com.aosika.phonelive.player_lhc_costom.libs.widght.SwipeRefresh;
import com.aosika.phonelive.player_lhc_costom.libs.widght.VerticalLoopViewPager;
import com.aosika.phonelive.utils.ToastUtil;
import com.aosika.phonelive.utils.WordUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.view.View.OVER_SCROLL_NEVER;

/**
 * Created by Administrator on 2017/12/15.
 */

public class VerticalLoopFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private VerticalLoopViewPager loopViewPager;
    private SwipeRefresh sr_viewpager;
    private VerticalAdapter verticalAdapter;
    private List<Fragment> mFragments = new ArrayList<>();
    private int mCurrentDataP;//当前数据位置
    private int mCurrentViewP;//fragment当前位置
    private int mOldP;//fragment在viewpager的上一个位置

    private List<VideoBean> mVideoBeens = new ArrayList<>();
    private int mPage;//当前的页数
    private Videos mVideos;//所有的video数据
    private boolean mIsLoading;//正在加载更多中
    private boolean mIsLoadAll;//到底了加载完所有的数据的size

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verticalveiwpager, container, false);
        initDatas();
        initViews(view);
        initViewDatas();
        initListeners();
        return view;
    }


    private void initDatas() {
        Bundle bundle = getArguments();
        mVideos = bundle.getParcelable("videos");
        mPage = mVideos.getPage();
        mVideoBeens = mVideos.getVideoBeens();
        mCurrentDataP = mVideos.getCurrentP();
        checkVideoList();

        for (int i = 0; i < 3; i++) {
            VideoLoopPlayerFragment personalFragment = new VideoLoopPlayerFragment();
            Bundle mBundle = new Bundle();
            if (i == 0) {
                mBundle.putParcelable("videoBean", mVideoBeens.get(mCurrentDataP));
            } else {
                mBundle.putParcelable("videoBean", null);
            }

          /*  personalFragment.setPlayListener(new VideoLoopPlayerFragment.OnPlayListener() {
                @Override
                public void onFirstFrame() {

                }

                @Override
                public void onLoadingStart() {

                }

                @Override
                public void onLoadingEnd() {

                }
            });*/
            personalFragment.setArguments(mBundle);
            mFragments.add(personalFragment);
        }
    }

    private void initViews(View view) {
        sr_viewpager = (SwipeRefresh) view.findViewById(R.id.sr_viewpager);
        loopViewPager = (VerticalLoopViewPager) view.findViewById(R.id.pager);
    }

    private void initViewDatas() {
        verticalAdapter = new VerticalAdapter(getChildFragmentManager(), mFragments);
        loopViewPager.setAdapter(verticalAdapter);
        loopViewPager.setPageTransformer(true, new DefaultTransformer());
        loopViewPager.setOverScrollMode(OVER_SCROLL_NEVER);
        checkViewPagerScroll();
        setChildDatas();
    }

    private void initListeners() {
        loopViewPager.addOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.e("懒加载", "-----onPageSelected----" + position);
        mCurrentViewP=position;
        loadPosition(position);
        checkVideoList();
        checkViewPagerScroll();
        setChildDatas();

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((VideoLoopPlayerFragment) mFragments.get(mCurrentViewP)).onVideoResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((VideoLoopPlayerFragment) mFragments.get(mCurrentViewP)).onVideoPause();
    }


    private void setChildDatas(){
        if(mCurrentDataP==0){
            if(mCurrentViewP==2){
                ((VideoLoopPlayerFragment) mFragments.get(0)).setDatas(mVideoBeens.get(mCurrentDataP));
            }else{
                ((VideoLoopPlayerFragment) mFragments.get(mCurrentViewP+1)).setDatas(mVideoBeens.get(mCurrentDataP));
            }
        }else if(mCurrentDataP==mVideoBeens.size()-1){
            if(mCurrentViewP==0){
                ((VideoLoopPlayerFragment) mFragments.get(2)).setDatas(mVideoBeens.get(mCurrentDataP));
            }else{
                ((VideoLoopPlayerFragment) mFragments.get(mCurrentViewP-1)).setDatas(mVideoBeens.get(mCurrentDataP));
            }
        }else{
            if(mCurrentViewP==0){
                ((VideoLoopPlayerFragment) mFragments.get(2)).setDatas(mVideoBeens.get(mCurrentDataP));
            }else{
                ((VideoLoopPlayerFragment) mFragments.get(mCurrentViewP-1)).setDatas(mVideoBeens.get(mCurrentDataP));
            }
            if(mCurrentViewP==2){
                ((VideoLoopPlayerFragment) mFragments.get(0)).setDatas(mVideoBeens.get(mCurrentDataP));
            }else{
                ((VideoLoopPlayerFragment) mFragments.get(mCurrentViewP+1)).setDatas(mVideoBeens.get(mCurrentDataP));
            }
        }

    }

    private void loadPosition(int position) {
        if (position == 0) {
            if (mOldP == 1) {//Top
                mCurrentDataP--;
            } else {//down
                mCurrentDataP++;
            }
        } else if (position == 1) {
            if (mOldP == 2) {//Top
                mCurrentDataP--;
            } else {//dowm
                mCurrentDataP++;
            }
        } else if (position == 2) {
            if (mOldP == 0) {//Top
                mCurrentDataP--;
            } else {//dowm
                mCurrentDataP++;
            }
        }
        mOldP = position;
    }

    private void checkVideoList() {
        if (mIsLoadAll) {
            return;
        }
        if (mIsLoading) {
            return;
        }
        if (mCurrentDataP == mVideoBeens.size() - 1) {
            mPage++;
            mIsLoading = true;
            HttpUtil.getVideoList(mPage, mLoadMoreCallback);
        }
    }

    private HttpCallback mLoadMoreCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                List<VideoBean> list = JSON.parseArray(Arrays.toString(info), VideoBean.class);
                if (list.size() > 0) {
                    mVideoBeens.addAll(list);
                } else {
                    ToastUtil.show(WordUtil.getString(R.string.no_more_data));
                    mPage--;
                    mIsLoadAll = true;
                }
            } else {
                ToastUtil.show(msg);
                mIsLoadAll = true;
            }
        }

        @Override
        public void onFinish() {
            mIsLoading = false;
            checkViewPagerScroll();
            setChildDatas();

        }
    };

    /**
     * 判断和设置viewpager是否可滑动  在checkVideoList之后调用
     */
    public void checkViewPagerScroll() {
        if (mCurrentDataP == 0) {
            loopViewPager.setDatchToDown(true);
        } else {
            loopViewPager.setDatchToDown(false);
        }
        if (mIsLoading || mIsLoadAll && mCurrentDataP == mVideoBeens.size() - 1) {
            loopViewPager.setDatchToTop(true);
        } else {
            loopViewPager.setDatchToTop(false);
        }
    }


}
