package org.lightsys.crmapp.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.lightsys.crmapp.R;

import java.io.File;


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


    public TimelineItemDetailActivity () {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String type = getIntent().getStringExtra("type");
        String name = getIntent().getStringExtra("name");
        String date = getIntent().getStringExtra("date");
        String subject = getIntent().getStringExtra("subject");
        String text = getIntent().getStringExtra("text");
        String followup = getIntent().getStringExtra("followup");
        String profilePictureFilename = getIntent().getStringExtra("profilePictureFilename");

        setContentView(R.layout.timeline_item_detail);

        TextView typeView = (TextView) findViewById(R.id.type);
        TextView nameView = (TextView) findViewById(R.id.name);
        TextView dateView = (TextView) findViewById(R.id.date_posted);
        TextView subjectView = (TextView) findViewById(R.id.subject);
        TextView textView = (TextView) findViewById(R.id.content);
        TextView followupView = (TextView) findViewById(R.id.followup);
        CardView followupCardView = (CardView) findViewById(R.id.cardview_followup);
        Button button = (Button) findViewById(R.id.backButton);

        typeView.setText(type);
        nameView.setText(name);
        dateView.setText(date);
        subjectView.setText(subject);
        textView.setText(text);

        //Load collaborator's profile picture
        if (profilePictureFilename == null || profilePictureFilename.equals("")) {
            Picasso.with(getApplication())
                    .load(R.drawable.persona)
                    .resize(64,64)
                    .into(((ImageView) findViewById(R.id.profile_picture)));
        } else {
            File directory = getDir("imageDir", Context.MODE_PRIVATE);
            int indexoffileName = profilePictureFilename.lastIndexOf("/");
            String finalPath = directory + "/" + profilePictureFilename.substring(indexoffileName + 1);

            Picasso.with(getApplication())
                    .load(new File(finalPath))
                    .resize(64,64)
                    .placeholder(R.drawable.persona)
                    .into(((ImageView) findViewById(R.id.profile_picture)));
        }

        //If a followup has been set, make it visible
        if(!followup.equals("")){
            followupCardView.setVisibility(View.VISIBLE);
            followup =  "Followup by: " + followup;
            followupView.setText(followup);
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
