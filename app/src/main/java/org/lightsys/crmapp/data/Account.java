package org.lightsys.crmapp.data;

/**
 * Created by Jake on 6/18/2015.
 */
public class Account {
    private int id;
    private String AccountName;
    private String AccountPassword;
    private String ServerName;

    public Account(){}

    public Account (int id, String accountName, String accountPassword, String serverName) {
        setId(id);
        setAccountName(accountName);
        setAccountPassword(accountPassword);
        setServerName(serverName);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        this.AccountName = accountName;
    }

    public String getAccountPassword() {
        return AccountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.AccountPassword = accountPassword;
    }

    public String getServerName() {
        return ServerName;
    }

    public void setServerName(String serverName) {
        this.ServerName = serverName;
    }

}
