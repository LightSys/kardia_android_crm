package org.lightsys.crmapp;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.lightsys.crmapp.data.Account;
import org.lightsys.crmapp.data.LocalDatabaseHelper;
import org.w3c.dom.Text;

import java.util.ArrayList;


public class LoginActivity extends ActionBarActivity {

    ListView accountsList;
    EditText accountName, accountPassword, serverAddress;
    TextView connectedAccounts;
    Toolbar toolbar;
    //private ArrayList<Account> accounts;
    Cursor accounts;

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

        loadAccounts();


    }

    public void loadAccounts() {
        LocalDatabaseHelper db = new LocalDatabaseHelper(this);

        accounts = db.getAccounts();

        if (accounts.getCount() > 0) {
            connectedAccounts.setText("Connected Accounts:");
        }
        else {
            connectedAccounts.setText("");
        }

        // Logic to display the returned accounts in a ListView. The ListView will need to be added to
        // the XML activity_login, and then a Cursor adapter will need to be used.
        // Looks like a loader might be a better option than a cursor adapter. Or I could go simple
        // and use an ArrayList and forgo the adapter altogether.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
