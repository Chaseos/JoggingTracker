package odometer.hfad.com.joggingtracker.data;

import android.provider.BaseColumns;

public class JogContract {

    public static final String AUTHORITY = "odometer.hfad.com.joggingtracker.data.JogsContentProvider";

    public static final class JogEntry implements BaseColumns {
        public static final String TABLE_NAME = "jogs";
        public static final String COLUMN_JOG_DATE_TIME = "jog_date_time";
        public static final String COLUMN_JOG_TIME_LENGTH = "jog_time_length";
        public static final String COLUMN_JOG_MILES_LENGTH = "jog_miles_length";
        public static final String COLUMN_JOG_PACE = "jog_pace";
        public static final String COLUMN_JOG_PATH_JSON = "jog_path_json";
    }
}
