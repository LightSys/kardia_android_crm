package org.lightsys.crmapp.data;

/**
 * Created by ca2br on 7/13/16.
 *
 * holds info relavent to the timeline
 */
public class TimelineItem {
    private String contactId;
    private String partnerId;
    private String collaborateeId;
    private String collaborateeName;
    private String contactHistoryId;
    private String contactHistoryType;
    private String subject;
    private String notes;
    private String date;

    public TimelineItem () {

    }

    public TimelineItem (String contactId) {
        this.contactId = contactId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getCollaborateeId() {
        return collaborateeId;
    }

    public void setCollaborateeId(String collaborateeId) {
        this.collaborateeId = collaborateeId;
    }

    public String getCollaborateeName() {
        return collaborateeName;
    }

    public void setCollaborateeName(String collaborateeName) {
        this.collaborateeName = collaborateeName;
    }

    public String getContactHistoryId() {
        return contactHistoryId;
    }

    public void setContactHistoryId(String contactHistoryId) {
        this.contactHistoryId = contactHistoryId;
    }

    public String getContactHistoryType() {
        return contactHistoryType;
    }

    public void setContactHistoryType(String contactHistoryType) {
        this.contactHistoryType = contactHistoryType;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
