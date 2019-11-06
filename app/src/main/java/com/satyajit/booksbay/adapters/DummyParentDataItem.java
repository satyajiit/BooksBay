package com.satyajit.booksbay.adapters;

import java.io.Serializable;
import java.util.ArrayList;

public class DummyParentDataItem implements Serializable {
    private String parentName,cls;
    private ArrayList<DummyChildDataItem> childDataItems;

    public DummyParentDataItem(String parentName, String cls, ArrayList<DummyChildDataItem> childDataItems) {
        this.parentName = parentName;
        this.childDataItems = childDataItems;
        this.cls = cls;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public ArrayList<DummyChildDataItem> getChildDataItems() {
        return childDataItems;
    }

    public void setChildDataItems(ArrayList<DummyChildDataItem> childDataItems) {
        this.childDataItems = childDataItems;
    }

    public String getCls() {
        return cls;
    }
}