package org.lightsys.crmapp.clean_domain;

import java.util.List;

/**
 * Created by Jake on 7/15/2015.
 */
public class LoggedInUser {
    private String username;
    private String password;
    private String serverAddress;
    private String partnerId;
    private List<UserIdentifier> collaborateeList;

    public LoggedInUser() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public List<UserIdentifier> getCollaborateeList() {
        return collaborateeList;
    }

    public void setCollaborateeList(List<UserIdentifier> collaborateeList) {
        this.collaborateeList = collaborateeList;
    }

    public LoggedInUser(String username, String password, String serverAddress) {

        this.username = username;
        this.password = password;
        this.serverAddress = serverAddress;
    }
}
