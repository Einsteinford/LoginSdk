package com.einstinford.apidemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hzpz.loginsdk.LoginListener;
import com.hzpz.loginsdk.QQLogin;
import com.hzpz.loginsdk.QQSdk;
import com.hzpz.loginsdk.QQUserInfo;
import com.hzpz.loginsdk.SdkConstants;
import com.hzpz.loginsdk.WXUserInfo;
import com.hzpz.loginsdk.WbLogin;
import com.hzpz.loginsdk.WxLogin;
import com.hzpz.loginsdk.WxSdk;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;

/**
 * @author kk
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoginListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO 第一步，安装第三方部件，只需一次
        QQSdk.install(this, new QQUserInfo(this, "app_id_qq", SdkConstants.QQ_SCOPE));
        //还需要替换manifest中的<data android:scheme="qq_scheme" /> 为自己的参数
        WbSdk.install(this, new AuthInfo(this, "2045436852", SdkConstants.WEIBO_REDIRECT_URL, SdkConstants.WEIBO_SCOPE));
        WxSdk.install(this, new WXUserInfo(this, "app_id_wechat", SdkConstants.WECHAT_REDIRECT_URL, SdkConstants.WECHAT_SCOPE, "app_secret_wechat"));

//        findViewById(R.id.tvQQLogin).setOnClickListener(this);
        findViewById(R.id.tvWeiboLogin).setOnClickListener(this);
//        findViewById(R.id.tvWeChatLogin).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvWeChatLogin:
                WxLogin.login(this);
                break;
            case R.id.tvWeiboLogin:
                WbLogin.login(this, this);
                break;
            case R.id.tvQQLogin:
                //TODO 第二步，进行所需要的操作，如登录，当然，可以直接在Activity中实现回调接口
                QQLogin.login(this, this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoginSuccess(String platFrom, String openId, String token, String socialName, String iconUrl) {
        //处理成功返回值
    }

    @Override
    public void onLoginFail(String errorMsg, String errorCode) {
        //处理错误返回值
    }

    @Override
    public void onLoginCancel() {
        //处理取消返回值
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO 第三步，在onActivityResult调用回调接口,微信除外
        QQLogin.callback(requestCode, resultCode, data);
        WbLogin.callback(requestCode, resultCode, data);
    }
}
