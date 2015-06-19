package org.lightsys.crmapp;

import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.lightsys.crmapp.data.Account;
import org.lightsys.crmapp.data.LocalDatabaseHelper;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LoginActivity extends ActionBarActivity {

    public enum ErrorType {
        Unauthorized, ServerNotFound, InvalidLogin
    }

    ListView accountsListView;
    EditText accountName, accountPassword, serverAddress;
    TextView connectedAccounts;
    Toolbar toolbar;
    ArrayList<Account> accounts;

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

        loadAccounts();


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
                Snackbar.make(v, "Account already stored.", Snackbar.LENGTH_LONG).show();
                db.close();
                return;
            }
        }
        if (addAccountName.equals("")) {
            Snackbar.make(v, "Please enter a username.", Snackbar.LENGTH_LONG).show();
            return;
        }
        else if (addAccountPassword.equals("")) {
            Snackbar.make(v, "Please enter a password.", Snackbar.LENGTH_LONG).show();
        }
        else if (addServerAddress.equals("")) {
            Snackbar.make(v, "Please enter a server.", Snackbar.LENGTH_LONG).show();
        }
        Account newAccount = new Account(addAccountName, addAccountPassword, addServerAddress);
        //Add an async connection to check if the account is valid with the server.

        while (isValidAccount == null) {
            continue;
        }
        if (isValidAccount) {
            db.addAccount(newAccount);
            isValidAccount = null;
            errorType = null;
            db.close();
        }
    }

    public static void setErrorType(ErrorType pErrorType) {
        errorType = pErrorType;
    }

    public static void setIsValidAccount(boolean isValid) {
        isValidAccount = isValid;
    }
}
