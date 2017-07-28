package org.lightsys.crmapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.models.Engagement;
import org.lightsys.crmapp.models.EngagementStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lightsys.crmapp.activities.EngagementActivity.COMMENTS;
import static org.lightsys.crmapp.activities.EngagementActivity.COMPLETON_STATUS;
import static org.lightsys.crmapp.activities.EngagementActivity.DESCRIPTION;
import static org.lightsys.crmapp.activities.EngagementActivity.ENGAGEMENT_ID;
import static org.lightsys.crmapp.activities.EngagementActivity.PARTNER_ID;
import static org.lightsys.crmapp.activities.EngagementActivity.STEP_NAME;
import static org.lightsys.crmapp.activities.EngagementActivity.TRACK_NAME;

public class EngagementDetailActivity extends AppCompatActivity
{
    ProgressBar progressBar;
    int currentProgress;
    int secondaryProgress;
    int maxProgress;
    Engagement engagement = new Engagement();
    TextView trackTextView;
    TextView stepTextView;
    TextView descriptionTextView;
    TextView commentsTextView;
    List<EngagementStep> steps = new ArrayList<>();
    int stepSequence;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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

        trackTextView = (TextView) findViewById(R.id.trackText);
        stepTextView = (TextView) findViewById(R.id.stepText);
        descriptionTextView = (TextView) findViewById(R.id.descriptionText);
        commentsTextView = (TextView) findViewById(R.id.commentsText);
        progressBar = (ProgressBar) findViewById(R.id.engagementProgress);

        new GetStepsforTrack().execute();

        trackTextView.setText(engagement.TrackName);
        stepTextView.setText(engagement.StepName);
        descriptionTextView.setText(engagement.Description);
        commentsTextView.setText(engagement.Comments);
    }

    public void FinishStep(View view)
    {
        currentProgress += 1;
        secondaryProgress += 1;
        stepSequence = currentProgress;

        progressBar.setProgress(currentProgress);
        progressBar.setSecondaryProgress(secondaryProgress);

        if (currentProgress == maxProgress) {
            //TODO: Possibly Toast or Dialog then Return to List of Engagements
        }
        else {

        }
    }

    public class GetStepsforTrack extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            Cursor cursor = getContentResolver().query(
                    CRMContract.EngagementStepTable.CONTENT_URI,
                    new String[]{CRMContract.EngagementStepTable.STEP_NAME,
                            CRMContract.EngagementStepTable.STEP_DESCRIPTION,
                            CRMContract.EngagementStepTable.STEP_SEQUENCE},
                    CRMContract.EngagementStepTable.TRACK_NAME + " = ?",
                    new String[]{ engagement.TrackName },
                    null
            );

            while (cursor.moveToNext())
            {
                EngagementStep step = new EngagementStep();
                step.TrackName = engagement.TrackName;
                step.StepName = cursor.getString(0);
                step.StepDescription = cursor.getString(1);
                step.StepSequence = cursor.getInt(2);
                steps.add(step);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            for (int i = 0; i < steps.size(); i++) {
                if (steps.get(i).StepName.equals(engagement.StepName)) {
                    stepSequence = steps.get(i).StepSequence;
                    break;
                }
            }

            maxProgress = steps.size();
            currentProgress = stepSequence;
            secondaryProgress = currentProgress + 1;

            progressBar.setMax(maxProgress);
            progressBar.setProgress(currentProgress);
            progressBar.setSecondaryProgress(secondaryProgress);
        }
    }
}
