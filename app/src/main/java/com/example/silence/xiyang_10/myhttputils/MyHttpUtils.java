package com.example.silence.xiyang_10.myhttputils;

import android.util.Log;


import com.example.silence.xiyang_10.myhttputils.bean.CommCallback;
import com.example.silence.xiyang_10.myhttputils.bean.HttpBody;
import com.example.silence.xiyang_10.myhttputils.bean.ICommCallback;
import com.example.silence.xiyang_10.myhttputils.module.DownLoadHttpRequester;
import com.example.silence.xiyang_10.myhttputils.module.GetHttpRequester;
import com.example.silence.xiyang_10.myhttputils.module.PostHttpRequester;
import com.example.silence.xiyang_10.myhttputils.module.ProvideHttpRequester;
import com.example.silence.xiyang_10.myhttputils.module.UpLoadHttpRequester;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * 轻量级网络请求框架MyHttptUtils
 * Created by HDL on 2016/12/21.
 */

public class MyHttpUtils {
    private static final String TAG = "MyHttpUtils";
    private HttpBody mHttpBody = new HttpBody();//请求体对象
    private ICommCallback callback;

    public static com.example.silence.xiyang_10.myhttputils.MyHttpUtils build() {
        return new com.example.silence.xiyang_10.myhttputils.MyHttpUtils();
    }

    /**
     * 构造url
     *
     * @param url desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils url(String url) {
        mHttpBody.setUrl(url);
        return this;
    }

    /**
     * 构造文件上传的url
     *
     * @param uploadUrl desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils uploadUrl(String uploadUrl) {
        mHttpBody.setUploadUrl(uploadUrl);
        return this;
    }

    /**
     * 构造javabean对象
     *
     * @param javaBean desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils setJavaBean(Class javaBean) {
        mHttpBody.setJavaBean(javaBean);
        return this;
    }

    /**
     * 设置读取时间超时时间，模式30s
     *
     * @param readTimeOut desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils setReadTimeOut(int readTimeOut) {
        mHttpBody.setReadTimeOut(readTimeOut);
        return this;
    }

    /**
     * 设置链接时间超时时间，模式5s
     *
     * @param connTimeOut desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils setConnTimeOut(int connTimeOut) {
        mHttpBody.setConnTimeOut(connTimeOut);
        return this;
    }

    /**
     * 设置请求体
     *
     * @param mHttpBody desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils setHttpBody(HttpBody mHttpBody) {
        this.mHttpBody = mHttpBody;
        return this;
    }

    /**
     * 获取httpbody
     *
     * @return this
     */
    public HttpBody getHttpBody() {
        return mHttpBody;
    }

    /**
     * 添加参数----键值对
     *
     * @param key   desc
     * @param value desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils addParam(String key, Object value) {
        mHttpBody.addParam(key, value);
        return this;
    }

    /**
     * 设置请求参数-----按集合
     *
     * @param params desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils addParams(Map<String, Object> params) {
        mHttpBody.setParams(params);
        return this;
    }

    /**
     * 设置文件保存的目录
     *
     * @param dir desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils setFileSaveDir(String dir) {
        mHttpBody.setFileSaveDir(dir);
        return this;
    }

    /**
     * 添加文件---按路径
     *
     * @param filePath desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils addFile(String filePath) {
        List<File> files = mHttpBody.getFiles();
        File file = new File(filePath);
        Log.e(TAG, "addFile: 文件路径为----------" + filePath);
        if (file.exists()) {
            files.add(file);
            mHttpBody.setFiles(files);
        } else {
            callback.onFailed(new FileNotFoundException("NOFile"));
            callback.onComplete();
        }
        return this;
    }

    /**
     * 添加文件---按文件
     *
     * @param filePath desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils addFile(File filePath) {
        List<File> files = mHttpBody.getFiles();
        if (filePath.exists()) {
            files.add(filePath);
            mHttpBody.setFiles(files);
        } else {
            callback.onFailed(new FileNotFoundException("NOFile"));
            callback.onComplete();
        }
        return this;
    }

    /**
     * 添加文件---按文件列表
     *
     * @param filePaths desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils addFiles(List<File> filePaths) {
        List<File> files = mHttpBody.getFiles();
        try {
            for (File filePath : filePaths) {
                if (filePath.exists()) {
                    files.add(filePath);
                }
            }
            mHttpBody.setFiles(files);
        } catch (Exception e) {
            callback.onFailed(new Exception("NOFile"));
            callback.onComplete();
        }

        return this;
    }

    /**
     * 添加文件---按文件路径列表
     *
     * @param filePaths desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils addFilesByPath(List<String> filePaths) {
        List<File> files = mHttpBody.getFiles();
        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (file.exists()) {
                files.add(file);
            } else {
                callback.onFailed(new FileNotFoundException("NOFile"));
                callback.onComplete();
            }
        }
        mHttpBody.setFiles(files);
        return this;
    }

    /**
     * 执行Get请求
     *
     * @param callback desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils onExecute(CommCallback callback) {
        this.callback = callback;
        ProvideHttpRequester requester = new ProvideHttpRequester(new GetHttpRequester(mHttpBody, callback));
        requester.startRequest();//开始请求
        return this;
    }

    /**
     * 执行Post请求
     *
     * @param callback desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils onExecuteByPost(CommCallback callback) {
        this.callback = callback;
        ProvideHttpRequester requester = new ProvideHttpRequester(new PostHttpRequester(mHttpBody, callback));
        requester.startRequest();//开始请求
        return this;
    }

    /**
     * 下载文件
     *
     * @param callback desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils onExecuteDwonload(CommCallback callback) {
        this.callback = callback;
        ProvideHttpRequester requester = new ProvideHttpRequester(new DownLoadHttpRequester(mHttpBody, callback));
        requester.startRequest();//开始请求
        return this;
    }

    /**
     * 上传文件
     *
     * @param callback desc
     * @return this
     */
    public com.example.silence.xiyang_10.myhttputils.MyHttpUtils onExecuteUpLoad(CommCallback callback) {
        this.callback = callback;
        ProvideHttpRequester requester = new ProvideHttpRequester(new UpLoadHttpRequester(mHttpBody, callback));
        requester.startRequest();//开始请求
        return this;
    }

}
