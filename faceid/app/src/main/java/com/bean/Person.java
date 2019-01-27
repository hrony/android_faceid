package com.bean;

import android.content.Context;

import com.vyagoo.faceid.R;


/**
 * Created by Administrator on 2018/7/26/026.
 */

public class Person {
    public int index;
    public int faceId;
    public int age;
    public int emotion;
    public int gender;
    public int attention;
    public String name;
    public Context mContext;
    int[] rect;

    public Person(int index, int[] rect, int faceId, int age, int emotion, int gender, int attention){
        this.index = index;
        this.rect = rect;
        this.faceId =faceId;
        this.age = age;
        this.emotion = emotion;
        this.gender = gender;
        this.attention = attention;


    }
    public void setContext(Context context){
        this.mContext = context;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
    public int getIndex(){
        return this.index;
    }
    public int getFaceId(){
        return this.faceId;
    }

    public int[] getRect(){
        return rect;
    }

    StringBuffer stringBuffer ;
    @Override
    public String toString() {
        stringBuffer = new StringBuffer();
        stringBuffer.append("faceid =" +faceId+"----"+age);


        switch (this.gender){
            case 0:
                stringBuffer.append(mContext.getResources().getString(R.string.woman));
                break;
            case 1:
                stringBuffer.append(mContext.getResources().getString(R.string.man));
                break;
            case  -1:
                stringBuffer.append(mContext.getResources().getString(R.string.unkown));
                break;
            case -2:
                stringBuffer.append(mContext.getResources().getString(R.string.id_no_exit));
                break;
        }

        switch (this.emotion){
            case 0:
                stringBuffer.append(mContext.getResources().getString(R.string.smile));
                break;
            case 1:
                stringBuffer.append(mContext.getResources().getString(R.string.normal));
                break;
            case 2:
                stringBuffer.append(mContext.getResources().getString(R.string.big_smial));
                break;
            case 3:
                stringBuffer.append(mContext.getResources().getString(R.string.sad));
                break;
            case  -1:
                stringBuffer.append(mContext.getResources().getString(R.string.unkown));
                break;
            case -2:
                stringBuffer.append(mContext.getResources().getString(R.string.id_no_exit));
                break;
        }
//        stringBuffer.append("-关注度："+this.attention+"%");
        return stringBuffer.toString();
    }
}
