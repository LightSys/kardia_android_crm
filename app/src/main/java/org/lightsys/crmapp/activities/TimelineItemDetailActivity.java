package org.lightsys.crmapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.lightsys.crmapp.R;


/**
 * Created by ca2br on 7/18/16.
 *
 * displays detailed information about a timelitem
 *
 * Modified by Daniel Garcia on 03/Aug/2017
 *
 * added functionality to display a Followup, if one has been set
 */
public class TimelineItemDetailActivity extends AppCompatActivity {


    private String type;
    private String name;
    private String subject;
    private String date;
    private String text;
    private String followup;

    public TimelineItemDetailActivity () {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        type = getIntent().getStringExtra("type");
        name = getIntent().getStringExtra("name");
        subject = getIntent().getStringExtra("subject");
        date = getIntent().getStringExtra("date");
        text = getIntent().getStringExtra("text");
        followup = getIntent().getStringExtra("followup");

        setContentView(R.layout.timeline_item_detail);

        TextView typeView = (TextView) findViewById(R.id.type);
        TextView nameView = (TextView) findViewById(R.id.name);
        TextView subjectView = (TextView) findViewById(R.id.subject);
        TextView dateView = (TextView) findViewById(R.id.date_posted);
        TextView textView = (TextView) findViewById(R.id.content);
        TextView followupView = (TextView) findViewById(R.id.followup);
        CardView followupCardView = (CardView) findViewById(R.id.cardview_followup);
        Button button = (Button) findViewById(R.id.backButton);

        typeView.setText(type);
        nameView.setText("Name: " + name);
        subjectView.setText("Subject: " + subject);
        dateView.setText("Date: " + date);
        textView.setText(text);

        //If a followup has been set,
        if(!followup.equals("")){
            followupCardView.setVisibility(View.VISIBLE);
            followupView.setText("Followup by: " + followup);
        } else {
            followupCardView.setVisibility(View.GONE);
        }

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
