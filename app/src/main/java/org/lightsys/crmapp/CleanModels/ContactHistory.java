package org.lightsys.crmapp.CleanModels;

import java.util.List;

/**
 * Created by Jake on 7/15/2015.
 */
public class ContactHistory {
    private List<Contact> contactList;

    public ContactHistory() {
    }

    public ContactHistory(List<Contact> contactList) {
        this.contactList = contactList;
    }

    public List<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
    }


}
