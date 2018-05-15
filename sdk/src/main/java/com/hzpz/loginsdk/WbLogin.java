package com.hzpz.loginsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;

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
import java.util.concurrent.Executors;

/**
 * @author kk
 * @date 17-5-24
 */

public class WbLogin {
    private static SsoHandler mSsoHandler;
    private static WbShareCallback sWbShareCallback;
    private static WbShareHandler sWbShareHandler;

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

    /**
     * 分享普通文本微博,属性基本都可以是空的
     */
    public static void share(final Activity activity,
                             final ShareListener listener,
                             @Nullable final String content,
                             @Nullable final String url,
                             @Nullable final String imageUrl) {
        sWbShareCallback = new WbShareCallback() {
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

        sWbShareHandler = new WbShareHandler(activity);
        sWbShareHandler.registerApp();

        final WeiboMultiMessage multiMessage = new WeiboMultiMessage();

        if (!TextUtils.isEmpty(content) || !TextUtils.isEmpty(url)) {
            TextObject textObject = new TextObject();
            if (!TextUtils.isEmpty(content)) {
                String str;
                if (content.length() > 100) {
                    str = content.substring(0, 100) + "...";
                } else {
                    str = content;
                }
                textObject.text = str;
            }
            if (!TextUtils.isEmpty(url)) {
                if (textObject.text == null) {
                    textObject.text = url;
                } else {
                    textObject.text += url;
                }
            }
            multiMessage.textObject = textObject;
        }

        if (!TextUtils.isEmpty(imageUrl)) {
            //有图片时，下载后进行分享
            DownloadImageUtil downloadImageUtil = new DownloadImageUtil(imageUrl, new DownloadImageUtil.OnLogoDownloadListener() {
                @Override
                public void getLogoBitmap(Bitmap bitmap) {
                    if (bitmap != null) {
                        ImageObject imageObject = new ImageObject();
                        imageObject.setImageObject(bitmap);
                        multiMessage.imageObject = imageObject;
                    }
                    sWbShareHandler.shareMessage(multiMessage, false);
                }
            });
            downloadImageUtil.execute();
        } else {
            //否则直接分享
            sWbShareHandler.shareMessage(multiMessage, false);
        }
    }

    public static void requestUserData(final Context context, final String tonken, final String openId, final LoginListener listener) throws Exception {
        final String path = "https://api.weibo.com/2/users/show.json";
        final WeiboParameters parameters = new WeiboParameters(WbSdk.getAuthInfo().getAppKey());
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("access_token", tonken);
        map.put("uid", openId);
        parameters.setParams(map);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = HttpManager.openUrl(context, path, "GET", parameters);
                    JSONObject jsonObject = new JSONObject(s);
                    String nickName = jsonObject.get("screen_name").toString();
                    String icon = jsonObject.get("profile_image_url").toString();
                    listener.onLoginSuccess("weibo", openId, tonken, nickName, icon);
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onLoginFail("登录未成功", "0");
                }
            }
        });
    }

    public static void callback(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public static void onNewIntent(Intent intent) {
        if (sWbShareHandler != null && sWbShareCallback != null) {
            sWbShareHandler.doResultIntent(intent, sWbShareCallback);
        }
    }
}
