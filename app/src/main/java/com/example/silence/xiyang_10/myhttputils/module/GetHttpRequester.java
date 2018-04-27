package com.example.silence.xiyang_10.myhttputils.module;

import android.os.Message;

import com.example.silence.xiyang_10.myhttputils.base.GlobalFied;
import com.example.silence.xiyang_10.myhttputils.bean.HttpBody;
import com.example.silence.xiyang_10.myhttputils.bean.ICommCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * get请求器
 * Created by HDL on 2016/12/21.
 */

public class GetHttpRequester extends HttpRequester {
    private static final String TAG = "GetHttpRequester";
    public GetHttpRequester(HttpBody mHttpBody, ICommCallback callback) {
        this.mHttpBody = mHttpBody;
        this.callback = callback;
    }

    @Override
    public void request() {
        new Thread() {
            @Override
            public void run() {
                String urlPath = mHttpBody.getUrl();
                try {
                    if (getParams() != null) {//判断是否有参数
                            urlPath += "?" + getParams();
                    }
                    URL url = new URL(urlPath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(mHttpBody.getReadTimeOut());
                    conn.setConnectTimeout(mHttpBody.getConnTimeOut());
                    conn.setDoInput(true);
                    conn.setUseCaches(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        int len = 0;
                        byte[] buf = new byte[1024 * 1024];
                        StringBuilder json = new StringBuilder();
                        while ((len = is.read(buf)) != -1) {
                            json.append(new String(buf, 0, len));
                        }
                        is.close();
                        Message msg = mHandler.obtainMessage();
                        msg.what = GlobalFied.WHAT_REQ_SUCCESS;
                        msg.obj = json.toString();
                        mHandler.sendMessage(msg);
                    } else {
                        mHandler.sendEmptyMessage(GlobalFied.WHAT_REQ_FAILED);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(GlobalFied.WHAT_MALFORMED_URL_EXCEPTION);
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(GlobalFied.WHAT_IO_EXCEPTION);
                }
            }
        }.start();
    }

}
