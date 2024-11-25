package com.amelia.dv.FormSurat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.amelia.dv.R;
import com.amelia.dv.rw.HomeFragment;

public class FormKartuKeluarga extends Fragment {
    ImageButton back;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_kartu_keluarga, container, false);

        back = view.findViewById(R.id.b_arrowback);
        back.setOnClickListener(v -> {
            // Ganti dengan Fragment lain, misalnya FragmentLogin
            HomeFragment home = new HomeFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, home) // R.id.fragment_container adalah ID container untuk menampilkan fragment
                    .addToBackStack(null) // Menambahkan ke back stack agar bisa kembali
                    .commit();
        });

        return view;
    }
}