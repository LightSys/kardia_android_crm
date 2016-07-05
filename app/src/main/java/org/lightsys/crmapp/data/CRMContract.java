package org.lightsys.crmapp.data;

import android.net.Uri;

/**
 * Created by nathan on 3/9/16.
 */
public class CRMContract {
    public static final String PROVIDER_AUTHORITY = "org.lightsys.crmapp.provider";
    public static final String ACCOUNT_TYPE = "org.lightsys.crmapp";
    public static final String AUTH_TOKEN_TYPE = "org.lightsys.crmapp";

    public static final class StaffTable {
        public static final String TABLE_NAME = "staff";
        public static final Uri CONTENT_URI = new Uri.Builder().scheme("content").authority("org.lightsys.crmapp.provider").path("staff").build();

        public static final String PARTNER_ID = "partnerId";
        public static final String KARDIA_LOGIN = "kardiaLogin";

        public static final String CREATE_TABLE = "CREATE TABLE " + CRMContract.StaffTable.TABLE_NAME + "(" +
                        CRMContract.StaffTable.PARTNER_ID + ", " +
                        CRMContract.StaffTable.KARDIA_LOGIN +
                        ")";
    }

    public static final class CollaborateeTable {
        public static final String TABLE_NAME = "collaboratee";
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

        public static final String CREATE_TABLE = "CREATE TABLE " + CRMContract.CollaborateeTable.TABLE_NAME + "(" +
                        CRMContract.CollaborateeTable.COLLABORATER_ID + ", " +
                        CRMContract.CollaborateeTable.PARTNER_ID + ", " +
                        CRMContract.CollaborateeTable.PARTNER_NAME + ", " +
                        CRMContract.CollaborateeTable.SURNAME + ", " +
                        CRMContract.CollaborateeTable.GIVEN_NAMES + ", " +
                        CRMContract.CollaborateeTable.PHONE + ", " +
                        CRMContract.CollaborateeTable.CELL + ", " +
                        CRMContract.CollaborateeTable.EMAIL + ", " +
                        CRMContract.CollaborateeTable.ADDRESS_1 + ", " +
                        CRMContract.CollaborateeTable.CITY + ", " +
                        CRMContract.CollaborateeTable.STATE_PROVINCE + ", " +
                        CRMContract.CollaborateeTable.POSTAL_CODE +
                        ")";
    }
}
