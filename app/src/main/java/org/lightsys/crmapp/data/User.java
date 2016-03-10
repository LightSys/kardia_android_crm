package org.lightsys.crmapp.data;

/**
 * Created by nathan on 3/9/16.
 */
public class User {
    private String username;
    private String password;
    private String server;
    private Staff staff;

    public User() {

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getServer() {
        return server;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}
