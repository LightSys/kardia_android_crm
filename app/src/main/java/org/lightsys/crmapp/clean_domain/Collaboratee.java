package org.lightsys.crmapp.clean_domain;

/**
 * Created by Jake on 7/15/2015.
 */
public class Collaboratee {
    private String id;
    private String name;

    public Collaboratee (String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
