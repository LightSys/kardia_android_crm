package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kofigyan.stateprogressbar.StateProgressBar;

import org.json.JSONException;
import org.json.JSONObject;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.data.PatchJson;
import org.lightsys.crmapp.data.PostJson;
import org.lightsys.crmapp.models.Engagement;
import org.lightsys.crmapp.models.EngagementStep;
import org.lightsys.crmapp.models.EngagementTrack;

import java.util.Calendar;
import java.util.Date;

import static org.lightsys.crmapp.activities.EngagementActivity.COMMENTS;
import static org.lightsys.crmapp.activities.EngagementActivity.COMPLETON_STATUS;
import static org.lightsys.crmapp.activities.EngagementActivity.DESCRIPTION;
import static org.lightsys.crmapp.activities.EngagementActivity.ENGAGEMENT_ID;
import static org.lightsys.crmapp.activities.EngagementActivity.PARTNER_ID;
import static org.lightsys.crmapp.activities.EngagementActivity.STEP_NAME;
import static org.lightsys.crmapp.activities.EngagementActivity.TRACK_NAME;

public class EngagementDetailActivity extends AppCompatActivity {
    StateProgressBar progressBar;
    int currentProgress;
    int secondaryProgress;
    int maxProgress;
    Engagement engagement = new Engagement();
    TextView trackStepTextView;
    TextView descriptionTextView;
    TextView commentsTextView;
    EngagementStep[] steps;
    private AccountManager accountManager;
    private Account mAccount;
    private int trackId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engagement_detail);

        Intent intent = getIntent();
        engagement.PartnerId = intent.getStringExtra(PARTNER_ID);
        engagement.EngagementId = intent.getStringExtra(ENGAGEMENT_ID);
        engagement.Description = intent.getStringExtra(DESCRIPTION);
        engagement.TrackName = intent.getStringExtra(TRACK_NAME);
        engagement.StepName = intent.getStringExtra(STEP_NAME);
        engagement.Comments = intent.getStringExtra(COMMENTS);
        engagement.CompletionStatus = intent.getStringExtra(COMPLETON_STATUS);

        trackStepTextView = (TextView) findViewById(R.id.trackStepText);
        descriptionTextView = (TextView) findViewById(R.id.descriptionText);
        commentsTextView = (TextView) findViewById(R.id.commentsText);
        progressBar = (StateProgressBar) findViewById(R.id.engagementProgress);

        new GetStepsforTrack().execute();
        new GetTrackId().execute();

        setTextFields();

        accountManager = AccountManager.get(this);
        final Account[] accounts = accountManager.getAccounts();
        if (accounts.length > 0) {
            mAccount = accounts[0];
        }
    }

    private void setTextFields() {
        setTrackStepText(engagement.StepName);
        descriptionTextView.setText(engagement.Description);
        commentsTextView.setText(engagement.Comments);
    }

    private void setTrackStepText(String stepName) {
        trackStepTextView.setText(engagement.TrackName + ": " + stepName);
    }

    public void FinishStep(View view) {
        if (currentProgress >= maxProgress) {
            //TODO: Possibly Toast or Dialog then Return to List of Engagements
            Toast.makeText(this, "Finished Track", Toast.LENGTH_SHORT).show();
            finish();
        } else {

            String patchStepUrl = Uri.parse(accountManager.getUserData(mAccount, "server"))
                    .buildUpon()
                    .appendEncodedPath("apps/kardia/api/crm/Partners/" + engagement.PartnerId + "/Tracks")
                    .appendEncodedPath(engagement.TrackName + "-" + engagement.EngagementId)
                    .appendEncodedPath("History/" + currentProgress)
                    .build().toString() + "?cx__mode=rest&cx__res_format=attrs&cx__res_attrs=basic&cx__res_type=element";

            /*
            * The Rest API this relies on will change eventually - Greg Beeley
            *
            * Patch current step
            * */
            PatchJson patchStep = new PatchJson(this, patchStepUrl, createStepPatchJson(), mAccount, false);
            patchStep.execute();

            currentProgress += 1;
            secondaryProgress += 1;

            progressBar.setCurrentStateNumber(numberToStateNumber(currentProgress));

            engagement.StepName = steps[currentProgress - 1].StepName;
            engagement.Description = steps[currentProgress - 1].StepDescription;

            String postStepUrl = Uri.parse(accountManager.getUserData(mAccount, "server"))
                    .buildUpon()
                    .appendEncodedPath("apps/kardia/api/crm/Partners/" + engagement.PartnerId + "/Tracks")
                    .appendEncodedPath(engagement.TrackName + "-" + engagement.EngagementId)
                    .appendEncodedPath("History")
                    .build().toString() + "?cx__mode=rest&cx__res_format=attrs&cx__res_attrs=basic&cx__res_type=collection";

            //Post new step.
            PostJson postStep = new PostJson(this, postStepUrl, createStepPostJson(), mAccount, false);
            postStep.execute();


            setTextFields();
        }
    }

    private JSONObject createStepPatchJson() {
        JSONObject step = new JSONObject();

        try {
            JSONObject currentDate = getCurrentDate();
            step.put("e_desc", descriptionTextView.getText());
            step.put("e_comments", commentsTextView.getText());
            step.put("e_completion_status", "C");
//            step.put("completion_date", getCurrentDate());
//            step.put("completed_by_partner_id", accountManager.getUserData(mAccount, "partnerId"));
//            step.put("completed_by_partner_ref", "/apps/kardia/api/partner/Partners/" + accountManager.getUserData(mAccount, "partnerId"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return step;
    }

    private JSONObject createStepPostJson() {
        JSONObject step = new JSONObject();

        try {
            JSONObject currentDate = getCurrentDate();
            step.put("p_partner_key", engagement.PartnerId);
            step.put("e_engagement_id", Integer.parseInt(engagement.EngagementId));
            step.put("e_track_id", trackId);
            step.put("e_step_id", currentProgress);
            step.put("e_is_archived", 0);
            step.put("e_completion_status", "I");
            step.put("e_desc", descriptionTextView.getText());
            step.put("e_comments", null);
            step.put("e_start_date", currentDate);
            step.put("e_started_by", accountManager.getUserData(mAccount, "partnerId"));
            step.put("s_date_created", currentDate);
            step.put("s_date_modified", currentDate);
            step.put("s_created_by", mAccount.name);
            step.put("s_modified_by", mAccount.name);

        } catch (JSONException e) {
            e.printStackTrace();
        }
///apps/kardia/api/crm/Partners/100048/Tracks/Intern-5/History/100048|5|3
        return step;
    }

    private JSONObject getCurrentDate() {
        //Get current date
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        JSONObject jsonDate = new JSONObject();

        try {
            jsonDate.put("month", cal.get(Calendar.MONTH));
            jsonDate.put("year", cal.get(Calendar.YEAR));
            jsonDate.put("day", cal.get(Calendar.DAY_OF_MONTH));
            jsonDate.put("minute", cal.get(Calendar.MINUTE));
            jsonDate.put("second", cal.get(Calendar.SECOND));
            jsonDate.put("hour", cal.get(Calendar.HOUR));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return jsonDate;
    }

    public class GetStepsforTrack extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Cursor cursor = getContentResolver().query(
                    CRMContract.EngagementStepTable.CONTENT_URI,
                    new String[]{CRMContract.EngagementStepTable.STEP_NAME,
                            CRMContract.EngagementStepTable.STEP_DESCRIPTION,
                            CRMContract.EngagementStepTable.STEP_SEQUENCE},
                    CRMContract.EngagementStepTable.TRACK_NAME + " = ?",
                    new String[]{engagement.TrackName},
                    null
            );

            if (cursor != null) {
                steps = new EngagementStep[cursor.getCount()];

                while (cursor.moveToNext()) {
                    EngagementStep step = new EngagementStep();
                    step.TrackName = engagement.TrackName;
                    step.StepName = cursor.getString(0);
                    step.StepDescription = cursor.getString(1);
                    step.StepSequence = cursor.getInt(2);
                    steps[step.StepSequence - 1] = step;
                }

                cursor.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            for (EngagementStep step : steps) {
                if (step.StepName.equals(engagement.StepName)) {
                    currentProgress = step.StepSequence;
                    break;
                }
            }

            maxProgress = steps.length;
            secondaryProgress = currentProgress + 1;

            progressBar.setMaxStateNumber(numberToStateNumber(maxProgress));
            progressBar.setCurrentStateNumber(numberToStateNumber(currentProgress));
        }
    }

    private class GetTrackId extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Cursor cursor = getContentResolver().query(
                    CRMContract.EngagementTrackTable.CONTENT_URI,
                    new String[]{
                            CRMContract.EngagementTrackTable.TRACK_ID,
                            CRMContract.EngagementTrackTable.TRACK_NAME
                    },
                    CRMContract.EngagementTrackTable.TRACK_NAME + " = ?",
                    new String[]{engagement.TrackName},
                    null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    trackId = cursor.getInt(0);
                }

                cursor.close();
            }
            return null;
        }
    }

    private StateProgressBar.StateNumber numberToStateNumber(int i) {
        switch (i) {
            case 1:
                return StateProgressBar.StateNumber.ONE;
            case 2:
                return StateProgressBar.StateNumber.TWO;
            case 3:
                return StateProgressBar.StateNumber.THREE;
            case 4:
                return StateProgressBar.StateNumber.FOUR;
            case 5:
                return StateProgressBar.StateNumber.FIVE;
            default:
                return StateProgressBar.StateNumber.ONE;
        }
    }
}
