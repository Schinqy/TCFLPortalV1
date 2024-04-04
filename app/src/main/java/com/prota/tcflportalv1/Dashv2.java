package com.prota.tcflportalv1;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Dashv2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashv2);

        LinearLayout announcementButton = findViewById(R.id.announcementButton);
        TextView salutation = findViewById(R.id.studentName);
        TextView regID = findViewById(R.id.studentId);
        // Find all buttons
        LinearLayout resultsButton = findViewById(R.id.resultsButton);
        LinearLayout  calendarButton = findViewById(R.id.calendarButton);

        LinearLayout  timetableButton = findViewById(R.id.timetableButton);
        LinearLayout financesButton = findViewById(R.id.financesButton);
        LinearLayout attendanceButton = findViewById(R.id.attendanceButton);
        TextView  logoutButton = findViewById(R.id.textView); // Assuming this is your logout button

        // Set onClickListeners for all buttons except announcementButton
        setButtonClickListener(resultsButton);
        setButtonClickListener(timetableButton);
        setButtonClickListener(financesButton);
        setButtonClickListener(attendanceButton);



        // Retrieve data from the Intent
        Intent intent = getIntent();
        String studentId = intent.getStringExtra("studentId");
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");



        // Set the text of the TextViews
        salutation.setText("Hi, " + name + " " + surname + "!");
        regID.setText("Reg Number: " + studentId);

        announcementButton.setOnClickListener(v -> {
            Toast.makeText(Dashv2.this, "Opening Announcements", Toast.LENGTH_SHORT).show();
            openAnnouncementActivity();
        });
        calendarButton.setOnClickListener(v -> {
            Toast.makeText(Dashv2.this, "Opening Calendar", Toast.LENGTH_SHORT).show();
            openCalendarActivity();
        });
    }

    private void openAnnouncementActivity() {
        Intent intent = new Intent(Dashv2.this, Announcements.class);
        startActivity(intent);
    }

    private void openCalendarActivity() {
        Intent intent = new Intent(Dashv2.this, CalendarActivity.class);
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
}
