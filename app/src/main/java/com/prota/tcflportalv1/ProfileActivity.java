package com.prota.tcflportalv1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.prota.tcflportalv1.network.ApiClient;
import com.prota.tcflportalv1.network.BlobInterface;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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
        String national_id = getIntent().getStringExtra("national_id");
        String year = getIntent().getStringExtra("year");
        String program = getIntent().getStringExtra("program");
        String address = getIntent().getStringExtra("address");

        // Find the TextViews in your layout
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewRegId = findViewById(R.id.textViewRegId);
        TextView textViewCourse = findViewById(R.id.textViewCourse);
        TextView textViewEmail = findViewById(R.id.textViewEmail);
        TextView textViewNationalID = findViewById(R.id.textViewNationalID);
        TextView textViewYear = findViewById(R.id.textViewYear);
        TextView textViewProgram = findViewById(R.id.textViewProgram);
        TextView textViewAddress = findViewById(R.id.textViewAddress);

        // Set text for each TextView
        textViewName.setText(HtmlCompat.fromHtml( name + " " + surname, HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewRegId.setText(HtmlCompat.fromHtml("<b>Registration ID:</b> " + studentId, HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewCourse.setText(HtmlCompat.fromHtml("<b>Department:</b> " + department, HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewEmail.setText(HtmlCompat.fromHtml("<b>Email:</b> " + studentId + "@tcfl.co.zw", HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewNationalID.setText(HtmlCompat.fromHtml("<b>National ID:</b> " + national_id, HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewYear.setText(HtmlCompat.fromHtml("<b>Year:</b> " + year, HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewProgram.setText(HtmlCompat.fromHtml("<b>Program:</b> " + program, HtmlCompat.FROM_HTML_MODE_LEGACY));
        textViewAddress.setText(HtmlCompat.fromHtml("<b>Address:</b> " + address, HtmlCompat.FROM_HTML_MODE_LEGACY));

        // Fetch and display profile picture
        fetchProfilePicture(studentId);
    }

    private void fetchProfilePicture(String studentId) {
        BlobInterface blobInterface = ApiClient.getBlobInterface();
        Call<ResponseBody> call = blobInterface.getDP(studentId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Get the byte array from the response
                        byte[] blobByteArray = response.body().bytes();

                        // Convert the Blob byte array to a Bitmap
                        Bitmap bitmap = convertBlobToBitmap(blobByteArray);

                        if (bitmap != null) {
                            // Set the Bitmap to the profile picture
                            ImageView profilePicImageView = findViewById(R.id.profilePicture);
                            profilePicImageView.setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private Bitmap convertBlobToBitmap(byte[] blobByteArray) {
        InputStream inputStream = new ByteArrayInputStream(blobByteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
