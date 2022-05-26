package org.lightsys.crmapp.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.activities.FormActivity;
import org.lightsys.crmapp.activities.MainActivity;
import org.lightsys.crmapp.data.LocalDBTables;

import java.util.ArrayList;

import static org.lightsys.crmapp.fragments.FormFragment.FORM_ID;


/**
 * Created by otter57 on 9/14/17.
 *
 * Allows a user to create a form.
 *
 */
public class AddFormFragment extends Fragment {
    private static final String LOG_TAG = AddFormFragment.class.getName();

    private TextView descriptionTxt;
    private LinearLayout formTagParent, signUpTagParent;
    private Spinner formTagSpinner, signUpTagSpinner, signUpSkillsTagSpinner;
    private String mFormDescription;
    private String formTags = ",", formSignUpTags = ",", skillsTag = ",";
    private String mEvent;
    private String mDescription;
    private int formId;
    private LayoutInflater inflater;
    private Spinner type;
    ArrayAdapter<String> spinnerArrayAdapter;
    RadioButton signUpRadio;
    CheckBox phoneCheckbox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        formId = getArguments().getInt(FORM_ID);
        this.inflater = inflater;
        final View rootView = inflater.inflate(R.layout.fragment_form_input, container, false);
        getActivity().setTitle("Create form");

        getActivity().getSupportFragmentManager().popBackStack("AddForm", 0);

        // Sets up views.
        ArrayList<String> allTags = getAllTags();
        formTagSpinner = (Spinner) rootView.findViewById(R.id.tagSpinnerForm);
        signUpTagSpinner = (Spinner) rootView.findViewById(R.id.tagSpinnerSignUp);
        signUpSkillsTagSpinner = (Spinner) rootView.findViewById(R.id.tagSpinnerSkills);
        descriptionTxt = (TextView) rootView.findViewById(R.id.descriptionFormET);
        phoneCheckbox = (CheckBox) rootView.findViewById(R.id.tagCheckbox);

        Button submit = (Button) rootView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFormDescription = descriptionTxt.getText().toString();

                //submitOnClick();
                addFormToDB();
                openForm(formId);
            }
        });

        // checks if form is simple sign up or more detailed info request
        type = (Spinner) rootView.findViewById(R.id.formTypeSpinner);
        final LinearLayout advancedSettings = (LinearLayout) rootView.findViewById(R.id.advancedSettingsLayout);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    advancedSettings.setVisibility(View.GONE);
                }else{
                    advancedSettings.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //adds items to Spinners
        spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, allTags);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item); // The drop down view
        formTagSpinner.setAdapter(spinnerArrayAdapter);
        signUpTagSpinner.setAdapter(spinnerArrayAdapter);
        signUpSkillsTagSpinner.setAdapter(spinnerArrayAdapter);


        // add tags to form
        rootView.findViewById(R.id.add_button_form).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formTagSpinner.getSelectedItem()!=null) {

                    String newTag = formTagSpinner.getSelectedItem().toString();
                    String newTagDesc = ((EditText) rootView.findViewById(R.id.descriptionET)).getText().toString();
                    if (!newTagDesc.equals("")) {
                        newTag += " (" + newTagDesc.replace(",", "") + ")";
                        ((EditText) rootView.findViewById(R.id.descriptionET)).setText(null);
                    }
                    addTag(rootView, (LinearLayout) rootView.findViewById(R.id.formItems), newTag);
                    formTags = (formTags == null) ? newTag + "," : formTags + newTag + ",";

                    spinnerArrayAdapter.remove((String) formTagSpinner.getSelectedItem());
                    spinnerArrayAdapter.notifyDataSetChanged();
                }

            }
        });

        //add tags to user input
        rootView.findViewById(R.id.add_button_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signUpTagSpinner.getSelectedItem()!=null) {

                    String newTag = signUpTagSpinner.getSelectedItem().toString();
                    addTag(rootView, (LinearLayout) rootView.findViewById(R.id.signUpInterestedItems), newTag);
                    formSignUpTags = (formSignUpTags == null) ? newTag + "," : formSignUpTags + newTag + ",";

                    spinnerArrayAdapter.remove((String) signUpTagSpinner.getSelectedItem());
                    spinnerArrayAdapter.notifyDataSetChanged();
                }
            }
        });

        // add tags to user input skills section
        rootView.findViewById(R.id.add_button_sign_up_skills).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signUpSkillsTagSpinner.getSelectedItem()!=null) {
                    String newTag = signUpSkillsTagSpinner.getSelectedItem().toString();
                    addTag(rootView, (LinearLayout) rootView.findViewById(R.id.signUpSkillsItems), newTag);
                    skillsTag = (skillsTag == null) ? newTag + "," : skillsTag + newTag + ",";

                    spinnerArrayAdapter.remove((String) signUpSkillsTagSpinner.getSelectedItem());
                    spinnerArrayAdapter.notifyDataSetChanged();
                }
            }
        });

        ((MainActivity) getActivity()).showNavButton(false);

        ((MainActivity) getActivity()).changeOptionsMenu(false, true);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        ((MainActivity) getActivity()).showNavButton(true);
        super.onDestroyView();
    }

    private void addTag(View v, final LinearLayout parent, final String Tag) {

        final LinearLayout childLayout = (LinearLayout) inflater.inflate(R.layout.tag_element,
                (ViewGroup) v.findViewById(R.id.form_tag), false);
        ((TextView) childLayout.findViewById(R.id.tag_text)).setText(Tag);

        childLayout.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.removeView(childLayout);
                switch (parent.getId()) {
                    case R.id.formItems:
                        formTags = formTags.replace(Tag + ",", "");
                        break;
                    case R.id.signUpInterestedItems:
                        formSignUpTags = formSignUpTags.replace(Tag + ",", "");
                        break;
                    case R.id.signUpSkillsItems:
                        skillsTag = skillsTag.replace(Tag + ",", "");
                        break;
                }
                spinnerArrayAdapter.add(Tag);
                spinnerArrayAdapter.notifyDataSetChanged();
            }
        });

        parent.addView(childLayout);
    }

    private ArrayList<String> getAllTags() {
        ArrayList<String> tags = new ArrayList<>();

        Cursor cursor = this.getActivity().getContentResolver().query(
                LocalDBTables.TagTable.CONTENT_URI,
                new String[]{LocalDBTables.TagTable.TAG_ID,
                        LocalDBTables.TagTable.TAG_LABEL,
                        LocalDBTables.TagTable.TAG_DESC,
                        LocalDBTables.TagTable.TAG_ACTIVE},
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                tags.add(cursor.getString(1));
            }
            cursor.close();
        }
        return tags;
    }


    private void addFormToDB() {

        String stringDate = (android.text.format.DateFormat.format("MM/dd/yyyy", new java.util.Date())).toString();
        if (type.getSelectedItemPosition() == 1) {
            formSignUpTags = formSignUpTags + "infoRequestSkillsTag:" + skillsTag;
        }

        ContentValues values = new ContentValues();
        values.put(LocalDBTables.FormTable.FORM_ID, formId);
        values.put(LocalDBTables.FormTable.FORM_TAGS, formTags);
        values.put(LocalDBTables.FormTable.FORM_DESCRIPTION, mFormDescription);
        values.put(LocalDBTables.FormTable.FORM_SIGN_UP_TAGS, formSignUpTags);
        values.put(LocalDBTables.FormTable.FORM_DATE, stringDate);
        getActivity().getContentResolver().insert(LocalDBTables.FormTable.CONTENT_URI, values);

    }

    private void openForm(int formId) {
        FormListFragment newFrag = new FormListFragment();

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, newFrag, "FormList")
                .addToBackStack("FormList").commit();

        Intent i = new Intent(getActivity(), FormActivity.class);
        i.putExtra(FORM_ID, formId);
        startActivity(i);
    }
}