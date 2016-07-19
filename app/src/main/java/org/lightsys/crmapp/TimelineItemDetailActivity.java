package org.lightsys.crmapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by ca2br on 7/18/16.
 */
public class TimelineItemDetailActivity extends AppCompatActivity {


    private String type;
    private String name;
    private String subject;
    private String date;
    private String text;

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

        setContentView(R.layout.timeline_item_detail);

        TextView typeView = (TextView) findViewById(R.id.type);
        TextView nameView = (TextView) findViewById(R.id.name);
        TextView subjectView = (TextView) findViewById(R.id.subject);
        TextView dateView = (TextView) findViewById(R.id.date_posted);
        TextView textView = (TextView) findViewById(R.id.content);
        Button button = (Button) findViewById(R.id.backButton);


        typeView.setText(type);
        nameView.setText("Name: " + name);
        subjectView.setText("Subject: " + subject);
        dateView.setText("Date: " + date);
        textView.setText(text);



        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
