package org.lightsys.crmapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import org.lightsys.crmapp.data.DataConnection;
import org.lightsys.crmapp.data.LocalDatabaseHelper;
import org.lightsys.crmapp.data.PullType;

/**
 * Created by Jake on 6/17/2015.
 */
public class PeopleTab extends Fragment {

    private ListView listView;
    public static Cursor c;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.people_tab,container,false);

        // This should be done asynchronously. With a small number of collaboratees it's not noticeable
        // but with a large number it will cause the interface to hang.
        LocalDatabaseHelper db = new LocalDatabaseHelper(getActivity());
        c = db.getCollaboratees();

        listView = (ListView) v.findViewById(R.id.myPeopleListView);

        c.moveToFirst();
        getActivity().startManagingCursor(c);

        listView.setAdapter(new SimpleCursorAdapter(getActivity(),
                R.layout.people_tab_listitem,
                c,
                new String[] {LocalDatabaseHelper.LocalDatabaseContract.MyPeopleTable.COLUMN_PARTNER_NAME}, new int[] { R.id.myPeopleTabListItem}));

        return v;
    }
}