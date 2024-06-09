package com.prota.tcflportalv1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.prota.tcflportalv1.network.ApiClient;
import com.prota.tcflportalv1.network.ApiInterface;
import com.prota.tcflportalv1.network.TokensInterface;

import org.json.JSONObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                loginUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password) {
        ApiInterface apiInterface = ApiClient.getApiInterface();
        Call<ResponseBody> call = apiInterface.loginUser(username, password);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        if (jsonResponse.has("success") && jsonResponse.getBoolean("success")) {
                            handleSuccessfulLogin(jsonResponse);
                        } else {
                            Toast.makeText(MainActivity.this, "Login Failed: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSuccessfulLogin(JSONObject jsonResponse) throws Exception {
        JSONObject userDetails = jsonResponse.getJSONObject("user_details");
        String userId = userDetails.getString("id");
        String studentId = userDetails.getString("student_id");
        String name = userDetails.getString("name");
        String surname = userDetails.getString("surname");
        String password = userDetails.getString("password");
        String year = userDetails.getString("year");
        String program = userDetails.getString("program");
        String department = userDetails.getString("dpt");
        String national_id = userDetails.getString("national_id");
        String address = userDetails.getString("address");

        Intent intent = new Intent(MainActivity.this, Dashv2.class);
        intent.putExtra("userId", userId);
        intent.putExtra("studentId", studentId);
        intent.putExtra("name", name);
        intent.putExtra("surname", surname);
        intent.putExtra("password", password);
        intent.putExtra("year", year);
        intent.putExtra("program", program);
        intent.putExtra("department", department);
        intent.putExtra("national_id", national_id);
        intent.putExtra("address", address);
        startActivity(intent);

        // Retrieve FCM token and send it to the server
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.getException());
                return;
            }

            String token = task.getResult();
            sendTokenToServer(token);
        });
    }

    private void sendTokenToServer(String token) {
        TokensInterface tokensInterface = ApiClient.getTokensInterface();
        Call<Void> call = tokensInterface.storeToken(token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("MainActivity", "Token stored successfully");
                } else {
                    Log.e("MainActivity", "Error storing token: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
            }
        });
    }
}
