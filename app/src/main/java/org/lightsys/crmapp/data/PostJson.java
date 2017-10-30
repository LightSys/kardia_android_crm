package org.lightsys.crmapp.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;
import org.lightsys.crmapp.activities.MainActivity;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by Judah Sistrunk on 7/7/2016.
 *
 * This class takes a json object, a baseUrl, and an account, and posts the json object to the server
 *
 * Edited by Tim Parr on 6/22/2017
 */
public class PostJson extends AsyncTask<String, Void, String> {

    private static final String TAG = "Post Json";
    private final String credential;
    private final Account account;
    private final AccountManager mAccountManager;
    private String baseUrl = "";
    private final JSONObject jsonObject;
    private final Context context;
    private boolean success = false;
    private OkHttpClient client;
    private final boolean finalTask;

    public PostJson(Context context, String Url, JSONObject jsonPost, Account userAccount, boolean finalAsyncTask){
        this.baseUrl = Url;
        String backupUrl = Url;
        jsonObject = jsonPost;
        account = userAccount;
        this.context = context;
        mAccountManager = AccountManager.get(context);
        credential = Credentials.basic(account.name, mAccountManager.getPassword(account));
        finalTask = finalAsyncTask;
    }

    @Override
    protected String doInBackground(String... params) {

        String result;
        try {

            //baseUrl used to retrieve the access token
            URL getUrl = new URL(mAccountManager.getUserData(account, "server") + "/?cx__mode=appinit&cx__groupname=Kardia&cx__appname=Donor");

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
            if (response.body() != null) {
                result = response.body().string();
                JSONObject token = new JSONObject(result);

                baseUrl += "&cx__akey=" + token.getString("akey");

                //post json object
                performPostCall(baseUrl, jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();}

        return null;
    }

    //function that posts a json object to the server
    private String performPostCall(String requestURL, JSONObject jsonObject) {

        URL url;
        String result = "";
        try {
            url = new URL(requestURL);

            if (client == null)
                client = new OkHttpClient();

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", credential)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            int responseCode = response.code();

            Log.e(TAG, "responseCode : " + responseCode);
            
            //if the things were sent properly, get the response code
            if (responseCode == HttpsURLConnection.HTTP_CREATED) {
                Log.e(TAG, "HTTP_OK");
                result = response.body().string();
                success = true;
            } else
            {
                Log.e(TAG, "False - HTTP_OK");//send failed :(
                result = "";
                success = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String params) {

        if (success) {
            if (finalTask)
            {
                Toast.makeText(context, "Data posted successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
        else {
            Toast.makeText(context, "Network Issues: Your data was not sent properly", Toast.LENGTH_SHORT).show();
        }
    }
}
