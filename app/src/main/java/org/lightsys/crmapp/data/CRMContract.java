package org.lightsys.crmapp.data;

import android.net.Uri;

/**
 * Created by nathan on 3/9/16.
 */
public class CRMContract {
    public static final class StaffTable {
        public static final String NAME = "staff";
        public static final Uri CONTENT_URI = new Uri.Builder().scheme("content").authority("org.lightsys.crmapp.provider").path("staff").build();

        public static final String PARTNER_ID = "partnerId";
        public static final String KARDIA_LOGIN = "kardiaLogin";
    }

    public static final class PartnerTable {
        public static final String NAME = "partner";
        public static final String PATH = "partners";

        public static final String PARNTER_ID = "partnerId";
        public static final String PARTNER_NAME = "partnerName";
    }

    public static final class CollaborateeTable {
        public static final String NAME = "collaboratee";
        public static final Uri CONTENT_URI = new Uri.Builder().scheme("content").authority("org.lightsys.crmapp.provider").path("collaboratees").build();

        public static final String COLLABORATER_ID = "collaboraterId";
        public static final String PARTNER_ID = "partnerId";
        public static final String PARTNER_NAME = "partnerName";
        public static final String SURNAME = "surname";
        public static final String GIVEN_NAMES = "givenNames";
        public static final String PHONE = "phone";
        public static final String CELL = "cell";
        public static final String EMAIL = "email";
        public static final String ADDRESS_1 = "address1";
        public static final String CITY = "city";
        public static final String STATE_PROVINCE = "stateProvince";
        public static final String POSTAL_CODE = "postalCode";
    }
}
