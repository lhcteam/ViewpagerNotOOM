package com.aosika.phonelive.player_lhc_costom.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aosika.phonelive.AppConfig;
import com.aosika.phonelive.AppContext;
import com.aosika.phonelive.R;
import com.aosika.phonelive.activity.VideoActivity;
import com.aosika.phonelive.bean.UserBean;
import com.aosika.phonelive.bean.VideoBean;
import com.aosika.phonelive.event.EMChatExitEvent;
import com.aosika.phonelive.fragment.CommentDialogFragment;
import com.aosika.phonelive.fragment.CommentFragment;
import com.aosika.phonelive.fragment.VideoPlayerFragment;
import com.aosika.phonelive.fragment.VideoShareFragment;
import com.aosika.phonelive.glide.ImgLoader;
import com.aosika.phonelive.http.HttpCallback;
import com.aosika.phonelive.http.HttpCallbackBase;
import com.aosika.phonelive.http.HttpUtil;
import com.aosika.phonelive.interfaces.CommonCallback;
import com.aosika.phonelive.player_lhc_costom.VerticalVideoPlayerActivity;
import com.aosika.phonelive.player_lhc_costom.constant.Constant;
import com.aosika.phonelive.player_lhc_costom.dialog.LoginDialog;
import com.aosika.phonelive.player_lhc_costom.dialog.VideoStopDialog;
import com.aosika.phonelive.player_lhc_costom.interfaces.ThumbUpListener;
import com.aosika.phonelive.player_lhc_costom.interfaces.VideoContentListener;
import com.aosika.phonelive.player_lhc_costom.interfaces.VideoControlListener;
import com.aosika.phonelive.player_lhc_costom.interfaces.VideoOneControl;
import com.aosika.phonelive.player_lhc_costom.interfaces.ViewpagerCompeledListener;
import com.aosika.phonelive.player_lhc_costom.weight.TimeView;
import com.aosika.phonelive.utils.DateUtil;
import com.aosika.phonelive.utils.DialogUitl;
import com.aosika.phonelive.utils.SharedSdkUitl;
import com.aosika.phonelive.utils.ToastUtil;
import com.aosika.phonelive.utils.WordUtil;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.target.SquaringDrawable;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import org.greenrobot.eventbus.EventBus;

import cn.sharesdk.framework.Platform;

/**
 * Created by mengyunfeng on 17/12/20.
 */

public class VideoFramePresenter implements View.OnClickListener, VideoPlayerFragment.OnPlayListener ,
        VideoContentListener {

    


    private RelativeLayout mRlVideoRoot;
    private ImageView mIvVideoClose;
    private ImageView mBtnCai;
    private ImageView mIvVideoMore;
    private TextView mShareNums;
    private ImageView mIvVideoShare;
    private TextView mTvVideoLaudnum;
    private ImageView mIvVideoLaud;
    private TextView mTvVideoCommrntnum;
    private ImageView mIvVideoComment;
    private TextView mBtnComment;
    private ImageView mIvVideoEmceeHead;
    private TextView mTvName;
    private TextView mIvTitle;
    private ImageView mTvAttention;
    private ImageView mIvVideoLaudgif;
    private TimeView mTvVideoplaytime;
    private ImageView iv_timelock;

    private Dialog mLoadingDialog;
    private CommentFragmentPresenter mCommentFragment;
    String uid;
    private Handler mHandler;
    private int mScreenHeight;
    UserBean mEmceeInfo;
    private CommentDialogFragmentPrestener mDialogFragment;
    private int mIsLike = -1;
    private VideoBean mVideoBean;
    private String mPlayUrl;
    private int mPlaytime;//观看时间
    private boolean isCompleled;//是否加载完
    private View mRootView;

    private Fragment mContext;
    private ViewpagerCompeledListener mViewpagerCompeledListener;



    public VideoFramePresenter(Fragment context, VideoBean videoBean, ViewGroup container) {
        mContext=context;
        mVideoBean=videoBean;
        LayoutInflater layoutInflater=LayoutInflater.from(context.getContext());
        mRootView=layoutInflater.inflate(R.layout.view_videoframe,container,true);
        initData();
        initView(mRootView);
        initListeners();
        initViewDatas();
        isCompleled=true;

    }

    public void setDatas(VideoBean videoBean){
        if(isCompleled){
            mVideoBean=videoBean;
            mEmceeInfo = mVideoBean.getUserinfo();
            initViewDatas();
        }
    }
    public void loadData(){
      /*  if(isCompleled){
            initViewDatas();
        }*/
    }
    public void detachData(){
      /*  if(isCompleled){
            mTvAttention.setVisibility(View.GONE);
            mTvVideoLaudnum.setText("");
            mTvVideoCommrntnum.setText("");
            mShareNums.setText("");
        }*/

    }
    public void setVisiable(int visiable){
        if(visiable==View.VISIBLE){
            mRootView.setVisibility(View.VISIBLE);
        }else{
            mRootView.setVisibility(View.GONE);
        }
    }

    private void initData() {
        mEmceeInfo = mVideoBean.getUserinfo();
    }

    private void initView(View view) {
        mRlVideoRoot = (RelativeLayout) view.findViewById(R.id.rl_video_root);
        mIvVideoClose = (ImageView) view.findViewById(R.id.iv_video_close);
        mBtnCai = (ImageView) view.findViewById(R.id.btn_cai);
        mIvVideoMore = (ImageView) view.findViewById(R.id.iv_video_more);
        mShareNums = (TextView) view.findViewById(R.id.share_nums);
        mIvVideoShare = (ImageView) view.findViewById(R.id.iv_video_share);
        mTvVideoLaudnum = (TextView) view.findViewById(R.id.tv_video_laudnum);
        mIvVideoLaud = (ImageView) view.findViewById(R.id.iv_video_laud);
        mTvVideoCommrntnum = (TextView) view.findViewById(R.id.tv_video_commrntnum);
        mIvVideoComment = (ImageView) view.findViewById(R.id.iv_video_comment);
        mBtnComment = (TextView) view.findViewById(R.id.btn_comment);
        mIvVideoEmceeHead = (ImageView) view.findViewById(R.id.iv_video_emcee_head);
        mTvName = (TextView) view.findViewById(R.id.tv_name);
        mTvAttention = (ImageView) view.findViewById(R.id.tv_attention);
        mIvVideoLaudgif = (ImageView) view.findViewById(R.id.iv_video_laudgif);
        mIvTitle = (TextView) view.findViewById(R.id.title);
        mTvVideoplaytime = (TimeView) view.findViewById(R.id.tv_videoplaytime);
        iv_timelock = (ImageView) view.findViewById(R.id.iv_timelock);
    }

    private void initListeners() {
        mIvVideoClose.setOnClickListener(this);
        mIvVideoComment.setOnClickListener(this);
        mBtnComment.setOnClickListener(this);
        mIvVideoShare.setOnClickListener(this);
        mIvVideoMore.setOnClickListener(this);
        mIvVideoLaud.setOnClickListener(this);
        mBtnCai.setOnClickListener(this);
        mTvAttention.setOnClickListener(this);
        mIvVideoEmceeHead.setOnClickListener(this);
        mRlVideoRoot.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListenernew);
        //mRlVideoRoot.setOnClickListener(mClickListener);
    }




    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListenernew = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            //获取当前界面可视部分
            mContext.getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            //获取屏幕的高度
            if (mScreenHeight == 0) {
                mScreenHeight = r.height();
            }
            int visibleHeight = r.height();
            if (visibleHeight == mScreenHeight) {
                if (mCommentFragment != null) {
                    mCommentFragment.onSoftInputHide();
                }
            } else {
                if (mCommentFragment != null) {
                    mCommentFragment.onSoftInputShow(visibleHeight);
                }
            }
        }
    };


    public void initViewDatas() {
        if(AppConfig.getInstance().getUid().equals(AppConfig.VISITOR)){
            mTvVideoplaytime.setVisibility(View.GONE);
            iv_timelock.setVisibility(View.GONE);
        }else{
            mTvVideoplaytime.setVisibility(View.VISIBLE);
            iv_timelock.setVisibility(View.VISIBLE);
        }
        //mTvVideoplaytime.resume(Constant.getTimePlay());
        uid = AppConfig.getInstance().getUid();
        mContext.getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);


        HttpUtil.getVideoInfo(mVideoBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {

                        JSONObject info0 = JSON.parseObject(info[0]);
                        mVideoBean=JSON.parseObject(info[0],VideoBean.class);
                        showDatas();
                        if(mViewpagerCompeledListener!=null){
                            mViewpagerCompeledListener.compeled();
                        }
                        if(!AppConfig.getInstance().getUid().equals(AppConfig.VISITOR)){
                            long all_looktimes = info0.getLong("all_looktimes");
                            if(Constant.getTimePlay()==0){
                                Constant.setTimePlay(all_looktimes);
                            }
                            mTvVideoplaytime.resume(Constant.getTimePlay());
                        }
                    }

                } else {
                    ToastUtil.show(msg);
                }

            }
        });

    }


    public void showDatas(){
        if (!uid.equals(mVideoBean.getUid())) {
            if(mVideoBean.getIslike().equals("1")){
                if(mIvVideoLaud!=null){
                    mIvVideoLaud.setImageResource(R.mipmap.icon_lauded);
                }

            }else{
                if(mIvVideoLaud!=null){
                    mIvVideoLaud.setImageResource(R.mipmap.icon_video_zan);
                }

            }
            if(mVideoBean.getIsattent().equals("1")){
                mTvAttention.setVisibility(View.GONE);
            }else{
                mTvAttention.setVisibility(View.VISIBLE);
            }
            if (1 == mVideoBean.getIsstep()) {
                if(mBtnCai!=null){
                    mBtnCai.setImageResource(R.mipmap.icon_video_cai_selected);
                }
            }else{
                if(mBtnCai!=null){
                    mBtnCai.setImageResource(R.mipmap.icon_video_cai);
                }

            }
        } else {
            mTvAttention.setVisibility(View.GONE);
            mBtnCai.setVisibility(View.GONE);
            mIvVideoLaud.setImageResource(R.mipmap.icon_video_zan);
        }
        mTvName.setText(mVideoBean.getUserinfo().getUser_nicename());
        ImgLoader.displayCircle(mVideoBean.getUserinfo().getAvatar(), mIvVideoEmceeHead);
        mIvTitle.setText(mVideoBean.getTitle());
        mPlayUrl = mVideoBean.getHref();
        mTvVideoLaudnum.setText(mVideoBean.getLikes());
        mTvVideoCommrntnum.setText(mVideoBean.getComments());
        mShareNums.setText(mVideoBean.getShares());
    }

    public void setOnViewPagerCompeledListener(ViewpagerCompeledListener viewPagerCompeledListener){
        if(mViewpagerCompeledListener==null){
            mViewpagerCompeledListener=viewPagerCompeledListener;
        }
    }


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.iv_video_close||v.getId()==R.id.iv_replay||v.getId()==R.id.iv_video_share||v.getId()==R.id.iv_video_more||v.getId()==R.id.iv_video_comment){
        }else{
            if(AppConfig.getInstance().getUid().equals(AppConfig.VISITOR)){
                LoginDialog.showLoginWarnDialog(mContext.getContext());
                return;
            }
        }

        switch (v.getId()) {
            case R.id.tv_attention:
                attention();
                break;
            case R.id.iv_video_comment:
                showCommentDialog();
                break;
            case R.id.btn_comment:
                showCommentDialog2();
                break;
            case R.id.iv_video_share:

            case R.id.iv_video_more:
                showSharePopWindow2();
                break;
            case R.id.iv_video_laud:
                /*if (mIsLike == 0) {
                    showLaudGif();
                }*/
                if(mVideoBean.getUid().equals(AppConfig.getInstance().getUid())){
                    ToastUtil.show(WordUtil.getString(R.string.not_zan));
                    return;
                }
                if(mVideoBean.getIslike().equals("0")){
                    thumbsUp(null);
                }
                break;
            case R.id.iv_video_close:
                closePlayer();
                mContext.getActivity().onBackPressed();
                break;
            case R.id.btn_cai:
                cai();
                break;
            case R.id.iv_video_emcee_head:
                ((VerticalVideoPlayerActivity) mContext.getContext()).setCurrentItem(1);
                break;
        }
    }



    //以下是自定义方法

    /**
     * 设置分享数
     *
     * @param s
     */
    public void setShareCount(String s) {
        mShareNums.setText(s);
    }

    /**
     * 设置评论数
     *
     * @param s
     */
    public void setCommentNum(String s) {
        mTvVideoCommrntnum.setText(s);
    }



    /**
     * 关注
     */
    private void attention() {
        HttpUtil.setAttention(mVideoBean.getUid(), new CommonCallback<Integer>() {
            @Override
            public void callback(Integer isAttention) {
                if (isAttention == 1) {
                    ToastUtil.show(WordUtil.getString(R.string.isattention));
                    mTvAttention.setVisibility(View.GONE);
                    mVideoBean.setIsattent("1");
                }
            }
        });
    }


    /**
     * 踩
     */
    private void cai() {
        if (mVideoBean.getIsstep() == 1) {
            return;
        }

        HttpUtil.setVideoStep(mVideoBean.getIsdialect(),mVideoBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        JSONObject info0 = JSON.parseObject(info[0]);
                        int isstep = info0.getIntValue("isstep");
                        if (isstep == 1) {
                            mVideoBean.setIsstep(1);
                            mBtnCai.setImageResource(R.mipmap.icon_video_cai_selected);
                        }
                    }

                }
                ToastUtil.show(msg);

            }
        });

    }


    /**
     * 点击视频流点赞
     */
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            if (mIsLike == 0) {
//                addLikes();
//            }
//            showLaudGif();
            VideoStopDialog videoStopDialog=new VideoStopDialog(VideoFramePresenter.this);
            videoStopDialog.show(mContext.getChildFragmentManager(),"VideoStopDialog");
            pasue();
        }
    };

    /**
     * 播放点赞动画
     */
    private void showLaudGif() {
        if (mIvVideoLaudgif.getVisibility() == View.GONE) {
            mIvVideoLaudgif.setVisibility(View.VISIBLE);
            ImgLoader.displayGif(R.mipmap.laud_gif, mIvVideoLaudgif);
            mHandler.sendEmptyMessageDelayed(0, 2000);
        }
    }


     /**
     * 点赞
     */

    public void addLikes(String type,final ThumbUpListener thumbUpListener) {
        HttpUtil.setLike(mVideoBean.getIsdialect(),mVideoBean.getId(),type, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        if (mVideoBean != null) {
                            mVideoBean.setIslike(obj.getString("islike"));
                            mVideoBean.setLikes(obj.getString("likes"));
                        }
                        if (mTvVideoLaudnum != null) {
                            mTvVideoLaudnum.setText(mVideoBean.getLikes());
                        }
                        mIsLike = obj.getIntValue("islike");
                        if (mIsLike == 1) {
                            ToastUtil.show(msg);
                            if(thumbUpListener!=null){
                                thumbUpListener.onSuccess();
                            }
                        }
                        if (mIvVideoLaud != null) {
                            if (mIsLike == 1) {
                                mIvVideoLaud.setImageResource(R.mipmap.icon_lauded);
                            } else {
                                mIvVideoLaud.setImageResource(R.mipmap.icon_nolaud);
                            }
                        }
                    }

                }
            }
        });
    }


    /**
     * 显示评论列表
     */
    private void showCommentDialog() {
        mCommentFragment = new CommentFragmentPresenter(this);
        Bundle bundle = new Bundle();
        bundle.putParcelable("bean", mVideoBean);
        mCommentFragment.setArguments(bundle);
        mCommentFragment.show(mContext.getChildFragmentManager(), "CommentFragmentPresenter");
    }


    /**
     * 显示评论输入框
     */
    private void showCommentDialog2() {
        if (mDialogFragment == null) {
            mDialogFragment = new CommentDialogFragmentPrestener(this);
            Bundle bundle = new Bundle();
            bundle.putParcelable("bean", mVideoBean);
            mDialogFragment.setArguments(bundle);
        }
        mDialogFragment.show(mContext.getChildFragmentManager(), "CommentDialogFragment");
    }

    /**
     * 视频结束释放资源
     */
    private void videoPlayerEnd() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mCommentFragment != null) {
            mCommentFragment = null;
        }
        mRlVideoRoot.getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListenernew);
    }


    /**
     * 退出播放
     */
    public void closePlayer() {

      /*  ((BitmapDrawable) iv_timelock.getDrawable()).getBitmap().recycle();
        ((BitmapDrawable) mIvVideoClose.getDrawable()).getBitmap().recycle();
        ((BitmapDrawable) mIvVideoShare.getDrawable()).getBitmap().recycle();
        ((BitmapDrawable) mIvVideoComment.getDrawable()).getBitmap().recycle();
        ((BitmapDrawable) mBtnCai.getDrawable()).getBitmap().recycle();*/

        iv_timelock.setImageDrawable(null);
        mIvVideoClose.setImageDrawable(null);
        mIvVideoShare.setImageDrawable(null);
        mIvVideoComment.setImageDrawable(null);
        mBtnCai.setImageDrawable(null);
        mTvAttention.setImageDrawable(null);

       /* iv_timelock=null;
        mIvVideoClose=null;
        mIvVideoShare=null;
        mIvVideoComment=null;
        mBtnCai=null;*/

        System.out.println(">>>>>>>>>回收图片");
        videoPlayerEnd();

    }

    /**
     * 环信发送通知消息到用户
     *
     * @param isfollow
     * @param content
     * @param touid
     */
    public void sendEMMessage(String isfollow, String content, String touid) {
        Log.e("sendEMMessage", "sendEMMessage:-----------> " + content + "----->" + touid);
        if (touid.equals(AppConfig.getInstance().getUid())) {
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(content, touid);
        message.setAttribute("isfollow", isfollow);
        String lastMsg = ((EMTextMessageBody) message.getBody()).getMessage();
        String lastTime = DateUtil.getDateString(message.getMsgTime());
        EMChatExitEvent e = new EMChatExitEvent(lastMsg, lastTime, touid, -1);
        EventBus.getDefault().post(e);
    }


    /**
     * 显示分享dialog
     */
    private void showSharePopWindow2() {
        VideoShareFragment f = new VideoShareFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("bean", mVideoBean);
        f.setArguments(bundle);
        f.show(mContext.getChildFragmentManager(), "VideoShareFragment");
    }


    /**
     * 显示视频下载等待框
     */
    public void showLoadingDialog() {
        mLoadingDialog = new Dialog(mContext.getContext(), R.style.loading_dialog);
        mLoadingDialog.setContentView(R.layout.dialog_loading);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show();
    }


    /**
     * 隐藏视频下载等待框
     */
    public void hideLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog = null;
    }





    /**
     * 分享
     *
     * @param index
     */
    private void shareVideo(int index) {
        try {
            String names = "";
            String s = AppConfig.getInstance().getConfig().getShare_type()[index];
            String des = mVideoBean.getTitle();
            if ("".equals(des)) {
                des = mVideoBean.getUserinfo().getUser_nicename() + AppConfig.getInstance().getConfig().getVideo_share_des();
            }
            SharedSdkUitl.getInstance().share(s, AppConfig.getInstance().getConfig().getVideo_share_title()
                    , des, mVideoBean.getThumb(), AppConfig.HOST + "/index.php?g=appapi&m=video&a=index&videoid=" + mVideoBean.getId(), new SharedSdkUitl.ShareListener() {
                        @Override
                        public void onSuccess(Platform platform) {
                            HttpUtil.setShare(mVideoBean.getIsdialect(),mVideoBean.getId(), new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    if (code == 0) {
                                        if (info.length > 0) {
                                            JSONObject info0 = JSON.parseObject(info[0]);
                                            setShareCount(info0.getString("shares"));
                                        }

                                    }
                                    ToastUtil.show(msg);

                                }
                            });
                        }

                        @Override
                        public void onError(Platform platform) {
                            ToastUtil.show(WordUtil.getString(R.string.share_fail));
                        }

                        @Override
                        public void onCancel(Platform platform) {
                            ToastUtil.show(WordUtil.getString(R.string.share_cancel));
                        }
                    });
        } catch (Exception e) {
        }
    }

    /**
     * 显示付费提示dialog
     * @param diamond
     */
    private void showWarnDialog(final int isfree , int diamond , final ThumbUpListener thumbUpListener){
        DialogUitl.confirmDialog(
                mContext.getContext(),
                mContext.getContext().getString(R.string.tip),
                mContext.getContext().getString(R.string.addlike_warn)+diamond+AppConfig.getInstance().getName_coin(),
                mContext.getContext().getString(R.string.confirm1),
                mContext.getContext().getString(R.string.cancel), true,
                new DialogUitl.Callback() {
                    @Override
                    public void confirm(Dialog dialog) {
                        dialog.dismiss();
                       addLikes(isfree+"",thumbUpListener);
                    }

                    @Override
                    public void cancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                }
        ).show();
    }

    @Override
    public void onFirstFrame() {
    }

    @Override
    public void onLoadingStart() {

    }

    @Override
    public void onLoadingEnd() {

    }

    @Override
    public int getIsLike() {
        return Integer.decode(mVideoBean.getIslike());
    }

    @Override
    public void thumbsUp(final ThumbUpListener thumbUpListener) {
        HttpUtil.likeIspay(mVideoBean.getIsdialect(), new HttpCallbackBase() {
            @Override
            public void onSuccess(String data) {
                JSONObject jsonObject=JSON.parseObject(data.toString());
                if(jsonObject.getString("code").equals("0")){
                    JSONObject jsonObject1=JSON.parseObject(jsonObject.getString("info"));
                    int isfree=jsonObject1.getInteger("isfree");
                    int likemoney = jsonObject1.getInteger("likemoney");
                    if(isfree==0){//免费
                        addLikes(isfree+"",thumbUpListener);
                    }else if(isfree==1){//付费
                        showWarnDialog(isfree,likemoney,thumbUpListener);
                    }
                }
            }
        });

    }

    @Override
    public void play() {
        if(mContext instanceof VideoOneControl){
            ((VideoOneControl)mContext).play();
        }else if(mContext instanceof VideoControlListener){
            ((VideoControlListener)mContext).play();
        }

    }

    @Override
    public void pasue() {
        if(mContext instanceof VideoOneControl){
            ((VideoOneControl)mContext).pause();
        }else if(mContext instanceof VideoControlListener){
            ((VideoControlListener)mContext).pasue();
        }
    }

    @Override
    public void stop() {
        ((VideoControlListener)mContext).stop();
    }

    @Override
    public void replay() {
        if(mContext instanceof VideoOneControl){
            ((VideoOneControl)mContext).replay();
        }else if(mContext instanceof VideoControlListener){
            ((VideoControlListener)mContext).replay();
        }
    }

    @Override
    public void share(int shareId) {
        shareVideo(shareId);
    }

    public TimeView getTvVideoplaytime(){
        return mTvVideoplaytime;
    }

}
