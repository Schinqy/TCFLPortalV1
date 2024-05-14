package com.prota.tcflportalv1;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.prota.tcflportalv1.network.ApiClient;
import com.prota.tcflportalv1.network.AnnouncementInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
public class AnnouncementsActivity extends AppCompatActivity {
    private String responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);
        fetchAnnouncementsData();
    }

    private void fetchAnnouncementsData() {
        AnnouncementInterface announcementInterface = ApiClient.getAnnouncementsInterface();

        Call<ResponseBody> call = announcementInterface.getAnnouncements();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        responseData = response.body().string();
                        // Parse the JSON data and update UI
                        Log.d("Response", "Response Data " + responseData);

                        // Parse JSON and get list of Announcement objects
                        List<Announcement> announcementList = parseAnnouncements(responseData);

                        // Dynamically populate the announcement cards
                        populateAnnouncementCards(announcementList);

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





    private void populateAnnouncementCards(List<Announcement> announcementList) {
        GridLayout buttonGrid = findViewById(R.id.buttonGrid); // Reference to the GridLayout
        int MAX_TEXT_LENGTH = 83;

        for (int i = 0; i < announcementList.size(); i++) {
            // Inflate the announcement card layout
            View announcementCard = getLayoutInflater().inflate(R.layout.announcement_card_layout, null);

            // Set margins for the announcement card
            int leftMargin = 0;
            int topMargin = 0;
            int rightMargin = 0;
            int bottomMargin = 16;

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

            // Apply the layout parameters to the announcement card
            announcementCard.setLayoutParams(layoutParams);

            // Get references to views inside the announcement card layout
            TextView announcementText = announcementCard.findViewById(R.id.announcementText);
            TextView announcementTime = announcementCard.findViewById(R.id.announcementTime);
            TextView announcementCategory = announcementCard.findViewById(R.id.announcementCategory);
            announcementTime.setTypeface(null, Typeface.BOLD); // Make text bold
            announcementCategory.setTypeface(null, Typeface.BOLD); // Make text bold

            // Populate views with data from the announcement list
            Announcement announcement = announcementList.get(i);

            // Populate text views with announcement data
            String announcementContent = announcement.getText();
            final String truncatedContent;
            final String fullContent = announcementContent; // Store full content
            if (announcementContent.length() > MAX_TEXT_LENGTH) {
                String boldEllipsis = "<strong><b> ...</strong></b><u><em>Read more</em></u>"; // Make "..." bold using HTML formatting
                truncatedContent = announcementContent.substring(0, MAX_TEXT_LENGTH) + boldEllipsis;
                announcementText.setTag(true); // Tag to indicate whether text is truncated or not
                announcementText.setOnClickListener(v -> {
                    boolean isTruncated = (boolean) v.getTag();
                    if (isTruncated) {
                        announcementText.setText(fullContent);
                        announcementText.setTag(false); // Update tag
                    } else {
                        announcementText.setText(Html.fromHtml(truncatedContent));
                        announcementText.setTag(true); // Update tag
                    }
                });
                announcementText.setText(Html.fromHtml(truncatedContent)); // Apply HTML formatting
            } else {
                truncatedContent = announcementContent;
                announcementText.setText(truncatedContent);
            }

            // Format and set announcement time
            String formattedTime = formatTimestamp(announcement.getTimestamp());
            announcementTime.setText(formattedTime);

            announcementCategory.setText(announcement.getCategory());

            // Set background color based on category
            int backgroundColor = getColorForCategory(announcement.getCategory());
            announcementCard.setBackgroundColor(backgroundColor);

            // Add the announcement card to the GridLayout
            buttonGrid.addView(announcementCard);
        }
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

    private int getColorForCategory(String category) {
        // Define colors for different categories
        switch (category) {
            case "Academic":
                return ContextCompat.getColor(this, R.color.lightBlue);
            case "Finance":
                return ContextCompat.getColor(this,R.color.royalBlue);
            case "Facilities":
                return ContextCompat.getColor(this, R.color.columbiaBlue);
            case "Events":
                return  ContextCompat.getColor(this, R.color.robinEggBlue);
            default:
                return ContextCompat.getColor(this, R.color.defaultColor);
        }
    }



    private List<Announcement> parseAnnouncements(String responseData) {
        List<Announcement> announcements = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String text = jsonObject.getString("text");
                String timestamp = jsonObject.getString("timestamp");
                String category = jsonObject.getString("category");

                Announcement announcement = new Announcement(text, timestamp, category);
                announcements.add(announcement);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return announcements;
    }

    public static class Announcement {
        private String text;
        private String timestamp;
        private String category;

        // Constructor
        public Announcement(String text, String timestamp, String category) {
            this.text = text;
            this.timestamp = timestamp;
            this.category = category;
        }

        // Getters and setters
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }
}
