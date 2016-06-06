package org.lightsys.crmapp.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by nathan on 3/9/16.
 */
public class KardiaFetcher {
    private Context mContext;
    private AccountManager mAccountManager;

    public KardiaFetcher(Context context) {
        mContext = context;
        mAccountManager = AccountManager.get(context);
    }

    public String getWithPassword(final String username, final String password, final String server, String api) throws IOException {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });

        URL url = new URL("http://" + server + ":800" + api);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            // TODO Change to buffered streams?
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + server);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toString();
        } finally {
            connection.disconnect();
        }
    }

    public String getWithAuthToken(Account account, String api) throws IOException {
        try {
            api = Uri.parse(api).buildUpon()
                    .appendQueryParameter("cx__akey", mAccountManager.blockingGetAuthToken(account, CRMContract.AUTH_TOKEN_TYPE, false))
                    .build().toString();
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        }

        URL url = new URL("http://" + mAccountManager.getUserData(account, "server") + ":800" + api);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            // TODO Change to buffered streams?
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + mAccountManager.getUserData(account, "server"));
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toString();
        } finally {
            connection.disconnect();
        }
    }

    public HashMap<String, String> getStaff(String username, String password, String server) {
        HashMap<String, String> staff = new HashMap<>();

        try {
            String api = Uri.parse("/apps/kardia/api/partner/Staff")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String jsonString = getWithPassword(username, password, server, api);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseStaffJson(staff, jsonBody);
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return staff;
    }


    private void parseStaffJson(HashMap<String, String> staff, JSONObject jsonBody) throws IOException, JSONException {
        Iterator<String> keys = jsonBody.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            if(!key.equals("@id")) {
                JSONObject jsonStaff = jsonBody.getJSONObject(key);

                staff.put(jsonStaff.getString("kardia_login"), jsonStaff.getString("partner_id"));
            }
        }
    }

    public List<Partner> getCollaboratees(Account account) {
        List<Partner> collaboratees = new ArrayList<>();

        try {
            String crmApi = Uri.parse("/apps/kardia/api/crm/Partners/" + AccountManager.get(mContext).getUserData(account, "partnerId") + "/Collaboratees")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String crmJsonString = getWithAuthToken(account, crmApi);
            JSONObject crmJsonBody = new JSONObject(crmJsonString);

            parseCollaborateesJson(collaboratees, crmJsonBody);
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return collaboratees;
    }

    private void parseCollaborateesJson(List<Partner> collaboratees, JSONObject crmJsonBody) throws IOException, JSONException {
        Iterator<String> crmKeys = crmJsonBody.keys();

        while(crmKeys.hasNext()) {
            String key = crmKeys.next();
            if(!key.equals("@id")) {
                JSONObject jsonPartner = crmJsonBody.getJSONObject(key);

                Partner collaboratee = new Partner();

                collaboratee.setPartnerId(jsonPartner.getString("partner_id"));
                collaboratee.setPartnerName(jsonPartner.getString("partner_name"));

                collaboratees.add(collaboratee);
            }
        }
    }

    public Partner getCollaborateeInfo(Account account, Partner collaboratee) {
        try {
            String partnerApi = Uri.parse("/apps/kardia/api/partner/Partners/" + collaboratee.getPartnerId())
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_format", "attrs")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String partnerJsonString = getWithAuthToken(account, partnerApi);
            JSONObject partnerJsonBody = new JSONObject(partnerJsonString);

            String addressApi = Uri.parse("/apps/kardia/api/partner/Partners/" + collaboratee.getPartnerId() + "/Addresses")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String addressJsonString = getWithAuthToken(account, addressApi);
            JSONObject addressJsonBody = new JSONObject(addressJsonString);

            String contactApi = Uri.parse("/apps/kardia/api/partner/Partners/" + collaboratee.getPartnerId() + "/ContactInfo")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String contactJsonString = getWithAuthToken(account, contactApi);
            JSONObject contactJsonBody = new JSONObject(contactJsonString);

            parseCollaborateeInfoJson(collaboratee, partnerJsonBody, addressJsonBody, contactJsonBody);
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return collaboratee;
    }

    private Partner parseCollaborateeInfoJson(Partner collaboratee, JSONObject partnerJsonBody, JSONObject addressJsonBody, JSONObject contactJsonBody) throws IOException, JSONException {
        //TODO Use contact provider
        collaboratee.setSurname(partnerJsonBody.getString("surname"));
        collaboratee.setGivenNames(partnerJsonBody.getString("given_names"));

        Iterator<String> addressKeys = addressJsonBody.keys();

        while(addressKeys.hasNext()) {
            String key = addressKeys.next();
            if(!key.equals("@id")) {
                JSONObject jsonAddress = addressJsonBody.getJSONObject(key);

                collaboratee.setAddress1(jsonAddress.getString("address_1"));
                collaboratee.setCity(jsonAddress.getString("city"));
                collaboratee.setStateProvince(jsonAddress.getString("state_province"));
                collaboratee.setPostalCode(jsonAddress.getString("postal_code"));
            }
        }

        Iterator<String> contactKeys = contactJsonBody.keys();

        while(contactKeys.hasNext()) {
            String key = contactKeys.next();
            if(!key.equals("@id")) {
                JSONObject jsonContact = contactJsonBody.getJSONObject(key);

                if("C".equals(jsonContact.getString("contact_type_code"))) {
                    collaboratee.setCell(jsonContact.getString("cell"));
                }
                if("E".equals(jsonContact.getString("contact_type_code"))) {
                    collaboratee.setEmail(jsonContact.getString("email"));
                }
                if("P".equals(jsonContact.getString("contact_type_code"))) {
                    collaboratee.setPhone(jsonContact.getString("phone"));
                }

                //commented methods of contact have not been added as columns for the database
                //also need to add methods to partner

                /*if("B".equals(jsonContact.getString("contact_type_code"))) {
                    collaboratee.setBlog(jsonContact.getString("blog"));
                }
                if("F".equals(jsonContact.getString("contact_type_code"))) {
                    collaboratee.setFax(jsonContact.getString("fax"));
                }
                if("K".equals(jsonContact.getString("contact_type_code"))) {
                    collaboratee.setFacebook(jsonContact.getString("facebook"));
                }
                if("S".equals(jsonContact.getString("contact_type_code"))) {
                    collaboratee.setSkype(jsonContact.getString("skype"));
                }
                if("T".equals(jsonContact.getString("contact_type_code"))) {
                    collaboratee.setTwitter(jsonContact.getString("twitter"));
                }
                if("W".equals(jsonContact.getString("contact_type_code"))) {
                    collaboratee.setWeb(jsonContact.getString("website"));
                }*/
            }
        }

        return collaboratee;
    }
}
