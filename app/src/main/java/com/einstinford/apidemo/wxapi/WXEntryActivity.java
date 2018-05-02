package com.einstinford.apidemo.wxapi;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hzpz.loginsdk.WxLogin;


public class WXEntryActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //TODO 相当与第三步
            WxLogin.callback(getIntent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}