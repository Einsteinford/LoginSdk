package com.hzpz.loginsdk;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by kk on 17-5-26.
 */

public class WeChatSdk {

    private static boolean init = false;
    private static QQUserInfo sQQUserInfo;
    private static IWXAPI mInstance;
    private static Context mContext;

    private WeChatSdk() {
    }

    public static void install(Context context, QQUserInfo info) {
        if (!init) {
            if (info == null || TextUtils.isEmpty(info.getAppKey()) ) {
                throw new RuntimeException("please set right app info (appKey,redirect");
            }
            mContext =context;
            sQQUserInfo = info;
            init = true;
        }

    }

    public static void checkInit() {
        if (!init) {
            throw new RuntimeException("WeChatSdk sdk was not initall! please use: WeChatSdk.install() in your app Application or your main Activity. when you want to use weibo sdk function, make sure call WeChatSdk.install() before this function");
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

    public static synchronized IWXAPI getInstance() {
        if (mInstance == null) {
            mInstance = WXAPIFactory.createWXAPI(getContext(), getQQUserInfo().getAppKey(), true);
        }
        return mInstance;
    }


}
