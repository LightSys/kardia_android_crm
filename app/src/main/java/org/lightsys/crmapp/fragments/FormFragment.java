package org.lightsys.crmapp.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.data.infoTypes.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author otter57
 * created on 9/14/2017.
 *
 * Displays sign up sheet, allows users to add themselves to the list
 *
 */
public class FormFragment extends Fragment {

    private ArrayList<Connection> formConnections;
    private List<Integer> forms;
    final static public String FORM_ID = "form_id";
    final static public String NEW_SIGN_UP = "sign_up_info";
    private int formId = -1;
    private TableRow addPersonButton;
    private String TAG = "SignUpListFrag";
    private LayoutInflater inflater;
    private TableLayout table;

    //keeps a list of all comments on this post
    private final ArrayList<HashMap<String, String>> commentList = new ArrayList<HashMap<String, String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sign_up_sheet_table_layout, container, false);
        getActivity().setTitle("Sign up form");
        this.inflater = inflater;
        table = (TableLayout) v;

        formId = getArguments().getInt(FORM_ID);


        return v;
    }

    public void onStart(){
        super.onStart();

        getFormData(formId);

        Bundle args = getArguments();

        displayPeople();

        setUpAddButton();
    }

    private void setUpAddButton(){
        TableRow addPersonButton = (TableRow) inflater.inflate(R.layout.sign_up_element_table_row,
                (ViewGroup) getView().findViewById(R.id.add_sign_up), false);

        TextView prompt = (TextView) addPersonButton.findViewById(R.id.name);
        prompt.setText(R.string.sign_up_prompt);
        prompt.setTextColor(getResources().getColor(R.color.green));

        table.addView(addPersonButton);

        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpFragment newFrag = new SignUpFragment();

                Bundle args = new Bundle();
                args.putInt(FormListFragment.FORM_ID, formId);

                newFrag.setArguments(args);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_profile_input_container, newFrag, "SignUp");
                transaction.addToBackStack("SignUp");
                transaction.commit();
            }
        });
    }

    //loads a list of people on sign up list
    private void displayPeople() {

        if (formConnections != null) {
            Log.d(TAG, "displayPeople: " + formConnections.size());
            for (Connection person : formConnections) {
                View childLayout = inflater.inflate(R.layout.sign_up_element_table_row,null);

                TextView name = (TextView) childLayout.findViewById(R.id.name);
                TextView email = (TextView) childLayout.findViewById(R.id.email);
                TextView gradYear = (TextView) childLayout.findViewById(R.id.grad_year);
                CheckBox internship = (CheckBox) childLayout.findViewById(R.id.internship);
                CheckBox oneYear = (CheckBox) childLayout.findViewById(R.id.one_year);
                CheckBox longTerm = (CheckBox) childLayout.findViewById(R.id.long_term);
                CheckBox springBreak = (CheckBox) childLayout.findViewById(R.id.spring_break);

                String[] nameArray = person.getName().split(", ");
                String nameText="";
                if(nameArray.length==2) {
                    nameText = nameArray[1] + " " + nameArray[0];
                }else{
                    nameText=person.getName().replace(","," ");
                }

                name.setText(nameText);
                email.setText(R.string.check_mark);
                gradYear.setText(person.getGradYear());

                String tags = person.getTags();

                internship.setChecked(tags.contains("internship"));
                oneYear.setChecked(tags.contains("oneYear"));
                longTerm.setChecked(tags.contains("longTerm"));
                springBreak.setChecked(tags.contains("springBreak"));

                table.addView(childLayout);
            }
        }
    }

    private void getFormData(int formId){
        formConnections =  new ArrayList<>();

        Log.d(TAG, "getFormData: " + formId);

        Cursor cursor = getActivity().getContentResolver().query(
                LocalDBTables.ConnectionTable.CONTENT_URI,
                new String[] {LocalDBTables.ConnectionTable.CONNECTION_NAME,
                        LocalDBTables.ConnectionTable.CONNECTION_EMAIL,
                        LocalDBTables.ConnectionTable.CONNECTION_GRAD_YEAR,
                        LocalDBTables.ConnectionTable.CONNECTION_TAGS},
                LocalDBTables.ConnectionTable.FORM_ID + " = " + formId,
                null,
                null
        );

        try {
            while (cursor.moveToNext()) {
                Connection temp = new Connection();
                temp.setName(cursor.getString(0));
                temp.setEmail(cursor.getString(1));
                temp.setGradYear(cursor.getString(2));
                temp.setTags(cursor.getString(3));
                formConnections.add(temp);
            }
        }catch(NullPointerException ne) {
            ne.printStackTrace();
        }
        cursor.close();
    }

    /**
     * Used to hold onto the id, in case the user comes back to this page
     * (like if their phone goes into sleep mode or they temporarily leave the app)
     */
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(FORM_ID, formId);
    }
}
