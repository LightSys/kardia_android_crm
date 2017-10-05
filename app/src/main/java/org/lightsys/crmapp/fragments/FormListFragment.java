package org.lightsys.crmapp.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.transition.Visibility;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.activities.FormActivity;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.data.infoTypes.Form;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author otter57
 * created on 9/14/2017.
 *
 * Displays sign up sheet, allows users to add themselves to the list
 *
 */
public class FormListFragment extends Fragment {

    private ArrayList<Form> forms;
    final static public String FORM_ID = "form_id";
    final static public String NEW_SIGN_UP = "sign_up_info";
    private int formId = -1;
    private String TAG = "SignUpListFrag";
    private LayoutInflater inflater;
    private TableLayout table;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.form_list_table_layout, container, false);
        this.inflater = inflater;
        table = (TableLayout) v;
        getActivity().setTitle("Forms");

        getAllForms();

        displayForms();

        return v;
    }

    public void onStart(){
        super.onStart();

        setUpAddButton();
    }

    private void setUpAddButton(){
        TableRow addFormButton = (TableRow) inflater.inflate(R.layout.form_element_table_row,
                (ViewGroup) getView().findViewById(R.id.form_row), false);

        TextView prompt = (TextView) addFormButton.findViewById(R.id.event);
        prompt.setText(R.string.add_form_prompt);
        prompt.setTextColor(getResources().getColor(R.color.green));

        table.addView(addFormButton);

        addFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addForm();
            }
        });
    }

    //loads a list of people on sign up list
    private void displayForms() {

        if (forms != null) {
            for (Form f : forms) {
                View childLayout = inflater.inflate(R.layout.form_element_table_row, null);

                TextView university = (TextView) childLayout.findViewById(R.id.university);
                TextView desc = (TextView) childLayout.findViewById(R.id.description);
                TextView event = (TextView) childLayout.findViewById(R.id.event);
                TextView date = (TextView) childLayout.findViewById(R.id.date);

                final int Id = f.getFormId();
                university.setText(f.getUniversity());
                desc.setText(f.getDescription());
                event.setText(f.getEvent());
                date.setText(f.getDate());

                childLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //((FormActivity) getActivity()).setLocked(true);
                        onFormClicked(Id);
                    }
                });

                table.addView(childLayout);
            }
        }
    }

    private void getAllForms(){
        forms =  new ArrayList<>();

        Cursor cursor = getActivity().getContentResolver().query(
                LocalDBTables.FormTable.CONTENT_URI,
                new String[] {LocalDBTables.FormTable.FORM_ID,
                        LocalDBTables.FormTable.FORM_DATE,
                        LocalDBTables.FormTable.FORM_TYPE,
                        LocalDBTables.FormTable.FORM_UNIVERSITY,
                        LocalDBTables.FormTable.FORM_DESC,
                        LocalDBTables.FormTable.FORM_EVENT
                        },
                null,
                null,
                LocalDBTables.FormTable.FORM_ID + " DESC"
        );

        try {
            while (cursor.moveToNext()) {
                Form temp = new Form();
                formId = cursor.getInt(0);
                temp.setFormId(formId);
                temp.setDate(cursor.getString(1));
                temp.setFormType(cursor.getString(2));
                temp.setUniversity(cursor.getString(3));
                temp.setDescription(cursor.getString(4));
                temp.setEvent(cursor.getString(5));

                forms.add(temp);
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
    }

    public void onFormClicked(int formId){
        FormFragment newFrag = new FormFragment();

        Bundle args = new Bundle();
        args.putInt(FORM_ID, formId);

        newFrag.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, newFrag, "Form")
                .addToBackStack("Form").commit();
    }

    public void addForm (){
        AddFormFragment newFrag = new AddFormFragment();

        Bundle args = new Bundle();
        args.putInt(FORM_ID, formId + 1);

        newFrag.setArguments(args);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main, newFrag, "AddForm");
        transaction.commit();
    }
}
