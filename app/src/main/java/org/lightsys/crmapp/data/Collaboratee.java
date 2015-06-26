package org.lightsys.crmapp.data;

import android.provider.Telephony;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jake- on 6/25/2015.
 */
public class Collaboratee {

    @SerializedName("@id")
    private String KardiaIdRef;

    @SerializedName("collaborator_id")
    private String CollaboratorId;

    private String CollaboratorName;
    private String CollaboratorTypeId;
    private String CollaboratorType;
    private String PartnerId;
    private String PartnerName;
    private String PartnerRef;

    public Collaboratee () {}

    public Collaboratee(String kardiaIdRef, String collaboratorId,
                        String collaboratorName, String collaboratorTypeId,
                        String collaboratorType, String partnerId,
                        String partnerName, String partnerRef) {
        KardiaIdRef = kardiaIdRef;
        CollaboratorId = collaboratorId;
        CollaboratorName = collaboratorName;
        CollaboratorTypeId = collaboratorTypeId;
        CollaboratorType = collaboratorType;
        PartnerId = partnerId;
        PartnerName = partnerName;
        PartnerRef = partnerRef;
    }

    public String getKardiaIdRef() {
        return KardiaIdRef;
    }
    public String getCollaboratorId() {
        return CollaboratorId;
    }
    public String getCollaboratorName() {
        return CollaboratorName;
    }
    public String getCollaboratorTypeId() {
        return CollaboratorTypeId;
    }
    public String getCollaboratorType() {
        return CollaboratorType;
    }
    public String getPartnerId() {
        return PartnerId;
    }
    public String getPartnerName() {
        return PartnerName;
    }
    public String getPartnerRef() {
        return PartnerRef;
    }

    public void setKardiaIdRef(String kardiaIdRef) {
        KardiaIdRef = kardiaIdRef;
    }

    public void setCollaboratorId(String collaboratorId) {
        CollaboratorId = collaboratorId;
    }

    public void setCollaboratorName(String collaboratorName) {
        CollaboratorName = collaboratorName;
    }

    public void setCollaboratorTypeId(String collaboratorTypeId) {
        CollaboratorTypeId = collaboratorTypeId;
    }

    public void setCollaboratorType(String collaboratorType) {
        CollaboratorType = collaboratorType;
    }

    public void setPartnerId(String partnerId) {
        PartnerId = partnerId;
    }

    public void setPartnerName(String partnerName) {
        PartnerName = partnerName;
    }

    public void setPartnerRef(String partnerRef) {
        PartnerRef = partnerRef;
    }
}
