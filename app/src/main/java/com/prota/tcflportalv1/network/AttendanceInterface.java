package com.prota.tcflportalv1.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AttendanceInterface {
    @GET("portal/getAttendance.php")

    Call<ResponseBody> getAttendance(@Query("student_id") String studentId);
}
