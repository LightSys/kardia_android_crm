package org.lightsys.crmapp.data;

/**
 * Created by nathan on 3/9/16.
 */
public class Staff extends Partner {
    private String kardiaLogin;

    public Staff() {
        super();
    }

    public Staff(String partnerId, String kardiaLogin) {
        super(partnerId);
        setPartnerId(partnerId);
        this.kardiaLogin = kardiaLogin;
    }

    public void setKardiaLogin(String kardiaLogin) {
        this.kardiaLogin = kardiaLogin;
    }

    public String getKardiaLogin() {
        return kardiaLogin;
    }
}
