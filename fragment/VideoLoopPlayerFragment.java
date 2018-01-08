package com.aosika.phonelive.player_lhc_costom.fragment;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aosika.phonelive.R;
import com.aosika.phonelive.bean.VideoBean;
import com.aosika.phonelive.glide.ImgLoader;
import com.aosika.phonelive.player_lhc_costom.libs.fragment.BaseFragment;
import com.google.android.exoplayer2.ExoPlaybackException;

import java.io.IOException;

import chuangyuan.ycj.videolibrary.listener.VideoInfoListener;
import chuangyuan.ycj.videolibrary.video.GestureVideoPlayer;
import chuangyuan.ycj.videolibrary.video.GestureVideoPlayerNo;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerViewNo;


/**
 * Created by cxf on 2017/11/4.
 */

public class VideoLoopPlayerFragment extends BaseFragment {

    private VideoPlayerViewNo mVideoView;
    private GestureVideoPlayerNo mExoPlayerManager;
    private View mLoading;
    private boolean mFirst = true;
    private boolean mPaused;
    private OnPlayListener mPlayListener;
    private VideoBean mVideoBean;
    private View mRootView;

    private ImageView iv_video_look_loading_bg;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        if(mRootView!=null){
            System.out.println(">>>>>>>>懒加载复用View");
            return mRootView;
        }
        mRootView = inflater.inflate(R.layout.fragment_video_playerno, container, false);
        initDatas();
        initViews();
        initViewDatas();
        main();
        return mRootView;
    }

    private void initDatas(){
        mVideoBean=getArguments().getParcelable("videoBean");
    }
    private void initViews(){
        iv_video_look_loading_bg= (ImageView) mRootView.findViewById(R.id.iv_video_look_loading_bg);
    }
    private void initViewDatas(){
        if(mVideoBean!=null){
            ImgLoader.display(mVideoBean.getThumb(),iv_video_look_loading_bg);
        }

    }
    protected void main() {
        mVideoView = (VideoPlayerViewNo) mRootView.findViewById(R.id.video_view);
        mVideoView.onVisibilityChange(View.GONE);
        mLoading = mRootView.findViewById(R.id.loading);
        mExoPlayerManager = new GestureVideoPlayerNo(getActivity(), mVideoView);
        mExoPlayerManager.setVideoInfoListener(new VideoInfoListener() {
            @Override
            public void onPlayStart() {
                if (mFirst) {
                    mFirst = false;
                    if (mPlayListener != null) {
                        mPlayListener.onFirstFrame();
                    }
                }
                hideLoading();
                iv_video_look_loading_bg.setVisibility(View.GONE);
                if (mPlayListener != null) {
                    mPlayListener.onLoadingEnd();
                }
            }

            @Override
            public void onLoadingChanged() {
                showLoading();
                if (mPlayListener != null) {
                    mPlayListener.onLoadingStart();
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException e) {

            }

            @Override
            public void onPlayEnd() {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void isPlaying(boolean playWhenReady) {

            }
        });
        mExoPlayerManager.setLooping(Integer.MAX_VALUE);
        //mExoPlayerManager.setPlayUri(getArguments().getString(URL));
    }


    /**
     * 播放
     */
    @Override
    public void loadData() {
        if (mExoPlayerManager != null) {
            mExoPlayerManager.setPlayUri(mVideoBean.getHref());

        }

    }

    /**
     * 暂停
     */
    @Override
    public void detachData() {
        if (mExoPlayerManager != null) {
            mExoPlayerManager.onPause();
            mExoPlayerManager.clearResumePosition();//清除进度
            iv_video_look_loading_bg.setVisibility(View.VISIBLE);
        }
    }


    public void setPlayListener(OnPlayListener playListener) {
        mPlayListener = playListener;
    }

    private void showLoading() {
        /*if (mLoading.getVisibility() != View.VISIBLE) {
            mLoading.setVisibility(View.VISIBLE);
        }*/
    }

    private void hideLoading() {

        /*if (mLoading.getVisibility() == View.VISIBLE) {
            mLoading.setVisibility(View.INVISIBLE);
        }*/
    }




    public void onVideoPause(){
        mPaused = true;
        if (mExoPlayerManager != null) {
            mExoPlayerManager.onPause();
        }
    }
    public void onVideoResume(){
        if (mPaused) {
            mPaused = false;
            if (mExoPlayerManager != null) {
                mExoPlayerManager.onResume();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mExoPlayerManager != null) {
            mExoPlayerManager.onBackPressed();
            mExoPlayerManager.onDestroy();
        }
    }


    /**
     *
     */
    public void setDatas(VideoBean videoBean){
        mVideoBean=videoBean;
        reLoad();
    }

    private void reLoad(){
        ImgLoader.display(mVideoBean.getThumb(),iv_video_look_loading_bg);
    }



    public interface OnPlayListener {
        void onFirstFrame();

        void onLoadingStart();

        void onLoadingEnd();

    }




}
