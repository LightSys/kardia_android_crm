package org.lightsys.crmapp.data;

import android.accounts.Account;
import android.accounts.AccountManager;
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

    public String getUrlString(final Account account, String api) throws IOException {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account.name, mAccountManager.getPassword(account).toCharArray());
            }
        });

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

    public List<Staff> getStaff(Account account) {
        List<Staff> staff = new ArrayList<>();

        try {
            String api = Uri.parse("/apps/kardia/api/partner/Staff")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String jsonString = getUrlString(account, api);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseStaffJson(staff, jsonBody);
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

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

                if(jsonContact.getString("contact_type").equals("Cell")) {
                    collaboratee.setCell(jsonContact.getString("contact"));
                }

                if(jsonContact.getString("contact_type").equals("Email")) {
                    collaboratee.setEmail(jsonContact.getString("contact"));
                }

                if(jsonContact.getString("contact_type").equals("Phone")) {
                    collaboratee.setCell(jsonContact.getString("contact"));
                }
            }
        }

        return collaboratee;
    }

    private void parseStaffJson(List<Staff> staff, JSONObject jsonBody) throws IOException, JSONException {
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

    public List<TimelineItem> getTimelineItems(Account account) {
        List<TimelineItem> timelineItems = new ArrayList<>();

        try {
            String crmApi = Uri.parse("/apps/kardia/api/crm/Partners/" + AccountManager.get(mContext).getUserData(account, "collabId") + "/ContactHistory")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String timelineJsonString = getUrlString(account, crmApi);
            JSONObject timelineJsonBody = new JSONObject(timelineJsonString);

            parseTimelineItemsJson(timelineItems, timelineJsonBody);
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return timelineItems;
    }

    private void parseTimelineItemsJson(List<TimelineItem> timelineItems, JSONObject timelineJsonBody) throws IOException, JSONException {
        Iterator<String> timelineKeys = timelineJsonBody.keys();

        while(timelineKeys.hasNext()) {
            String key = timelineKeys.next();
            if(!key.equals("@id")) {
                JSONObject jsonItem = timelineJsonBody.getJSONObject(key);

                TimelineItem item = new TimelineItem();

                item.setContactId(jsonItem.getString("name"));
                item.setPartnerId(jsonItem.getString("partner_id"));
                item.setCollaborateeId(jsonItem.getString("collab_partner_id"));
                item.setCollaborateeName(jsonItem.getString("collab_partner_name"));
                item.setContactHistoryId(jsonItem.getString("contact_history_id"));
                item.setContactHistoryType(jsonItem.getString("contact_history_type"));
                item.setSubject(jsonItem.getString("subject"));
                item.setNotes(jsonItem.getString("notes"));

                JSONObject jsonDate = jsonItem.getJSONObject("date_created");
                String date = jsonDate.getInt("year") + "-" + jsonDate.getInt("month") + "-" + jsonDate.getInt("day");
                item.setDate(date);


                timelineItems.add(item);
            }
        }
    }

}
