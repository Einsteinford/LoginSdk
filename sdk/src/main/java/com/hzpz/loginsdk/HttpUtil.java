package com.hzpz.loginsdk;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author kk
 * @date 2017/4/27
 */

public class HttpUtil extends AsyncTask<String, Void, String> {

    // 图片路径
    private String path;
    private OnRequestListener listener;

    /** 自定义一个接口，在需要下载图片的UI类中实现此接口 */
    public interface OnRequestListener {
        void onSuccess(String data);

        void onFailure(Exception e);
    }


    HttpUtil(String path, OnRequestListener listener) {
        this.path = path;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            if (connection.getResponseCode() == 200) {
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\r\n");
                }
                reader.close();
                return response.toString();
            }
        } catch (Exception e) {
            listener.onFailure(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            listener.onSuccess(result);
        }
    }

}
