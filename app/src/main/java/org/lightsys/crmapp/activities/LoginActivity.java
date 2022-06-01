package org.lightsys.crmapp.activities;

import static org.lightsys.crmapp.activities.ProfileActivity.saveImageFromUrl;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatCallback;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.models.Partner;
import org.lightsys.crmapp.models.Staff;
import org.lightsys.crmapp.models.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import okhttp3.Credentials;

/*
    LoginActivity - the activity that displays upon app startup and prompts the user for
        login info, such as username and server info
    Superclass: AccountAuthenticatorActivity
    Interface: AppCompatCallback
 */
public class LoginActivity extends Activity implements AppCompatCallback {

    private final int CONTACT_PERMISSION_REQUEST = 1;
    private final String TAG = "Login Activity";

    private AccountManager mAccountManager;
    public static String Credential;

    private String fullServerAddress = "";
    private Account account;
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Created");

        mAccountManager = AccountManager.get(this);

        AppCompatDelegate delegate = AppCompatDelegate.create(this, this);

        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.loginToolbar);
        delegate.setSupportActionBar(toolbar);

        Button button = (Button) findViewById(R.id.loginSubmit);
        button.setOnClickListener(v -> addAccount());

        EditText serverAddress = (EditText) findViewById(R.id.loginServer);
        serverAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                addAccount();
                return true;
            }

            return false;
        });
    }

    /**
     * Attempts to add new account to AccountManager using info input by user
     */
    private void addAccount() {
        // Access fields with login data
        EditText accountName = (EditText) findViewById(R.id.loginUsername);
        EditText serverAddress = (EditText) findViewById(R.id.loginServer);
        EditText portNumber = (EditText) findViewById(R.id.loginPort);
        Spinner protocol = (Spinner) findViewById(R.id.protocolSpinner);

        // Retrieve login data
        String addAccountName = accountName.getText().toString();
        String addServerAddress = serverAddress.getText().toString();
        String addPortNumber = portNumber.getText().toString();
        String addProtocol;

        //Set protocol with spinner
        //Other options can easily be added by adding items in strings.xml
        switch (protocol.getSelectedItemPosition()) {
            case 0:
                addProtocol = "http";
                break;
            case 1:
                addProtocol = "https";
                break;
            default:
                addProtocol = "http";
                break;
        }

        //If any field is left blank, display error message
        if (addAccountName.equals("")) {
            ((TextView) findViewById(R.id.loginUsernameError)).setText(R.string.enter_name);
            return;
        }
        else {
            ((TextView) findViewById(R.id.loginUsernameError)).setText("");
        }
        if (addServerAddress.equals("")) {
            ((TextView) findViewById(R.id.loginServerError)).setText(R.string.enter_server);
            return;
        }
        else {
            ((TextView) findViewById(R.id.loginServerError)).setText("");
        }
        if (addPortNumber.equals("")) {
            ((TextView) findViewById(R.id.loginPortError)).setText(R.string.enter_port);
            return;
        }
        else {
            ((TextView) findViewById(R.id.loginPortError)).setText("");
        }

        //Concatenate server info into full address
        fullServerAddress = addProtocol + "://" + addServerAddress + ":" + addPortNumber;

        // Create a new accounts object
        account = new Account(addAccountName, LocalDBTables.accountType);

        // Check if permission is not already granted, and if not, request it
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.GET_ACCOUNTS }, CONTACT_PERMISSION_REQUEST);
        }
        else {
            if (mAccountManager.getAccounts().length > 0) {
                authenticateWithToken();
            }

            else {
                authenticateWithPassword();
            }
        }
    }

    /**
     * Event listener for Contact permission request to user
     * @param requestCode - the code assigned to the permission request
     * @param permissions - the permissions requested
     * @param grantResults - the results of the permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CONTACT_PERMISSION_REQUEST) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If accounts already exist, attempt to authenticate with token
                if (mAccountManager.getAccounts().length > 0) {
                    authenticateWithToken();
                }

                // Otherwise authenticate with password to add account
                else {
                    authenticateWithPassword();
                }
            }
            // If the user did not grant permission, authenticate with password
            else {
                authenticateWithPassword();
            }
        }
    }

    /**
     * - Checks to ensure account was added successfully and obtains authenticator results
     * @param account the account info entered by the user
     */
    private void checkAccount(Account account) {

        // If the account was added successfully
        if (mAccountManager.getUserData(account, "partnerId") != null) {
            ContentResolver.setSyncAutomatically(account,
                    LocalDBTables.providerAuthority, true);

            Credential = Credentials.basic(account.name, mAccountManager.getPassword(account));

            new GetCollaborateesTask().execute(account);
            new GetTagsTask().execute(account);
            // Whichever of the above threads completes first will move the application to
            // the MainActivity
        }

        // If for some reason there is no account
        else {
            Toast.makeText(getApplicationContext(), "Account Error: Please sign in again",
                    Toast.LENGTH_SHORT).show();

            // Prompt for password to login and add account
            authenticateWithPassword();
        }
    }

    /**
     * Creates DialogBox for user to enter password
     */
    private void authenticateWithPassword() {
        // Popup DialogBox to enter password
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password");

        // Input field for password
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // OK button
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Get the password the user entered from the DialogBox
            password = input.getText().toString();

            // If a password has been entered, add the account with the password
            if (!password.equals("")) {
                Bundle bundle = new Bundle();
                bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, LocalDBTables.accountType);
                boolean success = mAccountManager.addAccountExplicitly(account, password, bundle);
                password = "";

                // If the account was added to the AccountManager successfully
                if (success) {
                    Log.i(TAG, "Adding Account Succeeded");
                    mAccountManager.setUserData(account, "server", fullServerAddress);
                    Credential = Credentials.basic(account.name, mAccountManager.getPassword(account));

                    new GetPartnerIdTask().execute(account);
                }

                else {
                    Log.e(TAG, "Adding Account Failed");
                }
            }
            password = "";
        });

        // Cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the DialogBox
        builder.show();
    }

    /**
     * Checks if username matches existing account
     * If so, authenticates using stored token
     * If not, triggers password authentication
     */
    private void authenticateWithToken()
    {
        // Check for existing account
        Account[] accounts = mAccountManager.getAccountsByType(LocalDBTables.accountType);
        for (Account acc : accounts)
        {
            if (acc.name.equals(account.name))
            {
                account = acc;
                break;
            }
        }

        // Retrieve token if it exists
        String token = mAccountManager.getPassword(account);

        // If the account is already stored, send the stored token
        if (token != null) {
            mAccountManager.setUserData(account, "server", fullServerAddress);
            Credential = Credentials.basic(account.name, mAccountManager.getPassword(account));
            new GetPartnerIdTask().execute(account);
        }

        // Otherwise authenticate with password for new account
        else {
            authenticateWithPassword();
        }
    }

    /**
     * - Starts the MainActivity after successful login
     */
    private void mainActivity() {
        // Get an auth token to use on subsequent logins
        replacePasswordWithToken();

        // Move to Main Activity
        Intent main = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }

    /**
     * Obtains and stores auth token upon successful login with password
     */
    private void replacePasswordWithToken()
    {
        // Get a token from the server
        String token = tokenPOSTRequest();

        // If a token was successfully obtained
        if (token != null) {
            Log.i(TAG, "Token Obtained");

            // Store token as the account password
            mAccountManager.setPassword(account, token);
        }

        // Otherwise token retrieval failed
        else{
            Log.e(TAG, "Token Acquisition Failed");
        }
    }

    /**
     * Makes POST request to server to retrieve token
     */
    private String tokenPOSTRequest() {
        /*try {
            URL url = new URL(fullServerAddress);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            String urlPostParameters = "userName=" + account.name + ";";
            outputStream.writeBytes(urlPostParameters);

            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            System.out.println(connection.getResponseMessage());

            BufferedReader input = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = input.readLine()) != null)
            {
                response.append(inputLine);
            }

            input.close();
*/
        StringBuffer response = new StringBuffer();
        response.append("{\ntoken:thebestesttokenever\n}");
        HashMap<String, String> returnedData = parseJSON(response.toString());

        if (!returnedData.isEmpty()) {
            return returnedData.get("token");
        }

        else{
            return null;
        }
        /*}

        catch (MalformedURLException ex)
        {

        }

        catch (IOException ex2)
        {

        }

        return null;*/
    }

    /**
     * Parses JSON string into a String HashMap so token can be retrieved
     * @param input - the String from the HTTP response to be parsed
     * @return a HashMap of key-value pairs retrieved from the JSON
     */
    private HashMap<String, String> parseJSON(String input) {
        // Define needed objects
        HashMap<String, String> userData = new HashMap<>();
        Scanner scanner = new Scanner(input);

        while (scanner.hasNext()) {
            // Read the string, ignoring brackets
            String thisLine = scanner.nextLine();
            if (thisLine.equals("{") || thisLine.equals("}"))
                continue;

            //Strip quotations, colons, and commas from the data
            String key = thisLine.split(":")[0];
            String val = thisLine.split(":")[1];

            key = key.replaceAll(",", "");
            val = val.replaceAll(",", "");

            key = key.replaceAll("\"", "");
            val = val.replaceAll("\"", "");

            userData.put(key, val);
        }

        return userData;
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    private class GetPartnerIdTask extends AsyncTask<Account, Void, Account> {
        @Override
        protected Account doInBackground(Account... accounts) {
            KardiaFetcher fetcher = new KardiaFetcher(LoginActivity.this);
            List<Staff> staff = fetcher.getStaff(accounts[0]);

            for(Staff staffMember : staff) {
                ContentValues values = new ContentValues();
                values.put(LocalDBTables.StaffTable.PARTNER_ID, staffMember.PartnerId);
                values.put(LocalDBTables.StaffTable.KARDIA_LOGIN, staffMember.getKardiaLogin());
                getContentResolver().insert(LocalDBTables.StaffTable.CONTENT_URI, values);
            }

            Cursor cursor = getContentResolver().query(
                    LocalDBTables.StaffTable.CONTENT_URI,
                    new String[] { LocalDBTables.StaffTable.PARTNER_ID },
                    LocalDBTables.StaffTable.KARDIA_LOGIN + " = ?",
                    new String[] { accounts[0].name },
                    null
            );

            if(cursor.moveToFirst()) {
                mAccountManager.setUserData(accounts[0], "partnerId", cursor.getString(0));
            }
            cursor.close();
            return accounts[0];
        }

        @Override
        protected void onPostExecute(Account account) {
            checkAccount(account);
        }
    }

    private class GetTagsTask extends AsyncTask<Account, Void, Void> {
        List<Tag> tags = null;
        @Override
        protected Void doInBackground(Account... accounts) {
            KardiaFetcher fetcher = new KardiaFetcher(LoginActivity.this);
            tags = fetcher.getTags(accounts[0]);

            for(Tag tag : tags) {
                ContentValues values = new ContentValues();
                values.put(LocalDBTables.TagTable.TAG_ID, tag.getTagId());
                values.put(LocalDBTables.TagTable.TAG_LABEL, tag.getTagLabel());
                values.put(LocalDBTables.TagTable.TAG_DESC, tag.getTagDesc());
                values.put(LocalDBTables.TagTable.TAG_ACTIVE, tag.isTagActive());

                getContentResolver().insert(LocalDBTables.TagTable.CONTENT_URI, values);
            }
            return null;
        }

        /**
         * If thread successfully retrieved data, then authentication succeeded
         * Opens Main Activity of application
         * @param nothing unused required parameter
         */
        @Override
        protected void onPostExecute(Void nothing) {
            if (tags != null && tags.size() > 0) {
                mainActivity();
            }
        }
    }

    private class GetCollaborateesTask extends AsyncTask<Account, Void, Void> {
        Exception error;
        Account account;
        @Override
        protected Void doInBackground(Account... accounts) {
            KardiaFetcher fetcher = new KardiaFetcher(LoginActivity.this);
            account = accounts[0];
            List<Partner> collaboratees = null;

            try {
                collaboratees = fetcher.getCollaboratees(account);

                for (Partner collaboratee : collaboratees) {
                    ContentValues values = new ContentValues();
                    values.put(LocalDBTables.CollaborateeTable.COLLABORATER_ID,
                            mAccountManager.getUserData(accounts[0], "partnerId"));
                    values.put(LocalDBTables.CollaborateeTable.PARTNER_ID,
                            Integer.parseInt(collaboratee.PartnerId));
                    values.put(LocalDBTables.CollaborateeTable.PARTNER_NAME,
                            collaboratee.PartnerName);
                    values.put(LocalDBTables.CollaborateeTable.PROFILE_PICTURE,
                            collaboratee.ProfilePictureFilename);

                    getContentResolver().insert(LocalDBTables.CollaborateeTable.CONTENT_URI,
                            values);

                    saveImageFromUrl(
                            mAccountManager.getUserData(accounts[0], "server"),
                            getApplicationContext(),
                            collaboratee.ProfilePictureFilename
                    );
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                error = e;
            }

            // If thread did not retrieve any data, assume authentication error
            if (collaboratees == null || collaboratees.size() == 0) {
                error = new Exception();
            }

            return null;
        }

        /**
         * If thread successfully retrieved data, then authentication succeeded
         * Opens Main Activity of application
         * @param nothing unused required parameter
         */
        @Override
        protected void onPostExecute(Void nothing) {
            // If thread successfully retrieved data, move to Main Activity
            if (error == null)
                mainActivity();

            // Else the thread did not retrieve any data, authentication failed due to incorrect
                // password or expired token
            // Reset account and prompt for new password authentication to re-add the account
            else {
                Toast.makeText(getApplicationContext(), "Invalid Login: Please enter password",
                        Toast.LENGTH_SHORT).show();
                mAccountManager.removeAccountExplicitly(account);
                authenticateWithPassword();
            }
        }
    }
}
