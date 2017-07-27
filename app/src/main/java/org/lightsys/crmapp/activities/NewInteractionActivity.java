package org.lightsys.crmapp.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.app.AlertDialog;
import android.widget.TableRow;

import org.lightsys.crmapp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

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
    public static final String DATE_KEY = "EXTRA_DATE";

    public String mType;
    public String mSpecificContact;
    public String mDate;

    public int mDay, mMonth, mYear;
    public TableRow followupDateTable, followupNoteTable;
    public Spinner typeSpinner, specificContactSpinner;
    public int date;
    public Button dateButton, backButton, submitButton;
    public CheckBox followupCheckBox;

    Calendar c = Calendar.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.interaction_detail);

        followupDateTable = (TableRow) findViewById(R.id.TableRow7);
        followupNoteTable = (TableRow) findViewById(R.id.TableRow8);
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        specificContactSpinner = (Spinner) findViewById(R.id.specificContactSpinner);
        dateButton = (Button) findViewById(R.id.dateButton);
        backButton = (Button) findViewById(R.id.backButton);
        submitButton = (Button) findViewById(R.id.submitButton);
        followupCheckBox = (CheckBox) findViewById(R.id.followupCheckBox);

        //Set Calendar automatically to today's date
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mMonth = c.get(Calendar.MONTH);
        mYear = c.get(Calendar.YEAR);
        mDate = mDay + "-" + mMonth + "-" + mYear;

        //Retrieve from extras any information to be autofilled
        //and set date to today's date
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mType = extras.getString(TYPE_KEY);
            mSpecificContact = extras.getString(SPECIFIC_CONTACT_KEY);
            dateButton.setText(mDate);
        }

        followupCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show followup info if box is checked
                if(followupCheckBox.isChecked()){
                    followupDateTable.setVisibility(View.VISIBLE);
                    followupNoteTable.setVisibility(View.VISIBLE);
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

                try {

                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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


    }

    @Override
    public void onBackPressed() {
        showCancelConfirmation();
    }

    private void showCancelConfirmation() {
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



}
