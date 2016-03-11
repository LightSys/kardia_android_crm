package org.lightsys.crmapp.data;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
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
import java.util.Iterator;
import java.util.List;

/**
 * Created by nathan on 3/9/16.
 */
public class KardiaFetcher {
    private Context mContext;

    public KardiaFetcher(Context context) {
        mContext = context;
    }

    public String getUrlString(final Account account, String api) throws IOException {
        Log.d("passwordFetcher", AccountManager.get(mContext).getPassword(account));
        Log.d("serverFetcher", AccountManager.get(mContext).getUserData(account, "server"));

        Authenticator.setDefault(new Authenticator() {
                                     protected PasswordAuthentication getPasswordAuthentication() {
                                         Log.d("passwordFetcher", AccountManager.get(mContext).getPassword(account));
                                         Log.d("serverFetcher", AccountManager.get(mContext).getUserData(account, "server"));
                                         return new PasswordAuthentication(account.name, AccountManager.get(mContext).getPassword(account).toCharArray());
                                     }
                                 });

        URL url = new URL("http://" + AccountManager.get(mContext).getUserData(account, "server") + ":800" + api);
        Log.d("partner", url.toString());
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + AccountManager.get(mContext).getUserData(account, "server"));
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                Log.d("requests", "number");
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toString();
        } finally {
            connection.disconnect();
        }
    }

    public List<Staff> getStaff(Account account) {
        List<Staff> staff = new ArrayList<>();

        try {
            String api = Uri.parse("/apps/kardia/api/partner/Staff")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            Log.d("staff", api);
            Log.d("serverSync", AccountManager.get(mContext).getUserData(account, "server"));
            String jsonString = getUrlString(account, api);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseStaff(staff, jsonBody);
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        Log.d("staff", "yes");

        return staff;
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
            String crmJsonString = getUrlString(account, crmApi);
            JSONObject crmJsonBody = new JSONObject(crmJsonString);

            parseCollaboratees(collaboratees, crmJsonBody);
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return collaboratees;
    }

    private void parseCollaboratees(List<Partner> collaboratees, JSONObject crmJsonBody) throws IOException, JSONException {
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
            String partnerJsonString = getUrlString(account, partnerApi);
            JSONObject partnerJsonBody = new JSONObject(partnerJsonString);

            String addressApi = Uri.parse("/apps/kardia/api/partner/Partners/" + collaboratee.getPartnerId() + "/Addresses")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String addressJsonString = getUrlString(account, addressApi);
            JSONObject addressJsonBody = new JSONObject(addressJsonString);

            String contactApi = Uri.parse("/apps/kardia/api/partner/Partners/" + collaboratee.getPartnerId() + "/ContactInfo")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String contactJsonString = getUrlString(account, contactApi);
            JSONObject contactJsonBody = new JSONObject(contactJsonString);

            parseCollaborateeInfo(collaboratee, partnerJsonBody, addressJsonBody, contactJsonBody);
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return collaboratee;
    }

    private void parseCollaborateeInfo(Partner collaboratee, JSONObject partnerJsonBody, JSONObject addressJsonBody, JSONObject contactJsonBody) throws IOException, JSONException {
        /*Iterator<String> partnerKeys = partnerJsonBody.keys();

        while(partnerKeys.hasNext()) {
            String key = partnerKeys.next();
            if(!key.equals("@id")) {
                JSONObject jsonPartner = crmJsonBody.getJSONObject(key);

                Partner collaboratee = new Partner();

                collaboratee.setPartnerId(jsonPartner.getString("partner_id"));
                collaboratee.setPartnerName(jsonPartner.getString("partner_name"));

                collaboratees.add(collaboratee);
            }
        }*/
    }

    private void parseStaff(List<Staff> staff, JSONObject jsonBody) throws IOException, JSONException {
        Iterator<String> keys = jsonBody.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            if(!key.equals("@id")) {
                JSONObject jsonStaff = jsonBody.getJSONObject(key);

                Staff staffMember = new Staff();

                staffMember.setPartnerId(jsonStaff.getString("partner_id"));
                staffMember.setKardiaLogin(jsonStaff.getString("kardia_login"));

                staff.add(staffMember);
            }
        }
    }
}
