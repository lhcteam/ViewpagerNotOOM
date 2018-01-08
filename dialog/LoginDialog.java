package com.aosika.phonelive.player_lhc_costom.dialog;

import android.app.Dialog;
import android.content.Context;

import com.aosika.phonelive.AppContext;
import com.aosika.phonelive.R;
import com.aosika.phonelive.utils.DialogUitl;
import com.aosika.phonelive.utils.LoginUtil;

/**
 * Created by mengyunfeng on 17/12/29.
 */

public class LoginDialog {
    /**
     * 显示登录提示dialog
     */
    public static  void showLoginWarnDialog(final Context context){
        DialogUitl.confirmDialog(
                context,
                AppContext.sInstance.getString(R.string.tip),
                AppContext.sInstance.getString(R.string.please_login),
                AppContext.sInstance.getString(R.string.confirm1),
                AppContext.sInstance.getString(R.string.cancel), true,
                new DialogUitl.Callback() {
                    @Override
                    public void confirm(Dialog dialog) {
                        dialog.dismiss();
                        LoginUtil.forwardLogin(context);
                    }

                    @Override
                    public void cancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                }
        ).show();
    }
}
