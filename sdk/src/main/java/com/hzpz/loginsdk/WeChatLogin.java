package com.hzpz.loginsdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

/**
 * Created by kk on 17-5-26.
 */

public class WeChatLogin {
    public static void share(Context context, String mTitle, String mContent, String mUrl, Bitmap bitmap,int type) {
        boolean sIsWXAppInstalledAndSupported = WeChatSdk.getInstance().isWXAppInstalled()
                && WeChatSdk.getInstance().isWXAppSupportAPI();
        if (!sIsWXAppInstalledAndSupported) {
            //TODO
            Toast.makeText(context,"请安装微信！",Toast.LENGTH_SHORT).show();
            return;
        }
        WXWebpageObject pageObject= new WXWebpageObject();
        pageObject.webpageUrl = mUrl;
        WXMediaMessage msg = new WXMediaMessage(pageObject);
        msg.title = mTitle;
        msg.description = mContent;
        try
        {
            if(null != bitmap) {
                msg.setThumbImage(bitmap);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = type;
        WeChatSdk.getInstance().sendReq(req);
    }

    public static void share(Context context, String mTitle, String mContent, String mUrl,int type) {
        boolean sIsWXAppInstalledAndSupported = WeChatSdk.getInstance().isWXAppInstalled()
                && WeChatSdk.getInstance().isWXAppSupportAPI();
        if (!sIsWXAppInstalledAndSupported) {
            //TODO
            Toast.makeText(context,"请安装微信！",Toast.LENGTH_SHORT).show();
            return;
        }
        WXWebpageObject pageObject= new WXWebpageObject();
        pageObject.webpageUrl = mUrl;
        WXMediaMessage msg = new WXMediaMessage(pageObject);
        msg.title = mTitle;
        msg.description = mContent;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = type;
        WeChatSdk.getInstance().sendReq(req);
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
