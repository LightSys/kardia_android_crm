package org.lightsys.crmapp.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by Judah Sistrunk on 7/7/2016.
 *
 * This class takes a json object a url and an account and patches the json object to the server
 *
 * Edited by Tim Parr on 6/22/2017
 */
public class PatchJson extends AsyncTask<String, Void, String> {

    private static final String TAG = "post JSon";
    private Account account;
    private AccountManager mAccountManager;
    private String baseUrl = "";
    private String backupUrl = "";
    private JSONObject jsonObject;
    private Context context;
    private boolean success = false;
    private static CookieManager cookieManager = new CookieManager();
    OkHttpClient client;

    public PatchJson(Context context, String Url, JSONObject jsonPost, Account userAccount){
        baseUrl = Url;
        backupUrl = Url;
        jsonObject = jsonPost;
        account = userAccount;
        this.context = context;
        mAccountManager = AccountManager.get(context);
        CookieHandler.setDefault(cookieManager);
    }

    @Override
    protected String doInBackground(String... params) {

        java.net.Authenticator.setDefault(new java.net.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account.name, mAccountManager.getPassword(account).toCharArray());
            }
        });

        InputStream inputStream;
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
                            String credential = Credentials.basic(account.name, mAccountManager.getPassword(account));
                            return response.request().newBuilder()
                                    .header("Authorization", credential)
                                    .build();
                        }
                    }).build();

            Request request = new Request.Builder()
                    .url(getUrl)
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

    private String convertInputStreamToString(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line, result = "";

        while ((line = reader.readLine()) != null) {
            result += line;
        }
        in.close();
        return result;
    }

    /*
        function that posts a json object to the server
    */
    private String performPostCall(String requestURL, JSONObject jsonObject) {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account.name, mAccountManager.getPassword(account).toCharArray());
            }
        });

        URL url;
        String result = "";
        try {
            url = new URL(requestURL);

            if (client == null)
                client = new OkHttpClient();

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .patch(body)
                    .build();

            Response response = client.newCall(request).execute();

            int responseCode = response.code();

            Log.e(TAG, "responseCode : " + responseCode);

            //if the things were sent properly, get the result code
            if (responseCode == HttpsURLConnection.HTTP_OK && response.isSuccessful()) {
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

class MyCookieJar implements CookieJar {

    private List<Cookie> cookies;

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        this.cookies =  cookies;
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        if (cookies != null)
            return cookies;
        return new ArrayList<>();

    }
}