package org.lightsys.crmapp.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.activities.FormActivity;
import org.lightsys.crmapp.activities.MainActivity;
import org.lightsys.crmapp.data.LocalDBTables;
import org.lightsys.crmapp.data.infoTypes.Connection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author otter57
 * created on 9/14/2017.
 *
 * Displays sign up sheet, allows users to add themselves to the list
 *
 */
public class FormFragment extends Fragment{

    private ArrayList<Connection> formConnections;
    private List<Integer> forms;
    final static public String FORM_ID = "form_id";
    final static public String NEW_SIGN_UP = "sign_up_info";
    private int formId = -1;
    private TableRow addPersonButton;
    private String TAG = "SignUpListFrag";
    private LayoutInflater inflater;
    private TableLayout table;
    private String mText;
    private boolean correctPassword = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sign_up_sheet_table_layout, container, false);
        getActivity().setTitle("Sign up form");
        this.inflater = inflater;
        table = (TableLayout) v;

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        formId = getArguments().getInt(FORM_ID);

        getActivity().getSupportFragmentManager().popBackStack("SignUp",0);
        getActivity().getSupportFragmentManager().popBackStack("Form",0);

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        ((MainActivity) getActivity()).showNavButton(false);
        
        return v;
    }


    public void onStart(){
        super.onStart();

        getFormData(formId);

        displayPeople();

        setUpAddButton();

        setUpCompleteButton();
    }

    public void onDestroyView(){
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE);

        ((MainActivity) getActivity()).showNavButton(true);

        super.onDestroyView();
    }

    private void setUpAddButton() {
        TableRow addPersonButton = (TableRow) inflater.inflate(R.layout.sign_up_element_table_row,
                (ViewGroup) getView().findViewById(R.id.add_sign_up), false);

        TextView prompt = (TextView) addPersonButton.findViewById(R.id.name);
        prompt.setText(R.string.sign_up_prompt);
        prompt.setTextColor(getResources().getColor(R.color.green));

        table.addView(addPersonButton);

        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correctPassword = true;
                SignUpFragment newFrag = new SignUpFragment();

                Bundle args = new Bundle();
                args.putInt(FormListFragment.FORM_ID, formId);

                newFrag.setArguments(args);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_main, newFrag, "SignUp")
                        .addToBackStack("SignUp").commit();
            }
        });
    }

    //Complete button for when sign up is over.

    private void setUpCompleteButton(){

        FrameLayout frame = new FrameLayout(this.getActivity());
        TableLayout.LayoutParams FrameLP = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout.LayoutParams ButtonLP = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        Button completeButton = new Button(this.getActivity());
        ButtonLP.gravity = Gravity.BOTTOM;
        completeButton.setPadding(15,15,15,15);
        completeButton.setBackgroundColor(getResources().getColor(R.color.primary));
        completeButton.setTextColor(getResources().getColor(R.color.white));
        completeButton.setText(R.string.complete_btn);
        completeButton.setTextSize(30);

        frame.setLayoutParams(FrameLP);
        completeButton.setLayoutParams(ButtonLP);

        frame.addView(completeButton);
        table.addView(frame);

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPasswordCorrect();
                uploadContacts();
            }
        });
    }

    //uploads contacts to server
    private void uploadContacts(){

    }

    //gets password from user and checks with account password
    private void isPasswordCorrect(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity(), R.style.MyThemeDialogCustom);
        builder.setTitle("Enter Password");

            // Set up the input
        final EditText input = new EditText(this.getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

            // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mText = input.getText().toString();
                FormListFragment newFrag = new FormListFragment();

                if(mText.equals(getPassword())) {
                    getActivity().stopLockTask();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_profile_input_container, newFrag, "Forms");
                    transaction.addToBackStack("Forms");
                    transaction.commit();
                    correctPassword = true;
                } else{
                    dialog.cancel();
                    isPasswordCorrect();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                correctPassword = false;
            }
        });

        builder.show();
    }

    //gets password for account
    private String getPassword(){
        // Gets user account.
        AccountManager mAccountManager = AccountManager.get(getContext());
        final Account[] accounts = mAccountManager.getAccounts();
        if(accounts.length > 0) {
            return mAccountManager.getPassword(accounts[0]);
        }
        return null;
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

    //gets all connections for this form
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
