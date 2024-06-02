package com.prota.tcflportalv1;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.prota.tcflportalv1.R;
import com.prota.tcflportalv1.network.AnnouncementInterface;
import com.prota.tcflportalv1.network.ApiClient;
import com.prota.tcflportalv1.network.FinancesInterface;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FinancesActivity extends AppCompatActivity {
    private String responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finances);

        // Retrieve student ID from the intent
        String studentId = getIntent().getStringExtra("studentId");

        // Get a reference to the TableLayout
        TableLayout tableLayout = findViewById(R.id.tableLayoutFinances);
        TextView balance = findViewById(R.id.financeBalance);

        // Make API call to fetch results for the given student ID
        FinancesInterface financesInterface = ApiClient.getFinancesInterface();
        Call<ResponseBody> call = financesInterface.getFinances(studentId);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Parse the JSON response
                        String responseBody = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseBody);
                        double bal = calculateBalance(jsonArray);
                        String formattedBalance = String.format("%.2f", Math.abs(bal));

                        // Display the balance with appropriate sign
                        if (bal < 0) balance.setText("Balance: -$" + formattedBalance);
                        else balance.setText("Balance: $" + formattedBalance);


                        // Inside onResponse method
                        TableRow headerRow = new TableRow(FinancesActivity.this);

                        // Add column headers
                        addHeaderCellToRow(headerRow, "TxId", 1);
                        addHeaderCellToRow(headerRow, "Description", 2);
                        addHeaderCellToRow(headerRow, "Amount", 1);
                        addHeaderCellToRow(headerRow, "Year", 1);

                        // Add the header row to the table
                        tableLayout.addView(headerRow);

                        // Define the base year
                        int baseYear = 2021;

                        // Add data rows
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            TableRow row = new TableRow(FinancesActivity.this);

                            // Add cells for each desired column
                            addCellToRow(row, jsonObject.getString("transaction_id"), 1);
                            addCellToRow(row, jsonObject.getString("description"), 2);

                            // Determine the type of transaction and add either "DR" or "CR" along with the amount
                            String type = jsonObject.getString("type");
                            double amount = jsonObject.getDouble("amount");
                            String amountText;
                            if (type.equals("Debit")) {
                                amountText = String.format("%.2f", amount) + "DR";
                            } else if (type.equals("Credit")) {
                                amountText = String.format("%.2f", amount) + "CR";
                            } else {
                                amountText = String.format("%.2f", amount);
                            }
                            addCellToRow(row, amountText, 1);

                            // Extract year and month from the timestamp
                            String timestamp = jsonObject.getString("timestamp");
                            int year = Integer.parseInt(timestamp.substring(0, 4));
                            int month = Integer.parseInt(timestamp.substring(5, 7));

                            // Determine the academic year
                            int academicYear = year - baseYear + 1;

                            // Determine the semester based on the month
                            String semester;
                            if (month >= 1 && month <= 7) {
                                semester = academicYear + ".1"; // First semester
                            } else {
                                semester = academicYear + ".2"; // Second semester
                            }
                            addCellToRow(row, semester, 1);

                            // Add the row to the table
                            tableLayout.addView(row);
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





    // Method to format timestamp
    private String formatTimestamp(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yy HH:mm", Locale.getDefault());

        try {
            Date date = inputFormat.parse(timestamp);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return timestamp; // Return original timestamp if formatting fails
        }
    }

    private void addHeaderCellToRow(TableRow row, String text, int weight) {
        // Create TextView for the value
        TextView textView = new TextView(FinancesActivity.this);
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
        TextView textView = new TextView(FinancesActivity.this);
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

    public double calculateBalance(JSONArray jsonArray) {
        double balance = 0.0;

        // Iterate through the JSON array
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                // Get the JSON object at the current index
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Get the amount and type from the JSON object
                double amount = jsonObject.getDouble("amount");
                String type = jsonObject.getString("type");

                // Update the balance based on the transaction type
                if (type.equals("Credit")) {
                    balance += amount;
                } else if (type.equals("Debit")) {
                    balance -= amount;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Return the calculated balance
        return balance;
    }


}
