package com.hzpz.loginsdk;

/**
 *
 * @author kk
 * @date 2017/6/8
 */

public interface ShareListener {
    void onShareSuccess();

    void onShareFail();

    void onShareCancel();
}
