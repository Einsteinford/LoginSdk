package com.hzpz.loginsdk;

/**
 * Created by kk on 17-5-26.
 */

public interface LoginListener {
    /**
     * @param platFrom   所使用的社交平台，例"qq"
     * @param openId     开放id
     * @param token      密钥
     * @param SocialName 第三方用户名
     * @param iconUrl    头像地址
     */
    void onLoginSuccess(String platFrom, String openId, String token, String SocialName, String iconUrl);

    void onLoginFail(String errorMsg, String errorCode);

    void onLoginCancel();
}
