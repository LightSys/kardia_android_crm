package org.lightsys.crmapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.data.Staff;
import org.lightsys.crmapp.data.User;
import org.lightsys.crmapp.data.UserLab;


public class LoginActivity extends ActionBarActivity {

    EditText accountName, accountPassword, serverAddress;
    View loginLayout;
    Toolbar toolbar;
    User account;
    Button button;

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
        UserLab userLab = UserLab.get(this);
        account = userLab.getUser();

        String addAccountName = accountName.getText().toString();
        String addAccountPassword = accountPassword.getText().toString();
        String addServerAddress = serverAddress.getText().toString();

        if (addAccountName.equals("")) {
            ((TextView) findViewById(R.id.loginUsernameError)).setText("Enter a username.");
            return;
        }
        else {
            ((TextView) findViewById(R.id.loginUsernameError)).setText("");
        }
        if (addAccountPassword.equals("")) {
            ((TextView) findViewById(R.id.loginPasswordError)).setText("Enter a password.");
            return;
        } else {
            ((TextView) findViewById(R.id.loginPasswordError)).setText("");
        }
        if (addServerAddress.equals("")) {
            ((TextView) findViewById(R.id.loginServerError)).setText("Enter a server address.");
            return;
        }
        else {
            ((TextView) findViewById(R.id.loginServerError)).setText("");
        }
        User newAccount = new User();
        newAccount.setUsername(addAccountName);
        newAccount.setPassword(addAccountPassword);
        newAccount.setServer(addServerAddress);
        Log.d("Login", "hellp?");
        new GetPartnerIdTask().execute(newAccount);
    }

    private void checkAccount(User user) {
        if (user.getStaff() != null) {
            UserLab.get(this).addUser(user);
        } else {
            Snackbar.make(loginLayout, "Connection to server failed", Snackbar.LENGTH_LONG).show();
            return;
        }
        Intent main = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }

    private class GetPartnerIdTask extends AsyncTask<User, Void, User> {
        @Override
        protected User doInBackground(User... params) {
            Log.d("Login", "yes");
            String partnerId = new KardiaFetcher().getPartnerId(params[0]);
            Log.d("Login", "yes2");
            params[0].setStaff(new Staff(partnerId, params[0].getUsername()));
            return params[0];
        }

        @Override
        protected void onPostExecute(User user) {
            checkAccount(user);
        }
    }
}
