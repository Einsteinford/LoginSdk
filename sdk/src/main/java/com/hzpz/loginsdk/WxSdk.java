package com.hzpz.loginsdk;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @author kk
 * @date 17-5-26
 */

public class WxSdk  {
    private static boolean init = false;
    private static WXUserInfo sTXUserInfo;
    private static IWXAPI mInstance;
    private static Context mContext;

    private WxSdk() {
    }

    public static void install(Context context, WXUserInfo info) {
        if (!init) {
            if (info == null || TextUtils.isEmpty(info.getAppKey())) {
                throw new RuntimeException("please set right app info (appKey,redirect,scope");
            }
            mContext = context;
            sTXUserInfo = info;
            init = true;
        }
    }

    public static void checkInit() {
        if (!init) {
            throw new RuntimeException("WxSdk sdk was not initall! please use: WxSdk.install() in your app Application or your main Activity. when you want to use weibo sdk function, make sure call WxSdk.install() before this function");
        }
    }

    public static WXUserInfo getWXUserInfo() {
        checkInit();
        return sTXUserInfo;
    }

    public static Context getContext() {
        checkInit();
        return mContext;
    }

    public static synchronized IWXAPI getInstance() {
        if (mInstance == null) {
            mInstance = WXAPIFactory.createWXAPI(getContext(), getWXUserInfo().getAppKey(), true);
            mInstance.registerApp(getWXUserInfo().getAppKey());
        }
        return mInstance;
    }

}
