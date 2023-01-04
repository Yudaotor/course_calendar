package com.example.a2022finalproject;

import android.os.Parcel;
import android.os.Parcelable;

public class Class implements Parcelable {
    private String weekNo;
    private String name;
    private String time;
    private String room;
    public Class(String weekNo, String name, String time, String room){
        this.weekNo = weekNo;
        this.name = name;
        this.time = time;
        this.room = room;
    }
    public Class(){

    }
    //Parcel
    protected Class(Parcel in) {
        weekNo = in.readString();
        name = in.readString();
        time = in.readString();
        room = in.readString();
    }

    public static final Creator<Class> CREATOR = new Creator<Class>() {
        @Override
        public Class createFromParcel(Parcel in) {
            return new Class(in);
        }

        @Override
        public Class[] newArray(int size) {
            return new Class[size];
        }
    };

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getWeekNo(){
        return weekNo;
    }
    public void setWeekNo(String weekNo){
        this.weekNo = weekNo;
    }
    public String getTime(){
        return time;
    }
    public void setTime(String time){
        this.time = time;
    }
    public String getRoom(){
        return room;
    }
    public void setRoom(String room){
        this.room = room;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(weekNo);
        parcel.writeString(name);
        parcel.writeString(time);
        parcel.writeString(room);
    }
}
