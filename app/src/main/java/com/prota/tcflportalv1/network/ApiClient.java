package com.prota.tcflportalv1.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    private static final String BASE_URL = "https://lui.co.zw";

    // Method to obtain an instance of ApiInterface
    public static ApiInterface getApiInterface() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface.class);
    }

    // Method to obtain an instance of CalendarInterface
    public static CalendarInterface getCalendarInterface() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CalendarInterface.class);
    }
    public static ResultsInterface getResultsInterface() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ResultsInterface.class);
    }

    public static TimetableInterface getTimetableInterface() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TimetableInterface.class);
    }


    public static AnnouncementInterface getAnnouncementsInterface() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AnnouncementInterface.class);
    }

    public static AttendanceInterface getAttendanceInterface() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AttendanceInterface.class);
    }

    public static FinancesInterface getFinancesInterface() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FinancesInterface.class);
    }
}
