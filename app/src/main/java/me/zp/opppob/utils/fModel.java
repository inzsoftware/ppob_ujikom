package me.zp.opppob.utils;

import android.support.v4.app.Fragment;

public class fModel {
    Fragment f;
    int t;

    public fModel(Fragment f, int iconid){
        this.f = f;
        this.t = iconid;
    }

    public Fragment getFragment() {
        return f;
    }

    public int getTitle() {
        return t;
    }
}
