package org.lightsys.crmapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.squareup.picasso.Picasso;

/**
 * Created by cubemaster on 3/11/16.
 */
public class ProfileInputFragment extends Fragment {
    private static final String LOG_TAG = ProfileFragment.class.getName();

    private String mName;
    private String mPartnerId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mName = arguments.getString(ProfileActivity.NAME_KEY);
            mPartnerId = arguments.getString(ProfileActivity.PARTNER_ID_KEY);
        }

        View rootView = inflater.inflate(R.layout.fragment_profile_input, container, false);

        Picasso.with(getActivity())
                .load(R.drawable.persona)
                .into(((ImageView) rootView.findViewById(R.id.profile_input_photo)));

        Spinner spinner = (Spinner) rootView.findViewById(R.id.profile_input_phone_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.name_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
//                R.array.name_spinner,android. R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return rootView;
    }
}
