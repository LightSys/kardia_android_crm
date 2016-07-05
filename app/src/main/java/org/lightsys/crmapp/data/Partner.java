package org.lightsys.crmapp.data;

import java.util.List;

/**
 * Created by nathan on 3/9/16.
 */
public class Partner {
    private String partnerId;
    private String partnerName;
    private String profilePictureFilename;

    public Partner() {

    }

    public Partner(String partnerId) {
        this.partnerId = partnerId;
    }

    public Partner(String partnerId, String partnerName) {
        this.partnerId = partnerId;
        this.partnerName = partnerName;
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
}
