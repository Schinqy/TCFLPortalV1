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




    public class ProfileActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);

            // Retrieve student details from the intent
            String department = getIntent().getStringExtra("department");
            String studentId = getIntent().getStringExtra("studentId");
            String name = getIntent().getStringExtra("name");
            String surname = getIntent().getStringExtra("surname");

            // Find the TextViews in your layout
            TextView textViewName = findViewById(R.id.textViewName);
            TextView textViewRegId = findViewById(R.id.textViewRegId);
            TextView textViewCourse = findViewById(R.id.textViewCourse);
            TextView textViewBatch = findViewById(R.id.textViewBatch);
            TextView textViewEmail = findViewById(R.id.textViewEmail);

            // Set the text of TextViews with the retrieved data
            textViewName.setText("Name: " + name + " " + surname);
            textViewRegId.setText("Registration ID: " + studentId);
            textViewCourse.setText("Department: " + department);
            textViewBatch.setText("Batch: " + getBatchFromStudentId(studentId));
            textViewEmail.setText("Email: " + studentId + "@tcfl.co.zw");
        }

        // Helper method to extract batch from student ID
        private String getBatchFromStudentId(String studentId) {
            // Assuming the format of student ID is "TXXXXXX"
            // where "T" is followed by 6 digits representing the batch year
            if (studentId.length() >= 2) {
                return "20" + studentId.substring(1, 3);
            }
            return "";
        }
    }



