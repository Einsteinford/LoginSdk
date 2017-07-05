package com.hzpz.loginsdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * Created by kk on 17-5-27.
 */

public class QQLogin {

    private QQLogin() {
    }
    public static void login(final Activity activity, final LoginListener listener) {
        final String[] token = new String[1];
        final String[] expires = new String[1];
        final String[] openId = new String[1];
        final String[] nickname = new String[1];
        final String[] iconUrl = new String[1];

        IUiListener mListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                try {
                    JSONObject QQObject = (JSONObject) o;
                    Log.i("kkTest", "onComplete: " + o.toString());
                    if (QQObject.has("nickname")) {
                        nickname[0] = QQObject.getString("nickname");
                        iconUrl[0] = QQObject.getString("figureurl_qq_2");
                        listener.onLoginSuccess("qq", openId[0], token[0], nickname[0], iconUrl[0]);
                    } else {
                        token[0] = QQObject.getString(Constants.PARAM_ACCESS_TOKEN);
                        openId[0] = QQObject.getString(Constants.PARAM_OPEN_ID);
                        expires[0] = QQObject.getString(Constants.PARAM_EXPIRES_IN);
                        if (!TextUtils.isEmpty(token[0]) && !TextUtils.isEmpty(expires[0])
                                && !TextUtils.isEmpty(openId[0])) {
                            //利用Tencent自带的方式获取用户数据
                            QQSdk.getInstance().setAccessToken(token[0], expires[0]);
                            QQSdk.getInstance().setOpenId(openId[0]);
                            UserInfo userInfo = new UserInfo(activity, QQSdk.getInstance().getQQToken());
                            userInfo.getUserInfo(this);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                Log.i("kkTest", "code: " + uiError.errorCode + " msg: " + uiError.errorMessage + " detail: " + uiError.errorDetail);
                listener.onLoginFail(uiError.errorMessage, String.valueOf(uiError.errorCode));
            }

            @Override
            public void onCancel() {
                listener.onLoginCancel();
            }
        };
            QQSdk.getInstance().login(activity, QQSdk.getQQUserInfo().getScope(), mListener);
    }

    public static void share(final Activity activity, final ShareListener listener, Bundle param) {
        IUiListener mListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                listener.onShareSuccess();
            }

            @Override
            public void onError(UiError uiError) {
                listener.onShareFail();
            }

            @Override
            public void onCancel() {
                listener.onShareCancel();
            }
        };
            QQSdk.getInstance().shareToQQ(activity, param, mListener);
    }

    public static void callback(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR || requestCode == Constants.REQUEST_QQ_SHARE) {
            QQSdk.getInstance().onActivityResultData(requestCode, resultCode, data, null);
        }
    }
}
