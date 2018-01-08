package com.aosika.phonelive.player_lhc_costom.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aosika.phonelive.R;
import com.aosika.phonelive.bean.VideoBean;
import com.aosika.phonelive.glide.ImgLoader;
import com.aosika.phonelive.http.HttpCallbackBase;
import com.aosika.phonelive.http.HttpUtil;
import com.aosika.phonelive.player_lhc_costom.constant.Constant;
import com.aosika.phonelive.player_lhc_costom.interfaces.VideoControlListener;
import com.aosika.phonelive.player_lhc_costom.interfaces.VideoOneControl;
import com.aosika.phonelive.player_lhc_costom.interfaces.ViewpagerCompeledListener;
import com.aosika.phonelive.player_lhc_costom.libs.fragment.BaseFragment;

import chuangyuan.ycj.videolibrary.sample.SampleVideoInfoListener;
import chuangyuan.ycj.videolibrary.video.GestureVideoPlayerNo;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerViewNo;


/**
 * Created by lhc on 2017/11/4.
 * 无循环视频播放
 */

public class VideoNoLoopPlayerFragment3One extends BaseFragment implements VideoOneControl {

    private VideoPlayerViewNo mVideoView;
    private GestureVideoPlayerNo mExoPlayerManager;
    private View mLoading;
    private boolean mFirst = true;
    private boolean mPaused;//手动控制
    private boolean mVideoPaused;//状态控制
    private OnPlayListener mPlayListener;
    private VideoBean mVideoBean;
    private View mRootView;
    private ImageView iv_video_look_loading_bg;
    private VideoFramePresenter mVideoFramePresenter;//Video上层逻辑处理Presenter
    private FrameLayout mReplaced;
    private FrameLayout mFlVideostop;
    private VideoStopPresenter mVideoStopPresenter;
    private boolean mPlayWhenReady;//是否播放  true 播放；false 暂停
    private ViewpagerCompeledListener mViewpagerCompeledListener;

    public VideoNoLoopPlayerFragment3One(ViewpagerCompeledListener viewpagerCompeledListener){
        mViewpagerCompeledListener=viewpagerCompeledListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        if (mRootView != null) {
            System.out.println(">>>>>>>>懒加载复用View");
            return mRootView;
        }
        mRootView = inflater.inflate(R.layout.fragment_video_playerno, container, false);
        initDatas();
        initViews();
        initViewDatas();
        initListeners();
        return mRootView;
    }

    @Override
    public void loadData() {

    }

    @Override
    public void detachData() {

    }

    private void initDatas(){
        mVideoBean=getArguments().getParcelable("videoBean");
    }


    private void initViews() {
        iv_video_look_loading_bg = (ImageView) mRootView.findViewById(R.id.iv_video_look_loading_bg);
        mVideoView = (VideoPlayerViewNo) mRootView.findViewById(R.id.video_view);
        mLoading = mRootView.findViewById(R.id.loading);
        mReplaced = (FrameLayout) mRootView.findViewById(R.id.replaced);
        mFlVideostop = (FrameLayout) mRootView.findViewById(R.id.fl_videostop);
    }

    private void initViewDatas() {
        ImgLoader.display(mVideoBean.getThumb(), iv_video_look_loading_bg);
        mVideoView.onVisibilityChange(View.GONE);
        mExoPlayerManager = new GestureVideoPlayerNo(getActivity(), mVideoView);
        mExoPlayerManager.setVideoInfoListener(mSampleVideoInfoListener);
        mExoPlayerManager.setLooping(1);
        mVideoFramePresenter = new VideoFramePresenter(this, mVideoBean, mReplaced);
        mVideoFramePresenter.setOnViewPagerCompeledListener(mViewpagerCompeledListener);

    }

    private void initListeners() {
        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showVideoStopDialog();
                pause();
            }
        });
    }





    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println(">>>>>>>----onDestroy");
        if (mExoPlayerManager != null) {
            mExoPlayerManager.onBackPressed();
            mExoPlayerManager.onDestroy();
        }
        if (mVideoFramePresenter != null) {
            mVideoFramePresenter.closePlayer();
        }

    }


    /**
     * 播放状态回调
     */
    private SampleVideoInfoListener mSampleVideoInfoListener = new SampleVideoInfoListener() {
        @Override
        public void onPlayStart() {
            System.out.println("视频监听onPlayStart");
            if (mFirst) {
                mFirst = false;
                if (mPlayListener != null) {
                    mPlayListener.onFirstFrame();
                }
            }
            //隐藏封面
            if(!mPaused){
                iv_video_look_loading_bg.setVisibility(View.GONE);
            }else{
                mPaused=false;
            }


            if (mPlayListener != null) {
                mPlayListener.onLoadingEnd();
            }

        }

        @Override
        public void onLoadingChanged() {
            System.out.println("视频监听onLoadingChanged");
            if (mPlayListener != null) {
                mPlayListener.onLoadingStart();
            }
        }

        @Override
        public void onPlayEnd() {
            System.out.println("视频监听onPlayEnd");
            //显示封面
            iv_video_look_loading_bg.setVisibility(View.VISIBLE);
            showVideoStopDialog();
        }

        @Override
        public void isPlaying(boolean playWhenReady) {
            if(playWhenReady==mPlayWhenReady){
                return;
            }
            mPlayWhenReady=playWhenReady;
            System.out.println("视频监听isPlaying" + playWhenReady);
            if (mVideoFramePresenter != null) {
                if (playWhenReady) {
                    mVideoFramePresenter.getTvVideoplaytime().resume(Constant.getTimePlay());
                } else {
                    saveTime();
                }

            }
        }
    };


    public void showVideoStopDialog() {
        if (mVideoStopPresenter == null) {
            mVideoStopPresenter = new VideoStopPresenter(mVideoFramePresenter, mFlVideostop, getContext(), mVideoBean);
        } else {
            if (mVideoStopPresenter.isAdded()) {
            } else {
                mVideoStopPresenter = new VideoStopPresenter(mVideoFramePresenter, mFlVideostop, getContext(), mVideoBean);
            }
        }
    }

    /**
     * 设置视频播放状态回调监听
     * @param playListener
     */
    public void setPlayListener(OnPlayListener playListener) {
        mPlayListener = playListener;
    }

    public interface OnPlayListener {
        void onFirstFrame();

        void onLoadingStart();

        void onLoadingEnd();

    }

    /**
     * 切后台
     */
    public void onVideoPause() {
        mVideoPaused = true;
        if (mExoPlayerManager != null) {
            mExoPlayerManager.setStartOrPause(false);
        }

    }

    /**
     * 页面恢复
     */
    public void onVideoResume() {
        if (mVideoPaused && !mPaused) {
            mVideoPaused = false;
            if (mExoPlayerManager != null) {
                mExoPlayerManager.setStartOrPause(true);
            }
        }

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println(">>>>>>>>>>>onDestroyView");
       /* if (null != mRootView) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
            mRootView=null;
            mVideoFramePresenter=null;
            mVideoStopPresenter=null;
            mExoPlayerManager=null;
        }*/
        if (mExoPlayerManager != null) {
            mExoPlayerManager.onBackPressed();
            mExoPlayerManager.onDestroy();
        }
        if (mVideoFramePresenter != null) {
            mVideoFramePresenter.closePlayer();
        }
    }


    /**
     * 保存并上传观看时长
     */
    public void saveTime(){
        Constant.setTimePlay(mVideoFramePresenter.getTvVideoplaytime().pause());
        //上传观看时间
        HttpUtil.addlooktimes(Constant.getTimePlay()/1000, new HttpCallbackBase() {
            @Override
            public void onSuccess(String data) {

            }
        });
    }


    /**
     * 重新加载数据 并开始播放
     * @param videoBean
     */
    public void reloadDatas(VideoBean videoBean){

        mVideoBean=videoBean;
        if (mVideoStopPresenter != null && mVideoStopPresenter.isAdded()) {
            mVideoStopPresenter.dismissDialog();
        }
        ImgLoader.display(mVideoBean.getThumb(), iv_video_look_loading_bg);
        iv_video_look_loading_bg.setVisibility(View.VISIBLE);
        mVideoFramePresenter.setDatas(mVideoBean);
        if (mExoPlayerManager != null) {
            //设置uri自动并初始化播放器自动播放
            mExoPlayerManager.clearResumePosition();//清除进度
            mExoPlayerManager.setPlayUri(mVideoBean.getHref());
            mExoPlayerManager.setStartOrPause(true);

        }
    }

    @Override
    public void play(){
        mPaused=false;
        if (mExoPlayerManager != null) {
            mExoPlayerManager.setStartOrPause(true);
        }
    }
    public void replay(){
        mVideoView.repplay();
    }

    public void pause(){
        mPaused = true;
        if (mExoPlayerManager != null) {
            mExoPlayerManager.setStartOrPause(false);
        }
    }
    public void stop(){
        if (mExoPlayerManager != null) {
            mExoPlayerManager.onPause();//停止播放
            mExoPlayerManager.clearResumePosition();//清除进度
        }
    }

}
