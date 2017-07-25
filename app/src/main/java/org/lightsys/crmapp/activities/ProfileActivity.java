package org.lightsys.crmapp.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.lightsys.crmapp.Formatter;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.models.Partner;
import org.lightsys.crmapp.models.TimelineItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import static org.lightsys.crmapp.data.CRMContract.CollaborateeTable.PARTNER_NAME;

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
    public static final String EMAIL_KEY = "EXTRA_EMAIL";
    public static final String PHONE_KEY = "EXTRA_PHONE";
    public static final String ADDRESS_KEY = "EXTRA_ADDRESS";
    public static final String CITY_KEY = "EXTRA_CITY";
    public static final String STATE_KEY = "EXTRA_STATE";
    public static final String POSTALCODE_KEY = "EXTRA_POSTALCODE";
    public static final String FULLADDRESS_KEY = "EXTRA_FULLADDRESS";
    public static final String CELL_KEY = "EXTRA_CELL";
    public static final String SURNAME_KEY = "EXTRA_SURNAME";
    public static final String GIVEN_NAMES_KEY = "EXTRA_GIVEN_NAMES";

    //These might not be necessary, but they are still here just in case
    public static final String PHONE_ID_KEY = "EXTRA_PHONE_ID";
    public static final String CELL_ID_KEY = "EXTRA_CELL_ID";
    public static final String EMAIL_ID_KEY = "EXTRA_EMAIL_ID";

    public static final String PHONE_JSON_ID_KEY = "EXTRA_PHONE_JSON_ID";
    public static final String CELL_JSON_ID_KEY = "EXTRA_CELL_JSON_ID";
    public static final String EMAIL_JSON_ID_KEY = "EXTRA_EMAIL_JSON_ID";
    public static final String ADDRESS_JSON_ID_KEY = "EXTRA_ADDRESS_JSON_ID";
    public static final String PARTNER_JSON_ID_KEY = "EXTRA_PARTNER_JSON_ID";

    public static final String BLOG_KEY = "EXTRA_BLOG";
    public static final String FAX_KEY = "EXTRA_FAX";
    public static final String FACEBOOK_KEY = "EXTRA_FAX";
    public static final String SKYPE_KEY = "EXTRA_SKYPE";
    public static final String TWITTER_KEY = "EXTRA_TWITTER";
    public static final String WEBSITE_KEY = "EXTRA_WEBSITE";

    //Variables that hold the stuff retrieved from the intent
    public String mName;
    public String mPartnerId;

    public String mEmail;
    public String mPhone;
    public String mAddress;
    public String mCity;
    public String mState;
    public String mPostalCode;
    public String mFullAddress;
    public String mCell;
    public String mSurname;
    public String mGivenName;

    public String mPhoneId;
    public String mCellId;
    public String mEmailId;

    public String mPhoneJsonId;
    public String mCellJsonID;
    public String mEmailJsonId;
    public String mAddressJsonId;
    public String mPartnerJsonId;

    public String mBlog;
    public String mFax;
    public String mFacebook;
    public String mSkype;
    public String mTwitter;
    public String mWebsite;

    public Toolbar mToolbar;
    public CollapsingToolbarLayout mCollapsingToolbarLayout;

    private String phones = "";

    private Account mAccount;
    private List<TimelineItem> mItems = new ArrayList<>();

    Partner mPartner2 = new Partner();

    AccountManager mAccountManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        mPartnerId = intent.getStringExtra(PARTNER_ID_KEY);
        mName = intent.getStringExtra(PARTNER_NAME);

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

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbarlayout_profile);
        if(mName != null) {
            mCollapsingToolbarLayout.setTitle(mName);
        }

        mAccountManager = AccountManager.get(getApplicationContext());

        Account[] accounts = mAccountManager.getAccountsByType(CRMContract.accountType);
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

    //Sorts timeline items by date
    //and puts newest items first
    private ArrayList<TimelineItem> sortByDate(List<TimelineItem> items) {

        TimelineItem highestItem = null;//this will hold the newest item for each pass

        ArrayList<TimelineItem> sortedItems = new ArrayList<TimelineItem>();
        while (items.size() > 0){
            int[] highestDate = {0, 0, 0};//start low because we are looking for dates larger
            for (TimelineItem item : items) {
                int[] date = parseStringDate(item.getDate());
                if (date[0] > highestDate[0]) { //if the year is bigger the date is newer
                    highestDate = date;
                    highestItem = item;
                }
                else if (date[0] == highestDate[0]){//if the years are the same look at the month
                    if (date[1] > highestDate[1]){//if the month in bigger the date is newer
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
            ArrayList<HashMap<String,String>> items = new ArrayList<HashMap<String, String>>();
            for (TimelineItem item : mItems){
                HashMap<String, String> newItem = new HashMap<>();

                newItem.put("type", item.getContactHistoryType());
                newItem.put("name", item.getCollaborateeName());
                newItem.put("subject", item.getSubject());
                newItem.put("date", Formatter.getFormattedDate(item.getDate()));
                newItem.put("text", item.getNotes());
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

        View timelineCardView;

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
                values.put(CRMContract.TimelineTable.CONTACT_ID, item.getContactId());
                values.put(CRMContract.TimelineTable.PARTNER_ID, item.getPartnerId());
                values.put(CRMContract.TimelineTable.COLLABORATEE_ID, item.getCollaborateeId());
                values.put(CRMContract.TimelineTable.COLLABORATEE_NAME, item.getCollaborateeName());
                values.put(CRMContract.TimelineTable.CONTACT_HISTORY_ID, item.getContactHistoryId());
                values.put(CRMContract.TimelineTable.CONTACT_HISTORY_TYPE, item.getContactHistoryType());
                values.put(CRMContract.TimelineTable.SUBJECT, item.getSubject());
                values.put(CRMContract.TimelineTable.NOTES, item.getNotes());
                values.put(CRMContract.TimelineTable.DATE, item.getDate());
                getContentResolver().insert(CRMContract.TimelineTable.CONTENT_URI, values);
            }

            //Open the partner's database to get items stored in it
            //Since new items are added above, both old and new items will be returned
            Cursor cursor = getContentResolver().query(
                    CRMContract.TimelineTable.CONTENT_URI,
                    new String[] {CRMContract.TimelineTable.CONTACT_ID, CRMContract.TimelineTable.PARTNER_ID,
                            CRMContract.TimelineTable.COLLABORATEE_ID, CRMContract.TimelineTable.COLLABORATEE_NAME,
                            CRMContract.TimelineTable.CONTACT_HISTORY_ID, CRMContract.TimelineTable.CONTACT_HISTORY_TYPE,
                            CRMContract.TimelineTable.SUBJECT, CRMContract.TimelineTable.NOTES,
                            CRMContract.TimelineTable.DATE},
                    CRMContract.TimelineTable.PARTNER_ID + " = ?",
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
                items.add(item);
            }
            cursor.close();


            return items;
        }


        //Sort items and, if there are none, hide timeline bar
        @Override
        protected void onPostExecute(List<TimelineItem> items) {
            mItems = sortByDate(items);
            setupAdapter();
            if (mItems.size() == 0)
            {
                timelineCardView.setEnabled(false);
                timelineCardView.setVisibility(View.INVISIBLE);
            }
        }

    }

    //Adapter for the timeline items
    private class TimeLineAdapter extends SimpleAdapter {

        Context context;
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        ArrayList<View> views;
        String[] from;
        int[] to;

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

            rowView.setItemViewText(pieces.get("type") + ": " + pieces.get("name") +
                    " on " + pieces.get("date"));
            views.add(rowView);

            return rowView;
        }
    }

    //Layout for the timeline items
    private class TimelineLayout extends RelativeLayout {

        TextView itemView = new TextView(getContext());
        CardView button;

        public String type = "";
        public String name = "";
        public String subject = "";
        public String date = "";
        public String textText = "";

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
                    CRMContract.CollaborateeTable.CONTENT_URI,
                    new String[] {CRMContract.CollaborateeTable.PARTNER_ID, PARTNER_NAME, CRMContract.CollaborateeTable.EMAIL,
                            CRMContract.CollaborateeTable.PHONE, CRMContract.CollaborateeTable.ADDRESS_1, CRMContract.CollaborateeTable.CITY,
                            CRMContract.CollaborateeTable.STATE_PROVINCE, CRMContract.CollaborateeTable.POSTAL_CODE, CRMContract.CollaborateeTable.CELL,
                            CRMContract.CollaborateeTable.SURNAME, CRMContract.CollaborateeTable.GIVEN_NAMES, CRMContract.CollaborateeTable.PHONE_ID,
                            CRMContract.CollaborateeTable.CELL_ID, CRMContract.CollaborateeTable.EMAIL_ID, CRMContract.CollaborateeTable.PHONE_JSON_ID,
                            CRMContract.CollaborateeTable.CELL_JSON_ID, CRMContract.CollaborateeTable.EMAIL_JSON_ID, CRMContract.CollaborateeTable.ADDRESS_JSON_ID,
                            CRMContract.CollaborateeTable.PARTNER_JSON_ID, CRMContract.CollaborateeTable.PROFILE_PICTURE},
                    CRMContract.CollaborateeTable.PARTNER_ID + " = ?",
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
                    values.put(CRMContract.CollaborateeTable.COLLABORATER_ID, collaboratee.PartnerId);
                }if (collaboratee.PartnerName != null) {
                    values.put(PARTNER_NAME, collaboratee.PartnerName);
                }if (collaboratee.Surname != null) {
                    values.put(CRMContract.CollaborateeTable.SURNAME, collaboratee.Surname);
                }if (collaboratee.GivenNames != null) {
                    values.put(CRMContract.CollaborateeTable.GIVEN_NAMES, collaboratee.GivenNames);
                }if (collaboratee.Phone != null) {
                    values.put(CRMContract.CollaborateeTable.PHONE, collaboratee.Phone);
                }if (collaboratee.Cell != null) {
                    values.put(CRMContract.CollaborateeTable.CELL, collaboratee.Cell);
                }if (collaboratee.Email != null) {
                    values.put(CRMContract.CollaborateeTable.EMAIL, collaboratee.Email);
                }if (collaboratee.Address1 != null) {
                    values.put(CRMContract.CollaborateeTable.ADDRESS_1, collaboratee.Address1);
                }if (collaboratee.City != null) {
                    values.put(CRMContract.CollaborateeTable.CITY, collaboratee.City);
                }if (collaboratee.StateProvince != null) {
                    values.put(CRMContract.CollaborateeTable.STATE_PROVINCE, collaboratee.StateProvince);
                }if (collaboratee.PostalCode != null) {
                    values.put(CRMContract.CollaborateeTable.POSTAL_CODE, collaboratee.PostalCode);
                }if (collaboratee.PhoneId != null) {
                    values.put(CRMContract.CollaborateeTable.PHONE_ID, collaboratee.PhoneId);
                }if (collaboratee.EmailId != null) {
                    values.put(CRMContract.CollaborateeTable.EMAIL_ID, collaboratee.EmailId);
                }if (collaboratee.CellId != null) {
                    values.put(CRMContract.CollaborateeTable.CELL_ID, collaboratee.CellId);
                }if (collaboratee.PhoneJsonId != null) {
                    values.put(CRMContract.CollaborateeTable.PHONE_JSON_ID, collaboratee.PhoneJsonId);
                }if (collaboratee.CellJsonId != null) {
                    values.put(CRMContract.CollaborateeTable.CELL_JSON_ID, collaboratee.CellJsonId);
                }if (collaboratee.EmailJsonId != null) {
                    values.put(CRMContract.CollaborateeTable.EMAIL_JSON_ID, collaboratee.EmailJsonId);
                }if (collaboratee.AddressJsonId != null) {
                    values.put(CRMContract.CollaborateeTable.ADDRESS_JSON_ID, collaboratee.AddressJsonId);
                }if (collaboratee.PartnerJsonId != null) {
                    values.put(CRMContract.CollaborateeTable.PARTNER_JSON_ID, collaboratee.PartnerJsonId);
                }if (collaboratee.ProfilePictureFilename != null) {
                    saveImageFromUrl(mAccountManager.getUserData(mAccount, "server"), getApplicationContext(), collaboratee.ProfilePictureFilename);
                    values.put(CRMContract.CollaborateeTable.PROFILE_PICTURE, collaboratee.ProfilePictureFilename);
                }

                //put new collaboratee info into database
                getApplicationContext().getContentResolver().update(CRMContract.CollaborateeTable.CONTENT_URI, values,
                        CRMContract.CollaborateeTable.PARTNER_ID + "= ?", new String[] {mPartnerId});

                //pull stuff back out of the database
                //this gets the original data back in case kardia returned nothing
                Cursor cursor2 = getContentResolver().query(
                        CRMContract.CollaborateeTable.CONTENT_URI,
                        new String[] {CRMContract.CollaborateeTable.PARTNER_ID, PARTNER_NAME, CRMContract.CollaborateeTable.EMAIL,
                                CRMContract.CollaborateeTable.PHONE, CRMContract.CollaborateeTable.ADDRESS_1, CRMContract.CollaborateeTable.CITY,
                                CRMContract.CollaborateeTable.STATE_PROVINCE, CRMContract.CollaborateeTable.POSTAL_CODE, CRMContract.CollaborateeTable.CELL,
                                CRMContract.CollaborateeTable.SURNAME, CRMContract.CollaborateeTable.GIVEN_NAMES, CRMContract.CollaborateeTable.PHONE_ID,
                                CRMContract.CollaborateeTable.CELL_ID, CRMContract.CollaborateeTable.EMAIL_ID, CRMContract.CollaborateeTable.PHONE_JSON_ID,
                                CRMContract.CollaborateeTable.CELL_JSON_ID, CRMContract.CollaborateeTable.EMAIL_JSON_ID, CRMContract.CollaborateeTable.ADDRESS_JSON_ID,
                                CRMContract.CollaborateeTable.PARTNER_JSON_ID, CRMContract.CollaborateeTable.PROFILE_PICTURE},
                        CRMContract.CollaborateeTable.PARTNER_ID + " = ?",
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
            View appBarView = findViewById(R.id.appbarlayout_profile);
            int width = appBarView.getWidth();
            int height = appBarView.getHeight();

            if (profilePictureFilename == null || profilePictureFilename.equals(""))
            {
                Picasso.with(getApplication())
                        .load(R.drawable.persona)
                        .resize(width, height)
                        .into(((ImageView) findViewById(R.id.backdrop_profile)));
            }
            else
            {
                int indexoffileName = profilePictureFilename.lastIndexOf("/");
                String finalPath = directory + "/" + profilePictureFilename.substring(indexoffileName + 1);
                Picasso.with(getApplication())
                        .load(new File(finalPath))
                        .placeholder(R.drawable.persona)
                        .resize(width, height)
                        .into(((ImageView) findViewById(R.id.backdrop_profile)));
            }

            //TODO: Rename this crap
            TextView mTextView = (TextView) findViewById(R.id.e_address);
            mTextView.setText(mEmail);

            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent eIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mEmail, null));
                    startActivity(Intent.createChooser(eIntent, "Send email..."));
                    //TODO: Ask to record
                }
            });

            TextView mTextView2 = (TextView) findViewById(R.id.phone_number);
            if(mCell != null) {
                mTextView2.setText(mCell);
                phones = mCell;
            }
            else if (mPhone!=null){
                mTextView2.setText(mPhone);
                phones = mPhone;
            }
            mTextView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tele = "+" + phones.replaceAll("[^0-9.]", "");
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", tele, null));
                    startActivity(intent);
                    //TODO: Ask to record
                }
            });


            TextView mTextView3 = (TextView) findViewById(R.id.s_address);
            mTextView3.setText(mAddress + ", " + mCity + ", " + mState + ", " + mPostalCode);

            mTextView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://www.google.com/maps/search/?api=1&query=" + mAddress + "%2C+" + mCity + "%2C+" + mState + "%2C+" + mPostalCode;
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }
            });
        }
    }

    public static void saveImageFromUrl(String server, Context context, String profilePictureFilename)
    {
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
    private boolean networkConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}


