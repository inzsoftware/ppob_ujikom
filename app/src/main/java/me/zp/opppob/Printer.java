package me.zp.opppob;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import me.zp.opppob.printer.Print;
import me.zp.opppob.utils.DService;
import me.zp.opppob.R;

public class Printer extends AppCompatActivity {

    TextView lItem;
    BluetoothAdapter bAdapter;
    Print printer = new Print();
    DService util;
    bAdapter adapter = new bAdapter();
    RecyclerView rv;
    String a = "ss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_printer);
        util = new DService(this);
        bAdapter = BluetoothAdapter.getDefaultAdapter();
        lItem = (TextView)findViewById(R.id.bName);
        rv = (RecyclerView)findViewById(R.id.lbD);
        rv.setLayoutManager(new LinearLayoutManager(this));
        if (bAdapter.isEnabled()){
            getDevice();
        }else {
            Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
            this.startActivityForResult(intent, 1);
        }
//        new tHread().execute(1);
    }

    private void getDevice(){
        rv.setAdapter(adapter);
        new tHread().execute(1);
    }

    class tHread extends AsyncTask<Integer, Integer, String[]>{

        @Override
        protected String[] doInBackground(Integer... i) {
            switch (i[0]){
                case 1:
                    for (BluetoothDevice device : bAdapter.getBondedDevices()){
                        adapter.addItem(new String[]{device.getName(), device.getAddress()});
                        a = device.getName();
                    }
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            adapter.ShowData();
        }
    }

    class bAdapter extends RecyclerView.Adapter<bVH>{

        LinkedList<String[]> list = new LinkedList<>();

        @Override
        public bVH onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = getLayoutInflater().inflate(R.layout.bditem, viewGroup, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog pd = new ProgressDialog(Printer.this);
                    final String[] hasil = list.get(Integer.valueOf(v.getTag().toString()));
                    pd.setCancelable(false);
                    pd.setMessage("Mengkoneksikan...");
                    pd.show();
                    printer.connectPrinter(hasil[1], new Print.PrinterConnectListener() {
                        @Override
                        public void onConnected() {
                            pd.dismiss();
                            util.getsPref().edit().putString("name", hasil[0]).putString("adress", hasil[1]).apply();
                        }

                        @Override
                        public void onFailed() {
                            pd.dismiss();
                            Toast.makeText(Printer.this, "Gagal mengkoneksikan", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            return new bVH(v);
        }

        public void addItem(String[] bIndentity){
            list.add(bIndentity);
        }

        public void ShowData(){
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(bVH v, int i) {
            v.tItem.setText(list.get(i)[0]);
            v.iView.setTag(i);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class bVH extends RecyclerView.ViewHolder{

        TextView tItem;
        View iView;

        public bVH(View v) {
            super(v);
            iView = v;
            tItem = (TextView)v.findViewById(R.id.bName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==1){
            if (bAdapter != null && bAdapter.isEnabled()){
                getDevice();
            }else {
                finish();
            }
            Toast.makeText(this, "HAsil "+requestCode+":"+resultCode, Toast.LENGTH_LONG).show();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    getDevice();
//                }
//            }, 1000);
        }
    }
}
