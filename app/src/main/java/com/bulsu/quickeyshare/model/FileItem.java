package com.bulsu.quickeyshare.model;

/**
 * Created by mykelneds on 23/03/2017.
 */

public class FileItem {

    String path;
    int category;
    boolean isSelected;

    public FileItem(String path, int category, boolean isSelected) {
        this.path = path;
        this.category = category;
        this.isSelected = isSelected;
    }

    public String getPath() {
        return path;
    }

    public int getCategory() {
        return category;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
