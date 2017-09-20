package org.lightsys.crmapp.data.infoTypes;

/**
 * Created by otter57 on 9/12/17.
 */

public class Form {
    private int formId;     //id unique to each form
    private String formType;
    private String year;
    private String date;
    private String tags; //event/form information i.e. {formType:signUpSheet, event:Missions week, location:LeTourneau...}

    /* ************************* Construct ************************* */
    public Form() {}

    public Form(int formId, String formType, String year, String date, String tags) {
        this.formId = formId;
        this.formType = formType;
        this.year = year;
        this.date = date;
        this.tags = tags;
    }

    /* ************************* Set ************************* */
    public void setFormId(int formId) {
        this.formId = formId;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /* ************************* Get ************************* */
    public int getFormId() {
        return formId;
    }

    public String getFormType() {
        return formType;
    }

    public String getTags() {
        return tags;
    }

    public String getYear() {
        return year;
    }

    public String getDate() {
        return date;
    }

    public String[] getSplitTags(){
        return tags.split(", ");
    }
}

