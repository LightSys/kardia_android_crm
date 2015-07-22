package org.lightsys.crmapp.clean_domain;

import java.util.List;

/**
 * Created by Jake on 7/15/2015.
 */
public class UserProfile {
    private String name;
    private String partnerId;
    private List<Address> addressList;
    private List<ContactInfo> contactInfoList;
    private ContactHistory contactHistory;
    /**
     * I'm not sure what exactly timeline is yet.
     */
    //private Timeline timeline


    // Not entirely sure if this is the best constructor yet. Depending on how info comes in, it might
    // be beneficial to create one that takes less parameters.
    /**
     *
     * @param name
     * @param partnerId
     * @param addressList
     * @param contactInfoList
     * @param contactHistory
     */
    public UserProfile(String name, String partnerId, List<Address> addressList, List<ContactInfo> contactInfoList, ContactHistory contactHistory) {
        this.name = name;
        this.partnerId = partnerId;
        this.addressList = addressList;
        this.contactInfoList = contactInfoList;
        this.contactHistory = contactHistory;
    }

    public UserProfile() {
    }
}
