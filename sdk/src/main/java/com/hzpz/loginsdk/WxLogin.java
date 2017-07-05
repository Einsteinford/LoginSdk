package com.hzpz.loginsdk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by kk on 17-5-26.
 * https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317851&token=&lang=zh_CN
 */

public class WxLogin {
    private static final String TAG = "kkTest";
    private static LoginListener sListener;

    public static void login(LoginListener listener) {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = WxSdk.getWXUserInfo().getScope();
        req.state = WxSdk.getWXUserInfo().getRedirectUri();
        WxSdk.getInstance().sendReq(req);
        sListener = listener;
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
                    case ConstantsAPI.COMMAND_SENDAUTH://登陆
                        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                            String code = ((SendAuth.Resp) resp).code;//即为所需的code
                            if (((SendAuth.Resp) resp).state.equals(WxSdk.getWXUserInfo().getRedirectUri())) {
                                getToken(code);
                            }
                        } else {
                            sListener.onLoginFail("登录未成功", "0");
                        }
                        break;
                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX://分享
                        switch (resp.errCode) {
                            case BaseResp.ErrCode.ERR_OK:
//                                doOnShareSuccess(resp.transaction);
//                                ToastUtil.Toast(this, "分享成功");
                                break;
                            case BaseResp.ErrCode.ERR_USER_CANCEL:
                            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                            default:
//                                ToastUtil.Toast(this, "分享失败");
                                break;
                        }
                        break;
                    case ConstantsAPI.COMMAND_PAY_BY_WX:
                        if (resp.errCode == 0) {
//                        dialog = ProgressDialog.show(this, "提示", "充值成功,正在刷新支付金额....");
                /*GetUserInfoRequest.getInstance().getUser(new GetUserInfoListener() {

					@Override
					public void success(int statusCode, UserInfo data) {
						if(dialog != null)
							dialog.dismiss();
						UserLoginManager.getInstance().setUser(data);
						BroadcastComm.sendBroadCast(WXEntryActivity.this, MineFragment.USERFEECHANGE_ACTION);
						finish();
					}

					@Override
					public void fail(int statusCode, String statusMsg) {
						if(dialog != null)
							dialog.dismiss();
						finish();
					}

				});*/
                        } else {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                        builder.setTitle("微信支付");
//                        builder.setMessage("微信充值失败!");
//				/*builder.setMessage("微信充值失败!" + "/n" + "结果:" + resp.errStr +";code=" + String.valueOf(resp.errCode)
//						+ "/n" + resp.toString() +
//						"/n transaction =" + resp.transaction
//						+ "/n getType=" + resp.getType()
//						+ "/n" + resp.openId);*/
//                        builder.setPositiveButton("确定", new OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // TODO Auto-generated method stub
//                                finish();
//                            }
//                        });
//                        builder.setCancelable(false);
//                        builder.show();
                        }
                        break;
                }
            }
        });
    }

    private static void getToken(String code) {
        final String path = "https://api.weixin.qq.com/sns/oauth2/access_token" + "?appid=" + WxSdk.getWXUserInfo().getAppKey() + "&secret=" + WxSdk.getWXUserInfo().getSecret() + "&grant_type=authorization_code&code=" + code;
        new HttpUtil(WxSdk.getContext(), path, new HttpUtil.onRequestListener() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if (data.contains("errcode")) {
                        sListener.onLoginFail(jsonObject.getString("errmsg"), jsonObject.getString("errcode"));
                    } else {
                        String access_token = jsonObject.getString("access_token");
                        String openid = jsonObject.getString("openid");
                        String scope = jsonObject.getString("scope");
                        switch (scope) {
                            case SDKConstants.WECHAT_SCOPE:
                                getUserInfo(access_token, openid);
                                break;
                            case "snsapi_base": //根据授权域不同，进行不同的操作
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

    private static void getUserInfo(final String access_token, final String openid) {
        final String path = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid;
        new HttpUtil(WxSdk.getContext(), path, new HttpUtil.onRequestListener() {
            @Override
            public void onSuccess(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if (data.contains("errcode")) {
                        sListener.onLoginFail(jsonObject.getString("errmsg"), jsonObject.getString("errcode"));
                    } else {
                        String nickname = jsonObject.getString("nickname");
                        String headimgurl = jsonObject.getString("headimgurl");
                        sListener.onLoginSuccess("wechat",openid,access_token,nickname,headimgurl);
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

    public static void share(Context context, final String mTitle, final String mContent, final String mUrl,
                             String imageUrl, final String transaction, final int type) {
        boolean sIsWXAppInstalledAndSupported = WxSdk.getInstance().isWXAppInstalled();

        //WeChatSdk.getInstance().isWXAppSupportAPI() 个别手机不支持api
        if (!sIsWXAppInstalledAndSupported) {
            //TODO
            Toast.makeText(context, "请安装微信！", Toast.LENGTH_SHORT).show();
            return;
        }
        new DownloadImageUtil(context, imageUrl, new DownloadImageUtil.onLogoDownloadListener() {

            @Override
            public void getLogoBitmap(Bitmap bitmap) {
                WXWebpageObject pageObject = new WXWebpageObject();
                pageObject.webpageUrl = mUrl;
                WXMediaMessage msg = new WXMediaMessage(pageObject);
                msg.title = mTitle;
                msg.description = mContent;
                if (null != bitmap) {
                    Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
                    msg.thumbData = Util.bmpToByteArray(thumbBmp, true); // 设置缩略图
//                Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_banner_default);
//                msg.thumbData = Util.bmpToByteArray(bitmap1, true);
                }
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = transaction;
                req.message = msg;
                req.scene = type;
                WxSdk.getInstance().sendReq(req);
            }
        }).execute();
    }

}
