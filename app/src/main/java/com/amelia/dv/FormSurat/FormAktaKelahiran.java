package com.amelia.dv.FormSurat;
import static com.amelia.dv.DB_contract.CONNECT.ip;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amelia.dv.R;
import com.amelia.dv.rw.HomeFragment;
import com.android.volley.AuthFailureError;
import com.google.android.material.textfield.TextInputEditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class FormAktaKelahiran extends Fragment {

    // Deklarasi variabel
    private TextInputEditText tKK, tNik, tNama, tTempat, tTanggal, tJK, tGol, tAlamat, tRT, tRW, tDesa, tkec, tAgama, tStatus, tPekerjaan, tWarga, tKeperluan;
    private ImageButton back;
    private int[] buttonIds = {
            R.id.imgbutton1, R.id.imgbutton2, R.id.imgbutton3, R.id.imgbutton4,
            R.id.imgbutton5, R.id.imgbutton6, R.id.imgbutton7, R.id.imgbutton8
    };
    private int[] layoutIds = {
            R.id.linearrlyott1, R.id.linearrlyott2, R.id.linearrlyott3, R.id.linearrlyott4,
            R.id.linearrlyott5, R.id.linearrlyott6, R.id.linearrlyott7, R.id.linearrlyott8
    };
    private Uri[] selectedImageUris = new Uri[8];
    private ActivityResultLauncher<Intent>[] imagePickerLaunchers = new ActivityResultLauncher[8];

    public FormAktaKelahiran() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_akta_kelahiran, container, false);

        // Inisialisasi TextInputEditText
        tKK = view.findViewById(R.id.t_nkk);
        tNik = view.findViewById(R.id.t_nik);
        tNama = view.findViewById(R.id.t_nama);
        tTempat = view.findViewById(R.id.t_tempat);
        tTanggal = view.findViewById(R.id.t_tanggal);
        tJK = view.findViewById(R.id.t_jeniskelamin);
        tGol = view.findViewById(R.id.t_golongandarah);
        tAlamat = view.findViewById(R.id.t_alamat);
        tRT = view.findViewById(R.id.t_rt);
        tRW = view.findViewById(R.id.t_rw);
        tDesa = view.findViewById(R.id.t_desa);
        tkec = view.findViewById(R.id.t_kecamatan);
        tAgama = view.findViewById(R.id.t_agama);
        tStatus = view.findViewById(R.id.t_kawin);
        tPekerjaan = view.findViewById(R.id.t_pekerjaan);
        tWarga = view.findViewById(R.id.t_kewarganegaraan);
        tKeperluan = view.findViewById(R.id.t_keperluan);

        // Tombol kembali
        back = view.findViewById(R.id.b_arrowback);
        back.setOnClickListener(v -> {
            HomeFragment home = new HomeFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, home)
                    .addToBackStack(null)
                    .commit();
        });

        // Ambil NIK dari SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String nik = sharedPreferences.getString("niklogin", null);

        if (nik != null) {
            tNik.setText(nik);
            fetchPendudukData(nik); // Ambil data penduduk berdasarkan NIK
        }

        // Inisialisasi Launcher dan Listener Tombol
        initializeImagePickers(view);

        return view;
    }


    private void initializeImagePickers(View view) {
        for (int i = 0; i < buttonIds.length; i++) {
            final int index = i;
            imagePickerLaunchers[i] = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                            selectedImageUris[index] = result.getData().getData();
                            displayImageInLayout(layoutIds[index], selectedImageUris[index]);
                        } else {
                            Toast.makeText(requireContext(), "Tidak ada gambar dipilih untuk tombol " + (index + 1), Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            ImageButton imageButton = view.findViewById(buttonIds[i]);
            imageButton.setOnClickListener(v -> openFileChooser(imagePickerLaunchers[index]));
        }
    }




    private void openFileChooser(ActivityResultLauncher<Intent> launcher) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    private void displayImageInLayout(int layoutId, Uri imageUri) {
        LinearLayout linearLayout = getView().findViewById(layoutId);

        if (linearLayout != null && imageUri != null) {
            ImageView imageView = new ImageView(requireContext());
            imageView.setImageURI(imageUri);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            linearLayout.removeAllViews();
            linearLayout.addView(imageView);

            imageView.setOnClickListener(v -> openFileChooser(imagePickerLaunchers[getLayoutIndex(layoutId)]));
        } else {
            Toast.makeText(requireContext(), "Gagal menampilkan gambar", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getFileDataFromUri(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void kirimData() {
        String url = "http://" + ip + "/mobile/simpan_data_dengan_foto.php"; // URL untuk mengirim data ke server

        // Ambil data dari input form
        String noKK = tKK.getText().toString();
        String nik = tNik.getText().toString();
        String nama = tNama.getText().toString();
        String tanggal = tTanggal.getText().toString();
        String jk = tJK.getText().toString();
        String gol = tGol.getText().toString();
        String alamat = tAlamat.getText().toString();
        String rt = tRT.getText().toString();
        String rw = tRW.getText().toString();
        String desa = tDesa.getText().toString();
        String kecamatan = tkec.getText().toString();
        String agama = tAgama.getText().toString();
        String status = tStatus.getText().toString();
        String pekerjaan = tPekerjaan.getText().toString();
        String warga = tWarga.getText().toString();
        String keperluan = tKeperluan.getText().toString();

        // Ambil URI gambar yang dipilih (misalnya gambar pertama)
        Uri imageUri = selectedImageUris[0];  // Misalnya memilih gambar dari tombol pertama

        // Jika tidak ada gambar yang dipilih, beri notifikasi
        if (imageUri == null) {
            Toast.makeText(requireContext(), "Harap pilih gambar terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Menyediakan RequestQueue
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Buat MultipartRequest untuk mengirim data gambar dan form
        MultipartRequest multipartRequest = new MultipartRequest(url,
                response -> {
                    try {
                        // Parse the response as JSONObject
                        JSONObject jsonResponse = new JSONObject(response);

                        // Check status from JSON response
                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(requireContext(), "Data berhasil dikirim!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Gagal mengirim data: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Gagal mengirim data: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("no_kk", noKK);
                params.put("nik", nik);
                params.put("nama", nama);
                params.put("tanggal_lahir", tanggal);
                params.put("jenis_kelamin", jk);
                params.put("golongan_darah", gol);
                params.put("alamat", alamat);
                params.put("rt", rt);
                params.put("rw", rw);
                params.put("desa", desa);
                params.put("kecamatan", kecamatan);
                params.put("agama", agama);
                params.put("status_perkawinan", status);
                params.put("pekerjaan", pekerjaan);
                params.put("kewarganegaraan", warga);
                params.put("keperluan", keperluan);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> byteData = new HashMap<>();
                if (imageUri != null) {
                    // Convert the image URI to byte array
                    byte[] imageData = getByteArrayFromUri(imageUri);
                    if (imageData != null) {
                        byteData.put("foto", new DataPart("foto.jpg", imageData));
                    }
                }
                return byteData;
            }
        };

        // Add request to the queue
        queue.add(multipartRequest);
    }

    private byte[] getByteArrayFromUri(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    private int getLayoutIndex(int layoutId) {
        for (int i = 0; i < layoutIds.length; i++) {
            if (layoutIds[i] == layoutId) {
                return i;
            }
        }
        return -1;
    }

    private void fetchPendudukData(String nik) {
        String url = "http://" + ip + "/mobile/tampildata.php?nik=" + nik;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        tKK.setText(response.getString("no_kk"));
                        tNama.setText(response.getString("nama_lengkap"));
                        tTempat.setText(response.getString("tempat_lahir"));
                        tTanggal.setText(response.getString("tanggal_lahir"));
                        tJK.setText(response.getString("jenis_kelamin"));
                        tGol.setText(response.getString("golongan_darah"));
                        tAlamat.setText(response.getString("alamat"));
                        tRT.setText(response.getString("rt"));
                        tRW.setText(response.getString("rw"));
                        tDesa.setText(response.getString("desa"));
                        tkec.setText(response.getString("kecamatan"));
                        tAgama.setText(response.getString("agama"));
                        tStatus.setText(response.getString("status_perkawinan"));
                        tPekerjaan.setText(response.getString("pekerjaan"));
                        tWarga.setText(response.getString("kewarganegaraan"));
                    } catch (JSONException e) {
                        Toast.makeText(requireContext(), "Gagal memproses data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Gagal mengambil data: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        queue.add(jsonObjectRequest);
    }



}
