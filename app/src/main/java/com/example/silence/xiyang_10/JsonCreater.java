package com.example.silence.xiyang_10;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.silence.xiyang_10.RichEditor.ContentType;
import com.example.silence.xiyang_10.RichEditor.DragScaleView;
import com.example.silence.xiyang_10.RichEditor.EditorBean;
import com.example.silence.xiyang_10.RichEditor.MyText;
import com.example.silence.xiyang_10.RichEditor.TakePhotoUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Silence on 2018/4/16.
 */

public class JsonCreater {

    public static List<EditorBean> StringToEditorbean(String str,View v){
        List<EditorBean> result = new CopyOnWriteArrayList<> ();;
        String[] beanlist = str.split(",");
        String[] beanstr;
        if(str.length() == 0)
            return null;
        for(int i=0; i<beanlist.length; i++){
            beanstr = beanlist[i].substring(beanlist[i].indexOf("{")+1,beanlist[i].indexOf("}")).split(";",3);
            if(beanstr[0].equals("IMG")){
                EditorBean bean = new EditorBean(ContentType.IMG,beanstr[1],Long.valueOf(beanstr[2]));
                result.add(bean);
            }else if(beanstr[0].equals("CONTENT")){
                EditorBean bean = new EditorBean(ContentType.CONTENT,beanstr[1],Long.valueOf(beanstr[2]));
                result.add(bean);
            }

        }
        return  result;
    }

    public static String addImageJson(RelativeLayout view, Long tag,String url){
        DragScaleView imageView = (DragScaleView)view.findViewWithTag(Long.toString(tag));
        StringBuilder imageString = new StringBuilder("");
        if(imageView == null){
            Toast.makeText(view.getContext(),"imageview is null",Toast.LENGTH_SHORT).show();
        }

        imageString.append(
            "\t\t{\n" +
            "\t\t\"widget\": \"com.example.silence.xiyang_10.RichEditor.ModelImage\",\n"+
            "\t\t\"properties\": [{\n"+
            "\t\t\t\"name\": \"src\",\n"+
            "\t\t\t\"type\": \"url\",\n"+
            "\t\t\t\"value\": \""+"/sdcard/takephoto/"+url+"\"\n"+
            "\t\t},\n"+

            "\t\t{\n"+
            "\t\t\t\"name\": \"tag\",\n"+
            "\t\t\t\"type\": \"\",\n"+
            "\t\t\t\"value\": \""+imageView.getTag()+"\"\n"+
            "\t\t},\n"+

            "\t\t{\n"+
            "\t\t\t\"name\": \"layout_width\",\n"+
            "\t\t\t\"type\": \"dimen\",\n"+
            "\t\t\t\"value\": \""+imageView.getWidth()+"\"\n"+
            "\t\t},\n"+

            "\t\t{\n"+
            "\t\t\t\"name\": \"layout_height\",\n"+
            "\t\t\t\"type\": \"dimen\",\n"+
            "\t\t\t\"value\": \""+imageView.getHeight()+"\"\n"+
            "\t\t},\n"+

            "\t\t{\n"+
            "\t\t\t\"name\": \"layout_marginLeft\",\n"+
            "\t\t\t\"type\": \"dimen\",\n"+
            "\t\t\t\"value\": \""+imageView.getLeft()+"\"\n"+
            "\t\t},\n"+

            "\t\t{\n"+
            "\t\t\t\"name\": \"layout_marginTop\",\n"+
            "\t\t\t\"type\": \"dimen\",\n"+
            "\t\t\t\"value\": \""+imageView.getTop()+"\"\n"+
            "\t\t}]\n"+
            "\t\t}\n");

        return imageString.toString();
    }

    public static String addContentJson(RelativeLayout view, Long tag,String content){
        MyText textView = (MyText) view.findViewWithTag(Long.toString(tag));
        int color = textView.getCurrentTextColor();
        int rgb = color & 0xffffff;
//        int red = (color & 0xff0000) >> 16;
//        int green = (color & 0x00ff00) >> 8;
//        int blue = (color & 0x0000ff);
//        String rgbColor = red + "," + green + "," + blue;
        StringBuilder imageString = new StringBuilder("");
        if(textView == null){
            Toast.makeText(view.getContext(),"Text view is null",Toast.LENGTH_SHORT).show();
        }
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        textView.measure(w, h);

        imageString.append(
            "\t\t{\n" +
            "\t\t\"widget\": \"com.example.silence.xiyang_10.RichEditor.MyText\",\n"+
            "\t\t\"properties\": [{\n"+
            "\t\t\t\"name\": \"text\",\n"+
            "\t\t\t\"type\": \"string\",\n"+
            "\t\t\t\"value\": \""+textView.getText()+"\"\n"+
            "\t\t},\n"+

            "\t\t{\n"+
            "\t\t\t\"name\": \"clickable\",\n"+
            "\t\t\t\"type\": \"boolean\",\n"+
            "\t\t\t\"value\": \"true\"\n"+
            "\t\t},\n"+

            "\t\t{\n"+
            "\t\t\t\"name\": \"tag\",\n"+
            "\t\t\t\"type\": \"\",\n"+
            "\t\t\t\"value\": \""+textView.getTag()+"\"\n"+
            "\t\t},\n"+

            "\t\t{\n"+
            "\t\t\t\"name\": \"textColor\",\n"+
            "\t\t\t\"type\": \"color\",\n"+
            "\t\t\t\"value\": \"#"+String.format("%06x",color)+"\"\n"+// 把int值转化为6位16进制字符串
            "\t\t},\n"+

            "\t\t{\n"+
            "\t\t\t\"name\": \"textSize\",\n"+
            "\t\t\t\"type\": \"dimen\",\n"+
            "\t\t\t\"value\": \""+(int)(textView.getTextSize()/2)+"\"\n"+
            "\t\t},\n"+

            "\t\t{\n"+
            "\t\t\t\"name\": \"layout_marginLeft\",\n"+
            "\t\t\t\"type\": \"dimen\",\n"+
            "\t\t\t\"value\": \""+textView.getLeft()+"\"\n"+
            "\t\t},\n"+

            "\t\t{\n"+
            "\t\t\t\"name\": \"layout_marginTop\",\n"+
            "\t\t\t\"type\": \"dimen\",\n"+
            "\t\t\t\"value\": \""+textView.getTop()+"\"\n"+
            "\t\t}]\n"+
            "\t\t}\n");


        return imageString.toString();
    }

    public static String createJsonStr(RelativeLayout view, List<EditorBean> editorList) {
        StringBuilder json;
        json = new StringBuilder("");//String拼接不出长字符，用可变长度的String
        //构造json文件
        json.append(
            "{\n"+
            "\t\"widget\": \"android.widget.RelativeLayout\",\n"+
            "\t\"properties\": ["+

            "{\n"+  //不能轻易改动
            "\t\t\"name\": \"tag\",\n"+
            "\t\t\"type\": \"\",\n"+
            "\t\t\"value\": \""+ editorList+"\"\n"+
            "\t},\n"+

            "\t{\n"+
            "\t\t\"name\": \"layout_width\",\n"+
            "\t\t\"type\": \"dimen\",\n"+
            "\t\t\"value\": \"match_parent\"\n"+
            "\t},\n"+

            "\t{\n"+
            "\t\t\"name\": \"layout_height\",\n"+
            "\t\t\"type\": \"dimen\",\n"+
            "\t\t\"value\": \"match_parent\"\n"+
            "\t}],\n"+
            "\t\"views\": [\n");


        int count = editorList.size();

        //根据编辑器列表中的内容拼接相应的标签
        for (EditorBean editorBean : editorList) {

            if(count<editorList.size()&&count!=0){
                json.append("\t\t,\n");
            }
            count--;
            switch (editorBean.getType()) {
                case CONTENT:
                    json.append(JsonCreater.addContentJson(view,editorBean.getTag(),editorBean.getContent()));
                    break;
                case IMG:
                    String url = TakePhotoUtils.getImageName(TakePhotoUtils.getImageAbsolutePath((Activity)view.getContext(), Uri.parse(editorBean.getContent())));
                    json.append(JsonCreater.addImageJson(view,editorBean.getTag(),url));
                    break;
            }
        }
        json.append("\t]\n"+
                "}");

        return json.toString();
    }
}
