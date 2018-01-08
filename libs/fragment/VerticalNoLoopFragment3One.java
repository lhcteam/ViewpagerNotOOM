package com.aosika.phonelive.player_lhc_costom.libs.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.aosika.phonelive.player_lhc_costom.fragment.VideoNoLoopPlayerFragment3One;
import com.aosika.phonelive.player_lhc_costom.fragment.VideoNoLoopPlayerFragment3Orther;
import com.aosika.phonelive.player_lhc_costom.interfaces.VideoControlListener;
import com.aosika.phonelive.player_lhc_costom.interfaces.VideoOneControl;
import com.aosika.phonelive.player_lhc_costom.interfaces.ViewpagerCompeledListener;
import com.aosika.phonelive.player_lhc_costom.libs.adapter.VerticalNoLoopAdapter;
import com.aosika.phonelive.player_lhc_costom.libs.widght.SwipeRefresh;
import com.aosika.phonelive.player_lhc_costom.libs.widght.VerticalViewPagerNoScroll;
import com.aosika.phonelive.utils.ToastUtil;
import com.aosika.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by Administrator on 2017/12/15.
 */

public class VerticalNoLoopFragment3One extends Fragment implements ViewPager.OnPageChangeListener,
        VideoControlListener ,ViewpagerCompeledListener{

    private VerticalViewPagerNoScroll mViewPager;
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
        mViewPager = (VerticalViewPagerNoScroll) view.findViewById(R.id.pager);
    }

    private void initViewDatas() {
        verticalAdapter = new VerticalNoLoopAdapter(getChildFragmentManager(), mFragments);
        mViewPager.setAdapter(verticalAdapter);
        //  mViewPager.setPageTransformer(true, new DefaultTransformer());
        //mViewPager.setOverScrollMode(OVER_SCROLL_NEVER);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                refrushDatas();
            }
        });
        mViewPager.setCurrentItem(1,false);
        System.out.println(">>>>>><<<<<<<<<1");
    }

    private void initListeners() {
        mViewPager.setOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        System.out.println(">>>>>>>position--" + position + "---positionOffset---" + positionOffset + "---positionOffsetPixels---" + positionOffsetPixels);
        System.out.println(">>>>>><<<<<<<<<2");
        if (0 != positionOffset) return;
        if(position==1){
            return;
        }
        mCurrentRelativeP = position;
        /*if(position==1){
            return;
        }
        mCurrentP=mCurrentP-(1-position);
        System.out.println(">>>>>><<<<<<<<<mCurrentP"+mCurrentP);
        mCurrentRelativeP = position;
        checkVideoList();
        ((VerticalVideoPlayerActivity) getActivity()).setUserDatas(mVideoBeens.get(mCurrentP).getUserinfo().getId());
        refrushDatas();*/


    }

    @Override
    public void onPageSelected(int position) {
        System.out.println(">>>>>>>position--onPageSelected" + position);
        System.out.println(">>>>>>>position--onPageSelected-----" + mCurrentP);


    }

    @Override
    public void onPageScrollStateChanged(int state) {
        System.out.println(">>>>>>>position--onPageScrollStateChanged" + state);
        // ViewPager.SCROLL_STATE_IDLE 标识的状态是当前页面完全展现，并且没有动画正在进行中，如果不
        // 是此状态下执行 setCurrentItem 方法回在首位替换的时候会出现跳动！
        if (state != ViewPager.SCROLL_STATE_IDLE) return;
        if(mCurrentRelativeP==1){
            return;
        }
        mCurrentP=mCurrentP-(1-mCurrentRelativeP);
        System.out.println(">>>>>><<<<<<<<<mCurrentP"+mCurrentP);

        checkVideoList();
        ((VerticalVideoPlayerActivity) getActivity()).setUserDatas(mVideoBeens.get(mCurrentP).getUserinfo().getId());
        refrushDatas();
    }

    private void refrushDatas(){
        mViewPager.setState(mViewPager.STATE_NOALL);
        ((VideoOneControl)mFragments.get(1)).reloadDatas(mVideoBeens.get(mCurrentP));
    }

    /**
     * 加载完数据进行回跳
     */
    @Override
    public void compeled() {

        mViewPager.setCurrentItem(1,false);
        for (int i = 0; i <3 ; i++) {
            if(i==1){
                continue;
            }
            if(mCurrentP==0){
                if(i==0){
                    continue;
                }
            }
            if(mCurrentP==mVideoBeens.size()-1){
                if(i==2){
                    continue;
                }
            }
            ((VideoOneControl)mFragments.get(i)).reloadDatas(mVideoBeens.get(mCurrentP+(i-1)));
        }
        if(mCurrentP==0){
            mViewPager.setState(mViewPager.STATE_NODOWN);
        }else if(mCurrentP==mVideoBeens.size()-1){
            mViewPager.setState(mViewPager.STATE_NOTOP);
        }else{
            mViewPager.setState(mViewPager.STATE_SCROLL);
        }

    }



    @Override
    public void onResume() {
        super.onResume();
        //((VideoNoLoopPlayerFragment) mFragments.get(mCurrentRelativeP)).onVideoResume();
    }

    @Override
    public void onPause() {
        super.onPause();
       // ((VideoNoLoopPlayerFragment) mFragments.get(mCurrentRelativeP)).onVideoPause();
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

    private void newVideoNoLoopPlayerFragment(List<VideoBean> list) {
        VideoNoLoopPlayerFragment3Orther v1=new VideoNoLoopPlayerFragment3Orther();
        mFragments.add(v1);
        VideoNoLoopPlayerFragment3One v2 = new VideoNoLoopPlayerFragment3One(this);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("videoBean", list.get(mCurrentP));
        v2.setArguments(mBundle);
        mFragments.add(v2);
        VideoNoLoopPlayerFragment3Orther v3=new VideoNoLoopPlayerFragment3Orther();
        mFragments.add(v3);
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
