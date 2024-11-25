package com.amelia.dv.rw;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.amelia.dv.rw.status.PengajuanSuratFragment;
import com.amelia.dv.rw.status.SuratDitolakFragment;
import com.amelia.dv.rw.status.SuratSelesaiFragment;

public class StatusPagerAdapter extends FragmentStateAdapter {

    public StatusPagerAdapter(@NonNull Fragment fragment) {
        super(fragment.getChildFragmentManager(), fragment.getLifecycle());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PengajuanSuratFragment();
            case 1:
                return new SuratSelesaiFragment();
            case 2:
                return new SuratDitolakFragment();
            default:
                throw new IllegalStateException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

