package org.lightsys.crmapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.models.Engagement;

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
    int currentProgress = 0;
    int secondaryProgress = 1;
    int maxProgress = 3;
    Engagement engagement = new Engagement();
    TextView trackTextView;
    TextView stepTextView;
    TextView descriptionTextView;
    TextView commentsTextView;

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

        trackTextView.setText(engagement.TrackName);
        stepTextView.setText(engagement.StepName);
        descriptionTextView.setText(engagement.Description);
        commentsTextView.setText(engagement.Comments);

        progressBar = (ProgressBar) findViewById(R.id.engagementProgress);
        progressBar.setMax(maxProgress);
        progressBar.setProgress(currentProgress);
        progressBar.setSecondaryProgress(secondaryProgress);
    }

    public void FinishStep(View view)
    {
        currentProgress += 1;
        secondaryProgress += 1;

        progressBar.setProgress(currentProgress);
        progressBar.setSecondaryProgress(secondaryProgress);

        if (currentProgress == maxProgress) {
            //TODO: Possibly Toast or Dialog then Return to List of Engagements
        }
    }
}
