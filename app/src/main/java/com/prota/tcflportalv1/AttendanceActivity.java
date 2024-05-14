package com.prota.tcflportalv1;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import android.content.Intent;

import java.util.HashSet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prota.tcflportalv1.network.AttendanceInterface;
import com.prota.tcflportalv1.network.ApiClient;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import androidx.core.text.HtmlCompat;

import java.time.LocalDate;
import java.util.Calendar;



public class AttendanceActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView;
    private  LinearLayout linearLayoutx;
    private ScrollView scrollviewx;
    // Declare the variables as class-level fields
    private int monthMaterialView;
    private int yearMaterialView;
    private  int year;
    private  int month;
    private  int day;
    int count;

    private String responseData;
    String studentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        calendarView = findViewById(R.id.calendarViewx);
        linearLayoutx = findViewById(R.id.linearLayoutx);

       scrollviewx = findViewById(R.id.scrollView);

       //  Retrieve student ID from the intent
        studentId = getIntent().getStringExtra("studentId");

       //  Set up the month changed listener
        calendarView.setOnMonthChangedListener((widget, date) -> {
            monthMaterialView = date.getMonth();
            yearMaterialView = date.getYear();
            updateUIBasedOnMonthViewChange(responseData, yearMaterialView, monthMaterialView);
           // Toast.makeText(AttendanceActivity.this, "Month: " + monthMaterialView + ", Year: " + yearMaterialView, Toast.LENGTH_SHORT).show();

        });
        // Set up the date click listener
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            // Get the position of the clicked date in the ScrollView
            int positionY = calculatePositionYForDate(date);

            // Scroll ScrollView to the position of the clicked date
            scrollviewx.scrollTo(0, positionY);
        });
        fetchAttendanceData();
    }

    // Custom decorator class to add dots to specified dates
    private void fetchAttendanceData() {
        AttendanceInterface attendanceInterface = ApiClient.getAttendanceInterface();

        Call<ResponseBody> call = attendanceInterface.getAttendance(studentId);



        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        responseData = response.body().string();
                        // Parse the JSON data and update UI
                       updateUIWithCalendarData(responseData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void updateUIWithCalendarData(String responseData) {
        try {
            JSONArray jsonArray = new JSONArray(responseData);

            linearLayoutx.removeAllViews(); // Clear existing views

            // Initialize lists for dates with different statuses
            List<CalendarDay> datesWithDotsAbsent = new ArrayList<>();
            List<CalendarDay> datesWithDotsSick = new ArrayList<>();
            List<CalendarDay> datesWithDotsEarly = new ArrayList<>();
            List<CalendarDay> datesWithDotsLate = new ArrayList<>();
            boolean eventsFound = false;

            // Get current calendar instance
            Calendar calendar = Calendar.getInstance();

// Get current year and month
            int yearCurrent = calendar.get(Calendar.YEAR);
            int monthCurrent = calendar.get(Calendar.MONTH) + 1; // Month starts from 0, so add 1 to get the actual month


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // Extract data from JSON object
                String inTime = jsonObject.getString("datetime_in");
                String status = jsonObject.getString("status");

                // Extract date from start time
                String[] startDateParts = inTime.split(" ")[0].split("-");
                int year = Integer.parseInt(startDateParts[0]);
                int month = Integer.parseInt(startDateParts[1]);
                int day = Integer.parseInt(startDateParts[2]);
                count = 0;
                if (yearCurrent == year && monthCurrent == month) {
                     eventsFound = true;
                     count++;
                }
                // Add dates to corresponding status lists
                switch (status) {
                    case "Sick":
                        datesWithDotsSick.add(CalendarDay.from(year, month - 1, day));
                        break;
                    case "Early":
                        datesWithDotsEarly.add(CalendarDay.from(year, month - 1, day));
                        break;
                    case "Late":
                        datesWithDotsLate.add(CalendarDay.from(year, month - 1, day));
                        break;
                    case "Absent":
                        datesWithDotsAbsent.add(CalendarDay.from(year, month - 1, day));
                        break;
                }
            }

            // Update the calendarView decorators with new dots
            calendarView.removeDecorators();
            calendarView.addDecorator(new DotDecorator(datesWithDotsAbsent, getResources().getColor(R.color.absentColor, null)));
            calendarView.addDecorator(new DotDecorator(datesWithDotsSick, getResources().getColor(R.color.sickColor, null)));
            calendarView.addDecorator(new DotDecorator(datesWithDotsEarly, getResources().getColor(R.color.earlyColor, null)));
            calendarView.addDecorator(new DotDecorator(datesWithDotsLate, getResources().getColor(R.color.lateColor, null)));

            // Check if any events exist for the current month
           // if (datesWithDotsAbsent.isEmpty() && datesWithDotsSick.isEmpty() && datesWithDotsEarly.isEmpty() && datesWithDotsLate.isEmpty()) {

               if(!eventsFound){ // If no events, display "no events" in the middle

                   // If no events, display "no events" in the middle
                   TextView noEventsTextView = new TextView(this);
                   noEventsTextView.setText("No Records");
                   noEventsTextView.setTextSize(20);
                   noEventsTextView.setTypeface(null, Typeface.BOLD); // Set date text to bold
                   noEventsTextView.setTextColor(Color.BLACK);
                   noEventsTextView.setGravity(Gravity.CENTER);
                   // Set margins for the "No Records" TextView
                   FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                   layoutParams.gravity = Gravity.CENTER;
                   layoutParams.setMargins(0, 30, 0, 0); // Adjust margins as needed
                   noEventsTextView.setLayoutParams(layoutParams);

                   // Create a FrameLayout to center the "No Events" TextView vertically and horizontally
                   FrameLayout frameLayout = new FrameLayout(this);
                   frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                   frameLayout.addView(noEventsTextView);

                   // Add the FrameLayout containing the "No Events" TextView to the linear layout
                   linearLayoutx.addView(frameLayout);
                   noEventsTextView.bringToFront();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    // Custom decorator class to add dots to specified dates
    private class DotDecorator implements com.prolificinteractive.materialcalendarview.DayViewDecorator {
        private final List<CalendarDay> datesWithDots;
        private final int dotColor; // Add a field to store the dot color

        public DotDecorator(List<CalendarDay> datesWithDots, int dotColor) {
            this.datesWithDots = datesWithDots;
            this.dotColor = dotColor;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return datesWithDots.contains(day);
        }

        @Override
        public void decorate(com.prolificinteractive.materialcalendarview.DayViewFacade view) {
            view.addSpan(new DotSpan(10, dotColor)); // Use the dotColor field here
        }
    }




    private void updateUIBasedOnMonthViewChange(String responseData, int year, int month) {
        try {
            JSONArray jsonArray = new JSONArray(responseData);

            // Clear existing views
            linearLayoutx.removeAllViews();

            // Initialize a boolean flag to track if any events are found for the month
            boolean eventsFound = false;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // Extract data from JSON object
                String inTime = jsonObject.getString("datetime_in");
                String status = jsonObject.getString("status");

                // Extract date from start time
                String[] startDateParts = inTime.split(" ")[0].split("-");
                int eventYear = Integer.parseInt(startDateParts[0]);
                int eventMonth = Integer.parseInt(startDateParts[1]);
                int eventDay = Integer.parseInt(startDateParts[2]);

                // Check if event year and month match the specified year and month
                if (eventYear == year && eventMonth == month + 1) {
                    // Create a custom layout for the calendar event
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View eventView = inflater.inflate(R.layout.calendar_event_layout, linearLayoutx, false);

                    // Set color box
                    ImageView colorBoxImageView = eventView.findViewById(R.id.colorBoxImageView);
                    int color;
                    switch (status) {
                        case "Sick":
                            color = getResources().getColor(R.color.sickColor, null);
                            break;
                        case "Early":
                            color = getResources().getColor(R.color.earlyColor, null);
                            break;
                        case "Late":
                            color = getResources().getColor(R.color.lateColor, null);
                            break;
                        case "Absent":
                            color = getResources().getColor(R.color.absentColor, null);
                            break;
                        default:
                            color = Color.BLACK;
                    }
                    // Set date TextView click listener
                    TextView dateTextView = eventView.findViewById(R.id.dateTextView);


                    colorBoxImageView.setBackgroundColor(color);
                    // Set time in
                    // Extract time from inTime
                    String[] timeParts = inTime.split(" ")[1].split(":");
                    String time = timeParts[0] + ":" + timeParts[1]; // Extract hours and minutes
                    // Set date

                    String[] monthsTxtArray = {"Jan","Feb", "Mar", "Apr","May","Jun",
                            "Jul","Aug", "Sep", "Oct", "Nov", "Dec"};
                    String monthTxt;

                    monthTxt= monthsTxtArray[eventMonth-1];

                    dateTextView.setText( eventDay + " " + monthTxt + " " + year );
                    dateTextView.setTypeface(null, Typeface.BOLD); // Set date text to bold
                    dateTextView.setTextColor(Color.BLACK);
                    // Set time in
                    TextView timeInTextView = eventView.findViewById(R.id.timeInTextView);
                    timeInTextView.setText("Time In: " + time);
                    timeInTextView.setTextColor(Color.BLACK);
                    // Set status
                    TextView statusTextView = eventView.findViewById(R.id.statusTextView);
                    statusTextView.setText(status);
                    statusTextView.setTextColor(Color.BLACK); // Set status text color to black

                    // Add the custom layout to the linear layout
                    linearLayoutx.addView(eventView);

                    // Set the eventsFound flag to true since at least one event was found for the month
                    eventsFound = true;
                }
            }

            // Check if no events were found for the month
            if (!eventsFound) {
                // If no events, display "no events" in the middle
                TextView noEventsTextView = new TextView(this);
                noEventsTextView.setText("No Records");
                noEventsTextView.setTextSize(20);
                noEventsTextView.setTypeface(null, Typeface.BOLD); // Set date text to bold
                noEventsTextView.setTextColor(Color.BLACK);
                noEventsTextView.setGravity(Gravity.CENTER);
                // Set margins for the "No Records" TextView
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.setMargins(0, 30, 0, 0); // Adjust margins as needed
                noEventsTextView.setLayoutParams(layoutParams);

                // Create a FrameLayout to center the "No Events" TextView vertically and horizontally
                FrameLayout frameLayout = new FrameLayout(this);
                frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                frameLayout.addView(noEventsTextView);

                // Add the FrameLayout containing the "No Events" TextView to the linear layout
                linearLayoutx.addView(frameLayout);
                noEventsTextView.bringToFront();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // Method to calculate the Y position for the clicked date in the ScrollView
    private int calculatePositionYForDate(CalendarDay date) {
        // Here you can implement the logic to calculate the Y position in the ScrollView
        // For example, you can calculate the position based on the date's index or any other logic
        // For simplicity, let's assume the position is the same as the day of the month (scaled)
        int dayOfMonth = date.getDay();
        // Let's assume each day occupies 100 pixels vertically
        if (dayOfMonth ==1 ) return 10;
        if (dayOfMonth> 1 && dayOfMonth<= 4) return dayOfMonth * 130;
        if (dayOfMonth> 4 && dayOfMonth<= 8) return dayOfMonth * 160;
        if (dayOfMonth> 8 && dayOfMonth< 14 ) return dayOfMonth * 175;

        return dayOfMonth * 180;
    }





}
