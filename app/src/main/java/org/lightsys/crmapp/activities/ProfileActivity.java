package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.lightsys.crmapp.Formatter;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.models.Partner;
import org.lightsys.crmapp.models.TimelineItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import static org.lightsys.crmapp.data.LocalDBTables.CollaborateeTable.PARTNER_NAME;
import static org.lightsys.crmapp.data.LocalDBTables.NotificationsTable.NOTIFICATION_ID;

/**
 * Created by cubemaster on 3/10/16.
 *
 * Commented by Judah on 7/26/16.
 * This class pretty much just gets a bunch of info and sends it to the profile fragment
 *
 * Edited by Daniel Garcia on 05/July/2017.
 * Added functionality that saves contact info so that it can be accessed offline
 */
public class ProfileActivity extends AppCompatActivity {
    //Constants for retrieving stuff from intents
    public static final String LOG_TAG = ProfileActivity.class.getName();
    public static final String NAME_KEY = "EXTRA_NAME";
    public static final String PARTNER_ID_KEY = "EXTRA_PARTNER_ID";
    private static final String EMAIL_KEY = "EXTRA_EMAIL";
    private static final String PHONE_KEY = "EXTRA_PHONE";
    private static final String ADDRESS_KEY = "EXTRA_ADDRESS";
    private static final String CITY_KEY = "EXTRA_CITY";
    private static final String STATE_KEY = "EXTRA_STATE";
    private static final String POSTALCODE_KEY = "EXTRA_POSTALCODE";
    private static final String FULLADDRESS_KEY = "EXTRA_FULLADDRESS";
    private static final String CELL_KEY = "EXTRA_CELL";
    private static final String SURNAME_KEY = "EXTRA_SURNAME";
    private static final String GIVEN_NAMES_KEY = "EXTRA_GIVEN_NAMES";

    //These might not be necessary, but they are still here just in case
    private static final String PHONE_ID_KEY = "EXTRA_PHONE_ID";
    private static final String CELL_ID_KEY = "EXTRA_CELL_ID";
    private static final String EMAIL_ID_KEY = "EXTRA_EMAIL_ID";

    private static final String PHONE_JSON_ID_KEY = "EXTRA_PHONE_JSON_ID";
    private static final String CELL_JSON_ID_KEY = "EXTRA_CELL_JSON_ID";
    private static final String EMAIL_JSON_ID_KEY = "EXTRA_EMAIL_JSON_ID";
    private static final String ADDRESS_JSON_ID_KEY = "EXTRA_ADDRESS_JSON_ID";
    private static final String PARTNER_JSON_ID_KEY = "EXTRA_PARTNER_JSON_ID";

    private static final String BLOG_KEY = "EXTRA_BLOG";
    private static final String FAX_KEY = "EXTRA_FAX";
    private static final String FACEBOOK_KEY = "EXTRA_FAX";
    private static final String SKYPE_KEY = "EXTRA_SKYPE";
    private static final String TWITTER_KEY = "EXTRA_TWITTER";
    private static final String WEBSITE_KEY = "EXTRA_WEBSITE";

    //Constants specifically for NewInteractionActivity
    private static final String TYPE_KEY = "EXTRA_TYPE";
    private static final String SPECIFIC_CONTACT_KEY = "EXTRA_SPECIFIC_CONTACT";

    //Variables that hold the stuff retrieved from the intent
    private String mPartnerId;
    private String mName;
    private String mNotificationId;

    private String mEmail;
    private String mPhone;
    private String mAddress;
    private String mCity;
    private String mState;
    private String mPostalCode;
    private String mFullAddress;
    private String mCell;
    private String mSurname;
    private String mGivenName;

    private String mPhoneId;
    private String mCellId;
    private String mEmailId;

    private String mPhoneJsonId;
    private String mCellJsonID;
    private String mEmailJsonId;
    private String mAddressJsonId;
    private String mPartnerJsonId;

    private String mBlog;
    private String mFax;
    private String mFacebook;
    private String mSkype;
    private String mTwitter;
    private String mWebsite;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Button addInteraction;

    private String phones = "";
    private String TAG = "ProfileActivity";

    private Account mAccount;
    private List<TimelineItem> mItems = new ArrayList<>();

    private Partner mPartner2 = new Partner();

    private AccountManager mAccountManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        mPartnerId = intent.getStringExtra(PARTNER_ID_KEY);
        mName = intent.getStringExtra(PARTNER_NAME);
        mNotificationId = intent.getStringExtra(NOTIFICATION_ID);

        //If activity was opened from a notification, delete notification from the database
        if(mNotificationId != null) {
            ProfileActivity.this.getContentResolver().delete(LocalDBTables.NotificationsTable.CONTENT_URI,
                    LocalDBTables.NotificationsTable.NOTIFICATION_ID + " = " + mNotificationId,
                    null);
        }

        //get stuff from saved instance state
        if(savedInstanceState != null) {
            mName = savedInstanceState.getString(NAME_KEY);
            mPartnerId = savedInstanceState.getString(PARTNER_ID_KEY);

            mEmail = savedInstanceState.getString(EMAIL_KEY);
            mPhone = savedInstanceState.getString(PHONE_KEY);
            mAddress = savedInstanceState.getString(ADDRESS_KEY);
            mCity = savedInstanceState.getString(CITY_KEY);
            mState = savedInstanceState.getString(STATE_KEY);
            mPostalCode = savedInstanceState.getString(POSTALCODE_KEY);
            mFullAddress = savedInstanceState.getString(FULLADDRESS_KEY);
            mCell = savedInstanceState.getString(CELL_KEY);
            mSurname = savedInstanceState.getString(SURNAME_KEY);
            mGivenName = savedInstanceState.getString(GIVEN_NAMES_KEY);

            mPhoneId = savedInstanceState.getString(PHONE_ID_KEY);
            mCellId = savedInstanceState.getString(CELL_ID_KEY);
            mEmailId = savedInstanceState.getString(EMAIL_ID_KEY);

            mPhoneJsonId = savedInstanceState.getString(PHONE_JSON_ID_KEY);
            mCellJsonID = savedInstanceState.getString(CELL_JSON_ID_KEY);
            mEmailJsonId = savedInstanceState.getString(EMAIL_JSON_ID_KEY);
            mAddressJsonId = savedInstanceState.getString(ADDRESS_JSON_ID_KEY);
            mPartnerJsonId = savedInstanceState.getString(PARTNER_JSON_ID_KEY);

            mBlog = savedInstanceState.getString(BLOG_KEY);
            mFax = savedInstanceState.getString(FAX_KEY);
            mFacebook = savedInstanceState.getString(FACEBOOK_KEY);
            mSkype = savedInstanceState.getString(SKYPE_KEY);
            mTwitter = savedInstanceState.getString(TWITTER_KEY);
            mWebsite = savedInstanceState.getString(WEBSITE_KEY);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout_profile);
        if(mName != null) {
            mCollapsingToolbarLayout.setTitle(mName);
        }

        addInteraction = (Button) findViewById(R.id.addInteraction);
        addInteraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), NewInteractionActivity.class);
                i.putExtra(PARTNER_ID_KEY, mPartnerId);
                i.putExtra(NAME_KEY, mName);
                startActivity(i);
            }
        });

        mAccountManager = AccountManager.get(getApplicationContext());

        Account[] accounts = mAccountManager.getAccountsByType(LocalDBTables.accountType);
        if(accounts.length > 0) {
            mAccount = accounts[0];
            new getCollaborateeInfoTask().execute();
            new getTimelineTask(findViewById(R.id.cardview_timeline)).execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit_profile:
                //put stuff into intent for editing
                Intent i = new Intent(getApplicationContext(), EditProfileActivity.class);
                i.putExtra(NAME_KEY, mName);
                i.putExtra(PARTNER_ID_KEY, mPartnerId);

                i.putExtra(EMAIL_KEY, mEmail);
                i.putExtra(PHONE_KEY, mPhone);
                i.putExtra(ADDRESS_KEY, mAddress);
                i.putExtra(CITY_KEY, mCity);
                i.putExtra(STATE_KEY, mState);
                i.putExtra(POSTALCODE_KEY, mPostalCode);
                i.putExtra(FULLADDRESS_KEY, mFullAddress);
                i.putExtra(CELL_KEY, mCell);
                i.putExtra(SURNAME_KEY, mSurname);
                i.putExtra(GIVEN_NAMES_KEY, mGivenName);

                i.putExtra(PHONE_ID_KEY, mPhoneId);
                i.putExtra(CELL_ID_KEY, mCellId);
                i.putExtra(EMAIL_ID_KEY, mEmailId);

                i.putExtra(PHONE_JSON_ID_KEY, mPhoneJsonId);
                i.putExtra(CELL_JSON_ID_KEY, mCellJsonID);
                i.putExtra(EMAIL_JSON_ID_KEY, mEmailJsonId);
                i.putExtra(ADDRESS_JSON_ID_KEY, mAddressJsonId);
                i.putExtra(PARTNER_JSON_ID_KEY, mPartnerJsonId);

                i.putExtra(BLOG_KEY, mBlog);
                i.putExtra(FAX_KEY, mFax);
                i.putExtra(FACEBOOK_KEY, mFacebook);
                i.putExtra(SKYPE_KEY, mSkype);
                i.putExtra(TWITTER_KEY, mTwitter);
                i.putExtra(WEBSITE_KEY, mWebsite);

                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(NAME_KEY, mName);
        savedInstanceState.putString(PARTNER_ID_KEY, mPartnerId);

        savedInstanceState.putString(EMAIL_KEY, mEmail);
        savedInstanceState.putString(PHONE_KEY, mPhone);
        savedInstanceState.putString(ADDRESS_KEY, mAddress);
        savedInstanceState.putString(CITY_KEY, mCity);
        savedInstanceState.putString(STATE_KEY, mState);
        savedInstanceState.putString(POSTALCODE_KEY, mPostalCode);
        savedInstanceState.putString(CELL_KEY, mCell);
        savedInstanceState.putString(SURNAME_KEY, mSurname);
        savedInstanceState.putString(GIVEN_NAMES_KEY, mGivenName);
        savedInstanceState.putString(PHONE_ID_KEY, mPhoneId);
        savedInstanceState.putString(CELL_ID_KEY, mCellId);
        savedInstanceState.putString(EMAIL_ID_KEY, mEmailId);
        savedInstanceState.putString(PHONE_JSON_ID_KEY, mPhoneJsonId);
        savedInstanceState.putString(CELL_JSON_ID_KEY, mCellJsonID);
        savedInstanceState.putString(EMAIL_JSON_ID_KEY, mEmailJsonId);
        savedInstanceState.putString(ADDRESS_JSON_ID_KEY, mAddressJsonId);
        savedInstanceState.putString(PARTNER_JSON_ID_KEY, mPartnerJsonId);
        savedInstanceState.putString(BLOG_KEY, mBlog);
        savedInstanceState.putString(FAX_KEY, mFax);
        savedInstanceState.putString(FACEBOOK_KEY, mFacebook);
        savedInstanceState.putString(SKYPE_KEY, mSkype);
        savedInstanceState.putString(TWITTER_KEY, mTwitter);
        savedInstanceState.putString(WEBSITE_KEY, mWebsite);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final String type;
        final String specificContact;

        //Set correct contact form based on code from the activity
        switch(requestCode){
            case 0:
                //email messge
                type = "Email Message";
                specificContact = mEmail;
                break;
            case 1:
                //phone call
                type = "Phone Call";
                specificContact = phones;
                break;
            default:
                //nothing
                type = "";
                specificContact = "";
                break;
        }

        /*
          Ask if user wants to record interaction,
          and if so start a new interaction activity
          with type and date automatically filled
         */
        new AlertDialog.Builder(ProfileActivity.this)
                .setCancelable(false)
                .setMessage("Record Interaction?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), NewInteractionActivity.class);
                        intent.putExtra(TYPE_KEY, type);
                        intent.putExtra(SPECIFIC_CONTACT_KEY, specificContact);
                        intent.putExtra(PARTNER_ID_KEY, mPartnerId);
                        intent.putExtra(NAME_KEY, mName);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    //Sorts timeline items by date
    //and puts newest items first
    private ArrayList<TimelineItem> sortByDate(List<TimelineItem> items) {

        TimelineItem highestItem = null;//this will hold the newest item for each pass

        ArrayList<TimelineItem> sortedItems = new ArrayList<>();
        while (items.size() > 0){
            int[] highestDate = {0, 0, 0};//start low because we are looking for dates larger
            for (TimelineItem item : items) {
                int[] date = parseStringDate(item.getDate());
                if (date[0] > highestDate[0]) { //if the year is bigger the date is newer
                    highestDate = date;
                    highestItem = item;
                }
                else if (date[0] == highestDate[0]){//if the years are the same look at the month
                    if (date[1] > highestDate[1]){//if the month is bigger the date is newer
                        highestDate = date;
                        highestItem = item;
                    }
                    else if (date[1] == highestDate[1]){//if the months are the same look at the days
                        if (date[2] >= highestDate[2]){//if the day is bigger the date is newer
                            highestDate = date;
                            highestItem = item;
                        }
                    }
                }

            }
            sortedItems.add(highestItem);//add newest item to list
            items.remove(highestItem);//get rid of newest item in old list as to not cause confusion
        }

        return sortedItems;//return sorted list of items
    }

    //Sets up adapter for list of timeline items
    private void setupAdapter() {
            ArrayList<HashMap<String,String>> items = new ArrayList<>();
            for (TimelineItem item : mItems){
                HashMap<String, String> newItem = new HashMap<>();

                newItem.put("type", item.getContactHistoryType());
                newItem.put("name", item.getCollaborateeName());
                newItem.put("subject", item.getSubject());
                newItem.put("date", Formatter.getFormattedDate(item.getDate()));
                newItem.put("text", item.getNotes());
                newItem.put("date_created", item.getDateCreated());
                newItem.put("profile_picture_filename", getProfilePictureFilename(item.getCollaborateeId()));
                items.add(newItem);
            }

            String[] from = {"item"};//stuff for the adapter
            int[] to = {R.id.timeline_item};//more stuff for the adapter
            if (mItems.size() > 0) {
                //if we have comments, set them to the adapter
                TimeLineAdapter adapter = new TimeLineAdapter(getApplication(), items, R.layout.timeline_item_layout, from, to);

                ListView listview = (ListView) findViewById(R.id.timelineList);
                listview.setAdapter(adapter);
            }
    }

    //Fetches timeline info from Kardia
    private class getTimelineTask extends AsyncTask<Void, Void, List<TimelineItem>> {

        final View timelineCardView;

        public getTimelineTask(View timelinecardview)
        {
            timelineCardView = timelinecardview;
        }

        @Override
        protected List<TimelineItem> doInBackground(Void... params) {

            List<TimelineItem> items;

            //Use KardiaFetcher to get fetch new timeline items from the server
            KardiaFetcher fetcher = new KardiaFetcher(getApplicationContext());
            mAccountManager.setUserData(mAccount, "collabId", mPartnerId);
            items = fetcher.getTimelineItems(mAccount);

            //Create each item from the server and add it to the partner's database
            //(Primary key will prevent any duplicate from being added)
            for(TimelineItem item : items) {
                ContentValues values = new ContentValues();
                values.put(LocalDBTables.TimelineTable.CONTACT_ID, item.getContactId());
                values.put(LocalDBTables.TimelineTable.PARTNER_ID, item.getPartnerId());
                values.put(LocalDBTables.TimelineTable.COLLABORATEE_ID, item.getCollaborateeId());
                values.put(LocalDBTables.TimelineTable.COLLABORATEE_NAME, item.getCollaborateeName());
                values.put(LocalDBTables.TimelineTable.CONTACT_HISTORY_ID, item.getContactHistoryId());
                values.put(LocalDBTables.TimelineTable.CONTACT_HISTORY_TYPE, item.getContactHistoryType());
                values.put(LocalDBTables.TimelineTable.SUBJECT, item.getSubject());
                values.put(LocalDBTables.TimelineTable.NOTES, item.getNotes());
                values.put(LocalDBTables.TimelineTable.DATE, item.getDate());
                values.put(LocalDBTables.TimelineTable.DATE_CREATED, item.getDateCreated());
                getContentResolver().insert(LocalDBTables.TimelineTable.CONTENT_URI, values);
            }

            //Open the partner's database to get items stored in it
            //Since new items are added above, both old and new items will be returned
            Cursor cursor = getContentResolver().query(
                    LocalDBTables.TimelineTable.CONTENT_URI,
                    new String[] {LocalDBTables.TimelineTable.CONTACT_ID,
                            LocalDBTables.TimelineTable.PARTNER_ID,
                            LocalDBTables.TimelineTable.COLLABORATEE_ID,
                            LocalDBTables.TimelineTable.COLLABORATEE_NAME,
                            LocalDBTables.TimelineTable.CONTACT_HISTORY_ID,
                            LocalDBTables.TimelineTable.CONTACT_HISTORY_TYPE,
                            LocalDBTables.TimelineTable.SUBJECT,
                            LocalDBTables.TimelineTable.NOTES,
                            LocalDBTables.TimelineTable.DATE,
                            LocalDBTables.TimelineTable.DATE_CREATED},
                    LocalDBTables.TimelineTable.PARTNER_ID + " = ?",
                    new String[] {mPartnerId},
                    null
            );

            //Clear the items list and place in everything from the database
            //(This allows for offline functionality, since new things get added to
            //the database without the risk of duplicates, but if nothing gets added,
            //the old items are still shown)
            items.clear();
            while (cursor.moveToNext()) {
                TimelineItem item = new TimelineItem(cursor.getString(0));
                item.PartnerId(cursor.getString(1));
                item.setCollaborateeId(cursor.getString(2));
                item.setCollaborateeName(cursor.getString(3));
                item.setContactHistoryId(cursor.getString(4));
                item.setContactHistoryType(cursor.getString(5));
                item.setSubject(cursor.getString(6));
                item.setNotes(cursor.getString(7));
                item.setDate(cursor.getString(8));
                item.setDateCreated(cursor.getString(9));
                items.add(item);
            }
            cursor.close();


            return items;
        }


        //Sort items
        @Override
        protected void onPostExecute(List<TimelineItem> items) {
            mItems = sortByDate(items);
            setupAdapter();
            if (mItems.size() == 0)
            {
                timelineCardView.setEnabled(false);
            }
        }

    }

    //Adapter for the timeline items
    private class TimeLineAdapter extends SimpleAdapter {

        final Context context;
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        final ArrayList<View> views;
        final String[] from;
        final int[] to;

        public TimeLineAdapter(Context context, ArrayList<HashMap<String, String>> data,
                               int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            this.context = context;
            this.views = new ArrayList<>();
            this.data = data;
            this.from = from;
            this.to = to;
        }

        /**
         * Creates and populates a timeline layout.
         */
        public View getView(final int position, View convertView, ViewGroup parent) {

            TimelineLayout rowView = (TimelineLayout) convertView;
            if(rowView == null) {
                rowView = new TimelineLayout(getApplicationContext());
            }

            final Map<String, String> pieces = data.get(position);

            rowView.type = pieces.get("type");
            rowView.name = pieces.get("name");
            rowView.subject = pieces.get("subject");
            rowView.date = pieces.get("date");
            rowView.textText = pieces.get("text");
            rowView.followup = checkForFollowup(pieces.get("date_created"));
            rowView.profilePictureFilename = pieces.get("profile_picture_filename");

            rowView.setItemViewText(pieces.get("type") + ": " + pieces.get("name") +
                    " on " + pieces.get("date"));
            views.add(rowView);

            return rowView;
        }
    }

    //Layout for the timeline items
    private class TimelineLayout extends RelativeLayout {

        TextView itemView = new TextView(getContext());
        final CardView button;

        public String type = "";
        public String name = "";
        public String subject = "";
        public String date = "";
        public String textText = "";
        public String followup = "";
        public String profilePictureFilename = "";

        public TimelineLayout(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.timeline_item_layout, this, true);
            itemView = (TextView)findViewById(R.id.timeline_item);
            button = (CardView) findViewById(R.id.cardview_timeline);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), TimelineItemDetailActivity.class);
                    i.putExtra("type", type);
                    i.putExtra("name", name);
                    i.putExtra("subject", subject);
                    i.putExtra("date", date);
                    i.putExtra("text", textText);
                    i.putExtra("followup", followup);
                    i.putExtra("profilePictureFilename", profilePictureFilename);
                    startActivity(i);
                }
            });

        }

        public void setItemViewText(final String text){
            itemView.setText(text);

        }

    }

    //Splits date into {year, month, day}
    private int[] parseStringDate(String date) {
        int[] dateInt = new int[3];
        String[] dateSplitStr = date.split("-");
        for (int i = 0; i < 3; i++) {
            dateInt[i] = Integer.parseInt(dateSplitStr[i]);
        }
        return dateInt;
    }

    //Gets detailed collaboratee information
    private class getCollaborateeInfoTask extends AsyncTask<Void, Void, Void> {

        /**
         * Background thread that fetches info from the server.
         * This where the magic happens.
         */
        @Override
        protected Void doInBackground(Void... params) {
            Partner collaboratee = new Partner(mPartnerId, mName);

            //get collaboratee from the database
            Cursor cursor = getContentResolver().query(
                    LocalDBTables.CollaborateeTable.CONTENT_URI,
                    new String[] {LocalDBTables.CollaborateeTable.PARTNER_ID, PARTNER_NAME, LocalDBTables.CollaborateeTable.EMAIL,
                            LocalDBTables.CollaborateeTable.PHONE, LocalDBTables.CollaborateeTable.ADDRESS_1, LocalDBTables.CollaborateeTable.CITY,
                            LocalDBTables.CollaborateeTable.STATE_PROVINCE, LocalDBTables.CollaborateeTable.POSTAL_CODE, LocalDBTables.CollaborateeTable.CELL,
                            LocalDBTables.CollaborateeTable.SURNAME, LocalDBTables.CollaborateeTable.GIVEN_NAMES, LocalDBTables.CollaborateeTable.PHONE_ID,
                            LocalDBTables.CollaborateeTable.CELL_ID, LocalDBTables.CollaborateeTable.EMAIL_ID, LocalDBTables.CollaborateeTable.PHONE_JSON_ID,
                            LocalDBTables.CollaborateeTable.CELL_JSON_ID, LocalDBTables.CollaborateeTable.EMAIL_JSON_ID, LocalDBTables.CollaborateeTable.ADDRESS_JSON_ID,
                            LocalDBTables.CollaborateeTable.PARTNER_JSON_ID, LocalDBTables.CollaborateeTable.PROFILE_PICTURE},
                    LocalDBTables.CollaborateeTable.PARTNER_ID + " = ?",
                    new String[] {mPartnerId},
                    null);

            //turn raw query stuffs into a partner
            if(cursor.moveToFirst()) {
                collaboratee.PartnerId = cursor.getString(0);
                collaboratee.PartnerName = cursor.getString(1);
                collaboratee.Email = cursor.getString(2);
                collaboratee.Phone = cursor.getString(3);
                collaboratee.Address1 = cursor.getString(4);
                collaboratee.City = cursor.getString(5);
                collaboratee.StateProvince = cursor.getString(6);
                collaboratee.PostalCode = cursor.getString(7);
                collaboratee.setFullAddress(cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
                collaboratee.Cell = cursor.getString(8);
                collaboratee.Surname = cursor.getString(9);
                collaboratee.GivenNames = cursor.getString(10);
                collaboratee.PhoneId = cursor.getString(11);
                collaboratee.CellId = cursor.getString(12);
                collaboratee.EmailId = cursor.getString(13);
                collaboratee.PhoneJsonId = cursor.getString(14);
                collaboratee.CellJsonId = cursor.getString(15);
                collaboratee.EmailJsonId = cursor.getString(16);
                collaboratee.AddressJsonId = cursor.getString(17);
                collaboratee.PartnerJsonId = cursor.getString(18);
                collaboratee.ProfilePictureFilename = cursor.getString(19);
            }
            cursor.close();

            //if the collaboratee is missing any information,
            // and there is a network connection available,
            // pull info down from the server

            if(networkConnected() && (collaboratee.Email == null || collaboratee.Phone == null
                    || collaboratee.Address1 == null || collaboratee.City == null
                    || collaboratee.StateProvince == null || collaboratee.PostalCode == null
                    || collaboratee.Cell == null) || collaboratee.ProfilePictureFilename == null) {

                //get all the collaboratee stuff from the server
                KardiaFetcher fetcher = new KardiaFetcher(getApplicationContext());
                collaboratee = fetcher.getCollaborateeInfo(mAccount, collaboratee);

                if (collaboratee.ProfilePictureFilename != null)
                    saveImageFromUrl(mAccountManager.getUserData(mAccount, "server"), getApplicationContext(), collaboratee.ProfilePictureFilename);

                //set partner ID to match account ID
                //this fixes an issue where a profile disappeared from the main list after being clicked
                collaboratee.PartnerId = mAccountManager.getUserData(mAccount, "partnerId");

                //puts all non null collaboratee values into database
                ContentValues values = new ContentValues();
                if (collaboratee.PartnerId != null) {
                    values.put(LocalDBTables.CollaborateeTable.COLLABORATER_ID, collaboratee.PartnerId);
                }if (collaboratee.PartnerName != null) {
                    values.put(PARTNER_NAME, collaboratee.PartnerName);
                }if (collaboratee.Surname != null) {
                    values.put(LocalDBTables.CollaborateeTable.SURNAME, collaboratee.Surname);
                }if (collaboratee.GivenNames != null) {
                    values.put(LocalDBTables.CollaborateeTable.GIVEN_NAMES, collaboratee.GivenNames);
                }if (collaboratee.Phone != null) {
                    values.put(LocalDBTables.CollaborateeTable.PHONE, collaboratee.Phone);
                }if (collaboratee.Cell != null) {
                    values.put(LocalDBTables.CollaborateeTable.CELL, collaboratee.Cell);
                }if (collaboratee.Email != null) {
                    values.put(LocalDBTables.CollaborateeTable.EMAIL, collaboratee.Email);
                }if (collaboratee.Address1 != null) {
                    values.put(LocalDBTables.CollaborateeTable.ADDRESS_1, collaboratee.Address1);
                }if (collaboratee.City != null) {
                    values.put(LocalDBTables.CollaborateeTable.CITY, collaboratee.City);
                }if (collaboratee.StateProvince != null) {
                    values.put(LocalDBTables.CollaborateeTable.STATE_PROVINCE, collaboratee.StateProvince);
                }if (collaboratee.PostalCode != null) {
                    values.put(LocalDBTables.CollaborateeTable.POSTAL_CODE, collaboratee.PostalCode);
                }if (collaboratee.PhoneId != null) {
                    values.put(LocalDBTables.CollaborateeTable.PHONE_ID, collaboratee.PhoneId);
                }if (collaboratee.EmailId != null) {
                    values.put(LocalDBTables.CollaborateeTable.EMAIL_ID, collaboratee.EmailId);
                }if (collaboratee.CellId != null) {
                    values.put(LocalDBTables.CollaborateeTable.CELL_ID, collaboratee.CellId);
                }if (collaboratee.PhoneJsonId != null) {
                    values.put(LocalDBTables.CollaborateeTable.PHONE_JSON_ID, collaboratee.PhoneJsonId);
                }if (collaboratee.CellJsonId != null) {
                    values.put(LocalDBTables.CollaborateeTable.CELL_JSON_ID, collaboratee.CellJsonId);
                }if (collaboratee.EmailJsonId != null) {
                    values.put(LocalDBTables.CollaborateeTable.EMAIL_JSON_ID, collaboratee.EmailJsonId);
                }if (collaboratee.AddressJsonId != null) {
                    values.put(LocalDBTables.CollaborateeTable.ADDRESS_JSON_ID, collaboratee.AddressJsonId);
                }if (collaboratee.PartnerJsonId != null) {
                    values.put(LocalDBTables.CollaborateeTable.PARTNER_JSON_ID, collaboratee.PartnerJsonId);
                }if (collaboratee.ProfilePictureFilename != null) {
                    saveImageFromUrl(mAccountManager.getUserData(mAccount, "server"), getApplicationContext(), collaboratee.ProfilePictureFilename);
                    values.put(LocalDBTables.CollaborateeTable.PROFILE_PICTURE, collaboratee.ProfilePictureFilename);
                }

                //put new collaboratee info into database
                getApplicationContext().getContentResolver().update(LocalDBTables.CollaborateeTable.CONTENT_URI, values,
                        LocalDBTables.CollaborateeTable.PARTNER_ID + "= ?", new String[] {mPartnerId});

                //pull stuff back out of the database
                //this gets the original data back in case kardia returned nothing
                Cursor cursor2 = getContentResolver().query(
                        LocalDBTables.CollaborateeTable.CONTENT_URI,
                        new String[] {LocalDBTables.CollaborateeTable.PARTNER_ID, PARTNER_NAME, LocalDBTables.CollaborateeTable.EMAIL,
                                LocalDBTables.CollaborateeTable.PHONE, LocalDBTables.CollaborateeTable.ADDRESS_1, LocalDBTables.CollaborateeTable.CITY,
                                LocalDBTables.CollaborateeTable.STATE_PROVINCE, LocalDBTables.CollaborateeTable.POSTAL_CODE, LocalDBTables.CollaborateeTable.CELL,
                                LocalDBTables.CollaborateeTable.SURNAME, LocalDBTables.CollaborateeTable.GIVEN_NAMES, LocalDBTables.CollaborateeTable.PHONE_ID,
                                LocalDBTables.CollaborateeTable.CELL_ID, LocalDBTables.CollaborateeTable.EMAIL_ID, LocalDBTables.CollaborateeTable.PHONE_JSON_ID,
                                LocalDBTables.CollaborateeTable.CELL_JSON_ID, LocalDBTables.CollaborateeTable.EMAIL_JSON_ID, LocalDBTables.CollaborateeTable.ADDRESS_JSON_ID,
                                LocalDBTables.CollaborateeTable.PARTNER_JSON_ID, LocalDBTables.CollaborateeTable.PROFILE_PICTURE},
                        LocalDBTables.CollaborateeTable.PARTNER_ID + " = ?",
                        new String[] {mPartnerId},
                        null);

                //turn raw query stuff into a partner
                if(cursor2.moveToFirst()) {
                    collaboratee.PartnerId = cursor2.getString(0);
                    collaboratee.PartnerName = cursor2.getString(1);
                    collaboratee.Email = cursor2.getString(2);
                    collaboratee.Phone = cursor2.getString(3);
                    collaboratee.Address1 = cursor2.getString(4);
                    collaboratee.City = cursor2.getString(5);
                    collaboratee.StateProvince = cursor2.getString(6);
                    collaboratee.PostalCode = cursor2.getString(7);
                    collaboratee.setFullAddress(cursor2.getString(4), cursor2.getString(5), cursor2.getString(6), cursor2.getString(7));
                    collaboratee.Cell = cursor2.getString(8);
                    collaboratee.Surname = cursor2.getString(9);
                    collaboratee.GivenNames = cursor2.getString(10);
                    collaboratee.PhoneId = cursor2.getString(11);
                    collaboratee.CellId = cursor2.getString(12);
                    collaboratee.EmailId = cursor2.getString(13);
                    collaboratee.PhoneJsonId = cursor2.getString(14);
                    collaboratee.CellJsonId = cursor2.getString(15);
                    collaboratee.EmailJsonId = cursor2.getString(16);
                    collaboratee.AddressJsonId = cursor2.getString(17);
                    collaboratee.PartnerJsonId = cursor2.getString(18);
                    collaboratee.ProfilePictureFilename = cursor2.getString(19);
                }

                cursor2.close();

                mPartner2 = collaboratee;
            }
            else {
                mPartner2 = collaboratee;
            }
            return null;
        }

        /**
         * Places info into an intent after Async task has finished
         */
        @Override
        protected void onPostExecute(Void params) {

            mName = mPartner2.PartnerName;

            mPartnerId = mPartner2.PartnerId;

            mEmail = mPartner2.Email;
            mPhone = mPartner2.Phone;
            mAddress = mPartner2.Address1;
            mCity = mPartner2.City;
            mState = mPartner2.StateProvince;
            mPostalCode = mPartner2.PostalCode;
            mFullAddress = mPartner2.getFullAddress();
            mCell = mPartner2.Cell;
            mSurname = mPartner2.Surname;
            mGivenName = mPartner2.GivenNames;

            mPhoneId = mPartner2.PhoneId;
            mCellId = mPartner2.CellId;
            mEmailId = mPartner2.EmailId;

            mPhoneJsonId = mPartner2.PhoneJsonId;
            mCellJsonID = mPartner2.CellJsonId;
            mEmailJsonId = mPartner2.EmailJsonId;
            mAddressJsonId = mPartner2.AddressJsonId;
            mPartnerJsonId = mPartner2.PartnerJsonId;

            mBlog = mPartner2.Blog;
            mFax = mPartner2.Fax;
            mSkype = mPartner2.Skype;
            mTwitter = mPartner2.Twitter;
            mWebsite = mPartner2.Website;
            mFacebook = mPartner2.Facebook;

            File directory = getDir("imageDir", Context.MODE_PRIVATE);

            String profilePictureFilename = mPartner2.ProfilePictureFilename;
            View appBarView = findViewById(R.id.app_bar_layout_profile);
            int width = appBarView.getMeasuredWidth();
            int height = appBarView.getMeasuredHeight();

            if(width != 0 && height != 0) {
                if (profilePictureFilename == null || profilePictureFilename.equals("")) {
                    Picasso.with(getApplication())
                            .load(R.drawable.persona)
                            .into(((ImageView) findViewById(R.id.backdrop_profile)));
                } else {
                    int indexoffileName = profilePictureFilename.lastIndexOf("/");
                    String finalPath = directory + "/" + profilePictureFilename.substring(indexoffileName + 1);
                    Picasso.with(getApplication())
                            .load(new File(finalPath))
                            .placeholder(R.drawable.persona)
                            .into(((ImageView) findViewById(R.id.backdrop_profile)));
                }
            }
            TextView emailTextView = (TextView) findViewById(R.id.e_address);
            emailTextView.setText(mEmail);

            emailTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent email_send = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mEmail, null));
                    try {
                        startActivity(Intent.createChooser(email_send, "Send email..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(ProfileActivity.this,"No Email app Found", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            TextView phoneTextView = (TextView) findViewById(R.id.phone_number);
            if(mCell != null) {
                phoneTextView.setText(mCell);
                phones = mCell;
            }
            else if (mPhone!=null){
                phoneTextView.setText(mPhone);
                phones = mPhone;
            }
            //call/text donor
            phoneTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(ProfileActivity.this)
                            .setCancelable(false)
                            .setMessage("Go to phone?")
                            //.setMessage("Server uses self-signed certificate. Allow connection?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String phoneCall = "+" + mPhone.replaceAll("[^0-9.]", "");
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneCall, null));
                                    try {
                                        startActivity(intent);
                                    } catch(android.content.ActivityNotFoundException ex) {
                                        Toast.makeText(ProfileActivity.this,"no Phone app found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });


            TextView addressTextView = (TextView) findViewById(R.id.s_address);
            addressTextView.setText(mAddress + ", " + mCity + ", " + mState + ", " + mPostalCode);

            addressTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://www.google.com/maps/search/?api=1&query=" + mAddress + "%2C+" + mCity + "%2C+" + mState + "%2C+" + mPostalCode;
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }
            });
        }
    }

    public static void saveImageFromUrl(String server, Context context, String profilePictureFilename) {
        if (profilePictureFilename == null || profilePictureFilename.equals(""))
            return;

        String finalPath;

        File directory = context.getDir("imageDir", Context.MODE_PRIVATE);
        int indexoffileName = profilePictureFilename.lastIndexOf("/");

        finalPath = directory + "/"+ profilePictureFilename.substring(indexoffileName + 1);

        if (new File(finalPath).exists())
            return;

        final OkHttpClient client = new OkHttpClient.Builder()
                .authenticator(new okhttp3.Authenticator()
                {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException
                    {
                        return response.request().newBuilder()
                                .header("Authorization", LoginActivity.Credential)
                                .build();
                    }
                }).build();

        Request request = new Request.Builder()
                .url(server + profilePictureFilename)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.code() == HttpsURLConnection.HTTP_OK && response.isSuccessful()) {
            FileOutputStream output;
            try
            {
                output = new FileOutputStream(finalPath);
                Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    //Checks if app is connected to a network
    private boolean networkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private String checkForFollowup(String dateCreated) {
        String followupDate = "";

        try {

            //Convert item date created int a String
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date mDateCreated = format.parse(dateCreated);
            String itemDateCreated = format.format(mDateCreated);

            // Open notification database with a cursor
            Cursor cursor = ProfileActivity.this.getContentResolver().query(
                    LocalDBTables.NotificationsTable.CONTENT_URI,
                    new String[] {LocalDBTables.NotificationsTable.TIME,
                            LocalDBTables.NotificationsTable.DATE_CREATED},
                    null, null, null);

            while (cursor.moveToNext()){

                //Convert notification date created into a String
                Calendar cal = Calendar.getInstance();
                long notificationInMillis = Long.parseLong(cursor.getString(1));
                cal.setTimeInMillis(notificationInMillis);
                String notificationDateCreated = format.format(cal.getTime());

                // Check if any of the item's created dates match the created date of a followup notification
                if(notificationDateCreated.equals(itemDateCreated)) {
                    long mFollowupDate = Long.parseLong(cursor.getString(0));

                    // Convert the actual followup date to a regular date and time
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(mFollowupDate);
                    followupDate = format.format(c.getTime());
                }
            }

            cursor.close();
        }
        catch (ParseException p) {
            p.printStackTrace();
        }

        return followupDate;
    }

    private String getProfilePictureFilename(String collaborateeID) {
        String filename = "";

        //Open cursor to access collaboratee table adnd get
        //profile picture filename for the collaboratee
        Cursor cursor = getContentResolver().query(
                LocalDBTables.CollaborateeTable.CONTENT_URI,
                new String[] {LocalDBTables.CollaborateeTable.PROFILE_PICTURE},
                LocalDBTables.CollaborateeTable.PARTNER_ID + " = ?",
                new String[] {collaborateeID},
                null);

        while(cursor.moveToNext()){
            filename = cursor.getString(0);
        }

        cursor.close();

        return filename;
    }
}