package me.zp.opppob;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import me.zp.opppob.utils.DService;

public class Splash extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final DService ds = new DService(this, null, "");
        final String sf = getSharedPreferences("analys", MODE_PRIVATE).getString("androidid", "0");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;
                if (sf!="0"){
                    i = new Intent(Splash.this, UserHome.class);
                    ds.sendToken();
                }else {
                    i = new Intent(Splash.this, LoginScreen.class);
                }
                Splash.this.startActivity(i);
                finish();
            }
        }, 1000);
    }
}
