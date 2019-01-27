package com.faceid;

import com.bean.Person;

import java.util.List;

/**
 * Created by Administrator on 2018/8/14/014.
 */

public interface FaceInfoCallBack {
    void onFaceInfoCallBack(List<Person> list);
}
