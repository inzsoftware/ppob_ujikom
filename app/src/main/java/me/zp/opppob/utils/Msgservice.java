package me.zp.opppob.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import me.zp.opppob.R;
import me.zp.opppob.Splash;

/**
 * Created by ZP on 9/2/2018.
 */

public class Msgservice extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    private dBHelper dH;
    DService ds;

    @Override
    public void onCreate() {
        super.onCreate();
        dH = new dBHelper(this);
        ds = new DService(getApplicationContext(), null,"");
    }

    @Override
    public void onMessageReceived(RemoteMessage d) {
        try{
            sendNotification(d.getData().get("title"), d.getData().get("body"));
            ContentValues cv = new ContentValues();
            cv.put("title",d.getData().get("title"));
            cv.put("time",d.getData().get("time"));
            cv.put("scope",d.getData().get("scope"));
            cv.put("data",d.getData().get("body"));
            if (dH.insert("notif", cv)) {
                sendMessage();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onNewToken(String s) {
        ds.getsPref().edit().putBoolean("uToken", false).apply();
        try {
            dBHelper.subscribe();
            ds.getsPref().edit().putString("token", s).apply();
            String uid = ds.getsPref().getString("androidid", "1123");
            if (!ds.getsPref().contains("androidid") || uid == "1123"){
                return;
            }
            StringRequest sr = new StringRequest(1,ds.getFullAPI()+"token", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.contains("Berhasil input token")) {
                        ds.getsPref().edit().putBoolean("uToken", true).apply();
                        Toast.makeText(getApplicationContext(), "Suskes token "+response, Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Gagal token", Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                public byte[] getBody() throws AuthFailureError {
                    String a = "";
                    try {
                        a = new JSONObject().put("token", ds.getsPref().getString("token", "00")).toString();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return a.getBytes();
                }
            };
            ds.getRq().add(sr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendNotification(String tit, String messageBody) {
        Intent intent = new Intent(this, Splash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.bell)
                .setContentTitle(tit)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setChannelId("ppobNotif");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("ppobNotif", "PPOB Notification", NotificationManager.IMPORTANCE_HIGH));
        }
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendMessage() {
        try {
            Intent intent = new Intent("notif-launcher");
            Cursor c = dH.getData("notif", "id=LAST_INSERT_ROWID()", "*");
            if (c != null && c.moveToNext()) {
                // You can also include some extra data.
                intent.putExtra("id", c.getString(0));
                intent.putExtra("title", c.getString(1));
                intent.putExtra("time", c.getString(2));
                intent.putExtra("scope", c.getString(3));
                intent.putExtra("data", c.getString(4));
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            } else {
                Toast.makeText(this, "Error, mungkin null ", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
