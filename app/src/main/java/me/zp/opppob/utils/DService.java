package me.zp.opppob.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.Locale;

import me.zp.opppob.R;

public class DService implements Response.Listener<String>, Response.ErrorListener {
//    private String BASE_URL = "http://www.z-p.me/pln/";
        private String BASE_URL = "http://192.168.42.86/appujikom/";
        private String HOST_URL = BASE_URL;
    String gAnalys = "analys";
    RequestQueue rq;
    Context ctx;
    hCallback cb;
    JSONObject jReq;
    SharedPreferences sP;
    boolean hs = false;

    public DService(Context c, hCallback callback, String uid){
        this.ctx = c;
        this.cb = callback;
        sP = ctx.getSharedPreferences(gAnalys, Context.MODE_PRIVATE);
        BASE_URL+="api/v1/operator/"+sP.getString("id","0")+"/";
        rq = Volley.newRequestQueue(ctx);
    }

    public DService(Context c, hCallback callback){
        this.ctx = c;
        this.cb = callback;
        sP = ctx.getSharedPreferences(gAnalys, Context.MODE_PRIVATE);
        rq = Volley.newRequestQueue(ctx);
    }

    public DService(Context c){
        this.ctx = c;
        sP = ctx.getSharedPreferences(gAnalys, Context.MODE_PRIVATE);
    }

    public void lReq(JSONObject dL){
        jReq = dL;
        StringRequest sr = new StringRequest(1, BASE_URL+"login/api", this, this){
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return jReq.toString().getBytes("utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                    return super.getBody();
                }
            }
        };
        rq.add(sr);
    }

    public void GetRequest(String url){
        String burl = BASE_URL+sP.getString("androidid", "1123")+"/";
        StringRequest sr = new StringRequest(0, burl+url, this, this);
        rq.add(sr);
    }

    public void postRequest(String url, JSONObject rString){
        String burl = BASE_URL+sP.getString("androidid", "1123")+"/";
        jReq = rString;
        StringRequest sr = new StringRequest(1, burl+url, this, this){
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return jReq.toString().getBytes("utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                    return super.getBody();
                }
            }
        };
        rq.add(sr);
    }

    public void sendToken(){
        try{
            if (!sP.getBoolean("uToken", false)){
                StringRequest sr = new StringRequest(1,getFullAPI()+"token", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Berhasil input token")) {
                            sP.edit().putBoolean("uToken", true).apply();
                            Toast.makeText(ctx, "Suskes token "+response, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, "Gagal token "+error.networkResponse.statusCode+":"+error.networkResponse.data.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        String a = "";
                        try {
                            a = new JSONObject().put("token", sP.getString("token", "00")).toString();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return a.getBytes();
                    }
                };
                rq.add(sr);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void Upload(String url, String path, String name){
        String burl = BASE_URL+sP.getString("androidid", "1123")+"/";
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(1, burl+url, this, this);
        smr.addFile("Image", path);
        rq.add(smr);
    }

    public SimpleMultiPartRequest Upload2(String url){
        String burl = BASE_URL+sP.getString("androidid", "1123")+"/";
        return new SimpleMultiPartRequest(1, burl+url, this, this);
    }

    public RequestQueue getRq(){
        return rq;
    }

    public String getBaseUrl() {
        return HOST_URL;
    }

    public String getFullAPI(){
        return BASE_URL+sP.getString("androidid", "1123")+"/";
    }

    public void postRequest(String url, final String rString){
        String burl = BASE_URL+sP.getString("androidid", "1123")+"/";
        StringRequest sr = new StringRequest(1, burl+url, this, this){
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return rString.getBytes("utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                    return super.getBody();
                }
            }
        };
        rq.add(sr);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (cb != null){
            ShowMessage("Error", error.getMessage()+":"+error.networkResponse.statusCode);
            try {
                cb.Selesai(false, null, error.getMessage());
            } catch (JSONException e) {
                ShowMessage(ctx.getResources().getString(R.string.error), e.getMessage());
            }
        }
    }

    public String getContext(String str) {
        String strr = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(str.getBytes(), 0, str.length());
            str = new BigInteger(1, mdEnc.digest()).toString(16);
            while (str.length() < 32) {
                str = "0" + str;
            }
            strr = str;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return strr;
    }

    @Override
    public void onResponse(String response) {
        JSONObject jo = toJson(response);
        try{
            if (cb != null){
                if (jo != null && jo.getBoolean("status")){
                    cb.Selesai(true, jo.getJSONObject("data"), "");
                    sP.edit().putString("androidid",
                            String.valueOf(Integer.parseInt(sP.getString("id","0"))+getId())+
                                    getContext(jo.getString("reqcount"))).apply();
                }else if (!jo.getBoolean("status")){
                    cb.Selesai(false, null, jo.getString("Msg"));
                }else {
                    cb.Selesai(false, null, response);
                }
            }
        }catch (Exception e){
            try {
                e.printStackTrace();
                cb.Selesai(false, null, "Code 01\n"+e.getMessage());
            } catch (JSONException e1) {
                ShowMessage(ctx.getResources().getString(R.string.error), "Code 02\n"+e1.getMessage());
            }
        }
    }

    public int getId(){
        return Integer.parseInt(ctx.getResources().getString(R.string.androidid));
    }

    private JSONObject toJson(String data){
        JSONObject jo = null;
        try {
            jo = new JSONObject(data.substring(data.indexOf("{-{")+3, data.indexOf("}-}")));
        } catch (Exception e) {
            ShowMessage("Error Conv", e.getMessage()+"\n"+data);
        }
        return jo;
    }

    public  void ShowMessage(String title, String body){
        AlertDialog.Builder ab = new AlertDialog.Builder(ctx);
        ab.setTitle(title).setMessage(body).setPositiveButton(ctx.getResources().getString(R.string.ok), null).show();
    }

    public  void ShowMessage(String title, String body, DialogInterface.OnClickListener callback){
        AlertDialog.Builder ab = new AlertDialog.Builder(ctx);
        ab.setTitle(title).setMessage(body)
                .setPositiveButton(R.string.ok, callback).setNegativeButton(R.string.cancel, null).show();
    }

    public  void ShowMessage(String title, View v, DialogInterface.OnClickListener callback){
        AlertDialog.Builder ab = new AlertDialog.Builder(ctx);
        ab.setTitle(title).setView(v).setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.send, callback).show();

    }

    public SharedPreferences getsPref() {
        return sP;
    }

    public static String getCurrency(int money, String append){
        return getCurrency(money)+" "+append;
    }

    public static String getCurrency(int money){
        Locale localeID = new Locale("in", "ID");
        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
        return format.format(money);
    }

    public String getPath(Uri uri) {
        Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = ctx.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }
}
