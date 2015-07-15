package org.lightsys.crmapp.CleanModels;

import java.util.TreeMap;

/**
 * Created by Jake on 7/15/2015.
 */
public class UserProfileList {
    private TreeMap<String, UserProfile> userProfileTreeMap;

    public UserProfileList(TreeMap<String, UserProfile> userProfileTreeMap) {
        this.userProfileTreeMap = userProfileTreeMap;
    }

    public UserProfileList() {
    }

    public TreeMap<String, UserProfile> getUserProfileTreeMap() {
        return userProfileTreeMap;
    }

    public void setUserProfileTreeMap(TreeMap<String, UserProfile> userProfileTreeMap) {
        this.userProfileTreeMap = userProfileTreeMap;
    }
}
