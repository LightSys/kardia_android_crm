package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.app.AlertDialog;
import android.widget.TableRow;
import android.widget.TimePicker;

import org.json.JSONException;
import org.json.JSONObject;
import org.lightsys.crmapp.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Daniel Garcia on 26/07/2017.
 *
 * This activity creates a new Interaction between the user and the current collaborator.
 * Currently it receives information but does not submit yet.
 *
 */

public class NewInteractionActivity extends AppCompatActivity {

    public static final String TYPE_KEY = "EXTRA_TYPE";
    public static final String SPECIFIC_CONTACT_KEY = "EXTRA_SPECIFIC_CONTACT";
    public static final String PARTNER_ID_KEY = "EXTRA_PARTNER_ID";

    public int mTypeId;
    public String mType;
    public String mSpecificContact;
    public String mPartnerId;
    public String subject;
    public String notes;

    //Values from the Interaction Detailed view
    public int mYear, mMonth, mDay, mHour, mMinute, mSecond;
    public TableRow followupDateTable, followupNoteTable;
    public Spinner typeSpinner, specificContactSpinner;
    public Button dateButton, followupDateButton, backButton, submitButton;
    public CheckBox followupCheckBox;
    public EditText subjectText, notesText;

    // Values for getting user account info
    private AccountManager mAccountManager;
    private Account mAccount;

    //Miscellaneous values for date info
    Calendar c = Calendar.getInstance();
    JSONObject jsonTodaysDate;
    JSONObject jsonDate;
    public String mDate;
    public int date;
    public boolean today = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gets user account
        mAccountManager = AccountManager.get(NewInteractionActivity.this);
        final Account[] accounts = mAccountManager.getAccounts();
        if(accounts.length > 0) {
            mAccount = accounts[0];
        }

        //Set up fields from the Interaction Detail view
        setContentView(R.layout.interaction_detail);

        followupDateTable = (TableRow) findViewById(R.id.TableRow7);
        followupNoteTable = (TableRow) findViewById(R.id.TableRow8);
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        specificContactSpinner = (Spinner) findViewById(R.id.specificContactSpinner);
        dateButton = (Button) findViewById(R.id.dateButton);
        followupDateButton = (Button) findViewById(R.id.followupDateButton);
        backButton = (Button) findViewById(R.id.backButton);
        submitButton = (Button) findViewById(R.id.submitButton);
        followupCheckBox = (CheckBox) findViewById(R.id.followupCheckBox);
        subjectText = (EditText) findViewById(R.id.subjectText);
        notesText = (EditText) findViewById(R.id.notesText);

        //Use Calendar to set today's date and time
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mSecond = c.get(Calendar.SECOND);
        mDate = mDay + "-" + mMonth + "-" + mYear;

        //Retrieve from extras any information to be autofilled
        //and set date to today's date
        Bundle extras = getIntent().getExtras();
        mPartnerId = extras.getString(PARTNER_ID_KEY);
        extras.remove(PARTNER_ID_KEY);
        if(extras.size() != 0) {
            mType = extras.getString(TYPE_KEY);
            mSpecificContact = extras.getString(SPECIFIC_CONTACT_KEY);
            dateButton.setText(mDate);
            if(mType.equals("Email Message")) {
                mTypeId = 2;
                typeSpinner.setSelection(0);
            } else if(mType.equals("Phone Call")){
                mTypeId = 1;
                typeSpinner.setSelection(2);
            }
            today = true;
        }

        //Show followup info if followupCheckBox is checked
        followupCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(followupCheckBox.isChecked()){
                    followupDateTable.setVisibility(View.VISIBLE);
                    followupNoteTable.setVisibility(View.VISIBLE);
                } else {
                    followupDateTable.setVisibility(View.INVISIBLE);
                    followupNoteTable.setVisibility(View.INVISIBLE);
                }
            }
        });

        //Switch to determine type of contact
        //(Numbers here are in accordance to the Kardia database)
        switch (typeSpinner.getSelectedItemPosition())
        {
            case 0:
                mTypeId = 2; //Email Message
                break;
            case 1:
                mTypeId = 3; //In-Person Conversation
                break;
            case 2:
                mTypeId = 1; //Phone Call
                break;
            case 3:
                mTypeId = 6; //Sign-Up List
                break;
            case 4:
                mTypeId = 7; //Update
                break;
            default:
                mTypeId = 2; //Default to Email Message
                break;
        }

        //When date button is pressed, open a date dialog and a time dialog
        //(Only date will be displayed once dialog is closed)
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(NewInteractionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        mHour = hour;
                        mMinute = minute;
                        mSecond = 0;
                    }
                }, mHour, mMinute, true).show();
                new DatePickerDialog(NewInteractionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mYear = year;
                        mMonth = month;
                        mDay = day;
                        mDate = mDay + "-" + mMonth + "-" + mYear;
                        dateButton.setText(mDate);
                    }
                }, mYear, mMonth, mDay).show();
            }
        });

        //Same as above for the followup date button
        followupDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(NewInteractionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        mHour = hour;
                        mMinute = minute;
                        mSecond = 0;
                    }
                }, mHour, mMinute, true).show();
                new DatePickerDialog(NewInteractionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mYear = year;
                        mMonth = month;
                        mDay = day;
                        mDate = mDay + "-" + mMonth + "-" + mYear;
                        followupDateButton.setText(mDate);
                    }
                }, mYear, mMonth, mDay).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancelConfirmation();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                try {
                    //TODO: Submit
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        showCancelConfirmation();
    }

    private void showCancelConfirmation()
    {
        new AlertDialog.Builder(NewInteractionActivity.this)
                .setCancelable(false)
                .setTitle("Cancel")
                .setMessage("Exit without saving interaction?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private JSONObject createInteractionJson()
    {
        JSONObject interactionJson = new JSONObject();

        setCurrentDate();
        subject = subjectText.toString();
        notes = notesText.toString();

        try
        {
            jsonDate = new JSONObject();
            jsonDate.put("year", mYear);
            jsonDate.put("month", mMonth);
            jsonDate.put("day", mDay);
            jsonDate.put("hour", mHour);
            jsonDate.put("minute", mMinute);
            jsonDate.put("second", mSecond);

            //Load information into a jsonObject
            //(Unique ID for each Interaction is automatically generated)
            interactionJson.put("p_partner_key", mPartnerId);
            interactionJson.put("e_contact_history_type", mTypeId);
            interactionJson.put("e_whom", mAccount);
            interactionJson.put("e_subject", subject);
            interactionJson.put("e_contact_date", jsonDate);
            interactionJson.put("e_notes", notes);
            interactionJson.put("s_date_created", jsonTodaysDate);
            interactionJson.put("s_created_by", mAccount.name);
            interactionJson.put("s_date_modified", jsonTodaysDate);
            interactionJson.put("s_modified_by", mAccount.name);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return interactionJson;
    }

    private void setCurrentDate()
    {
        //Get current date
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        try
        {
            //Create Date Json object of current datetime
            jsonTodaysDate = new JSONObject();
            jsonTodaysDate.put("month", cal.get(Calendar.MONTH));
            jsonTodaysDate.put("year", cal.get(Calendar.YEAR));
            jsonTodaysDate.put("day", cal.get(Calendar.DAY_OF_MONTH));
            jsonTodaysDate.put("minute", cal.get(Calendar.MINUTE));
            jsonTodaysDate.put("second", cal.get(Calendar.SECOND));
            jsonTodaysDate.put("hour", cal.get(Calendar.HOUR));
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
    }

}
