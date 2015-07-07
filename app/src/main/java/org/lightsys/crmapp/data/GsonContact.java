package org.lightsys.crmapp.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jake- on 7/7/2015.
 */
public class GsonContact {
    @SerializedName("@id")
    String id;

    @SerializedName("partner_id")
    String partnerId;

    @SerializedName("contact_id")
    String contactId;

    @SerializedName("contact")
    String contact;

    @SerializedName("address_id")
    String addressId;

    @SerializedName("contact_type")
    String contactType;

    @SerializedName("phone_country")
    String phoneCountry;

    @SerializedName("phone_area_city")
    String phoneAreaCode;

    @SerializedName("contact_data")
    String contactData;


}
