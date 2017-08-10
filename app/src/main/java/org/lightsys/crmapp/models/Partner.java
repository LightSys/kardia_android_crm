package org.lightsys.crmapp.models;


/**
 * Created by nathan on 3/9/16.
 */
public class Partner {
    private String partnerId;
    private String partnerName;
    private String profilePictureFilename;

    private String surname;
    private String givenNames;

    private String address1;
    private String city;
    private String stateProvince;
    private String postalCode;

    private String cell;
    private String cellId;
    private String phone;
    private String phoneId;
    private String email;
    private String emailId;

    private String phoneJsonId;
    private String cellJsonId;
    private String emailJsonId;
    private String addressJsonId;
    private String partnerJsonId;

    private String blog;
    private String fax;
    private String facebook;
    private String skype;
    private String twitter;
    private String website;


    // Default constructor for a partner with no given info
    public Partner() {

    }

    // Constructor for a partner given their Id
    public Partner(String partnerId) {

        this.partnerId = partnerId;
    }

    public Partner(String partnerId, String partnerName) {
        this.partnerId = partnerId;
        this.partnerName = partnerName;
    }

    //get and set ID and names for a contact
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

    //gets full/ complete address for when display address within a contact's profile
    public String getFullAddress() {
        return getAddress1() + ", " + getCity() + ", " + getStateProvince() + " " + getPostalCode();
    }

    public void setFullAddress(String address, String city, String state, String zipCode) {
        this.address1 = address;
        this.city = city;
        this.stateProvince = state;
        this.postalCode = zipCode;
    }

    //get and set various components of a contact's address
    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address) {
        this.address1 = address;
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




    //get and set various info for contacting a contact
    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getCellId() { return cellId; }

    public void setCellId(String cellId) { this.cellId = cellId; }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneId() { return phoneId; }

    public void setPhoneId(String phoneId) { this.phoneId = phoneId; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailId() { return emailId; }

    public void setEmailId(String emailId) { this.emailId = emailId; }



    public String getPhoneJsonId() {
        return phoneJsonId;
    }

    public void setPhoneJsonId(String phoneJsonId) {
        this.phoneJsonId = phoneJsonId;
    }

    public String getCellJsonId() {
        return cellJsonId;
    }

    public void setCellJsonId(String cellJsonId) {
        this.cellJsonId = cellJsonId;
    }

    public String getEmailJsonId() {
        return emailJsonId;
    }

    public void setEmailJsonId(String emailJsonId) {
        this.emailJsonId = emailJsonId;
    }

    public  String getAddressJsonId(){
        return addressJsonId;
    }

    public void setAddressJsonId(String addressJsonId){
        this.addressJsonId = addressJsonId;
    }

    public String getPartnerJsonId(){
        return partnerJsonId;
    }

    public void setPartnerJsonId(String partnerJsonId){
        this.partnerJsonId = partnerJsonId;
    }


    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

}
