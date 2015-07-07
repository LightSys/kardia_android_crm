package org.lightsys.crmapp.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jake- on 7/7/2015.
 */
public class GsonAddress {
    @SerializedName("@id")
    String id;

    @SerializedName("partner_id")
    String partnerId;

    @SerializedName("address")
    String address;

    @SerializedName("is_valid")
    String isValid;

    @SerializedName("record_status_code")
    String recordStatusCode;

    @SerializedName("location_id")
    String locationId;

    @SerializedName("location_type")
    String locationType;

    @SerializedName("location_type_code")
    String locationTypeCode;

    @SerializedName("date_effective")
    String dateEffective;

    @SerializedName("date_good_until")
    String dateGoodUntil;

    @SerializedName("in_care_of")
    String inCareOf;

    @SerializedName("address_1")
    String addressOne;

    @SerializedName("address_2")
    String addressTwo;

    @SerializedName("address_3")
    String addressThree;

    @SerializedName("city")
    String city;

    @SerializedName("state_province")
    String stateProvince;

    @SerializedName("postal_code")
    String postalCode;

    @SerializedName("country_code")
    String countryCode;

    @SerializedName("country_iso2")
    String countryIsoTwo;

    @SerializedName("country_iso3")
    String countryIso3;

    @SerializedName("country_fips104")
    String countryFips;

    @SerializedName("country_name")
    String countryName;

    @SerializedName("comments")
    String comments;

    @SerializedName("date_created")
    CustomDate dateCreated;

    @SerializedName("created_by")
    String createdBy;

    @SerializedName("date_modified")
    CustomDate dateModified;

    @SerializedName("modified_by")
    String modifiedBy;

    @SerializedName("name")
    String name;

    @SerializedName("annotation")
    String annotation;

    @SerializedName("inner_type")
    String innerType;

    @SerializedName("outer_type")
    String outerType;


    class CustomDate {
        int year;
        int month;
        int day;
        int hour;
        int minute;
        int second;
    }
}
