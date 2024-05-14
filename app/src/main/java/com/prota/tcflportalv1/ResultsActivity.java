package com.prota.tcflportalv1;// Inside ResultsActivity.java

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
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

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
                        TableRow headerRow = new TableRow(ResultsActivity.this);

// Add column headers
                        addHeaderCellToRow(headerRow, "Code", 1);
                        addHeaderCellToRow(headerRow, "Module", 2);
                        addHeaderCellToRow(headerRow, "Grade", 1);
                        addHeaderCellToRow(headerRow, "%", 1);

// Add the header row to the table
                        tableLayout.addView(headerRow);

// Add data rows
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            TableRow row = new TableRow(ResultsActivity.this);

                            // Add cells for each desired column
                            addCellToRow(row, jsonObject.getString("module_code"), 1);
                            addCellToRow(row, jsonObject.getString("module"), 2);
                            addCellToRow(row, jsonObject.getString("grade"), 1);
                            addCellToRow(row, jsonObject.getString("percentage"), 1);

                            // Add the row to the table
                            tableLayout.addView(row);
                        }

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
        TextView textView = new TextView(ResultsActivity.this);
        textView.setText(text);
        textView.setTextColor(Color.BLACK); // Set text color to black
        textView.setPadding(20, 10, 20, 10); // Add padding to the TextView
        textView.setGravity(Gravity.CENTER); // Set text alignment to center
       // textView.setBackgroundResource(R.drawable.cell_border); // Define a border drawable
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight)); // Set layout weight
        textView.setTypeface(null, Typeface.BOLD); // Make text bold

        // Add TextView to the TableRow
        row.addView(textView);
    }

    private void addCellToRow(TableRow row, String text, int weight) {
        // Create TextView for the value
        TextView textView = new TextView(ResultsActivity.this);
        textView.setText(text);
        textView.setTextColor(Color.BLACK); // Set text color to black
        textView.setPadding(20, 8, 20, 0); // Add padding to the TextView
        //textView.setMargin(0, 10, 0, 10); // Add padding to the TextView
        textView.setGravity(Gravity.CENTER); // Set text alignment to center
        //textView.setBackgroundResource(R.drawable.cell_border); // Define a border drawable
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight)); // Set layout weight

        // Add TextView to the TableRow
        row.addView(textView);
    }

}
