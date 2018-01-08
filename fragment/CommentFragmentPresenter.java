package com.aosika.phonelive.player_lhc_costom.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aosika.phonelive.AppConfig;
import com.aosika.phonelive.R;
import com.aosika.phonelive.activity.ReplyActivity;
import com.aosika.phonelive.adapter.CommentAdapter;
import com.aosika.phonelive.bean.CommentBean;
import com.aosika.phonelive.bean.UserBean;
import com.aosika.phonelive.bean.VideoBean;
import com.aosika.phonelive.custom.RefreshLayout;
import com.aosika.phonelive.event.DianzanEvent;
import com.aosika.phonelive.fragment.CommentFragment;
import com.aosika.phonelive.http.HttpCallback;
import com.aosika.phonelive.http.HttpUtil;
import com.aosika.phonelive.player_lhc_costom.dialog.LoginDialog;
import com.aosika.phonelive.utils.DpUtil;
import com.aosika.phonelive.utils.ToastUtil;
import com.aosika.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.List;


/**
 * Created by cxf on 2017/7/14.
 */

public class CommentFragmentPresenter extends DialogFragment implements View.OnClickListener, RefreshLayout.OnRefreshListener {

    private Context mContext;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private CommentAdapter mAdapter;
    private TextView mCommentCount;
    private VideoBean mActiveBean;
    private InputMethodManager imm;
    private Type mType;
    private int mHeight;
    private int mTempHeight;
    private EditText mEditText;
    private String mCurCommentId = "0";
    private RefreshLayout mRefreshLayout;
    private int mPage;
    private UserBean mCurReplyUser;
    private String mParentId = "0";
    private String mEMContent;//发送环信的内容
    private String mUid;
    private int mPosition = -1;
    private String mCommentNum;
    private View mLoading;
    private final int EMPTY = 0;
    private final int NOT_EMPTY = 1;
    private int status = EMPTY;
    private TextView mBtnSend;
    private Drawable mDrawable1;
    private Drawable mDrawable2;
    private VideoFramePresenter mVideoFramePresenter;

    public CommentFragmentPresenter(){

    }

    public CommentFragmentPresenter(VideoFramePresenter videoFramePresenter){
        mVideoFramePresenter=videoFramePresenter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        Dialog dialog = new Dialog(mContext, R.style.BottomViewTheme_Transparent);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_video_comment, null, false);
        dialog.setContentView(mRootView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        mHeight = DpUtil.dp2px(400);
        mTempHeight = mHeight;
        params.height = mTempHeight;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoFramePresenter.setCommentNum(mCommentNum);
    }

    private void initView() {
        Bundle bundle = getArguments();
        mActiveBean = (VideoBean) bundle.getParcelable("bean");
        mRootView.findViewById(R.id.close).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_send).setOnClickListener(this);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.listView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mRefreshLayout = (RefreshLayout) mRootView.findViewById(R.id.refreshLayout);
        mRefreshLayout.setScorllView(mRecyclerView);
        mRefreshLayout.setOnRefreshListener(this);

        mEditText = (EditText) mRootView.findViewById(R.id.comment_edit);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendComment();
                }
                return false;
            }
        });
        mBtnSend = (TextView) mRootView.findViewById(R.id.btn_send);
        mDrawable1 = ContextCompat.getDrawable(mContext, R.drawable.bg_comment_btn_send);
        mDrawable2 = ContextCompat.getDrawable(mContext, R.drawable.bg_comment_btn_send2);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendComment();
                    dismiss();
                }
                return false;
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (status == EMPTY) {
                        status = NOT_EMPTY;
                        mBtnSend.setBackgroundDrawable(mDrawable2);
                        mBtnSend.setTextColor(0xffffffff);
                    }
                } else {
                    if (status == NOT_EMPTY) {
                        status = EMPTY;
                        mBtnSend.setBackgroundDrawable(mDrawable1);
                        mBtnSend.setTextColor(0xff808080);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mLoading = mRootView.findViewById(R.id.loading);
        mCommentCount = (TextView) mRootView.findViewById(R.id.nums);
        mUid = AppConfig.getInstance().getUid();
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        EventBus.getDefault().register(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                dismiss();
                break;
            case R.id.btn_send:
                sendComment();
                break;
        }
    }


    /**
     * 发送评论
     */
    private void sendComment() {

        if(AppConfig.getInstance().getUid().equals(AppConfig.VISITOR)){
            LoginDialog.showLoginWarnDialog(mContext);
            return;
        }

        String content = mEditText.getText().toString();
        String touid = "";
        if ("".equals(content)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.comment_tips_null), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCurReplyUser != null) {
            mEMContent = getResources().getString(R.string.reply) + " " + mCurReplyUser.getUser_nicename() + ": " + content;
            touid = mCurReplyUser.getId();
        } else {
            mEMContent = getResources().getString(R.string.reply) + " " + mActiveBean.getUserinfo().getUser_nicename() + ": " + content;
            touid = mActiveBean.getUid();
        }
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0); //强制隐藏键盘
        HttpUtil.setComment(mActiveBean.getIsdialect(),touid, mActiveBean.getId(), content, mCurCommentId, mParentId, mSetCommentCallback);
        mEditText.setText("");
        if (mLoading.getVisibility() == View.GONE) {
            mLoading.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 评论成功回调
     */
    private HttpCallback mSetCommentCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (info.length > 0) {
                    JSONObject info0 = JSON.parseObject(info[0]);
                    sendEMMessage(info0.getString("isattent"), mEMContent);
                    mEditText.setHint(getResources().getString(R.string.talk));
                    mCommentNum = info0.getString("comments");
                    mVideoFramePresenter.setCommentNum(mCommentNum);
                    if (mCurReplyUser != null) {
                        mCurReplyUser = null;
                        mParentId = "0";
                        mCurCommentId = "0";
                    }
                    dismiss();
                }

            }
            ToastUtil.show(msg);

        }
    };


    /**
     * 环信发送私信
     *
     * @param isfollow 对方是否关注我
     * @param content
     */
    private void sendEMMessage(String isfollow, String content) {
        if (mCurReplyUser != null) {
            mVideoFramePresenter.sendEMMessage(isfollow, content, mCurReplyUser.getId());
        } else {
            mVideoFramePresenter.sendEMMessage(isfollow, content, mActiveBean.getUid());
        }
    }

    public void initData() {
        mPage = 1;
        HttpUtil.getComments(mActiveBean.getIsdialect(),mActiveBean.getId(), String.valueOf(mPage), mCallback);
    }


    private HttpCallback mCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (info.length > 0) {
                    JSONObject info0 = JSON.parseObject(info[0]);
                    mCommentNum = info0.getString("comments");
                    mCommentCount.setText(getResources().getString(R.string.all_comment) + "(" + mCommentNum + ")");
                    List<CommentBean> list = JSON.parseArray(info0.getString("commentlist"), CommentBean.class);
                    if (mAdapter == null) {
                        mAdapter = new CommentAdapter(mActiveBean.getIsdialect(),mContext, list);
                        mAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
                            @Override
                            public void OnItemClick(CommentBean bean, int position) {
                                if (!bean.getUid().equals(mUid)) {
                                    mCurReplyUser = bean.getUserinfo();
                                    mCurCommentId = bean.getCommentid();
                                    mParentId = bean.getId();
                                    mEditText.setHint(getResources().getString(R.string.reply) + mCurReplyUser.getUser_nicename());
                                    mEditText.requestFocus();
                                    imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
                                    mPosition = position;
                                }
                            }

                            @Override
                            public void OnMoreClick(CommentBean bean, int position) {
                                forwardReplyActivity(bean, position);
                            }
                        });
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        mAdapter.refreshList(list);
                        if (mPosition != -1) {
                            mRecyclerView.scrollToPosition(mPosition);
                            mPosition = -1;
                        } else {
                            mRecyclerView.scrollToPosition(0);
                        }
                    }
                }

            } else {
                ToastUtil.show(msg);
            }

        }

        @Override
        public void onFinish() {
            mRefreshLayout.completeRefresh();
            if (mLoading.getVisibility() == View.VISIBLE) {
                mLoading.setVisibility(View.GONE);
            }
        }
    };


    private void forwardReplyActivity(CommentBean bean, int position) {
        Intent intent = new Intent(mContext, ReplyActivity.class);
        intent.putExtra("host", bean);
        intent.putExtra("videoId", mActiveBean.getId());
        intent.putExtra("p", position);
        intent.putExtra("isdialect", mActiveBean.getIsdialect());
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            int position = data.getIntExtra("p", -1);
            if (position >= 0 && position < mAdapter.getItemCount()) {
                String reply = data.getStringExtra("reply");
                if (!"".equals(reply)) {
                    mAdapter.getList().get(position).setReplys(Integer.parseInt(reply));
                }
                mAdapter.notifyItemChanged(position);
            }
        }
    }


    private HttpCallback mloadMoreCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (info.length > 0) {
                    JSONObject info0 = JSON.parseObject(info[0]);
                    mCommentNum = info0.getString("comments");
                    mCommentCount.setText(getResources().getString(R.string.all_comment) + "(" + mCommentNum + ")");
                    List<CommentBean> list = JSON.parseArray(info0.getString("commentlist"), CommentBean.class);
                    if (list.size() > 0) {
                        if (mAdapter != null) {
                            mAdapter.insertList(list);
                        }
                    } else {
                        ToastUtil.show(WordUtil.getString(R.string.no_more_data));
                        mPage--;
                    }
                }

            } else {
                ToastUtil.show(msg);
            }

        }

        @Override
        public void onFinish() {
            if (mLoading.getVisibility() == View.VISIBLE) {
                mLoading.setVisibility(View.GONE);
            }
            mRefreshLayout.completeLoadMore();
        }
    };


    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onLoadMore() {
        mPage++;
        HttpUtil.getComments(mActiveBean.getIsdialect(),mActiveBean.getId(), String.valueOf(mPage), mloadMoreCallback);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDianzan(DianzanEvent e) {
        int position = e.getPosition();
        if (position >= 0 && position < mAdapter.getItemCount()) {
            CommentBean bean = mAdapter.getList().get(position);
            bean.setLikes(e.getLikes());
            bean.setIslike(e.getIsLike());
            mAdapter.notifyItemChanged(position);
        }
    }

    private void resize() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.height = mTempHeight;
            window.setAttributes(params);
            if (mPosition != -1) {
                mRecyclerView.scrollToPosition(mPosition);
            }
        }
    }

    public void onSoftInputShow(int height) {
        if (mTempHeight != height) {
            mTempHeight = height;
            resize();
        }
    }

    public void onSoftInputHide() {
        if (mTempHeight != mHeight) {
            mTempHeight = mHeight;
            resize();
            mCurCommentId = "0";
        }
    }
}
