package org.lightsys.crmapp.data;

import java.util.List;

/**
 * Created by nathan on 3/9/16.
 */
public class Partner {
    private String partnerId;
    private String partnerName;
    private String profilePictureFilename;
    private List<Contact> contacts;
    private List<Address> addresses;

    public Partner() {

    }

    public Partner(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getProfilePictureFilename() {
        return profilePictureFilename;
    }

    public void setProfilePictureFilename(String profilePictureFilename) {
        this.profilePictureFilename = profilePictureFilename;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
