package org.lightsys.crmapp.models;

/**
 * Created by otter57 on 9/12/17.
 *
 * stores information for a specific form
 * This data is added to the server with each person's data from the sign-up sheet
 */

public class Form {
    private int formId;     //id unique to each form
    private String formDescription;
    private String date;
    private String formTags;
    private String signUpTags;
    /* ************************* Construct ************************* */
    public Form() {}

    /* ************************* Set ************************* */
    public void setFormId(int formId) {
        this.formId = formId;
    }

    public void setFormDescription(String formDescription) {
        this.formDescription = formDescription;
    }

    public void setFormTags(String formTags) {
        this.formTags = formTags;
    }

    public void setSignUpTags(String signUpTags) {
        this.signUpTags = signUpTags;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /* ************************* Get ************************* */
    public int getFormId() {
        return formId;
    }

    public String getFormDescription() {
        return formDescription;
    }

    public String getFormTags() {
        return formTags;
    }

    public String getSignUpTags() {
        return signUpTags;
    }

    public String getDate() {
        return date;
    }
}

