package com.prota.tcflportalv1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class Dash extends AppCompatActivity {

    private Button resultsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);

        resultsButton = findViewById(R.id.resultsButton);

        resultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your login logic here

                    Toast.makeText(Dash.this, "Chipo Chipadza", Toast.LENGTH_SHORT).show();
                    // Open the HomeActivity
//                    Intent intent = new Intent(Dash.this, Dash.class);
//                    startActivity(intent);

            }
        });


    }
}