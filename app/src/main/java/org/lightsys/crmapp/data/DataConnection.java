package org.lightsys.crmapp.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lightsys.crmapp.LoginActivity;
import org.lightsys.crmapp.data.LocalDatabaseHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * This class is used to pull json files (from the API URLs)
 * for a specific account and then format and store the data into the
 * local SQLite database
 * 
 * This class was taken from the Android Donor App, but I have modified it to be used for the CRM app as well. -Jake
 *
 * @author Andrew Cameron
 * @author Jake Prem
 *
 */
public class DataConnection extends AsyncTask<String, Void, String> {

    private Account account;
    private String Host_Name;
    private int Donor_ID;
    private String Password;
    private String AccountName;
    private int Account_ID;
    private Context dataContext;
    private LocalDatabaseHelper db;
    LoginActivity.ErrorType errorType = null;

    private static final String Tag = "DPS";

    public DataConnection(Context context, Account a) {
        super();
        dataContext = context;
        account = a;
    }

    /**
     * @return if account was found to be valid
     */
    private boolean isValidAccount() {
        boolean isValid = false;
        // Account details already set in DataPull()

        try {
            // Attempt to pull information about the donor from the API
            String test = GET("http://" + Host_Name + ":800/apps/kardia/api/crm/" +
                    "/?cx__mode=rest&cx__res_type=collection&cx__res_format=attrs&cx__res_attrs=basic");
            // Unauthorized signals invalid ID
            // 404 not found signals incorrect username or password
            // Empty or null signals an incorrect server name
            if (test.equals("")) {
                errorType = LoginActivity.ErrorType.ServerNotFound;
            } else if (test.contains("<H1>Unauthorized</H1>")) {
                errorType = LoginActivity.ErrorType.Unauthorized;
            } else if (test.contains("404 Not Found")) {
                errorType = LoginActivity.ErrorType.InvalidLogin;
            } else {
                isValid = true;
            }
        }
        catch (Exception e) {
            // GET function throws an Exception if server not found
            errorType = LoginActivity.ErrorType.ServerNotFound;
            return false;
        }
        return isValid;
    }

    @Override
    protected String doInBackground(String... params) {
        try{
            DataPull();
        }catch(Exception e){
            Log.w(Tag, "The DataPull failed. (probably not connected to internet or vmplayer): "
                    + e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String params){

    }

    /**
     * Pulls all data attached to account
     */
    private void DataPull(){
        db = new LocalDatabaseHelper(dataContext);
        Host_Name = account.getServerName();
        Password = account.getAccountPassword();
        AccountName = account.getAccountName();
        //Account_ID = account.getId();
        boolean validAccount = true;


        // If account does not exist in the database, check to see if it is a valid account
        // Set the validation field in the respective class that account is being tested in
        // **Note that if not valid, sets errorType before setting validation
        //   As soon as validation is set, the activity will proceed and may not get errorType**


      ArrayList<Account> databaseAccounts = db.getAccounts();
        if (!databaseAccounts.contains(account)) {
            validAccount = isValidAccount();
            if (dataContext.getClass() == LoginActivity.class) {
                if (!validAccount) {
                    LoginActivity.setErrorType(errorType);
                }
                LoginActivity.setIsValidAccount(validAccount);
            } else if (dataContext.getClass() == LoginActivity.class) {
                if (!validAccount) {
                    LoginActivity.setErrorType(errorType);
                }
                LoginActivity.setIsValidAccount(validAccount);
            }
        }


        // If not valid account, do not attempt pulling info
        if(!validAccount) {
            return;
        }

        // If no timestamp found, add timestamp, otherwise update timestamp
        long originalStamp = db.getTimeStamp();
        if (originalStamp == -1) {
            db.addTimeStamp("" + Calendar.getInstance().getTimeInMillis());
        } else {
            long currentStamp = Calendar.getInstance().getTimeInMillis();
            db.updateTimeStamp("" + originalStamp, "" + currentStamp);
        }
        db.close();
        }


    /**
     * Attempts to do basic Http Authentication, and send a get request from the url
     *
     * @param url, url for get request.
     * @return string results of the query.
     * @throws Exception when could not connect to request
     */
    public String GET(String url) throws Exception {
        InputStream inputStream;
        String result = "";

        try {

            CredentialsProvider credProvider = new BasicCredentialsProvider();
            credProvider.setCredentials(new AuthScope(Host_Name, 800),
                    new UsernamePasswordCredentials(AccountName, Password));

            DefaultHttpClient client = new DefaultHttpClient();

            client.setCredentialsProvider(credProvider);

            HttpResponse response = client.execute(new HttpGet(url));

            inputStream = response.getEntity().getContent();

            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "";
            }
        } catch (Exception e) {
            // Rethrow exception for validation server error
            throw new Exception();
        }
        return result;
    }

    /**
     * If there are results, change them into a string.
     *
     * @param in, the inputStream containing the results of the query (if any)
     * @return a string with the results of the query.
     * @throws IOException
     */
    private String convertInputStreamToString(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line, result = "";

        while ((line = reader.readLine()) != null) {
            result += line;
        }
        in.close();
        return result;
    }
}