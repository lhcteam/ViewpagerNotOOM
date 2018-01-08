package com.aosika.phonelive.player_lhc_costom.fragment;

/**
 * Created by mengyunfeng on 17/12/22.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aosika.phonelive.AppConfig;
import com.aosika.phonelive.R;
import com.aosika.phonelive.bean.VideoBean;
import com.aosika.phonelive.http.HttpCallbackBase;
import com.aosika.phonelive.http.HttpUtil;
import com.aosika.phonelive.player_lhc_costom.constant.Constant;
import com.aosika.phonelive.player_lhc_costom.dialog.LoginDialog;
import com.aosika.phonelive.player_lhc_costom.interfaces.ThumbUpListener;
import com.aosika.phonelive.player_lhc_costom.interfaces.VideoContentListener;
import com.aosika.phonelive.player_lhc_costom.weight.circleprogress.CircularProgressBar;
import com.aosika.phonelive.utils.DialogUitl;
import com.aosika.phonelive.utils.ToastUtil;


/**
 * Created by cxf on 2017/8/19.
 * 观众直播间送礼物弹窗
 */

public class VideoStopPresenter implements View.OnClickListener {

    protected Context mContext;
    private View mRootView;
    private CircularProgressBar mCircularProgressBar;
    private FrameLayout mFlTomore;
    private android.widget.ImageView mIvZan;
    private android.widget.ImageView mIvContinued;
    private android.widget.ImageView mIvReplay;
    private android.widget.ImageView mIvShareWx;
    private android.widget.ImageView mIvShareQq;
    private android.widget.ImageView mIvShareTwitter;
    private android.widget.ImageView mIvShareFacebook;
    private android.widget.ImageView mIvShareWchat;
    private android.widget.ImageView mIvShareQzone;
    private TextView mTvTomore;
    private VideoContentListener mVideoContentListener;
    private int mCurrentTomore;//当前的催更进度
    private int mMaxTomore;//总的需要催更的进度
    private int mPrice;//催更单价
    private ViewGroup mParentView;
    private boolean isAdd;//添加
    private int mType;//类型  0：短视频 1：方言秀
    private VideoBean mVideoBean;

    public VideoStopPresenter(VideoContentListener videoContentListener, ViewGroup parentView, Context context, VideoBean videoBean){
        mVideoContentListener=videoContentListener;
        mParentView=parentView;
        mContext=context;
        mVideoBean=videoBean;
        mType=videoBean.getIsdialect();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_videostop, mParentView, false);
        mParentView.addView(mRootView);
        isAdd=true;
        initView();
        initViewDatas();
        initListeners();
    }
    private void initView() {
        mCircularProgressBar= (CircularProgressBar) mRootView.findViewById(R.id.cpb_tomore);
        mFlTomore= (FrameLayout) mRootView.findViewById(R.id.fl_tomore);
        mIvZan = (ImageView) mRootView.findViewById(R.id.iv_zan);
        mIvContinued = (ImageView) mRootView.findViewById(R.id.iv_continued);
        mIvReplay = (ImageView) mRootView.findViewById(R.id.iv_replay);
        mIvShareWx = (ImageView) mRootView.findViewById(R.id.iv_share_wx);
        mIvShareQq = (ImageView) mRootView.findViewById(R.id.iv_share_qq);
        mIvShareTwitter = (ImageView) mRootView.findViewById(R.id.iv_share_twitter);
        mIvShareFacebook = (ImageView) mRootView.findViewById(R.id.iv_share_facebook);
        mIvShareWchat = (ImageView) mRootView.findViewById(R.id.iv_share_wchat);
        mIvShareQzone = (ImageView) mRootView.findViewById(R.id.iv_share_qzone);
        mTvTomore = (TextView) mRootView.findViewById(R.id.tv_tomore);
    }
    public void initViewDatas(){

        mCircularProgressBar.setPrimaryColor(mContext.getResources().getColor(R.color.colorPrimary));
        if(mVideoContentListener.getIsLike()==1){
            mIvZan.setImageResource(R.mipmap.icon_lauded);//已赞
        }
        if(mVideoBean.getUid().equals(AppConfig.getInstance().getUid())){
            mFlTomore.setVisibility(View.GONE);
        }else if(mVideoBean.getIs_urge()==1){//可以催更
            mFlTomore.setVisibility(View.GONE);
            getVideoUrge(mType,mVideoBean.getId());
        }else{
            mFlTomore.setVisibility(View.GONE);
        }

    }
    public void initListeners(){
        mFlTomore.setOnClickListener(this);
        mIvZan.setOnClickListener(this);
        mIvContinued.setOnClickListener(this);
        mIvReplay.setOnClickListener(this);
        mIvShareWx.setOnClickListener(this);
        mIvShareQq.setOnClickListener(this);
        mIvShareTwitter.setOnClickListener(this);
        mIvShareFacebook.setOnClickListener(this);
        mIvShareWchat.setOnClickListener(this);
        mIvShareQzone.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        //判断是否是游客
        if(view.getId()==R.id.iv_continued||view.getId()==R.id.iv_replay){

        }else{
            if(AppConfig.getInstance().getUid().equals(AppConfig.VISITOR)){
                LoginDialog.showLoginWarnDialog(mContext);
                return;
            }
        }

        switch (view.getId()){
            case R.id.fl_tomore:
                showWarnDialog(mPrice);
                break;
            case R.id.iv_zan:
                if(mVideoContentListener.getIsLike()==1){
                    return;
                }
                mVideoContentListener.thumbsUp(new ThumbUpListener() {
                    @Override
                    public void onSuccess() {
                        mIvZan.setImageResource(R.mipmap.icon_lauded);
                    }
                });
                break;
            case R.id.iv_continued:
                mVideoContentListener.play();
                dismissDialog();
                break;
            case R.id.iv_replay:
                mVideoContentListener.replay();
                dismissDialog();
                break;
            case R.id.iv_share_wx:
                mVideoContentListener.share(Constant.wx);
                break;
            case R.id.iv_share_qq:
                mVideoContentListener.share(Constant.qq);
                break;
            case R.id.iv_share_twitter:
                mVideoContentListener.share(Constant.twitter);
                break;
            case R.id.iv_share_facebook:
                mVideoContentListener.share(Constant.facebook);
                break;
            case R.id.iv_share_wchat:
                mVideoContentListener.share(Constant.wchat);
                break;
            case R.id.iv_share_qzone:
                mVideoContentListener.share(Constant.qzone);
                break;

        }
    }

    public void dismissDialog(){
        isAdd=false;
        mParentView.removeAllViews();
    }

    public boolean isAdded(){
        return isAdd;
    }


    /**
     *获取催更进度
     */
    private void getVideoUrge(int type,String videoid){
        HttpUtil.getVideoUrge(String.valueOf(mType), videoid, new HttpCallbackBase() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject= JSON.parseObject(data);
                if(jsonObject.getString("code").equals("0")){
                    JSONObject info=jsonObject.getJSONObject("info");
                    mMaxTomore=info.getInteger("big_urgenums");
                    mCurrentTomore=mMaxTomore-info.getInteger("shengyunums");
                    mPrice=info.getInteger("urge_money");
                    showDatas();
                }else{
                    ToastUtil.show(jsonObject.getString("msg"));
                }
            }
        });
    }

    private void showDatas(){
        mFlTomore.setVisibility(View.VISIBLE);
        mCircularProgressBar.setMax(mMaxTomore);
        mTvTomore.setText(mContext.getString(R.string.tomore)+"\n"+mPrice+AppConfig.getInstance().getName_coin());
        mCircularProgressBar.setProgress(mCurrentTomore);
    }

    /**
     * 催更
     */
    private void urgeVideo(){
        HttpUtil.urgeVideo(String.valueOf(mType), mVideoBean.getId(), new HttpCallbackBase() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject= JSON.parseObject(data);
                if(jsonObject.getString("code").equals("0")){
                    JSONObject info=jsonObject.getJSONObject("info");
                    mMaxTomore=info.getInteger("big_urgenums");
                    mCurrentTomore=mMaxTomore-info.getInteger("shengyunums");
                    mPrice=info.getInteger("urge_money");
                    showDatas();
                    ToastUtil.show(jsonObject.getString("msg"));
                }else{
                    ToastUtil.show(jsonObject.getString("msg"));
                }
            }
        });
    }


    private void showWarnDialog(int diamond){
        DialogUitl.confirmDialog(
                mContext,
                mContext.getString(R.string.tip),
                mContext.getString(R.string.need)+diamond+AppConfig.getInstance().getName_coin(),
                mContext.getString(R.string.confirm1),
                mContext.getString(R.string.cancel), true,
                new DialogUitl.Callback() {
                    @Override
                    public void confirm(Dialog dialog) {
                        dialog.dismiss();
                        urgeVideo();
                    }

                    @Override
                    public void cancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                }
        ).show();
    }
}
