package com.hzpz.loginsdk.deprecated;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hzpz.loginsdk.QQSdk;
import com.hzpz.loginsdk.ShareListener;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

/**
 * Created by kk on 17-6-12.
 */

public class QQShareUtil {
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
        if (!QQSdk.getInstance().isSessionValid()) {
            QQSdk.getInstance().shareToQQ(activity, param, mListener);
        }
    }

    public static void callback(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            QQSdk.getInstance().onActivityResultData(requestCode, resultCode, data, null);
        }
    }
}
