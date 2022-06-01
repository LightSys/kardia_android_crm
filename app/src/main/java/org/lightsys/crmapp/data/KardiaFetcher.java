package org.lightsys.crmapp.data;

import static org.lightsys.crmapp.activities.LoginActivity.Credential;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lightsys.crmapp.models.Engagement;
import org.lightsys.crmapp.models.EngagementStep;
import org.lightsys.crmapp.models.EngagementTrack;
import org.lightsys.crmapp.models.Partner;
import org.lightsys.crmapp.models.Staff;
import org.lightsys.crmapp.models.Tag;
import org.lightsys.crmapp.models.TimelineItem;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by nathan on 3/9/16.
 *
 * Edited by Ca2br and Judah on 7/19/16
 *
 * Edited by Tim on 6/21/2017
 *
 * Edited by Alex Fehr on 6/1/2022
 */

public class KardiaFetcher {
    private final Context mContext;
    private final AccountManager mAccountManager;
    private final OkHttpClient client;
    private final String TAG = "CRM Fetcher";

    public KardiaFetcher(Context context) {
        mContext = context;
        mAccountManager = AccountManager.get(context);
        Authenticator authorization = (route, response) -> response.request().newBuilder()
                .header("Authorization", Credential).build();

        client = new OkHttpClient.Builder()
                .cookieJar(new MyCookieJar())
                .authenticator(authorization)
                .retryOnConnectionFailure(true).build();
    }

    //Makes Requests to the Kardia Server and returns response
    private String Request(Account account, String Uri)
    {
        URL url;
        String result = "";
        final String credential = Credentials.basic(account.name, mAccountManager.getPassword(account));

        try
        {
            // Build URL for the request
            url = new URL(mAccountManager.getUserData(account, "server") + Uri);

            // Build the HTTP request
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", credential)
                    .get()
                    .build();

            // Send the HTTP request
            Response response = client.newCall(request).execute();

            // Get the HTTP response code
            int responseCode = response.code();

            Log.e(TAG, "responseCode : " + responseCode);

            //if the things were sent properly, get the result code
            if (responseCode == HttpsURLConnection.HTTP_OK && response.isSuccessful()) {
                Log.e(TAG, "HTTP_OK");
                result = response.body().string();
            }
            else {
                Log.e(TAG, "False - HTTP_OK"); //send failed :(
                result = "";
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    //Returns a list of tags for sign-up forms
    public List<Tag> getTags(Account account) {
        ArrayList<Tag> tags = new ArrayList<>();

        try {
            //build url
            String api = Uri.parse("/apps/kardia/api/crm_config/TagTypes")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String jsonString = Request(account, api);//get json string from network
            if (!jsonString.equals("")) {
                JSONObject jsonBody = new JSONObject(jsonString);//build json object from json string
                parseTags(tags, jsonBody);//fills staff list with members
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tags;
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


            if (!jsonString.equals("")) {
                JSONObject jsonBody = new JSONObject(jsonString);//build json object from json string
                parseStaffJson(staff, jsonBody);//fills staff list with members
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return staff;
    }

    //Returns a list of collaboratees
    public List<Partner> getCollaboratees(Account account) {
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

            for (Partner c: collaboratees) {
                Log.d(TAG, "getCollaboratees: " + c.getPartnerName());
            }

            for (Partner collaboratee : collaboratees) {
                String profilePictureApi = Uri.parse("/apps/kardia/api/crm/Partners/" + collaboratee.PartnerId + "/ProfilePicture")
                        .buildUpon()
                        .appendQueryParameter("cx__mode", "rest")
                        .appendQueryParameter("cx__res_type", "element")
                        .appendQueryParameter("cx__res_format", "attrs")
                        .appendQueryParameter("cx__res_attrs", "basic")
                        .build().toString();

                String pictureJsonString = Request(account, profilePictureApi);
                if (!pictureJsonString.startsWith("HTTP/1.0 404 Not Found") && !pictureJsonString.equals("")) {
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
            //build partnerKey url
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
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return partnerKey;
    }

    //Get Engagements that have been created by a specific user
    public List<Engagement> getEngagements(Account account, List<Partner> collaboratees) {
        try {
            ArrayList<Engagement> engagements = new ArrayList<>();
            for (Partner partner : collaboratees) {
                String engagementApi = Uri.parse("/apps/kardia/api/crm/Partners/" + partner.PartnerId + "/Tracks")
                        .buildUpon()
                        .appendQueryParameter("cx__mode", "rest")
                        .appendQueryParameter("cx__res_format", "attrs")
                        .appendQueryParameter("cx__res_attrs", "basic")
                        .appendQueryParameter("cx__res_type", "collection")
                        .build().toString();
                String engagementJsonString = Request(account, engagementApi);

                if (!engagementJsonString.equals("")) {
                    JSONObject engagementJsonBody = new JSONObject(engagementJsonString);

                    parseEngagement(engagements, partner, engagementJsonBody);
                }
            }
            return engagements;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseEngagement(ArrayList<Engagement> engagements, Partner partner, JSONObject engagementJsonBody) throws JSONException {
        if (engagementJsonBody.length() < 2)
            return;

        JSONArray names = engagementJsonBody.names();
        for (int i = 0; i < names.length(); i++) {
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
            engagement.ProfilePicture = partner.ProfilePictureFilename;

            Calendar c = Calendar.getInstance();
            JSONObject createdDate = engagementValues.getJSONObject("date_created");
            c.set(createdDate.getInt("year"), createdDate.getInt("month"),
                    createdDate.getInt("day"), createdDate.getInt("hour"),
                    createdDate.getInt("minute"));
            engagement.CreatedDate = c.getTime();

            engagement.PartnerName = partner.PartnerName;
            engagement.Archived = engagementValues.getInt("is_archived") == 1;

            engagements.add(engagement);
        }
    }

    public String getProfilePictureUrl(Account account, String partnerId) throws JSONException {
        String profilePictureApi = Uri.parse("/apps/kardia/api/crm/Partners/" + partnerId + "/ProfilePicture")
                .buildUpon()
                .appendQueryParameter("cx__mode", "rest")
                .appendQueryParameter("cx__res_type", "element")
                .appendQueryParameter("cx__res_format", "attrs")
                .appendQueryParameter("cx__res_attrs", "basic")
                .build().toString();

        String pictureJsonString = Request(account, profilePictureApi);
        JSONObject pictureJsonBody = pictureJsonString.equals("") ? null : new JSONObject(pictureJsonString);
        Partner partner = new Partner();
        parseProfilePictureJson(partner, pictureJsonBody);

        return partner.ProfilePictureFilename;
    }

    public List<Partner> partnerSearch(Account account, String search) {
        List<Partner> partners = new ArrayList<>();

        String partnerSearchApi = Uri.parse("/apps/kardia/api/partnersearch")
                .buildUpon()
                .appendQueryParameter("cx__mode", "rest")
                .appendQueryParameter("cx__res_format", "attrs")
                .appendQueryParameter("cx__res_attrs", "basic")
                .appendQueryParameter("cx__res_type", "collection")
                .appendQueryParameter("string", search).toString();

        try {
            String partnerSearchJsonString = Request(account, partnerSearchApi);

            if (!partnerSearchJsonString.equals("")) {
                JSONObject partnerSearchJsonBody = new JSONObject(partnerSearchJsonString);

                parsePartnerSearch(partners, partnerSearchJsonBody);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return partners;
    }

    private void parsePartnerSearch(List<Partner> partners, JSONObject partnerSearchJsonBody) throws JSONException {
        Iterator<String> partnerSearchKeys = partnerSearchJsonBody.keys();

        while (partnerSearchKeys.hasNext()) {
            String key = partnerSearchKeys.next();
            if (!key.equals("@id")) {
                JSONObject jsonPartner = partnerSearchJsonBody.getJSONObject(key);

                Partner partner = new Partner();

                partner.PartnerId = jsonPartner.getString("partner_id");
                partner.PartnerName = jsonPartner.getString("partner_name");

                partners.add(partner);
            }
        }
    }

    //Fills a list of collaboratees based on a json string
    private void parseCollaborateesJson(List<Partner> collaboratees, JSONObject crmJsonBody) throws JSONException {
        Iterator<String> crmKeys = crmJsonBody.keys();

        while (crmKeys.hasNext()) {
            String key = crmKeys.next();
            if (!key.equals("@id")) {
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

        parseAddressJson(collaboratee, addressJsonBody);
        parseContactInfoJson(collaboratee, contactJsonBody);
        parseProfilePictureJson(collaboratee, profilePictureJsonBody);

        return collaboratee;
    }

    private void parseAddressJson(Partner collaboratee, JSONObject addressJsonBody) throws JSONException {
        Iterator<String> addressKeys = addressJsonBody.keys();

        while (addressKeys.hasNext()) {
            String key = addressKeys.next();
            if (!key.equals("@id")) {
                JSONObject jsonAddress = addressJsonBody.getJSONObject(key);

                collaboratee.Address1 = jsonAddress.getString("address_1");
                collaboratee.City = jsonAddress.getString("city");
                collaboratee.StateProvince = jsonAddress.getString("state_province");
                collaboratee.PostalCode = jsonAddress.getString("postal_code");
                collaboratee.AddressJsonId = jsonAddress.getString("@id");
            }
        }
    }

    private void parseContactInfoJson(Partner collaboratee, JSONObject contactJsonBody) throws JSONException {
        Iterator<String> contactKeys = contactJsonBody.keys();

        while (contactKeys.hasNext()) {
            String key = contactKeys.next();
            if (!key.equals("@id")) {
                JSONObject jsonContact = contactJsonBody.getJSONObject(key);

                if (jsonContact.getString("contact_type").equals("Cell")) {
                    collaboratee.Cell = jsonContact.getString("contact");
                    collaboratee.CellId = jsonContact.getString("contact_id");
                    collaboratee.CellJsonId = jsonContact.getString("@id");
                    Log.e("kardfetchcell", collaboratee.CellJsonId);
                }

                if (jsonContact.getString("contact_type").equals("Email")) {
                    collaboratee.Email = jsonContact.getString("contact");
                    collaboratee.EmailId = jsonContact.getString("contact_id");
                    collaboratee.EmailJsonId = jsonContact.getString("@id");
                    Log.e("kardfetchemail", collaboratee.EmailJsonId);
                }

                if (jsonContact.getString("contact_type").equals("Phone")) {
                    collaboratee.Phone = jsonContact.getString("contact");
                    collaboratee.PhoneId = jsonContact.getString("contact_id");
                    collaboratee.PhoneJsonId = jsonContact.getString("@id");
                }
            }
        }
    }

    private void parseProfilePictureJson(Partner collaboratee, JSONObject profilePictureJsonBody) throws JSONException {
        if (profilePictureJsonBody != null)
            collaboratee.ProfilePictureFilename = profilePictureJsonBody.getString("photo_folder") + "/" + profilePictureJsonBody.getString("photo_filename");
    }

    //Fills a list of staff based on a json object
    private void parseStaffJson(List<Staff> staff, JSONObject jsonBody) throws JSONException {
        Iterator<String> keys = jsonBody.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            if (!key.equals("@id")) {
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

        while (timelineKeys.hasNext()) {
            String key = timelineKeys.next();
            if (!key.equals("@id")) {
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

                JSONObject jsonDate = jsonItem.getJSONObject("contact_date");
                String date = jsonDate.getInt("year") + "-" + jsonDate.getInt("month") + "-" + jsonDate.getInt("day");
                item.setDate(date);

                //This pulls in the date the timeline item was created,
                //specifically for use in the Followup functionality
                JSONObject jsonDateCreated = jsonItem.getJSONObject("date_created");
                String dateCreated = jsonDateCreated.getInt("year") + "-" +
                        jsonDateCreated.getInt("month") + "-" +
                        jsonDateCreated.getInt("day") + " " +
                        jsonDateCreated.getInt("hour") + ":" +
                        jsonDateCreated.getInt("minute") + ":" +
                        jsonDateCreated.getInt("second");
                item.setDateCreated(dateCreated);

                timelineItems.add(item);
            }
        }
    }

    public List<EngagementTrack> getEngagementTracks(final Account account) throws JSONException {
        String trackApi = Uri.parse("/apps/kardia/api/crm_config/Tracks")
                .buildUpon()
                .appendQueryParameter("cx__mode", "rest")
                .appendQueryParameter("cx__res_format", "attrs")
                .appendQueryParameter("cx__res_attrs", "basic")
                .appendQueryParameter("cx__res_type", "collection")
                .build().toString();

        String trackJsonString = Request(account, trackApi);
        JSONObject trackJsonBody = new JSONObject(trackJsonString);

        return parseEngagementTracks(trackJsonBody);
    }

    private ArrayList<EngagementTrack> parseEngagementTracks(JSONObject trackJsonBody) throws JSONException {
        ArrayList<EngagementTrack> tracks = new ArrayList<>();

        Iterator<String> trackKeys = trackJsonBody.keys();

        while (trackKeys.hasNext()) {
            String key = trackKeys.next();
            if (!key.equals("@id")) {
                JSONObject jsonItem = trackJsonBody.getJSONObject(key);
                EngagementTrack track = new EngagementTrack();

                track.TrackId = jsonItem.getString("track_id");
                track.TrackName = key;
                track.TrackDescription = jsonItem.getString("track_description");
                track.TrackStatus = jsonItem.getString("track_status");

                tracks.add(track);
            }
        }

        return tracks;
    }

    private ArrayList<Tag> parseTags(ArrayList<Tag> tags, JSONObject trackJsonBody) throws JSONException {

        Iterator<String> trackKeys = trackJsonBody.keys();

        while (trackKeys.hasNext()) {
            String key = trackKeys.next();
            if (!key.equals("@id")) {
                JSONObject jsonItem = trackJsonBody.getJSONObject(key);
                Tag tag = new Tag();

                tag.setTagId(jsonItem.getInt("tag_id"));
                tag.setTagLabel(jsonItem.getString("tag_label"));
                tag.setTagDesc(jsonItem.getString("tag_desc"));
                tag.setTagActive(jsonItem.getInt("is_active")==1);

                tags.add(tag);
            }
        }

        return tags;
    }

    public List<EngagementStep> getEngagementSteps(Account account, List<EngagementTrack> tracks) throws JSONException {
        ArrayList<EngagementStep> steps = new ArrayList<>();

        for (EngagementTrack track : tracks) {
            String stepApi = Uri.parse("/apps/kardia/api/crm_config/Tracks/" + track.TrackName + "/Steps")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_format", "attrs")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .appendQueryParameter("cx__res_type", "collection")
                    .build().toString();

            String stepJsonString = Request(account, stepApi);

            if (!stepJsonString.equals("")) {
                JSONObject stepJsonBody = new JSONObject(stepJsonString);
                parseEngagementSteps(steps, stepJsonBody);
            }
        }

        return steps;
    }

    private void parseEngagementSteps(ArrayList<EngagementStep> steps, JSONObject stepJsonBody) throws JSONException {
        Iterator<String> stepKeys = stepJsonBody.keys();

        while (stepKeys.hasNext()) {
            String key = stepKeys.next();
            if (!key.equals("@id")) {
                JSONObject jsonItem = stepJsonBody.getJSONObject(key);

                EngagementStep step = new EngagementStep();
                step.StepId = jsonItem.getString("step_id");
                step.StepName = jsonItem.getString("step_name");
                step.TrackId = jsonItem.getString("track_id");
                step.TrackName = jsonItem.getString("track_name");
                step.StepDescription = jsonItem.getString("step_description");
                step.StepSequence = jsonItem.getInt("step_sequence");

                steps.add(step);
            }
        }
    }
}