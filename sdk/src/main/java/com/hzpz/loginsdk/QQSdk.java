package com.hzpz.loginsdk;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.tauth.Tencent;

/**
 * @author kk
 * @date 17-5-27
 * 此类只保存了context，user，boolean三个参数
 */

public class QQSdk {
    private static boolean init = false;
    private static QQUserInfo sQQUserInfo;
    private static Context mContext;

    private QQSdk() {
    }

    public static void install(Context context, QQUserInfo info) {
        if (!init) {
            if (info == null || TextUtils.isEmpty(info.getAppKey()) || TextUtils.isEmpty(info.getScope())) {
                throw new RuntimeException("please set right app info (appKey,Scope");
            }
            mContext = context;
            sQQUserInfo = info;
            init = true;
        }
    }

    public static void checkInit() {
        if (!init) {
            throw new RuntimeException("QQSdk was not install! please use: QQSdk.install(Context context, QQUserInfo info) in your app Application or your main Activity. when you want to use QQSdk function, make sure call QQSdk.install(Context context, QQUserInfo info) before this function");
        }
    }

    public static QQUserInfo getQQUserInfo() {
        checkInit();
        return sQQUserInfo;
    }

    public static Context getContext() {
        checkInit();
        return mContext;
    }

    public static synchronized Tencent getInstance() {
        return Tencent.createInstance(getQQUserInfo().getAppKey(), getContext());
    }
}
