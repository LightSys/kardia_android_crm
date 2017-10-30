package org.lightsys.crmapp.models;

/**
 * Created by otter57 on 10/11/17.
 */

public class Tag {
    private int tagId;
    private String tagLabel;
    private String tagDesc;
    private boolean tagActive;

    public Tag(){}


    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTagLabel() {
        return tagLabel;
    }

    public void setTagLabel(String tagLabel) {
        this.tagLabel = tagLabel;
    }

    public String getTagDesc() {
        return tagDesc;
    }

    public void setTagDesc(String tagDesc) {
        this.tagDesc = tagDesc;
    }

    public boolean isTagActive() {
        return tagActive;
    }

    public void setTagActive(boolean tagActive) {
        this.tagActive = tagActive;
    }

}
