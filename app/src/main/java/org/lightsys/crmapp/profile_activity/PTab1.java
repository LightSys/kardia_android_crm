package org.lightsys.crmapp.profile_activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.lightsys.crmapp.R;
import org.lightsys.crmapp.data.LocalDatabaseHelper;

/**
 * Created by Jake- on 7/1/2015.
 */
public class PTab1 extends Fragment {

    ImageView imageView;
    String profilePictureURL;
    String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.ptab1,container,false);



        try {
            imageView = (ImageView) v.findViewById(R.id.profilePictureImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        profileId = ((ProfileActivity) getActivity()).getPartnerId();

        LocalDatabaseHelper db = new LocalDatabaseHelper(getActivity());
        profilePictureURL = db.getProfilePictureURL(profileId);


        Picasso.with(getActivity())
                .load("http://10.5.10.63:800/apps/kardia/api/crm/Partners/100001/ProfilePicture/LOGO-4ff1a9a861e6.png")
                .error(R.drawable.abc_dialog_material_background_dark)
                .into(imageView);

        return v;
    }

}
