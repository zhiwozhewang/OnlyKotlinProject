package com.enn.ionic.js;

import com.google.gson.Gson;

public class FileResponse {

    private String[] urlList;
    private int  sCount;
    private int  fCount;

    public FileResponse(String[] urlList, int sCount, int fCount) {
        this.urlList = urlList;
        this.sCount = sCount;
        this.fCount = fCount;
    }

    public String[] getUrlList() {
        return urlList;
    }

    public void setUrlList(String[] urlList) {
        this.urlList = urlList;
    }

    public int getsCount() {
        return sCount;
    }

    public void setsCount(int sCount) {
        this.sCount = sCount;
    }

    public int getfCount() {
        return fCount;
    }

    public void setfCount(int fCount) {
        this.fCount = fCount;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
