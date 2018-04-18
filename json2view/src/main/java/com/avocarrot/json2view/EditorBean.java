package com.avocarrot.json2view;


/**
 * 编辑器实体对象
 * Created by HDL on 2016/9/29.
 */
public class EditorBean {
    private ContentType type;//类型有TITLE, CONTENT, IMG
    private String content;//内容
    private long tag;//用时间的毫秒值作为标记，用于删除时的判断

    public EditorBean() {
    }

    public EditorBean(ContentType type, String content, long tag) {
        this.type = type;
        this.content = content;
        this.tag = tag;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTag() {
        return tag;
    }

    public void setTag(long tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "EditorBean{" +
                 type +
                ";" + content +
                ";" + tag +
                '}';
    }
}
