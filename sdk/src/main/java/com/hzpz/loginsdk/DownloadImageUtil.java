package com.hzpz.loginsdk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author kk
 * @date 2017/4/27
 */

public class DownloadImageUtil extends AsyncTask<String, Integer, Bitmap> {
    // 图片路径
    private String path;
    private OnLogoDownloadListener listener;

    public interface OnLogoDownloadListener {
        /**
         * 下载成功后回调此方法
         * @param bitmap
         */
        void getLogoBitmap(Bitmap bitmap);
    }


    public DownloadImageUtil(String path, OnLogoDownloadListener listener) {
        this.path = path;
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        URL imageUrl = null;
        HttpURLConnection conn = null;
        Bitmap bitmap = null;
        InputStream is = null;
        BufferedInputStream bis = null;

        try {
            imageUrl = new URL(path);
            conn = (HttpURLConnection) imageUrl.openConnection();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is);
            bitmap = BitmapFactory.decodeStream(bis);
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (listener != null) {
            listener.getLogoBitmap(result);
        }
    }

}
