package com.example.silence.xiyang_10;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.silence.xiyang_10.RichEditor.DragScaleView;
import com.example.silence.xiyang_10.RichEditor.EditorBean;
import com.example.silence.xiyang_10.RichEditor.TakePhotoUtils;

import java.util.List;

/**
 * Created by Silence on 2018/4/16.
 */

public class JsonCreater {



    public static String addImageJson(RelativeLayout view, Long tag,String url){
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
            "\t\t\"widget\": \"com.example.silence.xiyang_10.RichEditor.DragScaleView\",\n"+
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
            "\t\t\t\"value\": \""+imageView.getTop()+"\"\n"+
            "\t\t}]\n"
        );
        return imageString.toString();
    }

    public static String createJsonStr(RelativeLayout view, List<EditorBean> editorList) {
        StringBuilder json;
        json = new StringBuilder("");//String拼接不出长字符，用可变长度的String
        //构造html文件
        json.append(
                "{\n"+
                        "\t\"widget\": \"android.widget.RelativeLayout\",\n"+
                        "\t\"properties\": [{\n"+

                        "\t\t\"name\": \"background\",\n"+
                        "\t\t\"type\": \"color\",\n"+
                        "\t\t\"value\": \"#919191\"\n"+
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

                        "\t\"views\": [{\n"
        );



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
                    json.append(JsonCreater.addImageJson(view,editorBean.getTag(),url));
                    break;
            }
        }
        json.append("\t}]\n"+
                "}");

        return json.toString();
    }
}
