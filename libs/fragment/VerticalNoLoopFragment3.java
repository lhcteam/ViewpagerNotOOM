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
import com.alibaba.fastjson.JSONObject;
import com.aosika.phonelive.R;
import com.aosika.phonelive.activity.MainActivity;
import com.aosika.phonelive.bean.VideoBean;
import com.aosika.phonelive.event.PositionEvent;
import com.aosika.phonelive.http.HttpCallbackBase;
import com.aosika.phonelive.http.HttpUtil;
import com.aosika.phonelive.player_lhc_costom.VerticalVideoPlayerActivity;
import com.aosika.phonelive.player_lhc_costom.bean.Videos;
import com.aosika.phonelive.player_lhc_costom.constant.Constant;
import com.aosika.phonelive.player_lhc_costom.fragment.VideoNoLoopPlayerFragment;
import com.aosika.phonelive.player_lhc_costom.fragment.VideoNoLoopPlayerFragment3;
import com.aosika.phonelive.player_lhc_costom.interfaces.VideoControlListener;
import com.aosika.phonelive.player_lhc_costom.libs.adapter.VerticalNoLoopAdapter;
import com.aosika.phonelive.player_lhc_costom.libs.widght.SwipeRefresh;
import com.aosika.phonelive.utils.ToastUtil;
import com.aosika.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by Administrator on 2017/12/15.
 */

public class VerticalNoLoopFragment3 extends Fragment implements ViewPager.OnPageChangeListener,
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
    private int mCurrentRelativeP;//viewpager相对位置

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
        mViewPager.setCurrentItem(mCurrentRelativeP);
        mViewPager.setOffscreenPageLimit(3);
    }

    private void initListeners() {
        mViewPager.setOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        System.out.println(">>>>>>>position--" + position + "---positionOffset---" + positionOffset + "---positionOffsetPixels---" + positionOffsetPixels);
        if (0 != positionOffset) return;
        /*if(mCurrentP==position){
            return;
        }*/
      /*  if(position==2){
           *//* for(int i=0;i<mFragments.size();i++){
                ((VideoNoLoopPlayerFragment)mFragments.get(i)).setVideoBean();
            }*//*
            mViewPager.setCurrentItem(0,false);
        }*/

    }

    @Override
    public void onPageSelected(int position) {
        System.out.println(">>>>>>>position--onPageSelected" + position);
       /* if(position==0){
            if(mCurrentP==0){

            }else{
                for(int i=0;i<mFragments.size();i++){
                    ((VideoNoLoopPlayerFragment)mFragments.get(i)).setVideoBean();
                }
                mViewPager.setCurrentItem(1);
            }

        }*/
        loadPosition(position);
        mCurrentRelativeP = position;
        System.out.println(">>>>>>>position--onPageSelected-----" + mCurrentP);

        checkVideoList();
        ((VerticalVideoPlayerActivity) getActivity()).setUserDatas(mVideoBeens.get(mCurrentP).getUserinfo().getId());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        System.out.println(">>>>>>>position--onPageScrollStateChanged" + state);
        // ViewPager.SCROLL_STATE_IDLE 标识的状态是当前页面完全展现，并且没有动画正在进行中，如果不
        // 是此状态下执行 setCurrentItem 方法回在首位替换的时候会出现跳动！
        if (state != ViewPager.SCROLL_STATE_IDLE) return;
        if (mCurrentRelativeP == 2) {
           if(mCurrentP==mVideoBeens.size()-1){
               return;
           }
            for(int i=0;i<mFragments.size();i++){
                ((VideoNoLoopPlayerFragment3)mFragments.get(i)).setVideoBean(mVideoBeens.get(mCurrentP+(i-1)));
                if(i==1){
                    ((VideoNoLoopPlayerFragment3)mFragments.get(i)).loadData();
                }else{
                    ((VideoNoLoopPlayerFragment3)mFragments.get(i)).detachData();
                }
            }
            mViewPager.setCurrentItem(1, false);
        }
        if (mCurrentRelativeP == 0) {
            if(mCurrentP==0){
                return;
            }
            for(int i=0;i<mFragments.size();i++){
                ((VideoNoLoopPlayerFragment3)mFragments.get(i)).setVideoBean(mVideoBeens.get(mCurrentP+(i-1)));
                if(i==1){
                    ((VideoNoLoopPlayerFragment3)mFragments.get(i)).loadData();
                }else{
                    ((VideoNoLoopPlayerFragment3)mFragments.get(i)).detachData();
                }
            }
            mViewPager.setCurrentItem(1, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((VideoNoLoopPlayerFragment3) mFragments.get(mCurrentRelativeP)).onVideoResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((VideoNoLoopPlayerFragment3) mFragments.get(mCurrentRelativeP)).onVideoPause();
    }

    private int mOldP;//fragment在viewpager的上一个位置
    private void loadPosition(int position) {
        if(position==1){
            if(mCurrentP==0||mCurrentP==mVideoBeens.size()-1){
            }else{
                mOldP=1;
                return;
            }
        }
        if (position == 0) {
            if (mOldP == 1) {//Top
                mCurrentP--;
            } else {//down
                mCurrentP++;
            }
        } else if (position == 1) {
            if (mOldP == 2) {//Top
                mCurrentP--;
            } else {//dowm
                mCurrentP++;
            }
        } else if (position == 2) {
            if (mOldP == 0) {//Top
                mCurrentP--;
            } else {//dowm
                mCurrentP++;
            }
        }
        mOldP = position;
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
            if (Constant.getFlag() == Constant.FLAG_VIDEO) {
                HttpUtil.getVideoList(mPage, mLoadMoreCallback);
            } else if (Constant.getFlag() == Constant.FLAG_DIALECT) {
                HttpUtil.getDialectList(mPage, MainActivity.dialectBean.getType(), mLoadMoreCallback);
            } else if (Constant.getFlag() == Constant.FLAG_MYVIDEO) {
                HttpUtil.getHomeVideo(mVideoBeens.get(0).getUid(), mPage, mLoadMoreCallback);
            }
            EventBus.getDefault().post(new PositionEvent(mVideoBeens.size() + 1));

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

    private VideoNoLoopPlayerFragment3 personalFragment;
    private void newVideoNoLoopPlayerFragment(List<VideoBean> list) {

        if (list.size() <= 3) {
            for (int i = 0; i < list.size(); i++) {
                personalFragment = new VideoNoLoopPlayerFragment3();
                Bundle mBundle = new Bundle();
                mBundle.putParcelable("videoBean", list.get(i));
                personalFragment.setArguments(mBundle);
                mFragments.add(personalFragment);
            }
            mCurrentRelativeP = mCurrentP;
        } else {
            if (mCurrentP == 0) {
                for (int i = 0; i < 3; i++) {
                    personalFragment = new VideoNoLoopPlayerFragment3();
                    Bundle mBundle = new Bundle();
                    mBundle.putParcelable("videoBean", list.get(i));
                    personalFragment.setArguments(mBundle);
                    mFragments.add(personalFragment);
                }
                mCurrentRelativeP = mCurrentP;
            } else if (mCurrentP == list.size() - 1) {
                for (int i = list.size() - 3; i < list.size(); i++) {
                    personalFragment = new VideoNoLoopPlayerFragment3();
                    Bundle mBundle = new Bundle();
                    mBundle.putParcelable("videoBean", list.get(i));
                    personalFragment.setArguments(mBundle);
                    mFragments.add(personalFragment);
                }
                mCurrentRelativeP = list.size() - 1;
            } else {
                for (int i = mCurrentP - 1; i < mCurrentP + 2; i++) {
                    personalFragment = new VideoNoLoopPlayerFragment3();
                    Bundle mBundle = new Bundle();
                    mBundle.putParcelable("videoBean", list.get(i));
                    personalFragment.setArguments(mBundle);
                    mFragments.add(personalFragment);
                }
                mCurrentRelativeP = mCurrentP;
            }
        }
    }


    public void loadMore() {

    }

    @Override
    public void play() {
        ((VideoControlListener) mFragments.get(mCurrentP)).play();
    }

    @Override
    public void pasue() {
        ((VideoControlListener) mFragments.get(mCurrentP)).pasue();
    }

    @Override
    public void stop() {

    }

    @Override
    public void replay() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new PositionEvent(mCurrentP));
    }
}
