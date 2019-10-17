package me.zp.opppob.tabItem;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.ImageRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import me.zp.opppob.R;
import me.zp.opppob.UserHome;
import me.zp.opppob.utils.DService;
import me.zp.opppob.utils.hCallback;

public class Profil extends Fragment implements View.OnClickListener, hCallback, DialogInterface.OnClickListener {

    EditText nama, uname, email, nohp, alamat;
    DService ds;
    LinearLayout lL;
    ImageView image;
    boolean edit = false;
    ProgressDialog pd;
    UserHome home;
    public EditText t1,t2;
    int mode=0;
    File imgpath = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ds = new DService(getContext(), this,"");
        home = (UserHome) getContext();
        inPD();
        requestStoragePermission();
    }

    public void inPD(){
        pd = new ProgressDialog(getContext());
        pd.setCancelable(false);
        pd.setMessage(getString(R.string.wait));
    }

    private void imageProfil(final Boolean force) {
        try{
            SharedPreferences sp = ds.getsPref();
            final String imgname = sp.getString("img", "none");
            if (imgname != null && !imgname.contains("none")){
                imgpath = new File(getContext().getFilesDir().getPath(),imgname);
                if (imgpath.exists() && !force){
                    Bitmap b = BitmapFactory.decodeFile(imgpath.getAbsolutePath());
                    image.setImageBitmap(b);
                }else {
                    if (force){
                        if (!imgpath.delete()){
                            ds.ShowMessage(getString(R.string.error), getString(R.string.errDelete));
                            ds.ShowMessage("path", imgpath.getAbsolutePath());
                        }
                    }
                    image.setImageResource(R.drawable.downloading);
                    ImageRequest request = new ImageRequest(ds.getBaseUrl()+"raw/operator/"+imgname, null, null, new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            try {
                                FileOutputStream out = new FileOutputStream(imgpath);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                                out.flush();
                                out.close();
                                image.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    },0,0,null, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ds.ShowMessage("Test", ds.getBaseUrl()+"raw/operator/"+imgname+" : "+error.getMessage()+":"+error.networkResponse.statusCode);
                        }
                    });
                    ds.getRq().getCache().clear();
                    ds.getRq().add(request);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.l_profil, container, false);
        lL = v.findViewById(R.id.containerP);
        lL.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        image = (ImageView)v.findViewById(R.id.imgP);
        nama = (EditText)v.findViewById(R.id.pName);
        uname = (EditText)v.findViewById(R.id.pUname);
        nohp = (EditText)v.findViewById(R.id.pNoHP);
        email = (EditText)v.findViewById(R.id.pEmail);
        alamat = (EditText)v.findViewById(R.id.pAlamat);
        nama.setOnClickListener(this);
        uname.setOnClickListener(this);
        email.setOnClickListener(this);
        alamat.setOnClickListener(this);
        image.setOnClickListener(onClickImg);
        nama.setText(ds.getsPref().getString("u_name", ""));
        uname.setText(ds.getsPref().getString("u_uname", ""));
        nohp.setText(ds.getsPref().getString("nohp", ""));
        email.setText(ds.getsPref().getString("email", ""));
        alamat.setText(ds.getsPref().getString("u_alamat", ""));
        imageProfil(false);
        return v;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(), "Test", Toast.LENGTH_LONG).show();
        if (!edit){
            home.showMenu(R.id.btnSave);
            lL.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
            v.requestFocus();
            edit = true;
        }
    }

    private View.OnClickListener onClickImg = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            requestStoragePermission();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        }
    };

    public void SaveEdit(){
        pd.show();
        try{
            JSONArray ja = new JSONArray();
            ja.put(uname.getText().toString());
            ja.put("");
            ja.put(nama.getText().toString());
            ja.put(nohp.getText().toString());
            ja.put(email.getText().toString());
            ja.put(alamat.getText().toString());
            JSONObject jo = new JSONObject();
            jo.put("func","edit");
            jo.put("role","operator");
            jo.put("id",ds.getsPref().getString("id",""));
            jo.put("edps",false);
            jo.put("data",ja);
            mode = 1;
            ds.postRequest("func", jo);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void Selesai(Boolean status, JSONObject hasil, String err) throws JSONException {
        pd.dismiss();
        if (status){
            switch (mode){
                case 1:
                    ds.getsPref().edit().putString("u_name", nama.getText().toString())
                            .putString("u_uname", uname.getText().toString())
                            .putString("email", email.getText().toString())
                            .putString("nohp", nohp.getText().toString())
                            .putString("u_alamat", alamat.getText().toString()).apply();
                    ds.ShowMessage(getString(R.string.success), hasil.getString("Msg"));
                    break;
                case 2:
                    ds.getsPref().edit().putString("img", hasil.getString("name")).apply();
                    ds.ShowMessage(getString(R.string.success), getString(R.string.imguploadsuccess));
                    imageProfil(true);
                    break;
                case 3:
                    ds.ShowMessage(getString(R.string.success), hasil.getString("Msg"));
                    break;
            }
        }else {
            ds.ShowMessage("Error", err);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String plama=t1.getText().toString(),pbaru=t2.getText().toString();
        if (plama.length()>0 || pbaru.length()>0){
            pd.show();
            JSONObject jo = new JSONObject();
            try{
                jo.put("func","edit");
                jo.put("role","operator");
                jo.put("id",ds.getsPref().getString("id",""));
                jo.put("oPass",true);
                jo.put("PW1",plama);
                jo.put("PW2",pbaru);
                mode = 3;
                ds.postRequest("func", jo);
            }catch (Exception e){

            }
        }else {
            ds.ShowMessage(getString(R.string.failed), getString(R.string.notEmpty));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1 && resultCode== Activity.RESULT_OK){
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                mode=2;
                pd.show();
                ds.Upload("upload", ds.getPath(data.getData()), "aaa.jpg");
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == 11) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(getContext(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getContext(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

//    class bgP extends AsyncTask<Bitmap, Integer, Boolean>{
//
//        @Override
//        protected Boolean doInBackground(Bitmap... b) {
//            String a = convertToBase64(b[0]);
//            ds.postRequest("upload2","filename=aaaa"+"&img="+a);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean aBoolean) {
//            super.onPostExecute(aBoolean);
//            Toast.makeText(getContext(), "Upload finish", Toast.LENGTH_LONG).show();
//        }
//    }
}
