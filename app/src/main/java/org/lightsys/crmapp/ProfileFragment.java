package org.lightsys.crmapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.lightsys.crmapp.data.CRMContract;
import org.lightsys.crmapp.data.KardiaFetcher;
import org.lightsys.crmapp.data.Partner;
import org.lightsys.crmapp.data.TimelineItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cubemaster on 3/10/16.
 * Edited by Ca2br and Judah on 7/19/16
 *
 * Holds contact information for a partner.
 * Lists timeline items for a partner.
 */
public class ProfileFragment extends Fragment {
    private static final String LOG_TAG = ProfileFragment.class.getName();

    private String mName;
    private String mPartnerId;

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

    private String mPhoneJsonId;
    private String mCellJsonId;
    private String mEmailJsonId;
    private String mAddressJsonId;
    private String mPartnerJsonId;

    private String mBlog;
    private String mFax;
    private String mFacebook;
    private String mSkype;
    private String mTwitter;
    private String mWebsite;

    private String phonez = "";

    private Account mAccount;
    private List<TimelineItem> mItems = new ArrayList<>();


    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Creates and assigns all of the textViews for a profile.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        if(arguments != null) {
            mName = arguments.getString(ProfileActivity.NAME_KEY);
            mPartnerId = arguments.getString(ProfileActivity.PARTNER_ID_KEY);

            mEmail = arguments.getString(ProfileActivity.EMAIL_KEY);
            mPhone = arguments.getString(ProfileActivity.PHONE_KEY);
            mAddress = arguments.getString(ProfileActivity.ADDRESS_KEY);
            mCity = arguments.getString(ProfileActivity.CITY_KEY);
            mState = arguments.getString(ProfileActivity.STATE_KEY);
            mPostalCode = arguments.getString(ProfileActivity.POSTALCODE_KEY);
            mFullAddress = arguments.getString(ProfileActivity.FULLADDRESS_KEY);
            mCell = arguments.getString(ProfileActivity.CELL_KEY);
            mSurname = arguments.getString(ProfileActivity.SURNAME_KEY);
            mGivenName = arguments.getString(ProfileActivity.GIVEN_NAMES_KEY);

            mPhoneJsonId = arguments.getString(ProfileActivity.PHONE_JSON_ID_KEY);
            mCellJsonId = arguments.getString(ProfileActivity.CELL_JSON_ID_KEY);
            mEmailJsonId = arguments.getString(ProfileActivity.EMAIL_JSON_ID_KEY);
            mAddressJsonId = arguments.getString(ProfileActivity.ADDRESS_JSON_ID_KEY);
            mPartnerJsonId = arguments.getString(ProfileActivity.PARTNER_JSON_ID_KEY);


            mBlog = arguments.getString(ProfileActivity.BLOG_KEY);
            mFax = arguments.getString(ProfileActivity.FAX_KEY);
            mFacebook = arguments.getString(ProfileActivity.FACEBOOK_KEY);
            mSkype = arguments.getString(ProfileActivity.SKYPE_KEY);
            mTwitter = arguments.getString(ProfileActivity.TWITTER_KEY);
            mWebsite = arguments.getString(ProfileActivity.WEBSITE_KEY);


        }

        /*
        Should take contact info and place it into the correct place for
            that particular contact's profile when that contact is selected
         */

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);


        TextView mTextView = (TextView) rootView.findViewById(R.id.e_address);
        mTextView.setText(mEmail);

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mEmail, null));
                startActivity(Intent.createChooser(eIntent, "Send email..."));
            }
        });


        TextView mTextView2 = (TextView) rootView.findViewById(R.id.phone_number);
        if(mCell != null) {
            mTextView2.setText(mCell);
            phonez = mCell;
        }
        else if (mPhone!=null){
            mTextView2.setText(mPhone);
            phonez = mPhone;
        }
        mTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tele = "+" + phonez.replaceAll("[^0-9.]", "");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", tele, null));
                startActivity(intent);
            }
        });


        TextView mTextView3 = (TextView) rootView.findViewById(R.id.s_address);
        mTextView3.setText(mAddress + ", " + mCity + ", " + mState + ", " + mPostalCode);

        mTextView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://maps.google.co.in/maps?q=" + mFullAddress;
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            }
        });

        AccountManager accountManager = AccountManager.get(getActivity());
        Account[] accounts = accountManager.getAccountsByType(CRMContract.accountType);
        if(accounts.length > 0) {
            mAccount = accounts[0];
            new getTimelineTask().execute();
        }

        return rootView;
    }

    //sorts items by date
    //puts newest items first
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

    private int[] parseStringDate(String date) {
        int[] dateInt = new int[3];
        String[] dateSplitStr = date.split("-");
        for (int i = 0; i < 3; i++) {
            dateInt[i] = Integer.parseInt(dateSplitStr[i]);
        }
        return dateInt;
    }

    /**
     * Sets up adapter for list of timeline items.
     */
    private void setupAdapter() {
        if (isAdded()) {

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
            if (mItems != null) {
                //if we have comments, set them to the adapter
                TimeLineAdapter adapter = new TimeLineAdapter(getActivity(), items, R.layout.timeline_item_layout, from, to);

                ListView listview = (ListView) getActivity().findViewById(R.id.timelineList);
                listview.setAdapter(adapter);
            }
        }
    }

    /**
     * Fetches timeline info from Kardia.
     */
    private class getTimelineTask extends AsyncTask<Void, Void, List<TimelineItem>> {
        @Override
        protected List<TimelineItem> doInBackground(Void... params) {
            KardiaFetcher fetcher = new KardiaFetcher(getContext());
            List<TimelineItem> items;
            AccountManager.get(getContext()).setUserData(mAccount, "collabId", mPartnerId);
            items = fetcher.getTimelineItems(mAccount);

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
                getContext().getContentResolver().insert(CRMContract.TimelineTable.CONTENT_URI, values);
            }

            Cursor cursor = getActivity().getContentResolver().query(
                    CRMContract.TimelineTable.CONTENT_URI,
                    new String[] {CRMContract.TimelineTable.CONTACT_ID, CRMContract.TimelineTable.PARTNER_ID,
                            CRMContract.TimelineTable.COLLABORATEE_ID, CRMContract.TimelineTable.COLLABORATEE_NAME,
                            CRMContract.TimelineTable.CONTACT_HISTORY_ID, CRMContract.TimelineTable.CONTACT_HISTORY_TYPE,
                            CRMContract.TimelineTable.SUBJECT, CRMContract.TimelineTable.NOTES,
                            CRMContract.TimelineTable.DATE},
                    CRMContract.TimelineTable.CONTACT_ID + " = ?",
                    new String[] {AccountManager.get(getActivity()).getUserData(mAccount, "partnerId")},
                    null
            );

            while(cursor.moveToNext()) {
                TimelineItem item = new TimelineItem(cursor.getString(0));
                item.setPartnerId(cursor.getString(1));
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


        @Override
        protected void onPostExecute(List<TimelineItem> items) {
            mItems = sortByDate(items);
            setupAdapter();
        }

    }


    //adapter for the timeline items
    private class TimeLineAdapter extends SimpleAdapter{

        Context context;
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        ArrayList<View> views;
        String[] from;
        int[] to;

        public TimeLineAdapter(Context context, ArrayList<HashMap<String, String>> data,
                               int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            this.context = context;
            this.views = new ArrayList<View>();
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
                rowView = new TimelineLayout(getContext());
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

    //layout for the timeline items
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

}
