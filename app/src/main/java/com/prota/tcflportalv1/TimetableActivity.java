package com.prota.tcflportalv1;// Inside ResultsActivity.java

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.prota.tcflportalv1.network.ApiClient;
import com.prota.tcflportalv1.network.TimetableInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimetableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        // Retrieve student ID from the intent
        String department = getIntent().getStringExtra("department");

        // Get a reference to the TableLayout
        TableLayout tableLayout = findViewById(R.id.tableLayoutTimetable);

        // Make API call to fetch results for the given student ID
        TimetableInterface timetableInterface = ApiClient.getTimetableInterface();
        Call<ResponseBody> call = timetableInterface.getTimetable(department);
Log.d("Dpt", department + "this is the department");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Parse the JSON response
                        String responseBody = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseBody);
                        Log.d("TimetableActivity", "Received JSON response: " + responseBody);


                        // Inside onResponse method
                        // Inside onResponse method
                        // Inside onResponse method
// Inside onResponse method
                        TableRow headerRow = new TableRow(TimetableActivity.this);

// Add column headers
                        addHeaderCellToRow(headerRow, "Code", 1);
                        addHeaderCellToRow(headerRow, "Module", 2);
                        addHeaderCellToRow(headerRow, "Date", 1); // Change "Time" to "Date" to match the new column
                        addHeaderCellToRow(headerRow, "Time", 1); // Add "Time" column
                        addHeaderCellToRow(headerRow, "Venue", 2);
                        addHeaderCellToRow(headerRow, "Seat", 1);

// Add the header row to the table
                        tableLayout.addView(headerRow);

// Add data rows
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            TableRow row = new TableRow(TimetableActivity.this);

                            // Add cells for each desired column
                            addCellToRow(row, jsonObject.getString("module_code"), 1);
                            addCellToRow(row, jsonObject.getString("module"), 2);
                            addCellToRow(row, formatDate(jsonObject.getString("timestamp")), 1); // Format date
                            addCellToRow(row, formatTime(jsonObject.getString("timestamp")), 1); // Format time
                            addCellToRow(row, jsonObject.getString("venue"), 2);
                            addCellToRow(row, jsonObject.getString("sitting_position"), 1);

                            // Add the row to the table
                            tableLayout.addView(row);
                        }

// Method to format date


// Method to add a header cell to a TableRow


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
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
    // Method to add a header cell to a TableRow
    private void addHeaderCellToRow(TableRow row, String text, int weight) {
        // Create TextView for the value
        TextView textView = new TextView(TimetableActivity.this);
        textView.setText(text);
        textView.setTextColor(Color.BLACK); // Set text color to black
        textView.setPadding(20, 20, 20, 10); // Add padding to the TextView
        textView.setGravity(Gravity.CENTER); // Set text alignment to center
       // textView.setBackgroundResource(R.drawable.cell_border); // Define a border drawable
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight)); // Set layout weight
        textView.setTypeface(null, Typeface.BOLD); // Make text bold

        // Add TextView to the TableRow
        row.addView(textView);
    }
    // Method to format timestamp string
    private String formatDate(String timestamp) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date parsedDate = dateFormat.parse(timestamp);
            SimpleDateFormat dateFormatOutput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dateFormatOutput.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            // Return original timestamp if parsing fails
            return timestamp;
        }
    }

    // Method to format time
    private String formatTime(String timestamp) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date parsedDate = dateFormat.parse(timestamp);
            SimpleDateFormat dateFormatOutput = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return dateFormatOutput.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            // Return original timestamp if parsing fails
            return timestamp;
        }
    }

    private void addCellToRow(TableRow row, String text, int weight) {
        // Create TextView for the value
        TextView textView = new TextView(TimetableActivity.this);
        textView.setText(text);
        textView.setTextColor(Color.BLACK); // Set text color to black
        textView.setPadding(20, 10, 20, 10); // Add padding to the TextView
        textView.setGravity(Gravity.CENTER); // Set text alignment to center
        //textView.setBackgroundResource(R.drawable.cell_border); // Define a border drawable
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight)); // Set layout weight

        // Add TextView to the TableRow
        row.addView(textView);
    }

}
