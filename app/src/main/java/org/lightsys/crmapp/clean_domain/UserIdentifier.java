package org.lightsys.crmapp.clean_domain;

/**
 * Created by Jake on 7/16/2015.
 */
public class UserIdentifier {
    private String name;
    private String partnerId;

    public UserIdentifier() {
    }

    public UserIdentifier(String name, String partnerId) {
        this.name = name;
        this.partnerId = partnerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }
}
