package com.hzpz.loginsdk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONObject;

/**
 * @author kk
 * @date 17-5-26
 * https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317851&token=&lang=zh_CN
 */

public class WxLogin {
    private static LoginListener sLoginListener;
    private static ShareListener sShareListener;

    public static void login(LoginListener listener) {
        if (!WxSdk.getInstance().isWXAppInstalled()) {
            Toast.makeText(WxSdk.getContext(), "请安装微信！", Toast.LENGTH_SHORT).show();
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = WxSdk.getWXUserInfo().getScope();
        req.state = WxSdk.getWXUserInfo().getRedirectUri();
        WxSdk.getInstance().sendReq(req);
        sLoginListener = listener;
    }

    public static void share(final int type,
                             ShareListener listener,
                             @NonNull final String title,
                             @Nullable final String content,
                             @NonNull final String url,
                             @Nullable String imageUrl) {

        if (!WxSdk.getInstance().isWXAppInstalled()) {
            Toast.makeText(WxSdk.getContext(), "请安装微信！", Toast.LENGTH_SHORT).show();
            return;
        }

        //成为一个网页分享的最低要求
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(title)) {
            sShareListener = listener;
            final SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = WxSdk.getWXUserInfo().getRedirectUri();
            req.scene = type;
            final WXWebpageObject pageObject = new WXWebpageObject();
            pageObject.webpageUrl = url;
            final WXMediaMessage msg = new WXMediaMessage(pageObject);
            msg.title = title;
            msg.description = (content == null ? "" : content);

            if (!TextUtils.isEmpty(imageUrl)) {
                new DownloadImageUtil(imageUrl, new DownloadImageUtil.OnLogoDownloadListener() {
                    @Override
                    public void getLogoBitmap(Bitmap bitmap) {
                        if (null != bitmap) {
                            Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
                            bitmap.recycle();
                            // 设置缩略图
                            msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                        }
                        req.message = msg;
                        WxSdk.getInstance().sendReq(req);
                    }
                }).execute();
            } else {
                req.message = msg;
                WxSdk.getInstance().sendReq(req);
            }
        } else {
            return;
        }
    }

    /**
     * 只分享文本
     *
     * @param type 是指针对朋友圈还是朋友进行分享
     */
    public static void shareText(final int type, ShareListener listener, final String content) {
        if (!WxSdk.getInstance().isWXAppInstalled()) {
            Toast.makeText(WxSdk.getContext(), "请安装微信！", Toast.LENGTH_SHORT).show();
            return;
        }
        sShareListener = listener;

        if (content != null) {
            // 初始化一个WXTextObject对象
            WXTextObject textObj = new WXTextObject();
            textObj.text = content;

            // 用WXTextObject对象初始化一个WXMediaMessage对象
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = textObj;
            // 发送文本类型的消息时，title字段不起作用
            // msg.title = "Will be ignored";
            msg.description = content;

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = WxSdk.getWXUserInfo().getRedirectUri();
            req.scene = type;
            req.message = msg;
            WxSdk.getInstance().sendReq(req);
        }
    }

    /**
     * 只分享图片
     *
     * @param type 是指针对朋友圈还是朋友进行分享
     */
    public static void shareImg(final int type, ShareListener listener, final String imageUrl) {
        if (!WxSdk.getInstance().isWXAppInstalled()) {
            Toast.makeText(WxSdk.getContext(), "请安装微信！", Toast.LENGTH_SHORT).show();
            return;
        }
        sShareListener = listener;
        if (!TextUtils.isEmpty(imageUrl)) {
            new DownloadImageUtil(imageUrl, new DownloadImageUtil.OnLogoDownloadListener() {
                @Override
                public void getLogoBitmap(Bitmap bitmap) {
                    if (null != bitmap) {
                        WXImageObject imgObj = new WXImageObject(bitmap);
                        WXMediaMessage msg = new WXMediaMessage();
                        msg.mediaObject = imgObj;
                        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
                        bitmap.recycle();
                        // 设置缩略图
                        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = WxSdk.getWXUserInfo().getRedirectUri();
                        req.scene = type;
                        req.message = msg;
                        WxSdk.getInstance().sendReq(req);
                    }
                }
            }).execute();
        }
    }

    public static void callback(Intent intent) {
        WxSdk.getInstance().handleIntent(intent, new IWXAPIEventHandler() {
            @Override
            public void onReq(BaseReq baseReq) {
//                Log.i(TAG, "onReq: " + (baseReq == null));
//                Intent intent = new Intent();
//                intent.putExtra("Type", baseReq.getType());
//                intent.putExtra("openId", baseReq.openId);
//                intent.putExtra("transaction", baseReq.transaction);
            }

            @Override
            public void onResp(BaseResp resp) {
                switch (resp.getType()) {
                    //登陆
                    case ConstantsAPI.COMMAND_SENDAUTH:
                        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                            //即为所需的code
                            String code = ((SendAuth.Resp) resp).code;
                            if (((SendAuth.Resp) resp).state.equals(WxSdk.getWXUserInfo().getRedirectUri())) {
                                getToken(code);
                            }
                        } else {
                            sLoginListener.onLoginFail("登录未成功", "0");
                        }
                        break;
                    //分享
                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                        switch (resp.errCode) {
                            case BaseResp.ErrCode.ERR_OK:
                                if (resp.transaction.equals(WxSdk.getWXUserInfo().getRedirectUri())) {
                                    sShareListener.onShareSuccess();
                                    break;
                                }
                                //验证不通过，就分享失败
                            case BaseResp.ErrCode.ERR_SENT_FAILED:
                                sShareListener.onShareFail();
                                break;
                            case BaseResp.ErrCode.ERR_USER_CANCEL:
                            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                                sShareListener.onShareCancel();
                                break;
                            default:
                                break;
                        }
                        break;
                    case ConstantsAPI.COMMAND_PAY_BY_WX:
                        if (resp.errCode == 0) {
                            //充值成功
                        } else {
                            //充值失败
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private static void getToken(String code) {
        final String path = "https://api.weixin.qq.com/sns/oauth2/access_token" + "?appid=" + WxSdk.getWXUserInfo().getAppKey() + "&secret=" + WxSdk.getWXUserInfo().getSecret() + "&grant_type=authorization_code&code=" + code;
        new HttpUtil(path, new HttpUtil.OnRequestListener() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if (data.contains("errcode")) {
                        sLoginListener.onLoginFail(jsonObject.getString("errmsg"), jsonObject.getString("errcode"));
                    } else {
                        String accessToken = jsonObject.getString("access_token");
                        String openid = jsonObject.getString("openid");
                        String scope = jsonObject.getString("scope");
                        switch (scope) {
                            case SdkConstants.WECHAT_SCOPE:
                                getUserInfo(accessToken, openid);
                                break;
                            //根据授权域不同，进行不同的操作
                            case "snsapi_base":
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        }).execute();
    }

    private static void getUserInfo(final String accessToken, final String openid) {
        final String path = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openid;
        new HttpUtil(path, new HttpUtil.OnRequestListener() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if (data.contains("errcode")) {
                        sLoginListener.onLoginFail(jsonObject.getString("errmsg"), jsonObject.getString("errcode"));
                    } else {
                        String nickname = jsonObject.getString("nickname");
                        String headimgurl = jsonObject.getString("headimgurl");
                        sLoginListener.onLoginSuccess("wechat", openid, accessToken, nickname, headimgurl);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        }).execute();
    }
}
