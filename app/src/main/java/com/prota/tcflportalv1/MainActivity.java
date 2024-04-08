package com.prota.tcflportalv1;

import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.prota.tcflportalv1.network.ApiClient;
import com.prota.tcflportalv1.network.ApiInterface;
import okhttp3.ResponseBody;
import org.json.JSONObject; // Import this line for JSONObject
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
                // Add your login logic here
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Log the entered username and password
                Log.d("MainActivity", "Username: " + username);
                Log.d("MainActivity", "Password: " + password);

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
                        // Parse the JSON response
                        JSONObject jsonResponse = new JSONObject(response.body().string());

                        // Check if "success" field exists
                        if (jsonResponse.has("success")) {
                            // Check the "success" field
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                // Handle successful login
                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                // Extract user details
                                JSONObject userDetails = jsonResponse.getJSONObject("user_details");
                                String userId = userDetails.getString("id");
                                String studentId = userDetails.getString("student_id");
                                String name = userDetails.getString("name");
                                String surname = userDetails.getString("surname");
                                String password = userDetails.getString("password");
                                String year = userDetails.getString("year");
                                String program = userDetails.getString("program");
                                String department = userDetails.getString("dpt");

                                // Pass user details to another activity)
                                Intent intent = new Intent(MainActivity.this, Dashv2.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("studentId", studentId);
                                intent.putExtra("name", name);
                                intent.putExtra("surname", surname);
                                intent.putExtra("password", password);
                                intent.putExtra("year", year);
                                intent.putExtra("program", program);
                                intent.putExtra("department", department);
                                startActivity(intent);


                            } else {
                                // Handle unsuccessful login
                                Toast.makeText(MainActivity.this, "Login Failed: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle missing "success" field in the response
                            Toast.makeText(MainActivity.this, "Login Failed: Response format error", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle login failure
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                    // Add debugging statements to log response details
                    Log.d("MainActivity", "Response Code: " + response.code());
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d("MainActivity", "Error Body: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error message
                Log.e("MainActivity", "Error: " + t.getMessage());

                // Handle network or other errors
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
