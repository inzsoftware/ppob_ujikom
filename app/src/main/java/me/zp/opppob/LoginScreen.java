package me.zp.opppob;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import me.zp.opppob.utils.DService;
import me.zp.opppob.utils.hCallback;
import me.zp.opppob.R;

public class LoginScreen extends AppCompatActivity implements hCallback {
    EditText uname, pass;
    ProgressDialog pd;
    DService inet;
    SharedPreferences sf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        uname = findViewById(R.id.username);
        pass = findViewById(R.id.password);
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        inet = new DService(this, this);
    }

    public void checkLogin(View v){
        if (uname.length()<1){
            uname.setError(getResources().getString(R.string.wrongUname));
        }else if(pass.length()<1){
            pass.setError(getResources().getString(R.string.wrongPass));
        }else {
            doLogin();
        }
    }

    private void doLogin(){
        pd.setMessage("Moghon menunggu");
        pd.show();
        JSONObject jo = new JSONObject();
        try {
            jo.put("uname", uname.getText().toString()).put("pass", pass.getText().toString()).put("role", "operator");
            inet.lReq(jo);
        } catch (Exception e) {
            inet.ShowMessage("Error", e.getMessage()+jo.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void Selesai(Boolean status, JSONObject hasil, String err) throws JSONException {
        pd.dismiss();
        if (status){
            SharedPreferences s = inet.getsPref();
            s.edit().putString("id", hasil.getString("id"))
                    .putString("u_name", hasil.getString("name"))
                    .putString("u_uname", hasil.getString("username"))
                    .putString("email", hasil.getString("email"))
                    .putString("nohp", hasil.getString("no_hp"))
                    .putString("u_alamat", hasil.getString("adress"))
                    .putString("img", hasil.getString("img")).apply();
            startActivity(new Intent(this, UserHome.class));
            new DService(this, null, "").sendToken();
            finish();
        }else {
            inet.ShowMessage("Gagal", err);
        }
    }
}
