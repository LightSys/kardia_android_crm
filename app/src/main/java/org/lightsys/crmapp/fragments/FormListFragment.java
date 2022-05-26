package org.lightsys.crmapp.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.activities.FormActivity;
import org.lightsys.crmapp.activities.MainActivity;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.models.Form;

import java.util.ArrayList;

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
    private int formId = -1;
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

        getActivity().getSupportFragmentManager().popBackStack("AddForm",0);
        getActivity().getSupportFragmentManager().popBackStack("FormList",0);

        ((MainActivity) getActivity()).changeOptionsMenu(false, false);

        setUpAddButton(v);

        return v;
    }

    @Override
    public void onDestroyView(){
        ((MainActivity) getActivity()).changeOptionsMenu(true, false);

        super.onDestroyView();
    }

    private void setUpAddButton(View v){
        TableRow addFormButton = (TableRow) inflater.inflate(R.layout.form_element_table_row,
                (ViewGroup) v.findViewById(R.id.form_row), false);

        TextView prompt = (TextView) addFormButton.findViewById(R.id.description);
        prompt.setText(R.string.add_form_prompt);
        prompt.setTextColor(ContextCompat.getColor(getContext(), R.color.green));

        table.addView(addFormButton);

        addFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addForm();
            }
        });
    }

    //loads a list of forms
    private void displayForms() {

        if (forms != null) {
            for (Form f : forms) {
                View childLayout = LayoutInflater.from(this.getContext()).inflate(R.layout.form_element_table_row, table, false);

                TextView description = (TextView) childLayout.findViewById(R.id.description);
                TextView date = (TextView) childLayout.findViewById(R.id.date);

                final int Id = f.getFormId();

                description.setText(f.getFormDescription());
                date.setText(f.getDate());

                childLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
                        LocalDBTables.FormTable.FORM_DESCRIPTION,
                        LocalDBTables.FormTable.FORM_TAGS,
                        LocalDBTables.FormTable.FORM_SIGN_UP_TAGS
                        },
                null,
                null,
                LocalDBTables.FormTable.FORM_ID + " ASC"
        );

        if (cursor != null){
            while (cursor.moveToNext()) {
                Form temp = new Form();
                formId = cursor.getInt(0);
                temp.setFormId(formId);
                temp.setDate(cursor.getString(1));
                temp.setFormDescription(cursor.getString(2));
                temp.setFormTags(cursor.getString(3));
                temp.setSignUpTags(cursor.getString(4));

                forms.add(temp);
            }
            cursor.close();
        }
    }

    private void onFormClicked(int formId){
        Intent i = new Intent(getActivity(), FormActivity.class);
        i.putExtra(FORM_ID, formId);
        startActivity(i);
    }

    private void addForm(){
        AddFormFragment newFrag = new AddFormFragment();

        Bundle args = new Bundle();
        args.putInt(FORM_ID, formId + 1);

        newFrag.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, newFrag, "AddForm")
                .addToBackStack("AddForm").commit();
    }
}
