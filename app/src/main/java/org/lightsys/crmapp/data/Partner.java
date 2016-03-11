package org.lightsys.crmapp.data;

import java.util.List;

/**
 * Created by nathan on 3/9/16.
 */
public class Partner {
    private String partnerId;
    private String partnerName;
    private String profilePictureFilename;
    private List<Partner> collaboratees;
    private String surname;
    private String givenNames;
    private String phone;
    private String cell;
    private String email;
    private String address1;
    private String city;
    private String stateProvince;
    private String postalCode;

    public Partner() {

    }

    public Partner(String partnerId) {
        this.partnerId = partnerId;
    }

    public Partner(String partnerId, String partnerName) {
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        // TODO Get name and collaboratees
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
