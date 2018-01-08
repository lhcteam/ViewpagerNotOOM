package com.aosika.phonelive.player_lhc_costom.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aosika.phonelive.AppConfig;
import com.aosika.phonelive.R;
import com.aosika.phonelive.activity.AttentionActivity;
import com.aosika.phonelive.activity.EMChatRoomActivity;
import com.aosika.phonelive.activity.OrderActivity;
import com.aosika.phonelive.bean.LiveBean;
import com.aosika.phonelive.bean.UserBean;
import com.aosika.phonelive.custom.LeftDrawableTextView;
import com.aosika.phonelive.fragment.LiveRecordFragment;
import com.aosika.phonelive.fragment.MyVideoFragment;
import com.aosika.phonelive.fragment.UserInfoHomeFragment;
import com.aosika.phonelive.glide.ImgLoader;
import com.aosika.phonelive.http.HttpCallback;
import com.aosika.phonelive.http.HttpUtil;
import com.aosika.phonelive.interfaces.CommonCallback;
import com.aosika.phonelive.player_lhc_costom.VerticalVideoPlayerActivity;
import com.aosika.phonelive.player_lhc_costom.libs.fragment.BaseFragment;
import com.aosika.phonelive.presenter.CheckLivePresenter;
import com.aosika.phonelive.utils.DialogUitl;
import com.aosika.phonelive.utils.IconUitl;
import com.aosika.phonelive.utils.ToastUtil;

/**
 * Created by cxf on 2017/8/11.
 * 他人主页
 */

public class UserInfoFragment extends BaseFragment implements View.OnClickListener {

    private String mTouid;
    private UserBean mToUser;
    private ImageView mHeadImg;
    private View mBtnLiving;
    private TextView mName;
    private ImageView mSex;
    private ImageView mAnchorLevel;
    private ImageView mLevel;
    private TextView mFollow;
    private TextView mFans;
    private TextView mSignature;
    private int mSexVal;
    private int mIsAttention;
    private TextView mAttentionText;
    private TextView mBlackText;
    private Dialog mLoadingDialog;
    private View mBottom;
    private RadioButton mBtnHome;
    private RadioButton mBtnLive;
    private RadioButton mBtnVideo;
    private LeftDrawableTextView mBtnPrivateMessage;
    private LeftDrawableTextView mBtnAttention;
    private LeftDrawableTextView mBtnPullBlack;
    private ImageView mIvBack;

    private FragmentManager mFragmentManager;
    private UserInfoHomeFragment mHomeFragment;
    private LiveRecordFragment mLiveRecordFragment;
    private MyVideoFragment mMyVideoFragment;
    private LiveBean mLiveBean;
    private CheckLivePresenter mCheckLivePresenter;
    private int mCurFragmentKey;
    private SparseArray<Fragment> mMap;
    private static final int HOME = 0;
    private static final int RECORD = 1;
    private static final int VIDEO = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_userinfo, container, false);
        mTouid = getArguments().getString("touid");
        initView(view);
        initData();
        initListeners();
        return view;
    }


    private void initView(View view) {
        mHeadImg = (ImageView) view.findViewById(R.id.headImg);
        mBtnLiving = view.findViewById(R.id.btn_living);
        mName = (TextView) view.findViewById(R.id.name);
        mSex = (ImageView) view.findViewById(R.id.sex);
        mAnchorLevel = (ImageView) view.findViewById(R.id.anchor_level);
        mLevel = (ImageView) view.findViewById(R.id.user_level);
        mFollow = (TextView) view.findViewById(R.id.follows);
        mFans = (TextView) view.findViewById(R.id.fans);
        mSignature = (TextView) view.findViewById(R.id.signature);
        mAttentionText = (TextView) view.findViewById(R.id.btn_attention);
        mBlackText = (TextView) view.findViewById(R.id.btn_pull_black);
        mBtnHome = (RadioButton) view.findViewById(R.id.btn_home);
        mBtnLive = (RadioButton) view.findViewById(R.id.btn_live);
        mBtnVideo = (RadioButton) view.findViewById(R.id.btn_video);
        mBtnPrivateMessage = (LeftDrawableTextView) view.findViewById(R.id.btn_private_message);
        mBtnAttention = (LeftDrawableTextView) view.findViewById(R.id.btn_attention);
        mBtnPullBlack = (LeftDrawableTextView) view.findViewById(R.id.btn_pull_black);
        mIvBack = (ImageView) view.findViewById(R.id.iv_back);
        mBottom = view.findViewById(R.id.bottom);
        if (mTouid.equals(AppConfig.getInstance().getUid())||AppConfig.getInstance().getUid().equals(AppConfig.VISITOR)) {
            mBottom.setVisibility(View.GONE);
        }
        mFragmentManager = getChildFragmentManager();
        mMap = new SparseArray<>();
    }

    private void initListeners() {
        mFollow.setOnClickListener(this);
        mFans.setOnClickListener(this);
        mBtnHome.setOnClickListener(this);
        mBtnLive.setOnClickListener(this);
        mBtnVideo.setOnClickListener(this);
        mBtnPrivateMessage.setOnClickListener(this);
        mBtnAttention.setOnClickListener(this);
        mBtnPullBlack.setOnClickListener(this);
        mBtnLiving.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
    }



    private void initData() {
        HttpUtil.getUserHome(mTouid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                UserBean bean = JSON.toJavaObject(obj, UserBean.class);
                mToUser = bean;
                ImgLoader.display(bean.getAvatar(), mHeadImg);
                mName.setText(bean.getUser_nicename());
                mSexVal = bean.getSex();
                mSex.setImageResource(IconUitl.getSexDrawable(mSexVal));
               // mAnchorLevel.setImageResource(IconUitl.getAnchorLiveDrawable(bean.getLevel_anchor()));
                ImgLoader.display(bean.getLevel_anchor_icon(),mAnchorLevel);
               // mLevel.setImageResource(IconUitl.getAudienceDrawable(bean.getLevel()));
                ImgLoader.display(bean.getLevel_icon(),mLevel);
                mFollow.setText(getString(R.string.attention2) + "：" + bean.getFollows());
                mFans.setText(getString(R.string.fans) + "：" + bean.getFans());
                mSignature.setText(bean.getSignature());
                mIsAttention = obj.getIntValue("isattention");
                mAttentionText.setText(mIsAttention == 1 ? getString(R.string.attention) : getString(R.string.no_attention));
                mBlackText.setText(obj.getIntValue("isblack") == 1 ? getString(R.string.no_pull_black) : getString(R.string.pull_black));

                if (obj.getIntValue("islive") == 1) {
                    mLiveBean = JSON.parseObject(obj.getString("liveinfo"), LiveBean.class);
                    // mBtnLiving.setVisibility(View.VISIBLE);
                }

                //添加fragment
                mHomeFragment = new UserInfoHomeFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", bean);
                bundle.putString("contribute", obj.getString("contribute"));
                mHomeFragment.setArguments(bundle);

                mLiveRecordFragment = new LiveRecordFragment();
                bundle = new Bundle();
                bundle.putString("liverecord", obj.getString("liverecord"));
                bundle.putString("cover", bean.getAvatar());
                mLiveRecordFragment.setArguments(bundle);

                mMyVideoFragment = new MyVideoFragment();
                bundle = new Bundle();
                bundle.putString("touid", bean.getId());
                mMyVideoFragment.setArguments(bundle);

                mMap.put(HOME, mHomeFragment);
                mMap.put(RECORD, mLiveRecordFragment);
                mMap.put(VIDEO, mMyVideoFragment);
                mCurFragmentKey = HOME;
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                for (int i = 0; i < mMap.size(); i++) {
                    Fragment f = mMap.valueAt(i);
                    ft.add(R.id.replaced, f);
                    if (mCurFragmentKey == mMap.keyAt(i)) {
                        ft.show(f);
                    } else {
                        ft.hide(f);
                    }
                }
                ft.commit();
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                if (mLoadingDialog == null) {
                    mLoadingDialog = DialogUitl.loadingDialog(getContext());
                }
                return mLoadingDialog;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.follows:
                forwardAttention("0");
                break;
            case R.id.fans:
                forwardAttention("1");
                break;
            case R.id.btn_home:
                toggleFragment(HOME);
                break;
            case R.id.btn_live:
                toggleFragment(RECORD);
                break;
            case R.id.btn_video:
                toggleFragment(VIDEO);
                break;
            case R.id.btn_private_message://私信
                Intent intent = new Intent(getContext(), EMChatRoomActivity.class);
                intent.putExtra("from", 0);
                intent.putExtra("touser", mToUser);
                intent.putExtra("isAttention", mIsAttention);
                startActivity(intent);
                break;
            case R.id.btn_attention://关注
                HttpUtil.setAttention(mTouid, attentionCallback);
                break;
            case R.id.btn_pull_black://拉黑
                HttpUtil.setBlack(mTouid, pullBlackCallback);
                break;
            case R.id.order://排行榜
                Intent intent2 = new Intent(getContext(), OrderActivity.class);
                intent2.putExtra("touid", mTouid);
                intent2.putExtra("type", 2);
                startActivity(intent2);
                break;
            case R.id.btn_living://观看直播
                forwardWatchLive();
                break;
            case R.id.iv_back:
                ((VerticalVideoPlayerActivity) getContext()).setCurrentItem(0);
                break;
        }
    }

    private void forwardWatchLive() {
        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(getContext());
        }
        mCheckLivePresenter.setSelectLiveBean(mLiveBean);
        mCheckLivePresenter.watchLive();
    }

    private void forwardAttention(String type) {
        Intent intent = new Intent(getContext(), AttentionActivity.class);
        intent.putExtra("touid", mTouid);
        intent.putExtra("type", type);
        intent.putExtra("sex", mSexVal);
        startActivity(intent);
    }

    private void toggleFragment(int key) {
        if (mCurFragmentKey == key) {
            return;
        }
        mCurFragmentKey = key;
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        for (int i = 0; i < mMap.size(); i++) {
            Fragment f = mMap.valueAt(i);
            if (mCurFragmentKey == mMap.keyAt(i)) {
                ft.show(f);
            } else {
                ft.hide(f);
            }
        }
        ft.commit();
    }


    private CommonCallback<Integer> attentionCallback = new CommonCallback<Integer>() {
        @Override
        public void callback(Integer res) {
            mIsAttention = res;
            if (mIsAttention == 1) {//已关注
                mAttentionText.setText(getString(R.string.attention));
                //关注的时候把拉黑取消
                mBlackText.setText(getString(R.string.pull_black));
            } else if (mIsAttention == 0) {//未关注
                mAttentionText.setText(getString(R.string.no_attention));
            }
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }

        @Override
        public Dialog createLoadingDialog() {
            if (mLoadingDialog == null) {
                mLoadingDialog = DialogUitl.loadingDialog(getContext());
            }
            return mLoadingDialog;
        }
    };

    private HttpCallback pullBlackCallback = new HttpCallback() {

        @Override
        public void onSuccess(int code, String msg, String[] info) {
            int res = JSON.parseObject(info[0]).getIntValue("isblack");
            if (res == 1) {//已拉黑
                mBlackText.setText(getString(R.string.no_pull_black));
                //拉黑的时候把关注取消
                mAttentionText.setText(getString(R.string.no_attention));
                ToastUtil.show(getString(R.string.pull_black_success));
            } else if (res == 0) {//解除拉黑
                mBlackText.setText(getString(R.string.pull_black));
                ToastUtil.show(getString(R.string.no_pull_black));
            }
        }

        @Override
        public boolean showLoadingDialog() {
            return true;
        }

        @Override
        public Dialog createLoadingDialog() {
            if (mLoadingDialog == null) {
                mLoadingDialog = DialogUitl.loadingDialog(getContext());
            }
            return mLoadingDialog;
        }
    };


    @Override
    public void loadData() {
        clearDatas();
        initData();
    }


    @Override
    public void detachData() {
        //clearDatas();
    }

    public void setDatas(String touid) {
        mTouid = touid;
    }


    public void clearDatas() {
        if(mFragmentManager==null){
            return;
        }
        if(mMap==null){
            return;
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        for (int i = 0; i < mMap.size(); i++) {
            Fragment f = mMap.valueAt(i);
            ft.remove(f);
            f=null;
        }
        ft.commit();
        mMap.clear();
    }
}
