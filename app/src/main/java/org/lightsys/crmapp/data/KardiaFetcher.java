package org.lightsys.crmapp.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lightsys.crmapp.models.Engagement;
import org.lightsys.crmapp.models.Partner;
import org.lightsys.crmapp.models.Staff;
import org.lightsys.crmapp.models.TimelineItem;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import static org.lightsys.crmapp.activities.LoginActivity.Credential;

/**
 * Created by nathan on 3/9/16.
 *
 * Edited by Ca2br and Judah on 7/19/16
 *
 * Edited by Tim on 6/21/2017
 */

public class KardiaFetcher {
    private Context mContext;
    private AccountManager mAccountManager;
    private static CookieManager cookieManager = new CookieManager();
    private OkHttpClient client;
    private String TAG = "CRM Fetcher";

    public KardiaFetcher(Context context) {
        mContext = context;
        mAccountManager = AccountManager.get(context);
        CookieHandler.setDefault(cookieManager);
        client = new OkHttpClient.Builder()
                .cookieJar(new MyCookieJar())
                .authenticator(new okhttp3.Authenticator()
                {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException
                    {
                        return response.request().newBuilder()
                                .header("Authorization", Credential)
                                .build();
                    }
                }).build();
    }

    //Makes Requests to the Kardia Server and returns response
    private String Request(final Account account, String Uri)
    {
        URL url;
        String result = "";
        final String credential = Credentials.basic(account.name, mAccountManager.getPassword(account));
        try
        {
            url = new URL(mAccountManager.getUserData(account, "server") + Uri);

            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", credential)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            int responseCode = response.code();

            Log.e(TAG, "responseCode : " + responseCode);
            boolean success;

            //if the things were sent properly, get the result code
            if (responseCode == HttpsURLConnection.HTTP_OK && response.isSuccessful())
            {
                Log.e(TAG, "HTTP_OK");
                result = response.body().string();
                success = true;
            } else
            {
                Log.e(TAG, "False - HTTP_OK");//send failed :(
                result = "";
                success = false;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    //Returns a list of staff members
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
            String jsonString = Request(account, api);//get json string from network
            JSONObject jsonBody = new JSONObject(jsonString);//build json object from json string
            parseStaffJson(staff, jsonBody);//fills staff list with members
        } catch (Exception e) {
            e.printStackTrace();
        }

        return staff;
    }

    //Returns a list of collaboratees
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
            String crmJsonString = Request(account, crmApi);//get json string from network
            JSONObject crmJsonBody = new JSONObject(crmJsonString);//build json object form string

            parseCollaborateesJson(collaboratees, crmJsonBody);//fill collaboratees list with collaboratees

            for (Partner collaboratee : collaboratees)
            {
                String profilePictureApi = Uri.parse("/apps/kardia/api/crm/Partners/" + collaboratee.PartnerId + "/ProfilePicture")
                        .buildUpon()
                        .appendQueryParameter("cx__mode", "rest")
                        .appendQueryParameter("cx__res_type", "element")
                        .appendQueryParameter("cx__res_format", "attrs")
                        .appendQueryParameter("cx__res_attrs", "basic")
                        .build().toString();

                String pictureJsonString = Request(account, profilePictureApi);
                if (!pictureJsonString.startsWith("HTTP/1.0 404 Not Found"))
                {
                    JSONObject pictureJsonBody = new JSONObject(pictureJsonString);//build json object
                    collaboratee.ProfilePictureFilename = pictureJsonBody.getString("photo_folder") + "/" + pictureJsonBody.getString("photo_filename");
                }
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return collaboratees;
    }

    //Returns detailed info on a collaboratee
    //Makes multiple requests because the collaboratee info is located in several places
    public Partner getCollaborateeInfo(Account account, Partner collaboratee) {

        try {
            //url that gets partner info
            String partnerApi = Uri.parse("/apps/kardia/api/partner/Partners/" + collaboratee.PartnerId)
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_format", "attrs")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String partnerJsonString = Request(account, partnerApi);//get json string from network
            JSONObject partnerJsonBody = new JSONObject(partnerJsonString);//build json object

            //url that gets address info
            String addressApi = Uri.parse("/apps/kardia/api/partner/Partners/" + collaboratee.PartnerId + "/Addresses")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String addressJsonString = Request(account, addressApi);//get json string from network
            JSONObject addressJsonBody = new JSONObject(addressJsonString);//build json object


            //url that gets contact info
            String contactApi = Uri.parse("/apps/kardia/api/partner/Partners/" + collaboratee.PartnerId + "/ContactInfo")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String contactJsonString = Request(account, contactApi);//get json string from network
            JSONObject contactJsonBody = new JSONObject(contactJsonString);//build json object

            String profilePictureApi = Uri.parse("/apps/kardia/api/crm/Partners/" + collaboratee.PartnerId + "/ProfilePicture")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "element")
                    .appendQueryParameter("cx__res_format", "attrs")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();

            String pictureJsonString = Request(account, profilePictureApi);
            JSONObject pictureJsonBody = pictureJsonString.equals("") ? null : new JSONObject(pictureJsonString);//build json object

            //build an object based on a combination of all the json objects
            parseCollaborateeInfoJson(collaboratee, partnerJsonBody, addressJsonBody, contactJsonBody, pictureJsonBody);
        } catch (JSONException | IOException je) {
            je.printStackTrace();
        }

        return collaboratee;
    }

    //Returns a list of time line items (timelitems)
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
            String timelineJsonString = Request(account, crmApi);//get timelitem json string from network
            JSONObject timelineJsonBody = new JSONObject(timelineJsonString);//build json object

            parseTimelineItemsJson(timelineItems, timelineJsonBody);//fill list of timelitems
        } catch (JSONException | IOException je) {
            je.printStackTrace();
        }

        return timelineItems;
    }

    //Returns next available PartnerKey for creating a new Partner.
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
            String partnerKeyJsonString = Request(account, partnerKeyApi);//get partnerKey Json string from network
            JSONObject partnerKeyJsonBody = new JSONObject(partnerKeyJsonString);//build json object
            partnerKey = partnerKeyJsonBody.getString("partner_id");
        } catch (JSONException je)
        {
            je.printStackTrace();
        }

        return partnerKey;
    }

    //Get Engagements that have been created by a specific user
    public List<Engagement> getEngagements(Account account, List<Partner> collaboratees) {
        try {
            ArrayList<Engagement> engagements = new ArrayList<>();
            for (Partner partner : collaboratees)
            {
                String engagementApi = Uri.parse("/apps/kardia/api/crm/Partners/" + partner.PartnerId + "/Tracks")
                        .buildUpon()
                        .appendQueryParameter("cx__mode", "rest")
                        .appendQueryParameter("cx__res_format", "attrs")
                        .appendQueryParameter("cx__res_attrs", "basic")
                        .appendQueryParameter("cx__res_type", "collection")
                        .build().toString();
                String engagementJsonString = Request(account, engagementApi);
                JSONObject engagementJsonBody = new JSONObject(engagementJsonString);

                if (engagementJsonBody.length() < 2)
                    continue;

                JSONArray names = engagementJsonBody.names();
                for (int i = 0; i < names.length(); i++)
                {
                    String trackName = names.getString(i);
                    if (trackName.equals("@id"))
                        continue;

                    JSONObject engagementValues = engagementJsonBody.getJSONObject(trackName);

                    Engagement engagement = new Engagement();
                    engagement.PartnerId = partner.PartnerId;
                    engagement.EngagementId = engagementValues.getString("engagement_id");
                    engagement.Description = engagementValues.getString("engagement_description");
                    engagement.TrackName = trackName.substring(0, trackName.length() - 2);
                    engagement.StepName = engagementValues.getString("engagement_step");
                    engagement.Comments = engagementValues.getString("engagement_comments");
                    engagement.CompletionStatus = engagementValues.getString("completion_status");

                    Calendar c = Calendar.getInstance();
                    JSONObject createdDate = engagementValues.getJSONObject("date_created");
                    c.set(  createdDate.getInt("year"), createdDate.getInt("month"),
                            createdDate.getInt("day"), createdDate.getInt("hour"),
                            createdDate.getInt("minute"));
                    engagement.CreatedDate = c.getTime();

                    engagement.PartnerName = partner.PartnerName;
                    engagement.Archived = engagementValues.getInt("is_archived") == 1;

                    engagements.add(engagement);
                }
            }
            return engagements;
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    //Fills a list of collaboratees based on a json string
    private void parseCollaborateesJson(List<Partner> collaboratees, JSONObject crmJsonBody) throws IOException, JSONException {
        Iterator<String> crmKeys = crmJsonBody.keys();

        while(crmKeys.hasNext()) {
            String key = crmKeys.next();
            if(!key.equals("@id")) {
                JSONObject jsonPartner = crmJsonBody.getJSONObject(key);

                Partner collaboratee = new Partner();

                collaboratee.PartnerId = jsonPartner.getString("partner_id");
                collaboratee.PartnerName = jsonPartner.getString("partner_name");

                collaboratees.add(collaboratee);
            }
        }
    }

    //Fills a partner with information based on a json object
    private Partner parseCollaborateeInfoJson(Partner collaboratee, JSONObject partnerJsonBody, JSONObject addressJsonBody, JSONObject contactJsonBody, JSONObject profilePictureJsonBody) throws IOException, JSONException {
        //TODO Use contact provider
        collaboratee.Surname = partnerJsonBody.getString("surname");
        collaboratee.GivenNames = partnerJsonBody.getString("given_names");
        collaboratee.PartnerJsonId = partnerJsonBody.getString("@id");

        Iterator<String> addressKeys = addressJsonBody.keys();

        while(addressKeys.hasNext()) {
            String key = addressKeys.next();
            if(!key.equals("@id")) {
                JSONObject jsonAddress = addressJsonBody.getJSONObject(key);

                collaboratee.Address1 = jsonAddress.getString("address_1");
                collaboratee.City = jsonAddress.getString("city");
                collaboratee.StateProvince = jsonAddress.getString("state_province");
                collaboratee.PostalCode = jsonAddress.getString("postal_code");
                collaboratee.AddressJsonId = jsonAddress.getString("@id");
            }
        }

        Iterator<String> contactKeys = contactJsonBody.keys();

        while(contactKeys.hasNext()) {
            String key = contactKeys.next();
            if(!key.equals("@id")) {
                JSONObject jsonContact = contactJsonBody.getJSONObject(key);

                if(jsonContact.getString("contact_type").equals("Cell")) {
                    collaboratee.Cell = jsonContact.getString("contact");
                    collaboratee.CellId = jsonContact.getString("contact_id");
                    collaboratee.CellJsonId = jsonContact.getString("@id");
                    Log.e("kardfetchcell", collaboratee.CellJsonId);
                }

                if(jsonContact.getString("contact_type").equals("Email")) {
                    collaboratee.Email = jsonContact.getString("contact");
                    collaboratee.EmailId = jsonContact.getString("contact_id");
                    collaboratee.EmailJsonId = jsonContact.getString("@id");
                    Log.e("kardfetchemail", collaboratee.EmailJsonId);
                }

                if(jsonContact.getString("contact_type").equals("Phone")) {
                    collaboratee.Phone = jsonContact.getString("contact");
                    collaboratee.PhoneId = jsonContact.getString("contact_id");
                    collaboratee.PhoneJsonId = jsonContact.getString("@id");

                }
            }
        }

        if (profilePictureJsonBody != null)
            collaboratee.ProfilePictureFilename = profilePictureJsonBody.getString("photo_folder") + "/" + profilePictureJsonBody.getString("photo_filename");

        return collaboratee;
    }

    //Fills a list of staff based on a json object
    private void parseStaffJson(List<Staff> staff, JSONObject jsonBody) throws IOException, JSONException {
        Iterator<String> keys = jsonBody.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            if(!key.equals("@id")) {
                JSONObject jsonStaff = jsonBody.getJSONObject(key);

                Staff staffMember = new Staff();

                staffMember.PartnerId = jsonStaff.getString("partner_id");
                staffMember.setKardiaLogin(jsonStaff.getString("kardia_login"));

                staff.add(staffMember);
            }
        }
    }

    //Fills a list of timelitems based on a json object
    private void parseTimelineItemsJson(List<TimelineItem> timelineItems, JSONObject timelineJsonBody) throws IOException, JSONException {
        Iterator<String> timelineKeys = timelineJsonBody.keys();

        while(timelineKeys.hasNext()) {
            String key = timelineKeys.next();
            if(!key.equals("@id")) {
                JSONObject jsonItem = timelineJsonBody.getJSONObject(key);

                TimelineItem item = new TimelineItem();

                item.setContactId(jsonItem.getString("name"));
                item.PartnerId(jsonItem.getString("partner_id"));
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
