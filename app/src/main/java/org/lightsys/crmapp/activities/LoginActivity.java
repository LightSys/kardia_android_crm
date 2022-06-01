package org.lightsys.crmapp.activities;

import static org.lightsys.crmapp.activities.ProfileActivity.saveImageFromUrl;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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

import com.google.android.gms.common.AccountPicker;
import com.google.android.material.snackbar.Snackbar;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.models.Partner;
import org.lightsys.crmapp.models.Staff;
import org.lightsys.crmapp.models.Tag;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Credentials;

/*
    LoginActivity - the activity that displays upon app startup and prompts the user for
        login info, such as username and server info
    Superclass: AccountAuthenticatorActivity
    Interface: AppCompatCallback
 */
public class LoginActivity extends AccountAuthenticatorActivity implements AppCompatCallback {

    private AccountManager mAccountManager;
    public static String Credential;
    private static String protocal;

    private String fullServerAddress = "";
    private Account account;
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Login Activity", "Created");

        mAccountManager = AccountManager.get(this);

        if (mAccountManager.getAccounts().length > 0)
        {
            Account acc = mAccountManager.getAccounts()[0];
            Log.d("Login Activity", "Token on startup: " + mAccountManager.peekAuthToken(acc, "BEARER"));
        }

        AppCompatDelegate delegate = AppCompatDelegate.create(this, this);

        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.loginToolbar);
        delegate.setSupportActionBar(toolbar);

        Button button = (Button) findViewById(R.id.loginSubmit);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAccount();
            }
        });

        EditText serverAddress = (EditText) findViewById(R.id.loginServer);
        serverAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    addAccount();
                    return true;
                }

                return false;
            }
        });
    }

    /**
     * - Attempts to add a new account to the AccountManager using the info
     * input by the user
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

        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.GET_ACCOUNTS }, 1);
        }
        else {
            if (mAccountManager.getAccounts().length > 0) {
                /*Intent intent = mAccountManager.newChooseAccountIntent(null,
                        null, new String[]{LocalDBTables.accountType}, null,
                        null, null, null);
                startActivityForResult(intent, 1);
                Log.d("LoginActivity", "Started account intent with " + mAccountManager.getAccounts().length + " accounts");*/
                authenticateWithToken();
            }

            else {
                authenticateWithPassword();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (mAccountManager.getAccounts().length > 0) {
                /*Intent intent = mAccountManager.newChooseAccountIntent(null,
                        null, new String[]{LocalDBTables.accountType}, null,
                        null, null, null);
                startActivityForResult(intent, 1);
                Log.d("LoginActivity", "Started account intent with " + mAccountManager.getAccounts().length + " accounts");*/
                    authenticateWithToken();
                }

                else {
                    authenticateWithPassword();
                }
            }
            else {
                authenticateWithPassword();
            }
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data == null)
            {
                authenticateWithPassword();
            }

            else
            {
                authenticateWithToken();
            }
        }
    }*/

    /**
     * - Validates login info
     * @param account the account info entered by the user
     */
    private void checkAccount(Account account) {
        // If the login is successful
        if (mAccountManager.getUserData(account, "partnerId") != null) {
            ContentResolver.setSyncAutomatically(account, LocalDBTables.providerAuthority, true);

            Bundle bundle = new Bundle();
            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, LocalDBTables.accountType);
            setAccountAuthenticatorResult(bundle);


            if (mAccountManager.peekAuthToken(account, "BEARER") == null)
                Credential = Credentials.basic(account.name, mAccountManager.getPassword(account));

            else
                Credential = Credentials.basic(account.name, mAccountManager.peekAuthToken(account, "BEARER"));


            // Get a token from the server
            String token = tokenPOSTRequest(account);

            // todo remove debug popup message
            View loginLayout = findViewById(R.id.loginLayout);
            Snackbar.make(loginLayout, "Received token: " + token, Snackbar.LENGTH_SHORT).show();

            // If a token was successfully obtained
            if (mAccountManager.peekAuthToken(account, "BEARER") == null) {
                Log.d("LoginActivity", "Token Stored");

                // Store token as the account password
                mAccountManager.setAuthToken(account, "BEARER", token);
                Log.d("LoginActivity", mAccountManager.peekAuthToken(account, "BEARER"));
            }

            // Otherwise POST request for token retrieval failed
            else{
                Log.d("LoginActivity", "Token Not Stored");
            }

            new GetCollaborateesTask().execute(account);
            new GetTagsTask().execute(account);
            // Move to MainActivity
        }

        else {
            View loginLayout = findViewById(R.id.loginLayout);
            Snackbar.make(loginLayout, "Invalid Login", Snackbar.LENGTH_LONG).show();

            // Remove the account
            hidePassword(account);

            // Prompt for password to login and retrieve new token
            authenticateWithPassword();
        }
    }

    /**
     * - Creates DialogBox for user to enter password
     */
    private void authenticateWithPassword() {
        // Popup DialogBox to enter password
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password");

        // Input field for password
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setText("knit-reef-best");
        builder.setView(input);

        // OK button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                        Log.d("LoginActivity", "Adding Account Succeeded: " + fullServerAddress);
                        mAccountManager.setUserData(account, "server", fullServerAddress);
                        Credential = Credentials.basic(account.name, mAccountManager.getPassword(account));

                        new GetPartnerIdTask().execute(account);
                    }

                    else {
                        Log.d("LoginActivity", "Adding Account Failed");
                    }
                }
            }
        });

        // Cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the DialogBox
        builder.show();
    }

    private void authenticateWithToken()
    {
        // Check for existing account
        Account[] accounts = mAccountManager.getAccountsByType(LocalDBTables.accountType);

        Log.d("LoginActivity", "Number of accounts: " + accounts.length);

        for (int i = 0; i < accounts.length; i++)
        {
            if (accounts[i].name.equals(account.name))
            {
                //Log.d("LoginActivity", mAccountManager.peekAuthToken(accounts[i], "BEARER"));
                account = accounts[i];
                break;
            }
        }

        // Retrieve token if it exists
        String token = mAccountManager.peekAuthToken(account, "BEARER");

        // todo remove debug popup message
        View loginLayout = findViewById(R.id.loginLayout);
        Snackbar.make(loginLayout, "Stored token: " + token + " in account " + account.name, Snackbar.LENGTH_SHORT).show();

        // If the account is already stored, send the stored token
        if (token != null) {
            //Bundle bundle = new Bundle();
            //bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            //bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, LocalDBTables.accountType);
            //boolean success = mAccountManager.addAccountExplicitly(account, token, bundle);

            // If the account was added to the AccountManager successfully
            //if (success) {
                mAccountManager.setUserData(account, "server", fullServerAddress);
                Credential = Credentials.basic(account.name, mAccountManager.peekAuthToken(account, "BEARER"));
                new GetPartnerIdTask().execute(account);
            //}

            //else {
            //    Log.d("LoginActivity", "Adding Account Failed");
            //}
        }

        else {
            authenticateWithPassword();
        }
    }

    /**
     * - Makes POST request to server to retrieve token
     * @param account the account to retrieve a token for
     */
    private String tokenPOSTRequest(Account account) {
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
            View loginLayout = findViewById(R.id.loginLayout);
            Snackbar.make(loginLayout, "Malformed URL", Snackbar.LENGTH_SHORT).show();
        }

        catch (IOException ex2)
        {
            View loginLayout = findViewById(R.id.loginLayout);
            Snackbar.make(loginLayout, "IO Exception", Snackbar.LENGTH_SHORT).show();
        }

        return null;*/
    }

    private HashMap<String, String> parseJSON(String input) {

        HashMap<String, String> userData = new HashMap<String, String>();
        Scanner scanner = new Scanner(input);

        while (scanner.hasNext())
        {
            String thisLine = scanner.nextLine();
            if (thisLine.equals("{") || thisLine.equals("}"))
                continue;

            //Strip quotations and commas from the data
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

    /**
     * - Starts the MainActivity after successful login
     */
    private void mainActivity() {
        for (Account acc : mAccountManager.getAccounts())
        {
            mAccountManager.clearPassword(acc);
        }

        Intent main = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }

    /**
     * - Deletes account in case of token failure and wipes local copy of password
     * @param account the account owning the password to wipe
     */
    private void hidePassword(Account account) {
        if (mAccountManager.getPassword(account).equals(password)) {
            mAccountManager.removeAccount(account, null, null, null);
        }
        password = "";

        Log.d("LoginActivity", "Wiping password and account");
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
        @Override
        protected Void doInBackground(Account... accounts) {
            KardiaFetcher fetcher = new KardiaFetcher(LoginActivity.this);
            List<Tag> tags = fetcher.getTags(accounts[0]);

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

        @Override
        protected void onPostExecute(Void nothing) {
            mainActivity();
        }
    }

    private class GetCollaborateesTask extends AsyncTask<Account, Void, Void> {
        Exception error;
        Account account;
        @Override
        protected Void doInBackground(Account... accounts) {
            KardiaFetcher fetcher = new KardiaFetcher(LoginActivity.this);
            account = accounts[0];
            try
            {
                List<Partner> collaboratees = fetcher.getCollaboratees(account);

                for (Partner collaboratee : collaboratees) {
                    ContentValues values = new ContentValues();
                    values.put(LocalDBTables.CollaborateeTable.COLLABORATER_ID, mAccountManager.getUserData(accounts[0], "partnerId"));
                    values.put(LocalDBTables.CollaborateeTable.PARTNER_ID, Integer.parseInt(collaboratee.PartnerId));
                    values.put(LocalDBTables.CollaborateeTable.PARTNER_NAME, collaboratee.PartnerName);
                    values.put(LocalDBTables.CollaborateeTable.PROFILE_PICTURE, collaboratee.ProfilePictureFilename);

                    getContentResolver().insert(LocalDBTables.CollaborateeTable.CONTENT_URI, values);

                    saveImageFromUrl(
                            mAccountManager.getUserData(accounts[0], "server"),
                            getApplicationContext(),
                            collaboratee.ProfilePictureFilename
                    );
                }
            } catch (IOException e)
            {
                e.printStackTrace();
                error = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            if (error == null)
                mainActivity();
            else
                Toast.makeText(getApplicationContext(), "Network Issues: Server rejected request.", Toast.LENGTH_SHORT).show();
        }
    }
}
