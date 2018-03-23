package com.example.silence.xiyang_10.RichEditor;

/**
 * 标题类型转换工具
 * Created by HDL on 2016/10/15.
 */

public class TitleTypeUtils {
    public static String valueOf(TitleType titleType) {
        String titleTypeStr;
        switch (titleType) {
            case H1:
                titleTypeStr = "h1";
                break;
            case H2:
                titleTypeStr = "h2";
                break;
            case H3:
                titleTypeStr = "h3";
                break;
            case H4:
                titleTypeStr = "h4";
                break;
            case H5:
                titleTypeStr = "h5";
                break;
            case H6:
                titleTypeStr = "h6";
                break;
            default:
                titleTypeStr = "h3";//默认h3
        }
        return titleTypeStr;
    }
}
