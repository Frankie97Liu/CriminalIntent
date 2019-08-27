package com.example.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime {
    //文章标题序号
    private UUID mTitleId;

    //标题
    private String title;

    //日期
    private Date titleData;

    //陋习是否被解决
    private boolean isSolved;


    public Crime(){
        //产生一个随机唯一ID值
        mTitleId = UUID.randomUUID();
        titleData = new Date();
    }

    public UUID getTitleId() {
        return mTitleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTitleData() {
        return titleData;
    }

    public void setTitleData(Date titleData) {
        this.titleData = titleData;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }
}