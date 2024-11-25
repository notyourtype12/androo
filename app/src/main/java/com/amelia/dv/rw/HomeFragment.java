package com.amelia.dv.rw;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amelia.dv.FormSurat.FormAktaKelahiran;
import com.amelia.dv.FormSurat.FormAktaPerkawinan;
import com.amelia.dv.FormSurat.FormKartuKeluarga;
import com.amelia.dv.FormSurat.FormMembuatKtp;
import com.amelia.dv.FormSurat.FormPindahPenduduk;
import com.amelia.dv.FormSurat.FormAktaKematian;
import com.amelia.dv.FormSurat.FormSktm;
import com.amelia.dv.FormSurat.FormSuratPernyataanMiskin;
import com.amelia.dv.MainActivity;
import com.amelia.dv.R;

public class HomeFragment extends Fragment {

    private TextView twelcome;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi komponen UI
        twelcome = view.findViewById(R.id.username);  // Memperbaiki inisialisasi TextView
        ImageView itemAktaKelahiran = view.findViewById(R.id.itemAktaKelahiran);
        ImageView itemPindahPenduduk = view.findViewById(R.id.itemPindahPenduduk);
        ImageView itemAktaKematian = view.findViewById(R.id.itemAktaKematian);
        ImageView itemAktaPernikahan = view.findViewById(R.id.itemAktaPernikahan);
        ImageView itemKK = view.findViewById(R.id.itemKK);
        ImageView itemKTP = view.findViewById(R.id.itemKTP);
        ImageView itemSuratMiskin = view.findViewById(R.id.itemSuratMiskin);
        ImageView itemSKTM = view.findViewById(R.id.itemSKTM);

        // Mengambil data dari SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String nik = sharedPreferences.getString("niklogin", null);
        String name = sharedPreferences.getString("namalogin", null);

        // Menampilkan pesan selamat datang jika login berhasil
        if (nik != null && name != null) {
            twelcome.setText(name);
        } else {
            // Jika belum login, arahkan ke halaman login
            Toast.makeText(getActivity(), "Anda belum login. Silahkan login terlebih dahulu.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        }

        // Set OnClickListener untuk setiap item
        itemAktaKelahiran.setOnClickListener(v -> navigateToFragment(new FormAktaKelahiran()));
        itemPindahPenduduk.setOnClickListener(v -> navigateToFragment(new FormPindahPenduduk()));
        itemAktaKematian.setOnClickListener(v -> navigateToFragment(new FormAktaKematian()));
        itemAktaPernikahan.setOnClickListener(v -> navigateToFragment(new FormAktaPerkawinan()));
        itemKK.setOnClickListener(v -> navigateToFragment(new FormKartuKeluarga()));
        itemKTP.setOnClickListener(v -> navigateToFragment(new FormMembuatKtp()));
        itemSuratMiskin.setOnClickListener(v -> navigateToFragment(new FormSuratPernyataanMiskin()));
        itemSKTM.setOnClickListener(v -> navigateToFragment(new FormSktm()));

        return view;
    }

    // Fungsi untuk mempermudah navigasi ke fragment tertentu
    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);  // Ganti dengan ID fragment container Anda
        transaction.addToBackStack(null);  // Agar bisa kembali ke fragment sebelumnya
        transaction.commit();
    }
}
