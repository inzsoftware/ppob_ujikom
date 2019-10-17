package me.zp.opppob.tabItem;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.request.SimpleMultiPartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import me.zp.opppob.R;
import me.zp.opppob.utils.DService;
import me.zp.opppob.utils.dBHelper;
import me.zp.opppob.utils.hCallback;
import me.zp.opppob.utils.jBayarAdapter;

public class Tab1 extends Fragment implements View.OnClickListener, hCallback {


    jBayarAdapter adapter;
    InputMethodManager imm;
    ProgressDialog pd;
    DService req;
    int mode=0;
    dBHelper db;
    public ImageView bukti;
    public Button dtF;
    EditText no_kwh,kwh;
    public boolean pic = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        adapter = new jBayarAdapter(getContext());
        pd = new ProgressDialog(getContext());
        req = new DService(getContext(), this, "1");
        db = new dBHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_home, container, false);
        bukti = (ImageView)v.findViewById(R.id.imgbukti);
        dtF = (Button)v.findViewById(R.id.btnDF);
        no_kwh = (EditText)v.findViewById(R.id.txtnokwh);
        kwh = (EditText)v.findViewById(R.id.txtkwh);
        return v;
    }

    public void setText(String text){
        dtF.setText(text);
    }

    public void Reset(){
        bukti.setImageBitmap(null);
        dtF.setText(getString(R.string.btnDF));
        no_kwh.setText("");
        kwh.setText("");
        pic=false;
    }

    public void SubmitData(final String imgpath, final String month, final String year){
        if (dtF.getText().toString().contains(getString(R.string.btnDF))){
            Toast.makeText(getContext(), getString(R.string.fRequired, "bulan dan tahun"), Toast.LENGTH_LONG).show();
        }else if(no_kwh.getText().length()<1){
            Toast.makeText(getContext(), getString(R.string.fRequired, "nomor kwh"), Toast.LENGTH_LONG).show();
        }else if(kwh.getText().length()<1){
            Toast.makeText(getContext(), getString(R.string.fRequired, "kwh sekarang"), Toast.LENGTH_LONG).show();
        }else if (!pic){
            Toast.makeText(getContext(), getString(R.string.fRequired, "gambar"), Toast.LENGTH_LONG).show();
        }else {
            req.ShowMessage(getString(R.string.konfir), getString(R.string.qSend,no_kwh.getText().toString()), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pd.setMessage(getString(R.string.wait));
                    pd.show();
                    mode=1;
                    SimpleMultiPartRequest re = req.Upload2("bukti");
                    re.addFile("Image", imgpath);
                    re.addStringParam("month", month);
                    re.addStringParam("year", year);
                    re.addStringParam("no_kwh", no_kwh.getText().toString());
                    re.addStringParam("kwh", kwh.getText().toString());
                    req.getRq().add(re);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void Selesai(Boolean status, final JSONObject hasil, String err) throws JSONException {
        pd.dismiss();
        if (status){
            switch (mode){
                case 1:
                    ContentValues cv = new ContentValues();
                    cv.put("tgl",hasil.getString("tgl"));
                    cv.put("name",hasil.getJSONObject("cData").getString("nama"));
                    cv.put("idpel",hasil.getJSONObject("cData").getString("idpel"));
                    cv.put("data",hasil.getJSONObject("cData").toString());
                    db.insert("history",cv);
                    req.ShowMessage(getString(R.string.success), hasil.getString("Msg"));
                    Reset();
                    break;
            }
        }else {
            req.ShowMessage(getString(R.string.failed), err);
        }
    }

    private void Clear(){

    }

}
