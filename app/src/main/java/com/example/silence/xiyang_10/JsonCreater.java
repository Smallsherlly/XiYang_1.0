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
        for(int i=0; i<beanlist.length; i++){
            beanstr = beanlist[i].substring(beanlist[i].indexOf("{")+1,beanlist[i].indexOf("}")).split(";",3);
            Toast.makeText(v.getContext(),beanstr[0]+beanstr[1]+beanstr[2],Toast.LENGTH_SHORT).show();
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

    public static String addImageJson(RelativeLayout view, Long tag,String url,int count){
        DragScaleView imageView = (DragScaleView)view.findViewWithTag(tag);
        Toast.makeText(view.getContext(),Long.toString((Long)imageView.getTag()),Toast.LENGTH_SHORT).show();
        StringBuilder imageString = new StringBuilder("");
        if(imageView == null){
            Toast.makeText(view.getContext(),"imageview is null",Toast.LENGTH_SHORT).show();
        }
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        imageView.measure(w, h);
        Toast.makeText(view.getContext(),Integer.toString(imageView.getTop()),Toast.LENGTH_SHORT).show();
        imageString.append(
            "\t\t\"widget\": \"com.example.silence.xiyang_10.RichEditor.ModelImage\",\n"+
            "\t\t\"properties\": [{\n"+
            "\t\t\t\"name\": \"src\",\n"+
            "\t\t\t\"type\": \"url\",\n"+
            "\t\t\t\"value\": \""+"/sdcard/takephoto/"+url+"\"\n"+
            "\t\t},\n"+
            "\t\t{\n"+
            "\t\t\t\"name\": \"layout_width\",\n"+
            "\t\t\t\"type\": \"dimen\",\n"+
            "\t\t\t\"value\": \""+imageView.getMeasuredWidth()+"\"\n"+
            "\t\t},\n"+
            "\t\t{\n"+
            "\t\t\t\"name\": \"layout_height\",\n"+
            "\t\t\t\"type\": \"dimen\",\n"+
            "\t\t\t\"value\": \""+imageView.getMeasuredHeight()+"\"\n"+
            "\t\t},\n"+
            "\t\t{\n"+
            "\t\t\t\"name\": \"layout_marinLeft\",\n"+
            "\t\t\t\"type\": \"dimen\",\n"+
            "\t\t\t\"value\": \""+imageView.getLeft()+"\"\n"+
            "\t\t},\n"+
            "\t\t{\n"+
            "\t\t\t\"name\": \"layout_marginTop\",\n"+
            "\t\t\t\"type\": \"dimen\",\n"+
            "\t\t\t\"value\": \""+imageView.getTop()+"\"\n");
        count--;
        if(count!=0){
            imageString.append("\t}],\n"+"\t\"views\": [{\n");
        }else{
            imageString.append("\t}]\n"+"\t\"views\": [{\n");
        }

        return imageString.toString();
    }

    public static String addContentJson(RelativeLayout view, Long tag,int count){
        MyText textView = (MyText) view.findViewWithTag(tag);
        Toast.makeText(view.getContext(),Long.toString((Long)textView.getTag()),Toast.LENGTH_SHORT).show();
        StringBuilder imageString = new StringBuilder("");
        if(textView == null){
            Toast.makeText(view.getContext(),"Text view is null",Toast.LENGTH_SHORT).show();
        }
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        textView.measure(w, h);
        Toast.makeText(view.getContext(),Integer.toString(textView.getTop()),Toast.LENGTH_SHORT).show();
        imageString.append(
                "\t\t\"widget\": \"com.example.silence.xiyang_10.RichEditor.ModelImage\",\n"+
                        "\t\t\"properties\": [{\n"+
                        "\t\t\t\"name\": \"text\",\n"+
                        "\t\t\t\"type\": \"string\",\n"+
                        "\t\t\t\"value\": \""+textView.getText()+"\"\n"+
                        "\t\t},\n"+
                        "\t\t{\n"+
                        "\t\t\t\"name\": \"contentcolor\",\n"+
                        "\t\t\t\"type\": \"dimen\",\n"+
                        "\t\t\t\"value\": \""+textView.getCurrentTextColor()+"\"\n"+
                        "\t\t},\n"+
                        "\t\t{\n"+
                        "\t\t\t\"name\": \"cotentsize\",\n"+
                        "\t\t\t\"type\": \"dimen\",\n"+
                        "\t\t\t\"value\": \""+textView.getTextSize()+"\"\n"+
                        "\t\t},\n"+
                        "\t\t{\n"+
                        "\t\t\t\"name\": \"layout_width\",\n"+
                        "\t\t\t\"type\": \"dimen\",\n"+
                        "\t\t\t\"value\": \""+textView.getMeasuredWidth()+"\"\n"+
                        "\t\t},\n"+
                        "\t\t{\n"+
                        "\t\t\t\"name\": \"layout_height\",\n"+
                        "\t\t\t\"type\": \"dimen\",\n"+
                        "\t\t\t\"value\": \""+textView.getMeasuredHeight()+"\"\n"+
                        "\t\t},\n"+
                        "\t\t{\n"+
                        "\t\t\t\"name\": \"layout_marinLeft\",\n"+
                        "\t\t\t\"type\": \"dimen\",\n"+
                        "\t\t\t\"value\": \""+textView.getLeft()+"\"\n"+
                        "\t\t},\n"+
                        "\t\t{\n"+
                        "\t\t\t\"name\": \"layout_marginTop\",\n"+
                        "\t\t\t\"type\": \"dimen\",\n"+
                        "\t\t\t\"value\": \""+textView.getTop()+"\"\n");
        count--;
        if(count!=0){
            imageString.append("\t}],\n");
        }else{
            imageString.append("\t}]\n");
        }

        return imageString.toString();
    }

    public static String createJsonStr(RelativeLayout view, List<EditorBean> editorList) {
        StringBuilder json;
        int count = editorList.size();

        json = new StringBuilder("");//String拼接不出长字符，用可变长度的String
        //构造json文件
        json.append(
                "{\n"+
                        "\t\"widget\": \"android.widget.RelativeLayout\",\n"+
                        "\t\"properties\": [{\n"+

                        "\t\t\"name\": \"layout_width\",\n"+
                        "\t\t\"type\": \"dimen\",\n"+
                        "\t\t\"value\": \"match_parent\"\n"+
                        "\t},\n"+

                        "\t{\n"+
                        "\t\t\"name\": \"tag\",\n"+
                        "\t\t\"type\": \"dimen\",\n"+
                        "\t\t\"value\": \""+ editorList+"\"\n"+
                        "\t},"+

                        "\t{\n"+
                        "\t\t\"name\": \"layout_height\",\n"+
                        "\t\t\"type\": \"dimen\",\n"+
                        "\t\t\"value\": \"match_parent\"\n"+
                        "\t}],\n"+
                        "\t\"views\": [{\n");



        //根据编辑器列表中的内容拼接相应的标签
        for (EditorBean editorBean : editorList) {

            switch (editorBean.getType()) {
                case CONTENT:
                    //xml.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;" + editorBean.getContent() + "</p>\n");//拼接内容，自动空两格
                    break;
                case TITLE:
                    //xml.append("<" + titleTypeStr + ">" + editorBean.getContent() + "</" + titleTypeStr + ">\n");//拼接标题---->默认h3
                    break;
                case IMG:
                    //xml.append("<center><img  src='" + baseImgUrl + "/" + TakePhotoUtils.getImageName(TakePhotoUtils.getImageAbsolutePath(mActivity, Uri.parse(editorBean.getContent()))) + "' /></center><br/>\n");//插入图片，图片的路径（服务器url）由调用这指定，图片名自动生成
                    Toast.makeText(view.getContext(),Integer.toString(view.getId()),Toast.LENGTH_SHORT).show();
                    String url = TakePhotoUtils.getImageName(TakePhotoUtils.getImageAbsolutePath((Activity)view.getContext(), Uri.parse(editorBean.getContent())));
                    json.append(JsonCreater.addImageJson(view,editorBean.getTag(),url,count));

                    break;
            }
        }
        json.append("\t}]\n"+
                "}");

        return json.toString();
    }
}
