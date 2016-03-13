package org.lightsys.crmapp;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.data.KardiaFetcher;

import java.util.HashMap;


public class LoginActivity extends AccountAuthenticatorActivity implements AppCompatCallback {

    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountManager = AccountManager.get(this);

        AppCompatDelegate delegate = AppCompatDelegate.create(this, this);

        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.loginToolbar);
        delegate.setSupportActionBar(toolbar);

        Button button = (Button) findViewById(R.id.loginSubmit);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAccount(v);
            }
        });

        EditText serverAddress = (EditText) findViewById(R.id.loginServer);
        serverAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    addAccount(v);
                    return true;
                }

                return false;
            }
        });
    }

    public void addAccount(View v) {
        EditText accountName = (EditText) findViewById(R.id.loginUsername);
        EditText accountPassword = (EditText) findViewById(R.id.loginPassword);
        EditText serverAddress = (EditText) findViewById(R.id.loginServer);

        String username = accountName.getText().toString();
        String password = accountPassword.getText().toString();
        String server = serverAddress.getText().toString();

        if (username.equals("")) {
            ((TextView) findViewById(R.id.loginUsernameError)).setText("Enter a username.");
            return;
        }
        else {
            ((TextView) findViewById(R.id.loginUsernameError)).setText("");
        }
        if (password.equals("")) {
            ((TextView) findViewById(R.id.loginPasswordError)).setText("Enter a password.");
            return;
        } else {
            ((TextView) findViewById(R.id.loginPasswordError)).setText("");
        }
        if (server.equals("")) {
            ((TextView) findViewById(R.id.loginServerError)).setText("Enter a server address.");
            return;
        }
        else {
            ((TextView) findViewById(R.id.loginServerError)).setText("");
        }

        new GetPartnerIdTask().execute(username, password, server);
    }

    private void processResult(boolean accountAdded) {
        if (accountAdded) {
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(main);
            finish();
        } else {
            View loginLayout = findViewById(R.id.loginLayout);
            Snackbar.make(loginLayout, "Connection to server failed", Snackbar.LENGTH_LONG).show();
        }
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

    private class GetPartnerIdTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... accountData) {
            String username = accountData[0];
            String password = accountData[1];
            String server = accountData[2];
            KardiaFetcher fetcher = new KardiaFetcher(LoginActivity.this);
            HashMap<String, String> staff = fetcher.getStaff(username, password, server);
            if(staff.containsKey(username)) {
                Account account = new Account(username, CRMContract.ACCOUNT_TYPE);
                mAccountManager.addAccountExplicitly(account, password, null);
                mAccountManager.setUserData(account, "server", server);
                mAccountManager.setUserData(account, "partnerId", staff.get(username));

                //TODO Request immediate sync

                getContentResolver().setSyncAutomatically(account, CRMContract.PROVIDER_AUTHORITY, true);

                Bundle bundle = new Bundle();
                bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, CRMContract.ACCOUNT_TYPE);
                setAccountAuthenticatorResult(bundle);

                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean accountAdded) {
            processResult(accountAdded);
        }
    }
}
