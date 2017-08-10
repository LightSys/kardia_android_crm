package org.lightsys.crmapp.data;

/**
 * Class represents a notification to be sent to user at a specific time
 * Created by Daniel Garcia on 01/Aug/2017 to model PrayerNotification class
 * in Donor App created by Andrew Lockridge on 6/8/2015.
 */
public class Notification {

    private int id; // used for database storage
    private long notificationTime; // time to be sent in milliseconds
    private String partnerID; // id of partner to be reminded of
    private String note; // note accompanying notification

    public Notification() {}

    public Notification(int id, long notificationTime, String partnerID, String note) {
        this.id = id;
        this.notificationTime = notificationTime;
        this.partnerID = partnerID;
        this.note = note;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public long getNotificationTime() {return notificationTime;}

    public void setNotificationTime(long time) {notificationTime = time;}

    public String getPartnerID() {return partnerID;}

    public void setPartnerID(String id) {partnerID = id;}

    public String getNote() {return note;}

    public void setNote(String n) {note = n;}


}