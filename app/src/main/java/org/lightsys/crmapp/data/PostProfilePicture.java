package org.lightsys.crmapp.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by tparr on 7/5/2017.
 */

public class PostProfilePicture extends AsyncTask<String, Void, String>
{
    private static final String TAG = "Post Profile Picture";
    private String url;
    private File image;
    private Account mAccount;
    private Context context;
    private AccountManager mAccountManager;
    private OkHttpClient client;
    private boolean success;
    private String credential;
    private JSONObject jsonDate;
    private String nextPartnerKey;
    private String tokenParam;
    private Calendar cal;

    public PostProfilePicture(Context context, String Url, File image, Account userAccount, String nextpartnerkey){
        url = Url;
        this.image = image;
        mAccount = userAccount;
        this.context = context;
        mAccountManager = AccountManager.get(context);
        credential = Credentials.basic(mAccount.name, mAccountManager.getPassword(mAccount));
        nextPartnerKey = nextpartnerkey;
    }

    @Override
    protected String doInBackground(String... objects)
    {
        java.net.Authenticator.setDefault(new java.net.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(mAccount.name, mAccountManager.getPassword(mAccount).toCharArray());
            }
        });

        String result;
        try
        {
            //url used to retrieve the access token
            URL getUrl = new URL(mAccountManager.getUserData(mAccount, "server") + "/?cx__mode=appinit&cx__groupname=Kardia&cx__appname=Donor");

            client = new OkHttpClient.Builder()
                    .cookieJar(new MyCookieJar())
                    .authenticator(new okhttp3.Authenticator()
                    {
                        @Override
                        public Request authenticate(Route route, Response response) throws IOException
                        {
                            return response.request().newBuilder()
                                    .header("Authorization", credential)
                                    .build();
                        }
                    }).build();

            Request request = new Request.Builder()
                    .url(getUrl)
                    .header("Authorization", credential)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            //get access token
            if (response.body() != null)
            {
                result = response.body().string();
                JSONObject token = new JSONObject(result);

                tokenParam = "cx__akey=" + token.getString("akey");
                url += tokenParam;

                //post profile picture
                String imageLocation = performPostCall();
                PostPictureMetadata(imageLocation);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private String performPostCall() {
        String result = "";
        try {
            if (client == null)
                client = new OkHttpClient();

            String imageName = image.getName();
            int indexOfImageType = imageName.lastIndexOf(".");
            String tmpContentType = imageName.substring(indexOfImageType + 1);
            if (tmpContentType.equals("jpg"))
                tmpContentType = "jpeg";
            String contentType = "image/".concat(tmpContentType);

            MultipartBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("profile", imageName, RequestBody.create(MediaType.parse(contentType), image))
                    .build();

            url += "&target=" + URLEncoder.encode("/apps/kardia/files", "utf-8");

            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", credential)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            int responseCode = response.code();

            Log.e(TAG, "responseCode : " + responseCode);

            //if the things were sent properly, get the result code
            if (responseCode == HttpsURLConnection.HTTP_ACCEPTED && response.isSuccessful()) {
                Log.e(TAG, "HTTP_OK");
                result = response.body().string();
                success = true;
            } else {
                Log.e(TAG, "False - HTTP_OK");//send failed :(
                result = "";
                success = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String PostPictureMetadata(String imageLocation)
    {
        //Get current date
        java.util.Date date = new Date();
        cal = Calendar.getInstance();
        cal.setTime(date);
        jsonDate = new JSONObject();

        String metadataUrl = mAccountManager.getUserData(mAccount, "server") + "/apps/kardia/data/Kardia_DB/e_document/rows?" + tokenParam + "&cx__mode=rest&cx__res_type=collection&cx__res_format=attrs&cx__res_attrs=basic";

        JSONObject metadata = new JSONObject();
        try
        {
            jsonDate.put("month", cal.get(Calendar.MONTH));
            jsonDate.put("year", cal.get(Calendar.YEAR));
            jsonDate.put("day", cal.get(Calendar.DAY_OF_MONTH));
            jsonDate.put("minute", cal.get(Calendar.MINUTE));
            jsonDate.put("second", cal.get(Calendar.SECOND));
            jsonDate.put("hour", cal.get(Calendar.HOUR));

            JSONArray result = new JSONArray(imageLocation);
            JSONObject data = (JSONObject) result.get(0);
            String uploadLocation = data.getString("up");
            int indexOfImageName = uploadLocation.lastIndexOf("/");
            String imageName = uploadLocation.substring(indexOfImageName + 1);
            String originalName = data.getString("fn");

            metadata.put("e_doc_type_id", 1);
            metadata.put("e_current_folder", "/apps/kardia/files");
            metadata.put("e_current_filename", imageName);
            metadata.put("e_orig_filename", originalName);
            metadata.put("s_created_by", mAccount.name);
            metadata.put("s_modified_by", mAccount.name);
            metadata.put("s_date_created", jsonDate);
            metadata.put("s_date_modified", jsonDate);
            metadata.put("e_uploading_collaborator", mAccountManager.getUserData(mAccount, "partnerId"));
            metadata.put("e_title", "Profile Photo");

        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), metadata.toString());

        Request request = new Request.Builder()
                .url(metadataUrl)
                .header("Authorization", credential)
                .post(body)
                .build();

        try
        {
            Response response = client.newCall(request).execute();

            //get access token
            if (response.body() != null)
            {
                JSONObject associationJson = new JSONObject();
                associationJson.put("p_partner_key", nextPartnerKey);
                associationJson.put("s_created_by", mAccount.name);
                associationJson.put("s_modified_by", mAccount.name);
                associationJson.put("s_date_created", jsonDate);
                associationJson.put("s_date_modified", jsonDate);
                JSONObject metadataResult = new JSONObject(response.body().string());
                associationJson.put("e_document_id", metadataResult.getInt("e_document_id"));

                body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), associationJson.toString());

                String associationUrl = mAccountManager.getUserData(mAccount, "server") + "/apps/kardia/data/Kardia_DB/e_partner_document/rows?" + tokenParam + "&cx__mode=rest&cx__res_type=collection&cx__res_format=attrs&cx__res_attrs=basic";
                request = new Request.Builder()
                        .url(associationUrl)
                        .header("Authorization", credential)
                        .post(body)
                        .build();

                response = client.newCall(request).execute();

                if (response.body() != null)
                {
                    System.out.println(response);
                }
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String params) {

        if (success) {
            Toast.makeText(context, "Data posted successfully!", Toast.LENGTH_SHORT).show();

        }
        else {
            Toast.makeText(context, "Network Issues: Your data was not properly sent.", Toast.LENGTH_SHORT).show();
        }
    }
}
