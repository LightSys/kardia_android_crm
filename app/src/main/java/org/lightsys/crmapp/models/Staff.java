package org.lightsys.crmapp.models;

/**
 * Created by nathan on 3/9/16.
 * Stores staff id and Login information
 */
public class Staff extends Partner {
    private String kardiaLogin;

    public Staff() {
        super();
    }

    public Staff(String partnerId, String kardiaLogin) {
        this.PartnerId = partnerId;
        this.kardiaLogin = kardiaLogin;
    }

    public void setKardiaLogin(String kardiaLogin) {
        this.kardiaLogin = kardiaLogin;
    }

    public String getKardiaLogin() {
        return kardiaLogin;
    }
}
