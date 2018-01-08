package com.aosika.phonelive.player_lhc_costom.libs.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aosika.phonelive.AppContext;
import com.aosika.phonelive.R;
import com.aosika.phonelive.activity.MainActivity;
import com.aosika.phonelive.bean.VideoBean;
import com.aosika.phonelive.event.PositionEvent;
import com.aosika.phonelive.http.HttpCallback;
import com.aosika.phonelive.http.HttpCallbackBase;
import com.aosika.phonelive.http.HttpUtil;
import com.aosika.phonelive.player_lhc_costom.VerticalVideoPlayerActivity;
import com.aosika.phonelive.player_lhc_costom.bean.Videos;
import com.aosika.phonelive.player_lhc_costom.constant.Constant;
import com.aosika.phonelive.player_lhc_costom.dialog.VideoStopDialog;
import com.aosika.phonelive.player_lhc_costom.fragment.VideoNoLoopPlayerFragment;
import com.aosika.phonelive.player_lhc_costom.interfaces.VideoControlListener;
import com.aosika.phonelive.player_lhc_costom.libs.adapter.VerticalNoLoopAdapter;
import com.aosika.phonelive.player_lhc_costom.libs.tansformer.DefaultTransformer;
import com.aosika.phonelive.player_lhc_costom.libs.widght.SwipeRefresh;
import com.aosika.phonelive.player_lhc_costom.libs.widght.VerticalNoLoopViewPager;
import com.aosika.phonelive.utils.ToastUtil;
import com.aosika.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import fr.castorflex.android.verticalviewpager.VerticalViewPager;

import static android.view.View.OVER_SCROLL_NEVER;

/**
 * Created by Administrator on 2017/12/15.
 */

public class VerticalNoLoopFragment extends Fragment implements ViewPager.OnPageChangeListener,
        VideoControlListener {

    private VerticalViewPager mViewPager;
    private SwipeRefresh sr_viewpager;
    private VerticalNoLoopAdapter verticalAdapter;
    private List<Fragment> mFragments = new ArrayList<>();
    private int mCurrentP;//当前位置
    private List<VideoBean> mVideoBeens = new ArrayList<>();
    private int mPage;//当前的页数
    private Videos mVideos;//所有的video数据
    private boolean mIsLoading;//正在加载更多中
    private boolean mIsLoadAll;//到底了加载完所有的数据的size

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verticalnoloop_veiwpager, container, false);
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
        mCurrentP = mVideos.getCurrentP();
        checkVideoList();
        newVideoNoLoopPlayerFragment(mVideoBeens);
    }

    private void initViews(View view) {
        sr_viewpager = (SwipeRefresh) view.findViewById(R.id.sr_viewpager);
        mViewPager = (VerticalViewPager) view.findViewById(R.id.pager);
    }

    private void initViewDatas() {
        verticalAdapter = new VerticalNoLoopAdapter(getChildFragmentManager(), mFragments);
        mViewPager.setAdapter(verticalAdapter);
      //  mViewPager.setPageTransformer(true, new DefaultTransformer());
        //mViewPager.setOverScrollMode(OVER_SCROLL_NEVER);
        mViewPager.setCurrentItem(mCurrentP);
        mViewPager.setOffscreenPageLimit(3);
    }

    private void initListeners() {
        mViewPager.setOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.e("懒加载", "-----onPageSelected----" + position);
        mCurrentP = position;
        checkVideoList();
        ((VerticalVideoPlayerActivity) getActivity()).setUserDatas(mVideoBeens.get(mCurrentP).getUserinfo().getId());

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((VideoNoLoopPlayerFragment) mFragments.get(mCurrentP)).onVideoResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((VideoNoLoopPlayerFragment) mFragments.get(mCurrentP)).onVideoPause();
        EventBus.getDefault().post(new PositionEvent(mCurrentP));
    }

    /**
     * 滑到最后一个进行检测数据
     */
    private void checkVideoList() {
        if (mIsLoadAll) {
            return;
        }
        if (mIsLoading) {
            return;
        }
        if (mCurrentP == mVideoBeens.size() - 1) {
            mPage++;
            mIsLoading = true;
            if(Constant.getFlag()==Constant.FLAG_VIDEO){
                HttpUtil.getVideoList(mPage, mLoadMoreCallback);
            }else if(Constant.getFlag()==Constant.FLAG_DIALECT){
                HttpUtil.getDialectList(mPage, MainActivity.dialectBean.getType(), mLoadMoreCallback);
            }else if(Constant.getFlag()==Constant.FLAG_MYVIDEO){
                HttpUtil.getHomeVideo(mVideoBeens.get(0).getUid(), mPage, mLoadMoreCallback);
            }
            EventBus.getDefault().post(new PositionEvent(mVideoBeens.size()+1));

        }
    }
    private HttpCallbackBase mLoadMoreCallback = new HttpCallbackBase() {
        @Override
        public void onSuccess(String data) {
            JSONObject jsonObject = JSON.parseObject(data);
            String code = jsonObject.getString("code");
            String msg = jsonObject.getString("msg");
            String info = jsonObject.getString("info");
            if ("0".equals(code)) {
                List<VideoBean> list = JSON.parseArray(info, VideoBean.class);
                if (list.size() > 0) {
                    mVideoBeens.addAll(list);
                    newVideoNoLoopPlayerFragment(list);
                    verticalAdapter.notifyDataSetChanged();
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

        }
    };


    private VideoNoLoopPlayerFragment personalFragment;
    private void newVideoNoLoopPlayerFragment(List<VideoBean> list) {
        for (int i = 0; i < list.size(); i++) {
            personalFragment = new VideoNoLoopPlayerFragment();
            Bundle mBundle = new Bundle();
            mBundle.putParcelable("videoBean", list.get(i));
            personalFragment.setArguments(mBundle);
            mFragments.add(personalFragment);
        }

    }

    @Override
    public void play() {
        ((VideoControlListener)mFragments.get(mCurrentP)).play();
    }

    @Override
    public void pasue() {
        ((VideoControlListener)mFragments.get(mCurrentP)).pasue();
    }

    @Override
    public void stop() {

    }

    @Override
    public void replay() {

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFragments.clear();
        mFragments=null;
    }
}
