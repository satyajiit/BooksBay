package com.satyajit.booksbay.adapters;

import java.io.Serializable;

public class DummyChildDataItem implements Serializable {
    private String childName;

    public DummyChildDataItem(String childName) {
        this.childName = childName;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }
}