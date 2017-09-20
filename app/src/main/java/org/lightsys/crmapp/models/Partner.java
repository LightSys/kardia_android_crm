package org.lightsys.crmapp.models;


import android.icu.text.MessagePattern;

/**
 * Created by nathan on 3/9/16.
 *
 * Edited by tparr on 8/10/17
 */
public class Partner {
    public String PartnerId;
    public String PartnerName;
    public String ProfilePictureFilename;

    public String Surname;
    public String GivenNames;

    public String Address1;
    public String City;
    public String StateProvince;
    public String PostalCode;

    public String Cell;
    public String CellId;
    public String Phone;
    public String PhoneId;
    public String Email;
    public String EmailId;

    public String PhoneJsonId;
    public String CellJsonId;
    public String EmailJsonId;
    public String AddressJsonId;
    public String PartnerJsonId;

    public String Blog;
    public String Fax;
    public String Facebook;
    public String Skype;
    public String Twitter;
    public String Website;

    public Partner(String id, String name)
    {
        this.PartnerId = id;
        this.PartnerName = name;
    }

    public Partner() { }

    //gets full/complete address for when display address within a contact's profile
    public String getFullAddress() {
        return Address1 + ", " + City + ", " + StateProvince + " " + PostalCode;
    }

    public void setFullAddress(String address, String city, String state, String zipCode)
    {
        this.Address1 = address;
        this.City = city;
        this.StateProvince = state;
        this.PostalCode = zipCode;
    }

    public String getPartnerName(){
        return PartnerName;
    }
}
