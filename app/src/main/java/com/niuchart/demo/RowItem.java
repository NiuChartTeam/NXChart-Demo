package com.niuchart.demo;

public class RowItem {
    private String mName;
    private String mDesc;
    private String mId;
    private String mReportName;

    public RowItem(String name, String desc, String id) {
        mName = name;
        mDesc = desc;
        mId = id;
    }

    public RowItem(String name, String desc, String id, String reportName) {
        mName = name;
        mDesc = desc;
        mId = id;
        mReportName = reportName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getReportName() {
        return mReportName;
    }

    public void setReportName(String reportName) {
        mReportName = reportName;
    }
}
