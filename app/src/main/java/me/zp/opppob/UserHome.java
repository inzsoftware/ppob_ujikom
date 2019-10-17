package me.zp.opppob;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.StaticLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.zp.opppob.tabItem.History;
import me.zp.opppob.tabItem.Notif;
import me.zp.opppob.tabItem.Profil;
import me.zp.opppob.tabItem.Tab1;
import me.zp.opppob.utils.DService;
import me.zp.opppob.utils.VPAdapter;
import me.zp.opppob.utils.fModel;
import me.zp.opppob.R;

public class UserHome extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    ViewPager vp;
    TabLayout tl;
    TextView nama, saldo;
    SharedPreferences sP;
    Tab1 t1 = new Tab1();
    InputMethodManager imm;
    History t2 = new History();
    Notif t3 = new Notif();
    Profil t4 = new Profil();
    DService ds;
    EditText e1,e2;
    File imgpath;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    Calendar newCalendar = Calendar.getInstance();
    String month="", year="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestStoragePermission();
        imgpath = new File(getCacheDir().getPath(), "Shutta.jpg");
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//        getActionBar().hide();
        setContentView(R.layout.activity_user_tab);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        saldo = findViewById(R.id.txtSaldo);
        ds = new DService(this);
        sP = ds.getsPref();
        vp = (ViewPager)findViewById(R.id.pager);
        tl = (TabLayout)findViewById(R.id.tab);
        VPAdapter vadapter = new VPAdapter(getSupportFragmentManager(),
                new fModel(t1, R.drawable.payment),
                new fModel(t3, R.drawable.bell),
                new fModel(t4, R.mipmap.account));
        vp.setAdapter(vadapter);
        vp.addOnPageChangeListener(this);
        tl.setupWithViewPager(vp);
        vadapter.cTitle(tl);
        month = String.valueOf(newCalendar.get(Calendar.MONTH));
        year = String.valueOf(newCalendar.get(Calendar.YEAR));
    }

    public void dChange(){
//        t2.showData();
    }

    public void showMenu(int id){
        ((Button)findViewById(id)).setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int i) {
        if (i==2){
            imm.hideSoftInputFromWindow(vp.getWindowToken(), 0);
//            t3.cfocus();
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.takepic:
                startCamera();
                break;
            case R.id.btnDF:
                showDateDialog();
                break;
            case R.id.submit:
                t1.SubmitData(imgpath.getAbsolutePath(), month, year);
                break;
            case R.id.btnLogout:
                ds.ShowMessage(getString(R.string.konfir), getString(R.string.konfLogout), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String token = sP.getString("token", "00");
                        sP.edit().clear().apply();
                        sP.edit().putString("token", token).apply();
                        getDatabasePath("app_db").delete();
                        startActivity(new Intent(UserHome.this, LoginScreen.class));
                        UserHome.this.finish();
                    }
                });
                break;
            case R.id.btnCPassword:
                View Mv = getLayoutInflater().inflate(R.layout.m_layout, null, false);
                t4.t1 = Mv.findViewById(R.id.edtxLP);
                t4.t2 = Mv.findViewById(R.id.edtxNP);
                ds.ShowMessage(getString(R.string.cPass), Mv, t4);
                break;
            case R.id.btnSave:
                t4.inPD();
                t4.SaveEdit();
                break;
        }
    }

    private void startCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
        startActivityForResult(intent, 1);
    }

    private void saveImage(Bitmap finalBitmap) {
        if (imgpath.exists()) imgpath.delete ();
        try {
            FileOutputStream out = new FileOutputStream(imgpath);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int rQ, int resQ, @Nullable Intent data) {
        if (data != null && data.getExtras() != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            saveImage(imageBitmap);
            t1.pic = true;
            t1.bukti.setImageBitmap(BitmapFactory.decodeFile(imgpath.getAbsolutePath()));
        }
    }

    public static String bundle2string(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        String string = "Bundle{";
        for (String key : bundle.keySet()) {
            string += " " + key + " => " + bundle.get(key) + ";";
        }
        string += " }Bundle";
        return string;
    }

    private void requestStoragePermission() {
        String[] p = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        for (int i=0; i<p.length; i++){
            if (ContextCompat.checkSelfPermission(this, p[i]) == PackageManager.PERMISSION_DENIED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, p[i])) {
                    ds.ShowMessage(getString(R.string.oops), getString(R.string.needperm));
                }
                break;
            }
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, p, 11);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean b = true;
        for (int i=0; i<permissions.length;i++){
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                b=false;
                break;
            }
        }
        if (!b){
            ds.ShowMessage(getString(R.string.oops), getString(R.string.needperm));
        }
    }

    private void showDateDialog(){
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                month = String.valueOf(monthOfYear);
                UserHome.this.year = String.valueOf(year);
                t1.dtF.setText(month+"-"+UserHome.this.year);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
