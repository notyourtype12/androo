package com.amelia.dv;

import static com.amelia.dv.DB_contract.CONNECT.ip;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewPassword extends Fragment {

    private TextView temail, totp, tpassword;  // TextView for email, OTP, and new password
    private Button button;
    private RequestQueue requestQueue;  // RequestQueue for network requests
    private String email2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_password, container, false);

        // Initialize the requestQueue
        requestQueue = Volley.newRequestQueue(getActivity());
        temail.setEnabled(false);

        // Retrieve email passed from the previous fragment
        if (getArguments() != null) {
            email2 = getArguments().getString("email");
        }

        // Ensure email is not null or empty
        if (email2 == null || email2.isEmpty()) {
            Toast.makeText(getActivity(), "Email tidak ditemukan.", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack(); // Go back to the previous fragment
            return view;
        }

        // Initialize views
        totp = view.findViewById(R.id.t_otp); // OTP input field
        tpassword = view.findViewById(R.id.t_password); // Password input field
        button = view.findViewById(R.id.btnSubmit); // Submit button
        temail = view.findViewById(R.id.t_email);  // TextView for email

        // Display the email in the TextView
        temail.setText(email2);

        // Button click listener
        button.setOnClickListener(v -> {
            String otp = totp.getText().toString().trim(); // Get OTP input
            String newPass = tpassword.getText().toString().trim(); // Get new password input

            // Validate inputs
            if (otp.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(getActivity(), "OTP dan Password harus diisi", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email2).matches()) {
                // Validate email format
                Toast.makeText(getActivity(), "Email tidak valid", Toast.LENGTH_SHORT).show();
            } else if (newPass.length() < 6) {
                // Check for a strong password
                Toast.makeText(getActivity(), "Password harus memiliki minimal 6 karakter", Toast.LENGTH_SHORT).show();
            } else {
                // Call method to verify OTP and update password
                updatePass(otp, newPass);
            }
        });

        return view;
    }

    // Method to verify OTP and update password
    private void updatePass(final String otp, final String password) {
        // Define URL to send POST request
        String url = "http://" + ip + "/CRUDVolley/CRUDVolley/new-password.php"; // URL API Anda

        // Create a StringRequest to send data as POST body
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            // Get the status and message from the response
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");

                            // Handle the server response
                            if ("success".equals(status)) {
                                // If OTP is valid and password updated
                                Toast.makeText(getActivity(), "Password berhasil diperbarui", Toast.LENGTH_SHORT).show();
                                // Navigate to Login Fragment
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, new Login())  // Switch to Login fragment
                                        .addToBackStack(null)  // Add to back stack
                                        .commit();
                            } else {
                                // If OTP is invalid or other error
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Terjadi kesalahan dalam menerima respons", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Gagal: " + error.toString(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();  // Log error for debugging purposes
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Create a map to send parameters
                Map<String, String> params = new HashMap<>();
                params.put("email", email2);  // Send email from the class variable
                params.put("reset_password_otp", otp);  // Send OTP
                params.put("password", password);  // Send new password
                return params;  // Return the parameters
            }
        };

        // Add the request to the queue
        requestQueue.add(stringRequest);
    }
}
