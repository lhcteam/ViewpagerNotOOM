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
import com.aosika.phonelive.player_lhc_costom.interfaces.VideoOneControl;
import com.aosika.phonelive.player_lhc_costom.libs.fragment.BaseFragment;

import chuangyuan.ycj.videolibrary.sample.SampleVideoInfoListener;
import chuangyuan.ycj.videolibrary.video.GestureVideoPlayerNo;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerViewNo;


/**
 * Created by lhc on 2017/11/4.
 * 无循环视频播放
 */

public class VideoNoLoopPlayerFragment3Orther extends BaseFragment implements VideoOneControl {

    private VideoBean mVideoBean;
    private View mRootView;
    private ImageView iv_video_look_loading_bg;
    private VideoFramePresenter mVideoFramePresenter;//Video上层逻辑处理Presenter
    private FrameLayout mReplaced;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        if (mRootView != null) {
            System.out.println(">>>>>>>>懒加载复用View");
            return mRootView;
        }
        mRootView = inflater.inflate(R.layout.fragment_video_playerno_orther, container, false);
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
    }

    private void initViews() {
        iv_video_look_loading_bg = (ImageView) mRootView.findViewById(R.id.iv_video_look_loading_bg);
        mReplaced = (FrameLayout) mRootView.findViewById(R.id.replaced);
    }

    private void initViewDatas() {
        if(mVideoBean!=null){
            ImgLoader.display(mVideoBean.getThumb(), iv_video_look_loading_bg);
            if(mVideoFramePresenter!=null){
                mVideoFramePresenter.setDatas(mVideoBean);
            }else{
                mVideoFramePresenter = new VideoFramePresenter(this, mVideoBean, mReplaced);
            }
        }


    }

    private void initListeners() {

    }








    /**
     * 切后台
     */
    public void onVideoPause() {

    }

    /**
     * 页面恢复
     */
    public void onVideoResume() {


    }






    /**
     * 重新加载数据 并开始播放
     * @param videoBean
     */
    public void reloadDatas(VideoBean videoBean){
        if(videoBean!=null){
            mVideoBean=videoBean;
            iv_video_look_loading_bg.setVisibility(View.VISIBLE);
           initViewDatas();

        }

    }

    public void play(){

    }
    public void replay(){

    }

    public void pause(){

    }
    public void stop(){

    }

}
