package org.lightsys.crmapp.old_data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jake- on 6/26/2015.
 */
public class GsonCollaboratee {

    @SerializedName("@id")
    String id;

    @SerializedName("collaborator_id")
    String collaboratorId;

    @SerializedName("collaborator_name")
    String collaboratorName;

    @SerializedName("collaborator_type_id")
    String collaboratorTypeId;

    @SerializedName("collaborator_type")
    String collaboratorType;

    @SerializedName("partner_id")
    String partnerId;

    @SerializedName("partner_name")
    String partnerName;

    @SerializedName("partner_ref")
    String partnerRef;

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

    public String getPartnerName() {
        return partnerName;
    }

    public String getPartnerId() {
        return partnerId;
    }

    class CustomDate {
        int year;
        int month;
        int day;
        int hour;
        int minute;
        int second;
    }
}
