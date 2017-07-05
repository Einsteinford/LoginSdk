package com.hzpz.loginsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.net.HttpManager;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by kk on 17-5-24.
 */

public class WbLogin {
    private static SsoHandler mSsoHandler;
    private WbShareHandler mWeiboShare;
    private WbShareCallback mWbShareCallback;

    public WbLogin() {
    }

    public static void login(final Activity activity, final LoginListener listener) {
        if (mSsoHandler == null) {
            mSsoHandler = new SsoHandler(activity);
        }

        final WbAuthListener mListener = new WbAuthListener() {
            @Override
            public void onSuccess(Oauth2AccessToken oauth2AccessToken) {
                String mToken = oauth2AccessToken.getToken();
                String mOpenId = oauth2AccessToken.getUid();
                if (oauth2AccessToken.isSessionValid()) {
                    AccessTokenKeeper.writeAccessToken(activity, oauth2AccessToken);
                }
                try {
                    requestUserData(activity, mToken, mOpenId, listener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void cancel() {
                listener.onLoginCancel();
            }

            @Override
            public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
                listener.onLoginFail(wbConnectErrorMessage.getErrorMessage(), wbConnectErrorMessage.getErrorCode());
            }
        };
        mSsoHandler.authorize(mListener);
    }

    public void Share(final Activity activity, final WbShareHandler webShareHandler, final ShareListener listener, String imageUrl, final String url, final String content) {
        DownloadImageUtil downloadImageUtil = new DownloadImageUtil(activity, imageUrl, new DownloadImageUtil.onLogoDownloadListener(){

            @Override
            public void getLogoBitmap(Bitmap bitmap) {
                WeiboMultiMessage multiMessage = new WeiboMultiMessage();
                TextObject textObject = new TextObject();
                String str = "";
                if (content.length() > 100) {
                    str = content.substring(0, 100) + "...." + url;
                } else {
                    str = content + "...." + url;
                }
                textObject.text = str;
                multiMessage.textObject = textObject;
                if (bitmap != null) {
                    ImageObject imageObject = new ImageObject();
                    imageObject.setImageObject(bitmap);
                    multiMessage.imageObject = imageObject;
                }
                mWeiboShare = webShareHandler;
                webShareHandler.registerApp();
                webShareHandler.shareMessage(multiMessage, false);
            }
        });
        downloadImageUtil.execute();
        mWbShareCallback = new WbShareCallback() {

            @Override
            public void onWbShareSuccess() {
                listener.onShareSuccess();
            }

            @Override
            public void onWbShareCancel() {
                listener.onShareCancel();
            }

            @Override
            public void onWbShareFail() {
                listener.onShareFail();
            }
        };

    }

    public static void requestUserData(final Context context, final String tonken, final String openId, final LoginListener listener) throws Exception {
        final String path = "https://api.weibo.com/2/users/show.json";
        final WeiboParameters parameters = new WeiboParameters(WbSdk.getAuthInfo().getAppKey());
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("access_token", tonken);
        map.put("uid", openId);
        parameters.setParams(map);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = HttpManager.openUrl(context, path, "GET", parameters);
                    Log.i("kkTest", s);
                    JSONObject jsonObject = new JSONObject(s);
                    String nickName = jsonObject.get("screen_name").toString();
                    String icon = jsonObject.get("profile_image_url").toString();
                    listener.onLoginSuccess("weibo", openId, tonken, nickName, icon);
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onLoginFail("登录未成功", "0");
                }
            }
        }).start();
    }

    public static void callback(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public void onNewIntent(Intent intent) {
        mWeiboShare.doResultIntent(intent, mWbShareCallback);
    }
}
