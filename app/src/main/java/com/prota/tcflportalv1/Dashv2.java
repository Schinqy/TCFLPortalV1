package com.prota.tcflportalv1;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Dashv2 extends AppCompatActivity {
    String studentId;
    String department;
    String name ;
    String surname;
    String national_id;
    String year;
    String program;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashv2);

        LinearLayout announcementButton = findViewById(R.id.announcementButton);
        TextView salutation = findViewById(R.id.studentName);
        TextView regID = findViewById(R.id.studentId);

        salutation.setOnClickListener(v -> {
            openProfileActivity();
        });
        // Find all buttons
        LinearLayout resultsButton = findViewById(R.id.resultsButton);
        LinearLayout  calendarButton = findViewById(R.id.calendarButton);

        LinearLayout  timetableButton = findViewById(R.id.timetableButton);
        LinearLayout financesButton = findViewById(R.id.financesButton);
        LinearLayout attendanceButton = findViewById(R.id.attendanceButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set onClickListeners for all buttons except announcementButton
        setButtonClickListener(financesButton);
        setButtonClickListener(attendanceButton);


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout actions here
                // 1. Clear any stored session data
                clearSessionData();
                // 2. Navigate back to the login screen
                navigateToLoginScreen();
            }
        });



        // Retrieve data from the Intent
        Intent intent = getIntent();
        studentId = intent.getStringExtra("studentId");
        department = intent.getStringExtra("department");
        name = intent.getStringExtra("name");
        surname = intent.getStringExtra("surname");
        national_id = intent.getStringExtra("national_id");
        year = intent.getStringExtra("year");
        program = intent.getStringExtra("program");
        address = intent.getStringExtra("address");





        // Set the text of the TextViews
        salutation.setText("Hi, " + name + " " + surname + "!");
        regID.setText("Reg Number: " + studentId);

        announcementButton.setOnClickListener(v -> {
          //  Toast.makeText(Dashv2.this, "Opening Announcements", Toast.LENGTH_SHORT).show();
            openAnnouncementActivity();
        });
        calendarButton.setOnClickListener(v -> {
           // Toast.makeText(Dashv2.this, "Opening Calendar", Toast.LENGTH_SHORT).show();
            openCalendarActivity();
        });
        resultsButton.setOnClickListener(v -> {
           // Toast.makeText(Dashv2.this, "Opening Results", Toast.LENGTH_SHORT).show();
            openResultsActivity();
        });
        timetableButton.setOnClickListener(v -> {
            // Toast.makeText(Dashv2.this, "Opening Timetable", Toast.LENGTH_SHORT).show();
            openTimetableActivity();
        });
        attendanceButton.setOnClickListener(v -> {
            // Toast.makeText(Dashv2.this, "Opening Attendance Sheet", Toast.LENGTH_SHORT).show();
            openAttendanceActivity();
        });
        financesButton.setOnClickListener(v -> {

            openFinancesActivity();
        });
    }

    private void openAnnouncementActivity() {
        Intent intent = new Intent(Dashv2.this, AnnouncementsActivity.class);
        startActivity(intent);
    }

    private void openCalendarActivity() {
        Intent intent = new Intent(Dashv2.this, CalendarActivity.class);
        startActivity(intent);
    }

    private void openResultsActivity() {
        Intent intent = new Intent(Dashv2.this, ResultsActivity.class);
        intent.putExtra("studentId", studentId);


        startActivity(intent);
    }
    private void openFinancesActivity() {
        Intent intent = new Intent(Dashv2.this, FinancesActivity.class);
        intent.putExtra("studentId", studentId);
        startActivity(intent);
    }

    private void openAttendanceActivity() {
        Intent intent = new Intent(Dashv2.this, AttendanceActivity.class);
       intent.putExtra("studentId", studentId);
        startActivity(intent);
    }

    private void openProfileActivity() {
        Intent intent = new Intent(Dashv2.this, ProfileActivity.class);
        intent.putExtra("studentId", studentId);
        intent.putExtra("department", department);
        intent.putExtra("name", name);
        intent.putExtra("surname", surname);
        intent.putExtra("national_id", national_id);
        intent.putExtra("year", year);
        intent.putExtra("program", program);
        intent.putExtra("address", address);

        startActivity(intent);
    }

    private void openTimetableActivity() {
        Intent intent = new Intent(Dashv2.this, TimetableActivity.class);
        intent.putExtra("department", department);
        startActivity(intent);
    }
    private void setButtonClickListener(LinearLayout button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComingSoonToast();
            }
        });
    }

    private void showComingSoonToast() {
        Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
    }

    // Method to clear session data
    private void clearSessionData() {
        // Clear any stored session data here
    }

    // Method to navigate to the login screen
    private void navigateToLoginScreen() {
        Intent intent = new Intent(Dashv2.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close the current activity to prevent user from navigating back
    }

}
