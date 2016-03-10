package org.lightsys.crmapp.data;

/**
 * Created by nathan on 3/9/16.
 */
public class CRMDbSchema {
    public static final class UserTable {
        public static final String NAME = "user";

        public static final class Cols {
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
            public static final String SERVER = "server";
            public static final String PARTNER_ID = "partenerId";
        }
    }

    public static final class PartnerTable {
        public static final String NAME = "partner";

        public static final class Cols {
            public static final String PARNTER_ID = "partnerId";
            public static final String PARTNER_NAME = "partnerName";
        }
    }

    public static final class CollaborateeTable {
        public static final String NAME = "collaboratee";

        public static final class Cols {
            public static final String COLLABORATEE_ID = "collaborateeId";
            public static final String PARTNER_ID = "partnerId";
        }
    }
}
