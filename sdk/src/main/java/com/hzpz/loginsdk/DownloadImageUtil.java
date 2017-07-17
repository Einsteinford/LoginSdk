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
 * Created by Administrator on 2017/4/27.
 */

public class DownloadImageUtil extends AsyncTask<String, Integer, Bitmap> {

private String path;// 图片路径
private onLogoDownloadListener listener;

    /** 自定义一个接口，在需要下载图片的UI类中实现此接口 */
    public interface onLogoDownloadListener {
        void getLogoBitmap(Bitmap bitmap);
    }


    public DownloadImageUtil(String path, onLogoDownloadListener listener) {
        this.path =  path;//http://www.baidu.com/image/dwadkwajda/134wd.jpg
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
