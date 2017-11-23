package com.hzpz.loginsdk;

/**
 *
 * @author kk
 * @date 17-5-26
 */

public interface LoginListener {
    /**
     * 登录成功
     * @param platFrom   所使用的社交平台，例"qq"
     * @param openId     开放id
     * @param token      密钥
     * @param socialName 第三方用户名
     * @param iconUrl    头像地址
     */
    void onLoginSuccess(String platFrom, String openId, String token, String socialName, String iconUrl);

    /**
     * 登录失败
     * @param errorMsg
     * @param errorCode
     */
    void onLoginFail(String errorMsg, String errorCode);

    /**
     * 登录取消
     */
    void onLoginCancel();
}
