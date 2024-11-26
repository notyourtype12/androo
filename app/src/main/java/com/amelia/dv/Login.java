package com.amelia.dv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amelia.dv.DB_contract.CONNECT;
import com.amelia.dv.rw.MainActivityRt;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends Fragment {

    private EditText etx_nik, etx_password;
    private TextView txactivate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Check if user is already logged in
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedNIK = sharedPreferences.getString("niklogin", null);
        String savedName = sharedPreferences.getString("namalogin", null);

        if (savedNIK != null && savedName != null) {
            // Redirect to MainActivityRt with saved data
            navigateToMainActivity(savedNIK, savedName);
        }

        // Initialize UI elements
        etx_nik = view.findViewById(R.id.input_nik);
        etx_password = view.findViewById(R.id.input_pass);
        txactivate = view.findViewById(R.id.tv_activate);
        Button btn_lgn = view.findViewById(R.id.btn_login);

        // Login button click event
        btn_lgn.setOnClickListener(v -> {
            String nik = etx_nik.getText().toString().trim();
            String password = etx_password.getText().toString().trim();

            if (!nik.isEmpty() && !password.isEmpty()) {
                if (isNetworkAvailable()) {
                    performLogin(nik, password);
                } else {
                    Toast.makeText(getActivity(), "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Username dan Password tidak boleh kosong.", Toast.LENGTH_SHORT).show();
            }
        });

        txactivate.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new Aktivasi())
                    .addToBackStack(null)
                    .commit();
        });
        return view;
    }

    // Perform login request
    private void performLogin(String nik, String password) {
        String url = CONNECT.urlLogin; // URL for login request
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String message = jsonResponse.getString("message");

                        if (message.equals("Selamat Datang")) {
                            String nikResult = jsonResponse.getString("nik");
                            String name = jsonResponse.getString("name");

                            // Save data to SharedPreferences
                            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("niklogin", nikResult);
                            editor.putString("namalogin", name);
                            editor.apply();

                            // Redirect to MainActivityRt
                            navigateToMainActivity(nikResult, name);

                        } else if (message.equals("0")) {
                            Toast.makeText(getActivity(), "Login gagal. NIK atau Password salah.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Kesalahan dalam pengolahan data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getActivity(), "Terjadi kesalahan koneksi.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nik", nik);
                params.put("password", password);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void navigateToMainActivity(String nik, String name) {
        Intent intent = new Intent(getActivity(), MainActivityRt.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("nik", nik);
        intent.putExtra("name", name);
        startActivity(intent);
        requireActivity().finish();
    }

    // Check network availability
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}