package com.example.android.unicarsindos.data;

import android.media.Image;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.android.unicarsindos.R;

/**
 * Created on 19-Dec-17.
 * This class is the contract which holds all the constants
 * for the Database and the Content Provider.
 */

public class UniCarContract {

    public static final String AUTHORITY = "com.example.android.unicarsindos";

    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+ AUTHORITY);

    public static final String PATH_USERS= "users";




    public static final class UniCarEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();


        public static final String TABLE_NAME = "users";

        public static final String COLUMN_FIRST_NAME = "fistname";
        public static final String COLUMN_SECOND_NAME = "secondname";
        public static final String COLUMN_EMAIL= "email";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_PASSWORD= "password";
        public static final String COLUMN_HAS_PROFILE="hasprofile";
        public static final String COLUMN_OFFER_RIDE ="offerride";
        public static final String COLUMN_ADDRESS="address";
        public static final String COLUMN_DAYS_HOURS="dayshours";
        public static final String COLUMN_INFORMATION="information";
        public static final String COLUMN_AREA="area";
        public static final String COLUMN_LATITUDE="latitude";
        public static final String COLUMN_LONGITUDE="longitude";

    }
}
