package org.lightsys.crmapp.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jake on 7/14/2015.
 */
public class GsonProfilePicture {
    @SerializedName("@id")
    String kardiaRef;

    @SerializedName("name")
    String name;

    @SerializedName("photo_id")
    String photoId;

    @SerializedName("photo_type")
    String photoType;

    @SerializedName("photo_title")
    String photoTitle;

    @SerializedName("photo_filename")
    String fileName;

    @SerializedName("photo_folder")
    String folder;
}
