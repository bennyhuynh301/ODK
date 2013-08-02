package org.ucb.collect.android.tracker;

public final class ActivityUtils {
    public enum REQUEST_TYPE {ADD, REMOVE}
    public static final String APPTAG = "ActivityRecogition";
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static final String ACTION_CONNECTION_ERROR =
            "com.example.android.activityrecognition.ACTION_CONNECTION_ERROR";
    public static final String ACTION_REFRESH_STATUS_LIST =
                    "com.example.android.activityrecognition.ACTION_REFRESH_STATUS_LIST";
    public static final String CATEGORY_LOCATION_SERVICES =
            "com.example.android.activityrecognition.CATEGORY_LOCATION_SERVICES";
    public static final String EXTRA_CONNECTION_ERROR_CODE =
            "com.example.android.activityrecognition.EXTRA_CONNECTION_ERROR_CODE";
    public static final String EXTRA_CONNECTION_ERROR_MESSAGE =
            "com.example.android.activityrecognition.EXTRA_CONNECTION_ERROR_MESSAGE";

    public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int DAYTIME_DETECTION_INTERVAL_SECONDS = 30;
    public static final int NIGHTTIME_DETECTION_INTERVAL_SECONDS = 600;
    public static final int DAYTIME_DETECTION_INTERVAL_MILLISECONDS =
            MILLISECONDS_PER_SECOND * DAYTIME_DETECTION_INTERVAL_SECONDS;
    public static final int NIGHTTIME_DETECTION_INTERVAL_MILLISECONDS =
            MILLISECONDS_PER_SECOND * NIGHTTIME_DETECTION_INTERVAL_SECONDS;
}
