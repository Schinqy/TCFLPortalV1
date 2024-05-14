package com.prota.tcflportalv1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import com.prota.tcflportalv1.network.ApiClient;
import com.prota.tcflportalv1.network.CalendarInterface;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;


import androidx.core.text.HtmlCompat;


public class CalendarActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView;
    private  LinearLayout linearLayout;
    // Declare the variables as class-level fields
    private int monthMaterialView;
    private int yearMaterialView;
   private  int year;
   private  int month;
   private  int day;

   private String responseData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Initialize MaterialCalendarView
        calendarView = findViewById(R.id.calendarView);
        linearLayout = findViewById(R.id.linearLayout);

        // Set up the month changed listener
        calendarView.setOnMonthChangedListener((widget, date) -> {
            monthMaterialView = date.getMonth();
            yearMaterialView = date.getYear();
           updateUIBasedOnMonthViewChange(responseData, yearMaterialView, monthMaterialView);
           // Toast.makeText(CalendarActivity.this, "Month: " + monthMaterialView + ", Year: " + yearMaterialView, Toast.LENGTH_SHORT).show();

        });



        // Fetch calendar data from the server
        fetchCalendarData();
    }
    // Custom decorator class to add dots to specified dates
    private class DotDecorator implements com.prolificinteractive.materialcalendarview.DayViewDecorator {
        private final CalendarDay day;

        public DotDecorator(CalendarDay day) {
            this.day = day;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            // Decorate if the day matches the specified day
            return day.equals(this.day);
        }

        @Override
        public void decorate(com.prolificinteractive.materialcalendarview.DayViewFacade view) {
            // Add a dot to the specified day
            view.addSpan(new DotSpan(10, getResources().getColor(R.color.dotColor))); // Adjust dot size and color as needed
        }
    }


    // Define a method to fetch calendar data from the server
    private void fetchCalendarData() {
        CalendarInterface calendarInterface = ApiClient.getCalendarInterface();

        Call<ResponseBody> call = calendarInterface.getCalendar();
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


    // Method to update UI with calendar data
    private void updateUIWithCalendarData(String responseData) {
        try {
            JSONArray jsonArray = new JSONArray(responseData);

            linearLayout.removeAllViews(); // Clear existing views

            List<CalendarDay> datesWithDots = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // Extract data from JSON object
                String activity = jsonObject.getString("activity");
                String startTime = jsonObject.getString("start_time");
                String endTime = jsonObject.getString("end_time");
                String location = jsonObject.getString("location");
                String status = jsonObject.getString("status");
                String notes = jsonObject.getString("notes");

                // Extract date from start time
                String[] startDateParts = startTime.split(" ")[0].split("-");
                year = Integer.parseInt(startDateParts[0]);
                month = Integer.parseInt(startDateParts[1]);
                day = Integer.parseInt(startDateParts[2]);




// Get current year and month using Calendar class
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);

                Log.d("ONCALENDARLOAD", "Extracted Date: " + year + "-" + month + "-" + day);

                // Log the extracted year and month
                Log.d("ONCALENDARLOAD", "Year: " + year + ", Month: " + month);
                Log.d("ONCALENDARLOAD", "Year Material View: " + yearMaterialView + ", Month Material View: " + monthMaterialView);

                // Create TextView to display calendar event details
                TextView textView = new TextView(this);
                if (year == currentYear && month == currentMonth + 1) {
                    String formattedText = "<b>" + activity + "</b> <br>" +
                            "<b>From</b> <em>" + startTime + "</em> <br>" +
                            "<b>To</b> " + endTime + "<br>" +
                            "<b>Location:</b> " + location + "<br>" +
                            "<b>Status:</b> " + status + "<br>" +
                            "<b><strong><em>"+ notes + "</b></strong></em>";

                    // Use HtmlCompat to format the text
                    CharSequence styledText = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY);
                    textView.setText(styledText);
                    // Set font size and family
                    textView.setTextSize(16); // Change the size as needed
                    textView.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL)); // Change the font family as needed

                    // Create LayoutParams with margins
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(100, 100, 0, 40); // Set margins

                    textView.setLayoutParams(layoutParams); // Apply LayoutParams to TextView
                    textView.setTextColor(ContextCompat.getColor(this, R.color.black));

                    linearLayout.addView(textView); // Add TextView to LinearLayout
                }
                // Add the date to the list of dates with dots
                datesWithDots.add(CalendarDay.from(year, month - 1, day));
            }

            // Add decorators with dots to the specified days
            for (CalendarDay day : datesWithDots) {
                calendarView.addDecorator(new DotDecorator(day));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateUIBasedOnMonthViewChange(String responseData, int year, int month) {
            try {
                JSONArray jsonArray = new JSONArray(responseData);

                // Clear existing views
                linearLayout.removeAllViews();



                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // Extract data from JSON object
                    String activity = jsonObject.getString("activity");
                    String startTime = jsonObject.getString("start_time");
                    String endTime = jsonObject.getString("end_time");
                    String location = jsonObject.getString("location");
                    String status = jsonObject.getString("status");
                    String notes = jsonObject.getString("notes");

                    // Extract date from start time
                    String[] startDateParts = startTime.split(" ")[0].split("-");
                    int eventYear = Integer.parseInt(startDateParts[0]);
                    int eventMonth = Integer.parseInt(startDateParts[1]);
                    int eventDay = Integer.parseInt(startDateParts[2]);

                    // Check if event year and month match the specified year and month
                    if (eventYear == year && eventMonth == month + 1) {
                        // Create TextView to display calendar event details
                        TextView textView = new TextView(this);

                        // Set text and format as needed
                        String formattedText = "<b>" + activity + "</b> <br>" +
                                "<b>From</b> <em>" + startTime + "</em> <br>" +
                                "<b>To</b> " + endTime + "<br>" +
                                "<b>Location:</b> " + location + "<br>" +
                                "<b>Status:</b> " + status + "<br>" +
                                "<b><strong><em>"+ notes + "</b></strong></em>";

                        // Use HtmlCompat to format the text
                        CharSequence styledText = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY);
                        textView.setText(styledText);

                        // Set font size and family
                        textView.setTextSize(16); // Change the size as needed
                        textView.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL)); // Change the font family as needed

                        // Create LayoutParams with margins
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(100, 100, 0, 40); // Set margins

                        textView.setLayoutParams(layoutParams); // Apply LayoutParams to TextView
                        textView.setTextColor(ContextCompat.getColor(this, R.color.black));

                        // Add TextView to LinearLayout
                        linearLayout.addView(textView);

                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

    }

}
