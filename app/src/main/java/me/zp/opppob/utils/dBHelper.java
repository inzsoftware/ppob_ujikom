package me.zp.opppob.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class dBHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public dBHelper(Context c) {
        super(c, "app_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE history (id   INTEGER PRIMARY KEY AUTOINCREMENT, tgl  TIME, name VARCHAR (100), idpel VARCHAR (20), data TEXT);");
        db.execSQL("CREATE TABLE notif (id   INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR (100), time  TIME, scope VARCHAR (20), data TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getData(String table, String where, String field){
        try{
            return getReadableDatabase().rawQuery("SELECT "+field+" FROM "+table+" WHERE "+where, null);
        }catch (Exception e){
            return null;
        }
    }

    public boolean insert(String table, ContentValues cv){
        try{
            getWritableDatabase().insert(table, null, cv);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean Delete(String table, String where){
        try {
            getWritableDatabase().execSQL("DELETE FROM "+table+" WHERE "+where);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static void subscribe(){
        FirebaseMessaging.getInstance().subscribeToTopic("global")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                        }else {
                        }
                    }
                });
    }
}
