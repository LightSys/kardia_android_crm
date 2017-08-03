package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.app.AlertDialog;
import android.widget.TableRow;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.data.NotifyAlarmReceiver;
import org.lightsys.crmapp.data.PostJson;
import org.lightsys.crmapp.data.Notification;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Daniel Garcia on 26/07/2017.
 *
 * This activity creates a new Interaction between the user and the current collaborator.
 * When coming to the Interaction from a third-party contact (Email/Phone Call),
 * date and contact type are automtically created.
 *
 */

public class NewInteractionActivity extends AppCompatActivity {

    public static final String TYPE_KEY = "EXTRA_TYPE";
    public static final String SPECIFIC_CONTACT_KEY = "EXTRA_SPECIFIC_CONTACT";
    public static final String PARTNER_ID_KEY = "EXTRA_PARTNER_ID";
    public static final String NAME_KEY = "EXTRA_NAME";

    public int mTypeId;
    public String mType;
    public String mSpecificContact;
    public String mPartnerId;
    public String mName;
    public String subject;
    public String notes;

    //Values from the Interaction Detailed view
    public int mYear, mMonth, mDay, mHour, mMinute, mSecond;
    public int fYear, fMonth, fDay, fHour, fMinute, fSecond;
    public TableRow followupDateTable, followupNoteTable;
    public Spinner typeSpinner, specificContactSpinner;
    public Button dateButton, followupDateButton, backButton, submitButton;
    public CheckBox followupCheckBox;
    public EditText subjectText, notesText, followupNoteText;
    public String fNotes;

    // Values for getting user account info
    private AccountManager mAccountManager;
    private Account mAccount;

    //Miscellaneous values for date info
    Calendar c = Calendar.getInstance();
    JSONObject jsonToday;
    JSONObject jsonDate;
    public String mDate;
    public String fDate;
    public int date;
    public long todayInMillis;
    public boolean today = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
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
        followupNoteText = (EditText) findViewById(R.id.followupNoteText);

        //Use Calendar to set today's date and time
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mSecond = c.get(Calendar.SECOND);
        mDate = mDay + "-" + (mMonth + 1) + "-" + mYear;

        //Set followup date by default to today's date
        fYear = mYear;
        fMonth = mMonth;
        fDay = mDay;
        fHour = mHour;
        fMinute = mMinute;
        fSecond = mSecond;
        fDate = mDate;

        //Retrieve from extras any information to be autofilled
        //and set date to today's date
        Bundle extras = getIntent().getExtras();
        mPartnerId = extras.getString(PARTNER_ID_KEY);
        mName = extras.getString(NAME_KEY);
        extras.remove(PARTNER_ID_KEY);
        extras.remove(NAME_KEY);
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
                        mDate = mDay + "-" + (mMonth + 1) + "-" + mYear;
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
                        fHour = hour;
                        fMinute = minute;
                        fSecond = 0;
                    }
                }, fHour, fMinute, true).show();
                new DatePickerDialog(NewInteractionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        fYear = year;
                        fMonth = month;
                        fDay = day;
                        fDate = fDay + "-" + (fMonth + 1) + "-" + fYear;
                        followupDateButton.setText(fDate);
                    }
                }, fYear, fMonth, fDay).show();
            }
        });

        //Show followup info if followupCheckBox is checked
        followupCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(followupCheckBox.isChecked()){
                    followupDateTable.setVisibility(View.VISIBLE);
                    followupNoteTable.setVisibility(View.VISIBLE);
                    followupNoteText.setText("Followup from " + typeSpinner.getSelectedItem() +
                    " on " + mDate);
                    fNotes = followupNoteText.getText().toString();
                } else {
                    followupDateTable.setVisibility(View.INVISIBLE);
                    followupNoteTable.setVisibility(View.INVISIBLE);
                }
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
                submitInteractionJson();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
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

        subject = subjectText.getText().toString();
        notes = notesText.getText().toString();
        fNotes = followupNoteText.getText().toString();

        //Get current date
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        try
        {
            //Create Json object with current datetime
            jsonToday = new JSONObject();
            jsonToday.put("year", cal.get(Calendar.YEAR));
            jsonToday.put("month", cal.get(Calendar.MONTH) + 1); //Calendar returns a 0-based month, added 1 to match Kardia
            jsonToday.put("day", cal.get(Calendar.DAY_OF_MONTH));
            jsonToday.put("hour", cal.get(Calendar.HOUR_OF_DAY));
            jsonToday.put("minute", cal.get(Calendar.MINUTE));
            jsonToday.put("second", cal.get(Calendar.SECOND));
            todayInMillis = cal.getTimeInMillis();

            //If date is auto-set, make jsonDate today's date
            if(today){
                jsonDate = jsonToday;
            } else {
                jsonDate = new JSONObject();
                jsonDate.put("year", mYear);
                jsonDate.put("month", mMonth + 1); //Calendar returns a 0-based month, added 1 to match Kardia
                jsonDate.put("day", mDay);
                jsonDate.put("hour", mHour);
                jsonDate.put("minute", mMinute);
                jsonDate.put("second", mSecond);
            }

            //Load information into a jsonObject
            //(Unique ID for each Interaction is automatically generated)
            interactionJson.put("p_partner_key", mPartnerId);
            interactionJson.put("e_contact_history_type", mTypeId);
            interactionJson.put("e_whom", mAccountManager.getUserData(mAccount, "partnerId"));
            interactionJson.put("e_subject", subject);
            interactionJson.put("e_contact_date", jsonDate);
            interactionJson.put("e_notes", notes);
            interactionJson.put("s_date_created", jsonToday);
            interactionJson.put("s_created_by", mAccount.name);
            interactionJson.put("s_date_modified", jsonToday);
            interactionJson.put("s_modified_by", mAccount.name);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return interactionJson;
    }

    private void submitInteractionJson()
    {
        try {

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

            //Create URL for posting interaction
            String postUrl = mAccountManager.getUserData(mAccount, "server")
                    + "/apps/kardia/api/crm/Partners/"
                    + mPartnerId
                    + "/ContactHistory?cx__mode=rest&cx__res_format=attrs&cx__res_attrs=basic&cx__res_type=collection";

            //Create new Async Task and post interaction
            final AsyncTask<String, Void, String> uploadJson;
            uploadJson = new PostJson(NewInteractionActivity.this, postUrl, createInteractionJson(), mAccount, true);


            if(followupCheckBox.isChecked()) {
                //Only allow notification to be set if date is set and is not in the past
                if(!followupDateButton.getText().toString().equals(getString(R.string.choose_date)) &&
                        (setTimeInMillis() > Calendar.getInstance().getTimeInMillis())) {

                    // Ask user if notifications should be set as they can not be edited later
                    new AlertDialog.Builder(NewInteractionActivity.this)
                            .setCancelable(false)
                            .setTitle("Set Notifications")
                            .setMessage("Set notification? You will not be able to edit this later.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new NotificationCreator().execute("");
                                    uploadJson.execute();
                                    finish();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    Toast.makeText(NewInteractionActivity.this, "Please choose a valid followup date", Toast.LENGTH_LONG).show();
                }

            } else {
                uploadJson.execute();
                finish();
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    private long setTimeInMillis()
    {
        long timeInMillis;
        Calendar cal;
        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, fYear);
        cal.set(Calendar.MONTH, fMonth);
        cal.set(Calendar.DAY_OF_MONTH, fDay);
        cal.set(Calendar.HOUR_OF_DAY, fHour);
        cal.set(Calendar.MINUTE, fMinute);
        cal.set(Calendar.SECOND, fSecond);
        timeInMillis = cal.getTimeInMillis();

        return timeInMillis;
    }

    // Class uses an asynchronous thread to set alarms
    private class NotificationCreator extends AsyncTask<String, Void, String>
    {

        public NotificationCreator() {}

        @Override
        protected String doInBackground(String... params)
        {
            remind(mPartnerId, fNotes);
            return null;
        }

        /**
         * Sets alarms at specified times until the endDate for a specific request
         * @param name, name for notification to display
         * @param note, note for notification to display
         */
        private void remind (String name, String note)
        {
            Notification notification;
            int notificationID = 0;
            String time;
            String todaysDate;
            Intent alarmIntent;
            PendingIntent pendingIntent;

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            //Set year, month, and day of calendar for notification date
            Calendar c = Calendar.getInstance();
            c.set(fYear, fMonth, fDay);

            //Alarm times will be set and stored in millisecond form
            long alarmTime;

            //Setting alarms requires sdk version 19 or higher
            if (Build.VERSION.SDK_INT >= 19) {
                alarmTime = setTimeInMillis();
                time = Long.toString(alarmTime);

                //If alarm time is not in the past, set alarm for notification
                if (alarmTime > Calendar.getInstance().getTimeInMillis()) {

                    //Set notificationID by getting last ID and incrementing
                    Cursor cursor = NewInteractionActivity.this.getContentResolver().query(
                            CRMContract.NotificationsTable.CONTENT_URI,
                            new String[] {CRMContract.NotificationsTable.NOTIFICATION_ID},
                            null, null, null);

                    while (cursor.moveToNext()){
                        if (cursor.isLast()){
                            notificationID = Integer.parseInt(cursor.getString(0)) + 1;
                        }
                    }

                    cursor.close();

                    alarmIntent = new Intent(NewInteractionActivity.this, NotifyAlarmReceiver.class);
                    alarmIntent.putExtra("notificationId", Integer.toString(notificationID));
                    alarmIntent.putExtra("name", mName);
                    alarmIntent.putExtra("partnerID", name);
                    alarmIntent.putExtra("note", note);

                    pendingIntent = PendingIntent.getBroadcast(NewInteractionActivity.this,
                            notificationID, alarmIntent, 0);

                    //Create notification
                    notification = new Notification();
                    notification.setId(notificationID);
                    notification.setNotificationTime(alarmTime);
                    notification.setPartnerID(name);
                    notification.setNote(note);

                    //Convert todayInMillis to a String
                    //This will serve as a unique ID to associate each notification with one Interaction
                    todaysDate = Long.toString(todayInMillis);

                    //Store notifications in local database
                    ContentValues values = new ContentValues();
                    values.put(CRMContract.NotificationsTable.NOTIFICATION_ID, notificationID);
                    values.put(CRMContract.NotificationsTable.TIME, time);
                    values.put(CRMContract.NotificationsTable.PARTNER_ID, name);
                    values.put(CRMContract.NotificationsTable.NOTES, note);
                    values.put(CRMContract.NotificationsTable.DATE_CREATED, todaysDate);
                    getContentResolver().insert(CRMContract.NotificationsTable.CONTENT_URI, values);

                    //Set alarm
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    Log.w("tag", "Alarm set for: " + format.format(alarmTime) + ", ID:" +
                            Integer.toString(notificationID) + ", Name:" + name);
                }
            } else {
                Toast.makeText(NewInteractionActivity.this, "Sorry, but your device " +
                                "does not have the proper update to support this feature",
                        Toast.LENGTH_LONG).show();
            }


        }
    }
}
