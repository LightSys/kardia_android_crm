package org.lightsys.crmapp.activities;

import android.accounts.AccountManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.fragments.FormFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lightsys.crmapp.fragments.FormFragment.FORM_ID;


/**
 * Created by otter57 on 9/20/17.
 */

public class FormActivity extends AppCompatActivity{

    int formId = -1;
    Fragment currentFrag;
    ActionBar ab;
    public static final String LOG_TAG = FormActivity.class.getName();
    private MenuItem closeButton;
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //lock task for kiosk mode
        startLockTask();

        Intent mIntent = getIntent();

        formId = mIntent.getIntExtra(FORM_ID, 0);

        if (savedInstanceState == null) {

            FormFragment newFrag = new FormFragment();

            Bundle args = new Bundle();
            args.putInt(FORM_ID, formId);
            newFrag.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_profile_input_container, newFrag, "Form");
            transaction.addToBackStack("Form");
            transaction.commit();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.activity_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile_input);
        setSupportActionBar(toolbar);

        currentFrag = getFragmentManager().findFragmentByTag("FORM");

        ab = getSupportActionBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form, menu);

        closeButton = menu.findItem(R.id.action_close);
        closeButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getSupportFragmentManager().popBackStackImmediate();
                return false;
            }
        });

        closeButton.setVisible(false);

        return true;
    }

    public void setCloseButton(boolean onOff){
        closeButton.setVisible(onOff);
    }

    @Override
    public void onResume(){
        startLockTask();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        // nothing to do here
        // … really
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }
}
