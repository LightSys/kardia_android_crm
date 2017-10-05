package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
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
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.data.PatchJson;
import org.lightsys.crmapp.data.PostJson;
import org.lightsys.crmapp.models.Engagement;
import org.lightsys.crmapp.models.EngagementStep;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static org.lightsys.crmapp.fragments.EngagementFragment.COMMENTS;
import static org.lightsys.crmapp.fragments.EngagementFragment.COMPLETON_STATUS;
import static org.lightsys.crmapp.fragments.EngagementFragment.DESCRIPTION;
import static org.lightsys.crmapp.fragments.EngagementFragment.ENGAGEMENT_ID;
import static org.lightsys.crmapp.fragments.EngagementFragment.PARTNER_ID;
import static org.lightsys.crmapp.fragments.EngagementFragment.STEP_NAME;
import static org.lightsys.crmapp.fragments.EngagementFragment.TRACK_NAME;
import static org.lightsys.crmapp.data.LocalDBTables.CollaborateeTable.PARTNER_NAME;

public class EngagementDetailActivity extends AppCompatActivity {
    StateProgressBar progressBar;
    int currentProgress;
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
        engagement.PartnerName = intent.getStringExtra(PARTNER_NAME);

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
            CompleteCurrentStep(this);

            Toast.makeText(this, engagement.PartnerName + " Finished " + engagement.TrackName + " Track", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            CompleteCurrentStep(this);

            currentProgress += 1;

            engagement.StepName = steps[currentProgress - 1].StepName;
            engagement.Description = steps[currentProgress - 1].StepDescription;

            StartNewStep(this);
        }
    }

    public void SaveEngagement(View view) {
        UpdateCurrentStep(this);
    }

    private void UpdateCurrentStep(Context context) {
        String patchStepUrl = Uri.parse(accountManager.getUserData(mAccount, "server"))
                .buildUpon()
                .appendEncodedPath("apps/kardia/api/crm/Partners/" + engagement.PartnerId + "/Tracks")
                .appendEncodedPath(engagement.TrackName + "-" + engagement.EngagementId)
                .appendEncodedPath("History/" + currentProgress)
                .build().toString() + "?cx__mode=rest&cx__res_format=attrs&cx__res_attrs=basic&cx__res_type=element";

        PatchJson patchStep = new PatchJson(context, patchStepUrl, createStepPatchUpdateJson(), mAccount, false);
        patchStep.execute();
    }

    private void CompleteCurrentStep(Context context) {
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
        PatchJson patchStep = new PatchJson(context, patchStepUrl, createStepPatchJson(), mAccount, false);
        try {
            patchStep.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    private void StartNewStep(Context context) {

        // Post Next Step
        String postStepUrl = Uri.parse(accountManager.getUserData(mAccount, "server"))
                .buildUpon()
                .appendEncodedPath("apps/kardia/api/crm/Partners/" + engagement.PartnerId + "/Tracks")
                .appendEncodedPath(engagement.TrackName + "-" + engagement.EngagementId)
                .appendEncodedPath("History")
                .build().toString() + "?cx__mode=rest&cx__res_format=attrs&cx__res_attrs=basic&cx__res_type=collection";

        /*
        * Post New Step
        *
        * This gives us an error on return but still works.
        * Greg knows about this fact.
        * */
        PostJson postStep = new PostJson(context, postStepUrl, createStepPostJson(), mAccount, false);
        postStep.execute();

        setTextFields();
        progressBar.setCurrentStateNumber(numberToStateNumber(currentProgress));
    }

    private JSONObject createStepPatchJson() {
        JSONObject step = new JSONObject();

        try {
            JSONObject currentDate = getCurrentDate();
            step.put("engagement_description", descriptionTextView.getText());
            step.put("engagement_comments", commentsTextView.getText());
            step.put("completion_status_code", "C");
            step.put("completion_date", getCurrentDate());
            step.put("completed_by_partner_id", accountManager.getUserData(mAccount, "partnerId"));
            step.put("date_modified", currentDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return step;
    }

    private JSONObject createStepPatchUpdateJson() {
        JSONObject stepJson = new JSONObject();

        try {
            JSONObject currentDate = getCurrentDate();
            stepJson.put("engagement_description", descriptionTextView.getText());
            stepJson.put("engagement_comments", commentsTextView.getText());
            stepJson.put("date_modified", currentDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return stepJson;
    }

    private JSONObject createStepPostJson() {
        JSONObject stepJson = new JSONObject();

        try {
            JSONObject currentDate = getCurrentDate();
            stepJson.put("p_partner_key", engagement.PartnerId);
            stepJson.put("e_engagement_id", Integer.parseInt(engagement.EngagementId));
            stepJson.put("e_track_id", trackId);
            stepJson.put("e_step_id", currentProgress);
            stepJson.put("e_is_archived", 0);
            stepJson.put("e_completion_status", "I");
            stepJson.put("e_desc", descriptionTextView.getText());
            stepJson.put("e_comments", null);
            stepJson.put("e_start_date", currentDate);
            stepJson.put("e_started_by", accountManager.getUserData(mAccount, "partnerId"));
            stepJson.put("s_date_created", currentDate);
            stepJson.put("s_date_modified", currentDate);
            stepJson.put("s_created_by", mAccount.name);
            stepJson.put("s_modified_by", mAccount.name);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return stepJson;
    }

    private JSONObject getCurrentDate() {
        //Get current date
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        JSONObject dateJson = new JSONObject();

        try {
            dateJson.put("month", cal.get(Calendar.MONTH));
            dateJson.put("year", cal.get(Calendar.YEAR));
            dateJson.put("day", cal.get(Calendar.DAY_OF_MONTH));
            dateJson.put("minute", cal.get(Calendar.MINUTE));
            dateJson.put("second", cal.get(Calendar.SECOND));
            dateJson.put("hour", cal.get(Calendar.HOUR));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return dateJson;
    }

    private class GetStepsforTrack extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Cursor cursor = getContentResolver().query(
                    LocalDBTables.EngagementStepTable.CONTENT_URI,
                    new String[]{LocalDBTables.EngagementStepTable.STEP_NAME,
                            LocalDBTables.EngagementStepTable.STEP_DESCRIPTION,
                            LocalDBTables.EngagementStepTable.STEP_SEQUENCE},
                    LocalDBTables.EngagementStepTable.TRACK_NAME + " = ?",
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

            progressBar.setMaxStateNumber(numberToStateNumber(maxProgress));
            progressBar.setCurrentStateNumber(numberToStateNumber(currentProgress));
        }
    }

    private class GetTrackId extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Cursor cursor = getContentResolver().query(
                    LocalDBTables.EngagementTrackTable.CONTENT_URI,
                    new String[]{
                            LocalDBTables.EngagementTrackTable.TRACK_ID,
                            LocalDBTables.EngagementTrackTable.TRACK_NAME
                    },
                    LocalDBTables.EngagementTrackTable.TRACK_NAME + " = ?",
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
