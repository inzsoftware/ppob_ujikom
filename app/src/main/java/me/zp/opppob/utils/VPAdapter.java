package me.zp.opppob.utils;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class VPAdapter extends FragmentStatePagerAdapter {
    int tCount;
    fModel[] frag;

    public VPAdapter(FragmentManager fm, fModel... fM) {
        super(fm);
        frag = fM;
    }

    public void cTitle(TabLayout tl){
        for (int i=0; i<frag.length;i++){
            tl.getTabAt(i).setIcon(frag[i].getTitle());
        }
    }

    @Override
    public Fragment getItem(int i) {
        return frag[i].getFragment();
    }

    @Override
    public int getCount() {
        return frag.length;
    }
}
