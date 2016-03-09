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
import android.view.MotionEvent;
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

        accountName = (EditText) findViewById(R.id.loginUsername);
        accountPassword = (EditText) findViewById(R.id.loginPassword);
        serverAddress = (EditText) findViewById(R.id.loginServer);
        button = (Button) findViewById(R.id.loginSubmit);
        loginLayout = findViewById(R.id.loginLayout);

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
            ((TextView) findViewById(R.id.loginUsernameError)).setText("Enter a username.");
            db.close();
            return;
        }
        else {
            ((TextView) findViewById(R.id.loginUsernameError)).setText("");
        }
        if (addAccountPassword.equals("")) {
            ((TextView) findViewById(R.id.loginPasswordError)).setText("Enter a password.");
            db.close();
            return;
        } else {
            ((TextView) findViewById(R.id.loginPasswordError)).setText("");
        }
        if (addServerAddress.equals("")) {
            ((TextView) findViewById(R.id.loginServerError)).setText("Enter a server address.");
            db.close();
            return;
        }
        else {
            ((TextView) findViewById(R.id.loginServerError)).setText("");
        }
        Account newAccount = new Account(addAccountName, addAccountPassword, addServerAddress);

        //Add an async connection to check if the account is valid with the server.
        (new DataConnection(this, newAccount, PullType.ValidateAccount)).execute("");

        // waiting to hear back from DataConnection
        while (isValidAccount == null) {
            continue;
        }

        // after done
        if (isValidAccount) {
            newAccount.setPartnerId(PartnerId);
            db.addAccount(newAccount);
            MainActivity.setLoggedInAccount(newAccount);
            (new DataConnection(this, newAccount, PullType.GetPartners)).execute("");
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
