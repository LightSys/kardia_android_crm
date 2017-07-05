package org.lightsys.crmapp.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.PasswordAuthentication;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Credentials;
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
    private Account account;
    private Context context;
    private AccountManager mAccountManager;
    private CookieHandler cookieManager = new CookieManager();
    private OkHttpClient client;
    private boolean success;

    public PostProfilePicture(Context context, String Url, File image, Account userAccount){
        url = Url;
        this.image = image;
        account = userAccount;
        this.context = context;
        mAccountManager = AccountManager.get(context);
        CookieHandler.setDefault(cookieManager);
    }

    @Override
    protected String doInBackground(String... objects)
    {
        java.net.Authenticator.setDefault(new java.net.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account.name, mAccountManager.getPassword(account).toCharArray());
            }
        });

        String result;
        try {
            //url used to retrieve the access token
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

                url += "cx__akey=" + token.getString("akey");

                //post profile picture
                performPostCall();
            }
        } catch (Exception e) {
            e.printStackTrace();}

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

            url += "&target=/apps/kardia/files/";

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
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
