package org.lightsys.crmapp.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.lightsys.crmapp.models.Partner;
import org.lightsys.crmapp.models.Staff;
import org.lightsys.crmapp.models.TimelineItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;

import static java.lang.System.in;

/**
 * Created by nathan on 3/9/16.
 *
 * Edited by Ca2br and Judah on 7/19/16
 *
 * Edited by Tim on 6/21/2017
 *
 * This is the thing that fetches stuffs from kardia
 */

public class KardiaFetcher {
    private Context mContext;
    private AccountManager mAccountManager;
    private static CookieManager cookieManager = new CookieManager();

    public KardiaFetcher(Context context) {
        mContext = context;
        mAccountManager = AccountManager.get(context);
        CookieHandler.setDefault(cookieManager);
    }

    //this thing gets a json object as a string from the network
    //it takes an account for creds and a url and returns a string that contains a json object
    public String getUrlString(final Account account, String api) throws IOException {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account.name, mAccountManager.getPassword(account).toCharArray());
            }
        });

        //the url to get stuff from
        URL url = new URL(mAccountManager.getUserData(account, "server") + api);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {

            InputStream inputStream = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally
            {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } finally {
            connection.disconnect();
        }
        return "";
    }

    //function that gets a list of staff members
    public List<Staff> getStaff(Account account) {
        List<Staff> staff = new ArrayList<>();

        try {
            //build url
            String api = Uri.parse("/apps/kardia/api/partner/Staff")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String jsonString = getUrlString(account, api);//get json string from network
            JSONObject jsonBody = new JSONObject(jsonString);//build json object from json string
            parseStaffJson(staff, jsonBody);//fills staff list with members
        } catch (Exception e) {
            e.printStackTrace();
        }

        return staff;
    }

    //function that gets a list of collaboratees
    public List<Partner> getCollaboratees(Account account) throws IOException
    {
        List<Partner> collaboratees = new ArrayList<>();//empty list of collaboratees

        try {
            //build url
            String crmApi = Uri.parse("/apps/kardia/api/crm/Partners/" + AccountManager.get(mContext).getUserData(account, "partnerId") + "/Collaboratees")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String crmJsonString = getUrlString(account, crmApi);//get json string from network
            JSONObject crmJsonBody = new JSONObject(crmJsonString);//build json object form string

            parseCollaborateesJson(collaboratees, crmJsonBody);//fill collaboratees list with collaboratees

            for (Partner collaboratee : collaboratees)
            {
                String profilePictureApi = Uri.parse("/apps/kardia/api/crm/Partners/" + collaboratee.getPartnerId() + "/ProfilePicture")
                        .buildUpon()
                        .appendQueryParameter("cx__mode", "rest")
                        .appendQueryParameter("cx__res_type", "element")
                        .appendQueryParameter("cx__res_format", "attrs")
                        .appendQueryParameter("cx__res_attrs", "basic")
                        .build().toString();
                String pictureJsonString = getUrlString(account, profilePictureApi);
                JSONObject pictureJsonBody = new JSONObject(pictureJsonString);//build json object
                collaboratee.setProfilePictureFilename(pictureJsonBody.getString("photo_folder") + "/" + pictureJsonBody.getString("photo_filename"));
            }

        } catch (JSONException je) {
            je.printStackTrace();
        }

        return collaboratees;
    }

    //function that gets detailed info on a collaboratee
    //getting collaboratee info is a little harder that getting the other things
    //this is because all the collaboratee info is stored in multiple places
    //thus you need more that one url to get what you need
    public Partner getCollaborateeInfo(Account account, Partner collaboratee) {

        try {
            //url that gets partner info
            String partnerApi = Uri.parse("/apps/kardia/api/partner/Partners/" + collaboratee.getPartnerId())
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_format", "attrs")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String partnerJsonString = getUrlString(account, partnerApi);//get json string from network
            JSONObject partnerJsonBody = new JSONObject(partnerJsonString);//build json object

            //url that gets address info
            String addressApi = Uri.parse("/apps/kardia/api/partner/Partners/" + collaboratee.getPartnerId() + "/Addresses")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String addressJsonString = getUrlString(account, addressApi);//get json string from network
            JSONObject addressJsonBody = new JSONObject(addressJsonString);//build json object


            //url that gets contact info
            String contactApi = Uri.parse("/apps/kardia/api/partner/Partners/" + collaboratee.getPartnerId() + "/ContactInfo")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String contactJsonString = getUrlString(account, contactApi);//get json string from network
            JSONObject contactJsonBody = new JSONObject(contactJsonString);//build json object

            String profilePictureApi = Uri.parse("/apps/kardia/api/crm/Partners/" + collaboratee.getPartnerId() + "/ProfilePicture")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "element")
                    .appendQueryParameter("cx__res_format", "attrs")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String pictureJsonString = getUrlString(account, profilePictureApi);
            JSONObject pictureJsonBody = new JSONObject(pictureJsonString);//build json object

            //build an object based on a combination of all the json objects
            parseCollaborateeInfoJson(collaboratee, partnerJsonBody, addressJsonBody, contactJsonBody, pictureJsonBody);
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return collaboratee;
    }

    //function that gets a list of time line items (timelitems)
    public List<TimelineItem> getTimelineItems(Account account) {
        List<TimelineItem> timelineItems = new ArrayList<>();//empty list of timelitems

        try {
            //build timelitem url
            String crmApi = Uri.parse("/apps/kardia/api/crm/Partners/" + AccountManager.get(mContext).getUserData(account, "collabId") + "/ContactHistory")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String timelineJsonString = getUrlString(account, crmApi);//get timelitem json string from network
            JSONObject timelineJsonBody = new JSONObject(timelineJsonString);//build json object

            parseTimelineItemsJson(timelineItems, timelineJsonBody);//fill list of timelitems
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return timelineItems;
    }

    public String getNextPartnerKey(Account account) {
        String partnerKey = null;

        try {
            //build partnerkey url
            String partnerKeyApi = Uri.parse("/apps/kardia/api/partner/NextPartnerKey")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_format", "attrs")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .appendQueryParameter("cx__res_type", "element")
                    .build().toString();
            String partnerKeyJsonString = getUrlString(account, partnerKeyApi);//get partnerKey Json string from network
            JSONObject partnerKeyJsonBody = new JSONObject(partnerKeyJsonString);//build json object
            partnerKey = partnerKeyJsonBody.getString("partner_id");
        } catch (JSONException | IOException je)
        {
            je.printStackTrace();
        }

        return partnerKey;
    }

    //function that fills a list of collaboratees based on a json string
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

    //function that fills a partner with information based on a json object
    private Partner parseCollaborateeInfoJson(Partner collaboratee, JSONObject partnerJsonBody, JSONObject addressJsonBody, JSONObject contactJsonBody, JSONObject profilePictureJsonBody) throws IOException, JSONException {
        //TODO Use contact provider
        collaboratee.setSurname(partnerJsonBody.getString("surname"));
        collaboratee.setGivenNames(partnerJsonBody.getString("given_names"));
        collaboratee.setPartnerJsonId(partnerJsonBody.getString("@id"));

        Iterator<String> addressKeys = addressJsonBody.keys();

        while(addressKeys.hasNext()) {
            String key = addressKeys.next();
            if(!key.equals("@id")) {
                JSONObject jsonAddress = addressJsonBody.getJSONObject(key);

                collaboratee.setAddress1(jsonAddress.getString("address_1"));
                collaboratee.setCity(jsonAddress.getString("city"));
                collaboratee.setStateProvince(jsonAddress.getString("state_province"));
                collaboratee.setPostalCode(jsonAddress.getString("postal_code"));
                collaboratee.setAddressJsonId(jsonAddress.getString("@id"));
            }
        }

        Iterator<String> contactKeys = contactJsonBody.keys();

        while(contactKeys.hasNext()) {
            String key = contactKeys.next();
            if(!key.equals("@id")) {
                JSONObject jsonContact = contactJsonBody.getJSONObject(key);

                if(jsonContact.getString("contact_type").equals("Cell")) {
                    collaboratee.setCell(jsonContact.getString("contact"));
                    collaboratee.setCellId(jsonContact.getString("contact_id"));
                    collaboratee.setCellJsonId(jsonContact.getString("@id"));
                    Log.e("kardfetchcell", collaboratee.getCellJsonId());
                }

                if(jsonContact.getString("contact_type").equals("Email")) {
                    collaboratee.setEmail(jsonContact.getString("contact"));
                    collaboratee.setEmailId(jsonContact.getString("contact_id"));
                    collaboratee.setEmailJsonId(jsonContact.getString("@id"));
                    Log.e("kardfetchemail", collaboratee.getEmailJsonId());
                }

                if(jsonContact.getString("contact_type").equals("Phone")) {
                    collaboratee.setPhone(jsonContact.getString("contact"));
                    collaboratee.setPhoneId(jsonContact.getString("contact_id"));
                    collaboratee.setPhoneJsonId(jsonContact.getString("@id"));

                }
            }
        }

        collaboratee.setProfilePictureFilename(profilePictureJsonBody.getString("photo_folder") + "/" + profilePictureJsonBody.getString("photo_filename"));

        return collaboratee;
    }

    //function that fills a list of staff based on a json object
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

    //function that fills a list of timelitems based on a json object
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
