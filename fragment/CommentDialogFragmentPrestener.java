package com.aosika.phonelive.player_lhc_costom.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aosika.phonelive.R;
import com.aosika.phonelive.bean.VideoBean;
import com.aosika.phonelive.http.HttpCallback;
import com.aosika.phonelive.http.HttpUtil;
import com.aosika.phonelive.utils.DpUtil;
import com.aosika.phonelive.utils.ToastUtil;
import com.aosika.phonelive.utils.WordUtil;


/**
 * Created by cxf on 2017/9/9.
 */

public class CommentDialogFragmentPrestener extends DialogFragment implements View.OnClickListener {

    private Context mContext;
    private View mRootView;
    private EditText mEditText;
    private String mEMContent;//发送环信的内容
    private VideoBean mActiveBean;
    private InputMethodManager imm;
    private Handler mHandler;
    private final int EMPTY = 0;
    private final int NOT_EMPTY = 1;
    private int status = EMPTY;
    private TextView mBtnSend;
    private Drawable mDrawable1;
    private Drawable mDrawable2;
    private VideoFramePresenter mVideoFramePresenter;


    public CommentDialogFragmentPrestener(VideoFramePresenter videoFramePresenter){
        mVideoFramePresenter=videoFramePresenter;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_comment_dialog, null);
        Dialog dialog = new Dialog(mContext, R.style.BottomViewTheme_Transparent);
        dialog.setContentView(mRootView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(50);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mActiveBean == null) {
            Bundle bundle = getArguments();
            mActiveBean = (VideoBean) bundle.getParcelable("bean");
        }
        mEditText = (EditText) mRootView.findViewById(R.id.comment_edit);
        mBtnSend = (TextView) mRootView.findViewById(R.id.btn_send);
        mDrawable1 = ContextCompat.getDrawable(mContext, R.drawable.bg_comment_btn_send);
        mDrawable2 = ContextCompat.getDrawable(mContext, R.drawable.bg_comment_btn_send2);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendComment();
//                    dismiss();
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
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            };
        }
        mRootView.findViewById(R.id.btn_send).setOnClickListener(this);
    }

    private void sendComment() {
        String content = mEditText.getText().toString();
        if ("".equals(content)) {
            ToastUtil.show(WordUtil.getString(R.string.no_content));
        }
        mEMContent = getResources().getString(R.string.reply) + " " + mActiveBean.getUserinfo().getUser_nicename() + ": " + content;
        if (!"".equals(content)) {
            dismiss();
            HttpUtil.setComment(mActiveBean.getIsdialect(),mActiveBean.getUid(), mActiveBean.getId(), content, "0", "0", mSetCommentCallback);
        }
        mEditText.setText("");

    }


    private HttpCallback mSetCommentCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (info.length > 0) {
                    JSONObject info0 = JSON.parseObject(info[0]);
                    sendEMMessage(info0.getString("isattent"),mEMContent);//对方是否关注我
                    mVideoFramePresenter.setCommentNum(info0.getString("comments"));
                }
            }
            ToastUtil.show(msg);
        }
    };

    private void sendEMMessage(String isfollow, String content) {
        mVideoFramePresenter.sendEMMessage(isfollow, content, mActiveBean.getUid());
    }

    @Override
    public void onResume() {
        super.onResume();
        //不加延时，软键盘不会自动弹出
        mHandler.sendEmptyMessageDelayed(0, 200);
    }

    @Override
    public void onPause() {
        super.onPause();
        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        sendComment();
//        dismiss();
    }
}
