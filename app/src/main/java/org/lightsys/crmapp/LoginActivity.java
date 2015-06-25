package org.lightsys.crmapp;

import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.lightsys.crmapp.data.Account;
import org.lightsys.crmapp.data.DataConnection;
import org.lightsys.crmapp.data.ErrorType;
import org.lightsys.crmapp.data.LocalDatabaseHelper;
import org.lightsys.crmapp.data.PullType;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LoginActivity extends ActionBarActivity {



    ListView accountsListView;
    EditText accountName, accountPassword, serverAddress;
    TextView connectedAccounts;
    View loginLayout;
    Toolbar toolbar;
    ArrayList<Account> accounts;
    private static String PartnerId;
    Button button;

    // These will be set by the async task's callback function.
    private static Boolean isValidAccount = null;
    private static ErrorType errorType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);
        // This should be centered, it seems that will require creating a custom view using XML.
        getSupportActionBar().setTitle("Kardia CRM");
        getSupportActionBar().setSubtitle("Login");

        accountName = (EditText) findViewById(R.id.loginUsername);
        accountPassword = (EditText) findViewById(R.id.loginPassword);
        serverAddress = (EditText) findViewById(R.id.loginServer);
        connectedAccounts = (TextView) findViewById(R.id.loginConnectedAccounts);
        accountsListView = (ListView) findViewById(R.id.loginListView);
        button = (Button) findViewById(R.id.loginSubmit);
        loginLayout = findViewById(R.id.loginLayout);

        loadAccounts();

        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAccount(v);
                //finish();
            }
        });

        serverAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                addAccount(v);

                if (actionId == EditorInfo.IME_ACTION_GO) {
                    handled = true;
                }
                //finish();
                return handled;
            }
        });




    }

    public void loadAccounts() {
        LocalDatabaseHelper db = new LocalDatabaseHelper(this);

        // From a pure efficiency standpoint, db.getAccounts() should return the data in a format that's already
        // ready to be put into the adapter. However, I like having the actual accounts for flexibility.
        // At some point if this function is only used to populate the ListView it should probably be replaced
        // for the sake of memory.
        accounts = db.getAccounts();

        if (accounts.size() > 0) {
            connectedAccounts.setText("Connected Accounts:");
        }
        else {
            connectedAccounts.setText("");
        }

        ArrayList<HashMap<String,String>> accountList = new ArrayList<>(accounts.size());

        for (Account account : accounts) {
            HashMap<String, String> accountMap = new HashMap<>();
            accountMap.put("accountName", account.getAccountName());
            accountMap.put("serverAddress", account.getServerName());
            accountList.add(accountMap);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, accountList,
                R.layout.login_list_item,
                new String[]{"accountName", "serverAddress"},
                new int[]{R.id.loginListUsername, R.id.loginListServerAddress});


        accountsListView.setAdapter(simpleAdapter);

    }

    public void addAccount(View v) {
        LocalDatabaseHelper db = new LocalDatabaseHelper(this);
        accounts = db.getAccounts();

        String addAccountName = accountName.getText().toString();
        String addAccountPassword = accountPassword.getText().toString();
        String addServerAddress = serverAddress.getText().toString();

        for(Account account : accounts) {
            if(account.getAccountName().equals(addAccountName) && account.getServerName().equals(addServerAddress)
                    && account.getAccountPassword().equals(addAccountPassword)) {
                //Snackbar.make(v, "Account already stored.", Snackbar.LENGTH_LONG).show();
                db.close();
                //Probably should load data from the server here?
                finish();
                return;
            }
        }
        if (addAccountName.equals("")) {
            ((TextInputLayout) findViewById(R.id.accountNameTIL)).setError("Enter a username.");
            db.close();
            return;
        }
        else {
            ((TextInputLayout) findViewById(R.id.accountNameTIL)).setError(null);
        }
        if (addAccountPassword.equals("")) {
            ((TextInputLayout) findViewById(R.id.accountPasswordTIL)).setError("Enter a password.");
            db.close();
            return;
        }
        else {
            ((TextInputLayout) findViewById(R.id.accountPasswordTIL)).setError(null);
        }
        if (addServerAddress.equals("")) {
            ((TextInputLayout) findViewById(R.id.serverAddressTIL)).setError("Enter a server address.");
            db.close();
            return;
        }
        else {
            ((TextInputLayout) findViewById(R.id.serverAddressTIL)).setError(null);
        }
        Account newAccount = new Account(addAccountName, addAccountPassword, addServerAddress);
        //Add an async connection to check if the account is valid with the server.

        (new DataConnection(this, newAccount, PullType.ValidateAccount)).execute("");

        while (isValidAccount == null) {
            continue;
        }
        if (isValidAccount) {
            newAccount.setPartnerId(PartnerId);
            db.addAccount(newAccount);
            MainActivity.setLoggedInAccount(newAccount);
            isValidAccount = null;
            errorType = null;
            db.close();
        } else {
            // Set error statement based on error provided by async task
            String errorStatement;
            if (errorType == ErrorType.ServerNotFound) {
                errorStatement = "Server not found";
            } else if (errorType == ErrorType.InvalidLogin) {
                errorStatement = "Incorrect username/password";
            } else if (errorType == ErrorType.Unauthorized) {
                errorStatement = "Unauthorized";
            } else {
                errorStatement = "Unknown issue. \n 1) Check Internet connection" +
                        "\n 2) Server may be down";
            }
            Snackbar.make(loginLayout, "Connection to server failed: " + errorStatement, Snackbar.LENGTH_LONG).show();

            // set async flags back to null for next account connection
            isValidAccount = null;
            errorType = null;
            db.close();
            return;
        }
        finish();
    }

    public static void setErrorType(ErrorType pErrorType) {
        errorType = pErrorType;
    }

    public static void setIsValidAccount(boolean isValid) {
        isValidAccount = isValid;
    }

    public static void returnPartnerId(String partnerId) {
        PartnerId = partnerId;
    }
}
