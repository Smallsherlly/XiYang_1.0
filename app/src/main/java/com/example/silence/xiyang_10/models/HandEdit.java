package com.example.silence.xiyang_10.models;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Silence on 2018/4/20.
 */
// 这是一个可序列化的手账类
public class HandEdit implements Parcelable{

    Long creation;
    Long lastmodification;
    Long zan_number;

    String author;
    String json_path;
    String cover_path;
    String title;
    String content;
    String address;
    Boolean archived;
    Boolean trashed;


    public HandEdit(){
        this.title = "";
        this.content = "";
        this.creation = 0L;
        this.zan_number = 0L;
        this.archived = Boolean.valueOf(false);
        this.trashed = Boolean.valueOf(false);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public HandEdit(Parcel in) {
        setCreation(in.readString());
        setLastModification(in.readString());
        setZan_number(in.readLong());
        setAuthor(in.readString());
        setJson_path(in.readString());
        setCover_path(in.readString());
        setTitle(in.readString());
        setContent(in.readString());
        setArchived(in.readInt());
        setTrashed(in.readInt());
        setAddress(in.readString());
        //super.setCategory(in.readParcelable(Category.class.getClassLoader()));

    }

    public Long get_id() {
        return this.creation;
    }

    public Long getCreation(){
        return creation;
    }

    public Long getLastModification(){
        return lastmodification;
    }

    public Long getZan_number(){return zan_number;}

    public String getAuthor(){return author;}

    public String getJson_path(){return json_path;}

    public String getCover_path(){return cover_path;}

    public  String getTitle(){
        return title;
    }

    public String getContent(){
        return content;
    }

    public Boolean isArchived(){
        return archived;
    }

    public int getArchived(){
        if(archived)
            return 1;
        else
            return 0;
    }

    public String getAddress(){
        return address;
    }

    public Boolean isTrashed(){
        return trashed;
    }
    public int getTrashed(){
        if(trashed)
            return 1;
        else
            return 0;
    }


    public void setCreation(Long mcreation){
        creation = mcreation;
    }

    public void setCreation(String mcreation){
        if(mcreation != null)
            creation = Long.valueOf(mcreation);
    }
    public void setLastModification(String mlastModification){
        lastmodification = Long.valueOf(mlastModification);
    }
    public void setLastModification(Long lastModification){
        lastmodification = lastModification;
    }

    public  void setZan_number(Long zan_num){
        this.zan_number = zan_num;
    }

    public  void setAuthor(String author){
        this.author = author;
    }

    public  void setJson_path(String path){
        this.json_path = path;
    }

    public  void setCover_path(String path){
        this.cover_path = path;
    }

    public void setTitle(String mtitle){
        title = mtitle;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setArchived(Boolean index){
        this.archived = index;
    }
    public void setArchived(int index){
        if(index != 0)
            this.archived = Boolean.valueOf(true);
        else
            this.archived = Boolean.valueOf(false);
    }
    public void setAddress(String address){
        this.address = address;
    }

    public void setTrashed(Boolean index){
        this.trashed = index;
    }

    public void setTrashed(int index){
        if(index != 0)
            this.trashed = Boolean.valueOf(true);
        else
            this.trashed = Boolean.valueOf(false);
    }



    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(String.valueOf(getCreation()));
        parcel.writeString(String.valueOf(getLastModification()));
        parcel.writeLong(getZan_number());
        parcel.writeString(getAuthor());
        parcel.writeString(getJson_path());
        parcel.writeString(getCover_path());
        parcel.writeString(getTitle());
        parcel.writeString(getContent());
        parcel.writeInt(isArchived() ? 1 : 0);
        parcel.writeInt(isTrashed() ? 1 : 0);
        parcel.writeString(getAddress());
    }

    public static final Parcelable.Creator<HandEdit> CREATOR = new Parcelable.Creator<HandEdit>() {

        public HandEdit createFromParcel(Parcel in) {
            return new HandEdit(in);
        }


        public HandEdit[] newArray(int size) {
            return new HandEdit[size];
        }
    };
}
