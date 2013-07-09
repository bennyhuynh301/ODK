package org.odk.collect.android.tracker;

public final class LocationUtils {

    public static final String APPTAG = "LocationUpdate";
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int DAYTIME_UPDATE_INTERVAL_IN_SECONDS = 30;
    public static final int NIGHTTIME_UPDATE_INTERVAL_IN_SECONDS = 600;
    public static final int FAST_CEILING_IN_SECONDS = 25;
    public static final long DAYTIME_UPDATE_INTERVAL_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * DAYTIME_UPDATE_INTERVAL_IN_SECONDS;
    public static final long NIGHTTIME_UPDATE_INTERVAL_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * NIGHTTIME_UPDATE_INTERVAL_IN_SECONDS;
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;
}