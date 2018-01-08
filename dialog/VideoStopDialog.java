package com.aosika.phonelive.player_lhc_costom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aosika.phonelive.R;
import com.aosika.phonelive.player_lhc_costom.constant.Constant;
import com.aosika.phonelive.player_lhc_costom.interfaces.ThumbUpListener;
import com.aosika.phonelive.player_lhc_costom.interfaces.VideoContentListener;
import com.aosika.phonelive.player_lhc_costom.weight.circleprogress.CircularProgressBar;

/**
 * Created by cxf on 2017/8/19.
 * 观众直播间送礼物弹窗
 */

public class VideoStopDialog extends DialogFragment implements View.OnClickListener {

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

    public VideoStopDialog(VideoContentListener videoContentListener){
        mVideoContentListener=videoContentListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.dialog2);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_videostop, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height =  WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        mCircularProgressBar.setMax(100);
        mCircularProgressBar.setPrimaryColor(getContext().getResources().getColor(R.color.colorPrimary));
        if(mVideoContentListener.getIsLike()==1){
            mIvZan.setImageResource(R.mipmap.icon_lauded);//已赞
        }
        mTvTomore.setText(getString(R.string.tomore)+"\n"+10);
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

        switch (view.getId()){
            case R.id.fl_tomore:
                mCurrentTomore++;
                mCircularProgressBar.setProgress(mCurrentTomore);
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
        dismiss();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }



}
