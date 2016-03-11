package org.lightsys.crmapp;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.data.KardiaProvider;


public class LoginActivity extends AccountAuthenticatorActivity implements AppCompatCallback {

    EditText accountName, accountPassword, serverAddress;
    View loginLayout;
    Toolbar toolbar;
    Button button;
    private AppCompatDelegate delegate;
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountManager = AccountManager.get(this);

        delegate = AppCompatDelegate.create(this, this);

        delegate.onCreate(savedInstanceState);
        delegate.setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.loginToolbar);
        delegate.setSupportActionBar(toolbar);

        accountName = (EditText) findViewById(R.id.loginUsername);
        accountPassword = (EditText) findViewById(R.id.loginPassword);
        serverAddress = (EditText) findViewById(R.id.loginServer);
        button = (Button) findViewById(R.id.loginSubmit);
        loginLayout = findViewById(R.id.loginLayout);

        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAccount(v);
            }
        });

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

        //createAccount();
    }

    private void createAccount() {
        Account account = new Account("Methuselah96", KardiaProvider.accountType);
        if (mAccountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            //ContentResolver.setIsSyncable(account, KardiaProvider.providerAuthority, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, KardiaProvider.providerAuthority, true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.
            ContentResolver.addPeriodicSync(
                    account, KardiaProvider.providerAuthority, new Bundle(), 60 * 60);
        }
    }

    public void addAccount(View v) {
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

        Account newAccount = new Account(addAccountName, KardiaProvider.accountType);
        Bundle userData = new Bundle();
        mAccountManager.addAccountExplicitly(newAccount, addAccountPassword, userData);
        mAccountManager.setUserData(newAccount, "server", addServerAddress);
        Log.d("Login", "hellp?");
        try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        new GetPartnerIdTask().execute(newAccount);
    }

    private void checkAccount(Account account) {
        if (mAccountManager.getUserData(account, "partnerId") != null) {
            Bundle bundle = new Bundle();
            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, KardiaProvider.accountType);
            setAccountAuthenticatorResult(bundle);

            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(main);
            finish();
        } else {
            mAccountManager.removeAccountExplicitly(account);
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

    private class GetPartnerIdTask extends AsyncTask<Account, Void, Account> {
        @Override
        protected Account doInBackground(Account... accounts) {
            //getContentResolver().setSyncAutomatically(accounts[0], KardiaProvider.providerAuthority, true);
            //ContentResolver.setIsSyncable(accounts[0], KardiaProvider.providerAuthority, 1);
            //ContentResolver.setSyncAutomatically(accounts[0], KardiaProvider.providerAuthority, true);
            //ContentResolver.addPeriodicSync(
            //        accounts[0], KardiaProvider.providerAuthority, new Bundle(), 60 * 60);
            Bundle settingsBundle = new Bundle();
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_MANUAL, true);
            settingsBundle.putBoolean(
                    ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            Log.d("serverLogin", mAccountManager.getUserData(accounts[0], "server"));
            ContentResolver.requestSync(accounts[0], KardiaProvider.providerAuthority, settingsBundle);
            Log.d("Login", "yes");
            Log.d("URI", CRMContract.StaffTable.CONTENT_URI.toString());
            try {
                Thread.sleep(1000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            Cursor cursor = getContentResolver().query(
                    CRMContract.StaffTable.CONTENT_URI,
                    new String[] {CRMContract.StaffTable.PARTNER_ID},
                    CRMContract.StaffTable.KARDIA_LOGIN + " = ?",
                    new String[] {accounts[0].name},
                    null
            );
            String partnerId = null;
            if(cursor.moveToFirst()) {
                Log.d("StaffResults", "yes");
                partnerId = cursor.getString(0);
            }
            Log.d("Login", "yes2");
            mAccountManager.setUserData(accounts[0], "partnerId", partnerId);
            return accounts[0];
        }

        @Override
        protected void onPostExecute(Account account) {
            checkAccount(account);
        }
    }
}
