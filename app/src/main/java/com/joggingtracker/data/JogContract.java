package com.joggingtracker.data;

import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class JogContract {

    public static final String AUTHORITY = "com.joggingtracker.data.JogsContentProvider.java";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class JogEntry implements BaseColumns {
        public static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath("jogs")
                .build();

        public static Uri buildJogUriWithDate(long jogDate) {
            Uri uri = CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(jogDate))
                    .build();
            return uri;
        }

        public static final String TABLE_NAME = "jogs";
        public static final String COLUMN_JOG_DATE_TIME = "jog_date_time";
        public static final String COLUMN_JOG_TIME_LENGTH = "jog_time_length";
        public static final String COLUMN_JOG_MILES_LENGTH = "jog_miles_length";
        public static final String COLUMN_JOG_PACE = "jog_pace";
        public static final String COLUMN_JOG_PATH_JSON = "jog_path_json";
    }
}
