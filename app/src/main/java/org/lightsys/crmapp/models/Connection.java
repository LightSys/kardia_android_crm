package org.lightsys.crmapp.models;

/**
 * Created by otter57 on 9/12/17.
 *
 * Stores information for a new connection (from sign-up sheet or Interest form)
 * Data sent to server along with form information as a new person
 */

public class Connection {
    private String name;    //last name, first name
    private String gradYear;
    private String tags; //what they are interested in/skills/etc i.e. {intern, programming}
    private String phone;
    private String email;
    private int formId; //id of the form the information was received from

    /* ************************* Construct ************************* */
    public Connection() {}

    public Connection(int formId, String name, String gradYear, String tags, String phone, String email) {
        this.formId = formId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.tags = tags;
        this.gradYear = gradYear;
    }

    /* ************************* Set ************************* */
    public void setName(String name) {
        this.name = name;
    }

    public void setGradYear(String gradYear) {
        this.gradYear = gradYear;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    /* ************************* Get ************************* */
    public String getName() {
        return name;
    }

    public String getGradYear() {
        return gradYear;
    }

    public String getTags() {
        return tags;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public int getFormId() {
        return formId;
    }

    public String[] getSplitTags(){
        return tags.split(", ");
    }
}
