package org.lightsys.crmapp.data;

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
    public String getUrlString(final User user, String api) throws IOException {
        Authenticator.setDefault(new Authenticator() {
                                     protected PasswordAuthentication getPasswordAuthentication() {
                                         return new PasswordAuthentication(user.getUsername(), user.getPassword().toCharArray());
                                     }
                                 });

        URL url = new URL("http://" + user.getServer() + ":800" + api);
        Log.d("partner", url.toString());
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + user.getServer());
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

    public String getPartnerId(User user) {
        List<Staff> staff = new ArrayList<>();

        try {
            String api = Uri.parse("/apps/kardia/api/partner/Staff")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String jsonString = getUrlString(user, api);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseStaff(staff, jsonBody);
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        Log.d("staff", "yes");

        for(Staff staffMember : staff) {
            Log.d("staff", staffMember.getKardiaLogin());
            if(user.getUsername().equals(staffMember.getKardiaLogin())) {
                return staffMember.getPartnerId();
            }
        }

        return null;
    }

    public List<Partner> getCollaboratees(User user) {
        List<Partner> collaboratees = new ArrayList<>();

        try {
            String api = Uri.parse("/apps/kardia/api/crm/Partners/" + user.getStaff().getPartnerId() + "/Collaboratees")
                    .buildUpon()
                    .appendQueryParameter("cx__mode", "rest")
                    .appendQueryParameter("cx__res_type", "collection")
                    .appendQueryParameter("cx__res_attrs", "basic")
                    .build().toString();
            String jsonString = getUrlString(user, api);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseCollaboratees(collaboratees, jsonBody);
        } catch (JSONException je) {
            je.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return collaboratees;
    }

    private void parseCollaboratees(List<Partner> collaboratees, JSONObject jsonBody) throws IOException, JSONException {
        Iterator<String> keys = jsonBody.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            if(!key.equals("@id")) {
                JSONObject jsonPartner = jsonBody.getJSONObject(key);

                Partner collaboratee = new Partner();

                collaboratee.setPartnerId(jsonPartner.getString("partner_id"));
                collaboratee.setPartnerName(jsonPartner.getString("partner_name"));

                collaboratees.add(collaboratee);
            }
        }
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
