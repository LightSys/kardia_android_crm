package org.lightsys.crmapp.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lightsys.crmapp.LoginActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

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
    private String Host;
    private String Base_Host_Name;
    private String PORT = "800";
    private String apiEndpoint;
    private String collectionQueryOptions;
    private String elementQueryOptions;
    private String Password;
    private String AccountName;
    private String AccountPartnerId;
    private int Account_ID;
    private Context dataContext;
    private LocalDatabaseHelper db;
    ErrorType errorType = null;
    private PullType pullType;
    private String partnerId;

    private static final String Tag = "DPS";

    public DataConnection(Context context, Account a, PullType type) {
        super();
        dataContext = context;
        account = a;
        pullType = type;
    }

    public DataConnection(Context context, Account a, PullType type, String partnerId) {
        super();
        dataContext = context;
        account = a;
        pullType = type;
        this.partnerId = partnerId;
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
     * Used to pull the requested data for the account. A switch statement is used to determine which
     * data is needed.
     */
    private void DataPull(){
        db = new LocalDatabaseHelper(dataContext);
        Base_Host_Name = account.getServerName();
        Host = "http://" + Base_Host_Name + ":800";
        collectionQueryOptions = "?cx__mode=rest&cx__res_type=collection&cx__res_format=attrs&cx__res_attrs=basic";
        elementQueryOptions = "?cx__mode=rest&cx__res_type=element&cx__res_format=attrs&cx__res_attrs=basic";
        Password = account.getAccountPassword();
        AccountName = account.getAccountName();
        AccountPartnerId = account.getPartnerId();

        switch (pullType) {
            case ValidateAccount:
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
                        else if (validAccount) {
                            AccountPartnerId = getPartnerId();
                            LoginActivity.returnPartnerId(AccountPartnerId);
                            LoginActivity.setIsValidAccount(validAccount);
                        }

                    //This block is actual for an account edit activity.
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
                break;

            case SearchPerson:
                break;
            case GetPartners:
                    getCollaboratees(dataContext);
                break;
            case GetProfileData:
                /** I'm going to hold off on starting this until I have a more solid answer on the API.
                 * Currently, most of the information is stored in different API endpoints. It would require
                 * at least 5 API calls to retrieve the neccessary information.
                 *
                 * The requested profile information is:
                 *
                 * 1. Name          -- Done
                 * 2. Profile Picture
                 * 3. Contact Information -- Done
                 * 3a. Email Address -- Done
                 * 3b. Phone Number -- Done
                 * 3c. Mailing Address -- Done
                 * 4. Show the persons timeline.
                 *
                 * With the ability to send an email via intents, make a phone call via intents,
                 * open Google maps on an address, and update the persons profile picture with android camera.
                 */

                getAddress(dataContext);
                getContactInfo(dataContext);
                getProfilePicture(dataContext);


                break;
        }
        db.close();

    }

    /* ************ This section queries the API to retrieve data and/or validate the account. **************** */
    /**
     * It's probably worthwhile to investigate Square's Retrofit library. It could potentially clean up a lot of the
     * extra code here.
     * Currently Google's Gson library is used to map the JSON results to Java objects. Because the API returns
     * an object and not an array, some amount of data gymnastics are required to make the data into an array
     * that will play nicely with Gson.
     */

    /**
     * @return if account was found to be valid
     */
    private boolean isValidAccount() {
        boolean isValid = false;
        apiEndpoint = "/apps/kardia/api/crm/";
        String query = Host + apiEndpoint + collectionQueryOptions;
        // Account details already set in DataPull()
        try {
            // Attempt to pull information about the donor from the API

            String test = GET(query);
            // Unauthorized signals invalid ID
            // 404 not found signals incorrect username or password
            // Empty or null signals an incorrect server name
            if (test.equals("")) {
                errorType = ErrorType.ServerNotFound;
            } else if (test.contains("<H1>Unauthorized</H1>")) {
                errorType = ErrorType.Unauthorized;
            } else if (test.contains("404 Not Found")) {
                errorType = ErrorType.InvalidLogin;
            } else {
                isValid = true;
            }
        }
        catch (Exception e) {
            // GET function throws an Exception if server not found
            errorType = ErrorType.ServerNotFound;
            return false;
        }
        return isValid;
    }

    /**
     * Retrieves the Partner ID of the account that is logged in.
     * @return the partnerId of the logged in account.
     */

    private String getPartnerId() {
        apiEndpoint = "/apps/kardia/api/partner/Staff";
        String query = Host + apiEndpoint + collectionQueryOptions;
        String returnedPartnerId = "NaN";

        try {
            String queryResponse = GET(query);

            //Some amount of error handling on the response goes here.

            JSONObject jsonData = null;
            jsonData = new JSONObject(queryResponse);

            JSONArray jsonArray = jsonData.names();

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    // @id signals a new object but contains no information
                    if(!jsonArray.getString(i).equals("@id")) {
                        String kardiaLogin =
                                ((JSONObject) jsonData
                                        .get(jsonArray.get(i).toString()))
                                        .get("kardia_login").toString();
                        if (kardiaLogin.equals(AccountName)) {
                            returnedPartnerId = ((JSONObject) jsonData
                                    .get(jsonArray.get(i).toString()))
                                    .get("partner_id").toString();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            errorType = ErrorType.ServerNotFound;
        }
        return returnedPartnerId;
    }

    /**
     * Retrieves the logged in accounts Collaboratees from the server.
     * @param dataContext
     */

    private void getCollaboratees(Context dataContext) {
        apiEndpoint = "/apps/kardia/api/crm/Partners/";
        String query = Host + apiEndpoint + AccountPartnerId + "/Collaboratees" + collectionQueryOptions;


        try {
            String queryResponse = GET(query);

            // Some amount of error handling on the response goes here.

            JSONObject jsonData = null;
            jsonData = new JSONObject(queryResponse);

            /**
             * The JSON logic gets messy here. The API returns a JSON object, however, Gson works
             * best when the results are returned in an array. Therefore, some amount of work is required
             * to get the results into a JSON array.
             */

            JSONArray jsonArray = jsonData.names();

            ArrayList<String> tempList = new ArrayList<>(jsonArray.length());
            for (int i=0; i < jsonArray.length(); i++) {
                tempList.add(jsonArray.get(i).toString());
            }
            tempList.remove("@id");

            JSONArray trimmedArray = new JSONArray(tempList);

            JSONArray finalArray = jsonData.toJSONArray(trimmedArray);

            Gson gson = new Gson();
            GsonCollaborateeList list = gson.fromJson("{ \"collaboratees\" : " + finalArray.toString() + "}", GsonCollaborateeList.class);
            LocalDatabaseHelper db = new LocalDatabaseHelper(dataContext);
            db.addCollaboratees(list.getCollaboratees());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProfilePicture(Context dataContext){
        // The API currently returns information about the picture from /ProfilePicture, retrieving
        // the actual picture requires /ProfilePicture/{filename}. If the API is updated to support
        // a single /Profile endpoint, it would be useful to return the picture directly there.


        apiEndpoint = "/apps/kardia/api/crm/Partners/";
        String query = Host + apiEndpoint + partnerId + "/ProfilePicture" + elementQueryOptions;

        try {
            String queryResponse = GET(query);

            Gson gson = new Gson();
            GsonProfilePicture profPic = gson.fromJson(queryResponse, GsonProfilePicture.class);

            db.addProfilePicture(profPic, partnerId, Host + apiEndpoint + partnerId + "/ProfilePicture/");


        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * Logic for retrieving and storing the profile picture should go here. I have opted to wait
         * to implement this feature due to possible API changes. The picture should be stored in Android's
         * file system, with the image path being stored in the database. This should be more efficient and scalable
         * than storing the image in the database, as well as allowing for the possibility of a LRU
         * (Least Recently Used) swapping scheme to manage the storage space used by the app.
         */
    }

    private void getAddress(Context dataContext) {
        apiEndpoint = "/apps/kardia/api/partner/Partners/";
        String query = Host + apiEndpoint + partnerId + "/Addresses" + collectionQueryOptions;

        try {
            String queryResponse = GET(query);

            // Some amount of error handling on the response should be added here.

            JSONObject jsonData = null;
            jsonData = new JSONObject(queryResponse);

            /**
             * Again, we have the messy JSON logic.
             */

            JSONArray jsonArray = jsonData.names();

            ArrayList<String> tempList = new ArrayList<>(jsonArray.length());
            for (int i=0; i< jsonArray.length(); i++) {
                tempList.add(jsonArray.get(i).toString());
            }
            tempList.remove("@id");

            JSONArray trimmedArray = new JSONArray(tempList);
            JSONArray finalArray = jsonData.toJSONArray(trimmedArray);

            Gson gson = new Gson();
            GsonAddressList list = gson.fromJson("{ \"addresses\" : " + finalArray.toString() + "}", GsonAddressList.class);

            db.addAddresses(list.getAddresses());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getContactInfo(Context dataContext) {
        apiEndpoint = "/apps/kardia/api/partner/Partners/";
        String query = Host + apiEndpoint + partnerId + "/ContactInfo" + collectionQueryOptions;

        try {
            String queryResponse = GET(query);

            // Some amount of error handling on the response should be added here.

            JSONObject jsonData = null;
            jsonData = new JSONObject(queryResponse);

            /**
             * Again, we have the messy JSON logic.
             */

            JSONArray jsonArray = jsonData.names();

            ArrayList<String> tempList = new ArrayList<>(jsonArray.length());
            for (int i=0; i< jsonArray.length(); i++) {
                tempList.add(jsonArray.get(i).toString());
            }
            tempList.remove("@id");

            JSONArray trimmedArray = new JSONArray(tempList);
            JSONArray finalArray = jsonData.toJSONArray(trimmedArray);

            Gson gson = new Gson();
            GsonContactList list = gson.fromJson("{ \"contacts\" : " + finalArray.toString() + "}", GsonContactList.class);

            db.addContactInfos(list.getContactInfo());

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            credProvider.setCredentials(new AuthScope(Base_Host_Name, 800),
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