package com.hzpz.loginsdk;

/**
 * Created by Administrator on 2017/6/8.
 */

public interface ShareListener {
    void onShareSuccess();

    void onShareFail();

    void onShareCancel();
}
