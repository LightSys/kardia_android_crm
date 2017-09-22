package org.lightsys.crmapp.data.infoTypes;

/**
 * Created by otter57 on 9/12/17.
 */

public class Form {
    private int formId;     //id unique to each form
    private String formType;
    private String date;
    private String university;
    private String event;
    private String description;
    /* ************************* Construct ************************* */
    public Form() {}

    /* ************************* Set ************************* */
    public void setFormId(int formId) {
        this.formId = formId;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getUniversity() {
        return university;
    }

    public String getEvent() {
        return event;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() { return description; }
}

