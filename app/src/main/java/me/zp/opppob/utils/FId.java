package me.zp.opppob.utils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by ZP on 9/2/2018.
 */

public class FId extends FirebaseInstanceIdService{
    private static final String TAG = "FirebaseIIDServiceDemo";
    public String[] name;

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        System.out.println("my firebase token " + token );
    }
    private void sendRegistrationToServer(String token) {

    }
}
