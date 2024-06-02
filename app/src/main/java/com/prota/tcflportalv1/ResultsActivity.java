package com.prota.tcflportalv1;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.prota.tcflportalv1.network.ApiClient;
import com.prota.tcflportalv1.network.ResultsInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Retrieve student ID from the intent
        String studentId = getIntent().getStringExtra("studentId");

        // Get a reference to the TableLayout
        TableLayout tableLayout = findViewById(R.id.tableLayoutResults);

        // Make API call to fetch results for the given student ID
        ResultsInterface resultsInterface = ApiClient.getResultsInterface();
        Call<ResponseBody> call = resultsInterface.getResults(studentId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Parse the JSON response
                        String responseBody = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseBody);

                        // Inside onResponse method
                        Map<String, JSONArray> resultsByYear = new TreeMap<>(Collections.reverseOrder()); // Use TreeMap to sort by year in descending order

                        // Group results by academic year
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String moduleCode = jsonObject.getString("module_code");
                            String academicYear = extractAcademicYear(moduleCode);
                            if (!resultsByYear.containsKey(academicYear)) {
                                resultsByYear.put(academicYear, new JSONArray());
                            }
                            resultsByYear.get(academicYear).put(jsonObject);
                        }

                        // Iterate over the grouped results and add them to the table
                        for (Map.Entry<String, JSONArray> entry : resultsByYear.entrySet()) {
                            // Add a header row for the academic year
                            TableRow yearHeaderRow = new TableRow(ResultsActivity.this);
                            TextView yearHeaderTextView = new TextView(ResultsActivity.this);

                            // Set the year header text based on the academic year
                            switch (entry.getKey())
                            {
                                case "1.1": yearHeaderTextView.setText("Year 1 Semester 1");
                                break;
                                case "1.2": yearHeaderTextView.setText("Year 1 Semester 2");
                                break;
                                case "2.1": yearHeaderTextView.setText("Year 2 Semester 1");
                                break;
                                case "2.2": yearHeaderTextView.setText("Year 2 Semester 2");
                                break;
                                case "3.1": yearHeaderTextView.setText("Year 3 Semester 1");
                                break;
                                case "3.2": yearHeaderTextView.setText("Year 3 Semester 2");
                                break;
                                case "4.1": yearHeaderTextView.setText("Year 4 Semester 1");
                                break;
                                case "4.2": yearHeaderTextView.setText("Year 4 Semester 2");
                                break;
                                case "5.1": yearHeaderTextView.setText("Year 5 Semester 1");
                                break;
                                case "5.2": yearHeaderTextView.setText("Year 5 Semester 2");
                                break;
                                default: yearHeaderTextView.setText("Unknown Year");
                                break; }

                            yearHeaderTextView.setTextColor(Color.BLACK); // Set text color to black
                            yearHeaderTextView.setTypeface(null, Typeface.BOLD);
                            yearHeaderTextView.setGravity(Gravity.CENTER);
                            yearHeaderTextView.setPadding(20, 10, 20, 10);
                            yearHeaderRow.addView(yearHeaderTextView);
                            tableLayout.addView(yearHeaderRow);

                            // Add header row for results
                            TableRow headerRow = new TableRow(ResultsActivity.this);
                            addHeaderCellToRow(headerRow, "Code", 1);
                            addHeaderCellToRow(headerRow, "Module", 2);
                            addHeaderCellToRow(headerRow, "Grade", 1);
                            addHeaderCellToRow(headerRow, "%", 1);
                            tableLayout.addView(headerRow);

                            // Add data rows for each result in the academic year
                            JSONArray results = entry.getValue();
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject result = results.getJSONObject(i);
                                TableRow row = new TableRow(ResultsActivity.this);
                                addCellToRow(row, result.getString("module_code"), 1);
                                addCellToRow(row, result.getString("module"), 2);
                                addCellToRow(row, result.getString("grade"), 1);
                                addCellToRow(row, result.getString("percentage"), 1);
                                tableLayout.addView(row);
                            }
                        }

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

    // Method to extract academic year from module code
    private String extractAcademicYear(String moduleCode) {
        String yearPart = moduleCode.substring(3, 4);
        String semesterPart = moduleCode.substring(4, 5);
        return yearPart + "." + semesterPart;
    }

    // Method to add a cell to a TableRow
    private void addCellToRow(TableRow row, String text, int weight) {
        // Create TextView for the value
        TextView textView = new TextView(ResultsActivity.this);
        textView.setText(text);
        textView.setTextColor(Color.BLACK); // Set text color to black
        textView.setPadding(20, 8, 20, 0); // Add padding to the TextView
        textView.setGravity(Gravity.CENTER); // Set text alignment to center
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight)); // Set layout weight

        // Add TextView to the TableRow
        row.addView(textView);
    }

    // Method to add a header cell to a TableRow
    private void addHeaderCellToRow(TableRow row, String text, int weight) {
        // Create TextView for the value
        TextView textView = new TextView(ResultsActivity.this);
        textView.setText(text);
        textView.setTextColor(Color.BLACK); // Set text color to black
        textView.setPadding(20, 10, 20, 10); // Add padding to the TextView
        textView.setGravity(Gravity.CENTER); // Set text alignment to center
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight)); // Set layout weight
        textView.setTypeface(null, Typeface.BOLD); // Make text bold

        // Add TextView to the TableRow
        row.addView(textView);
    }

    // Method to add a cell to a TableRow


}
