package me.zp.opppob.tabItem;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import me.zp.opppob.R;
import me.zp.opppob.utils.DService;
import me.zp.opppob.utils.dBHelper;
import me.zp.opppob.utils.hModel;

public class Notif extends Fragment implements DialogInterface.OnClickListener {

    RecyclerView rv;
    int ids;
    lAdapter adapter = new lAdapter();
    dBHelper db;
    DService req;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new dBHelper(getContext());
        adapter = new lAdapter();
        req = new DService(getContext());
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                mMessageReceiver, new IntentFilter("notif-launcher"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.l_notif, container, false);
        rv = (RecyclerView)v.findViewById(R.id.hList);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        showData();
        return v;
    }

    public void showData() {
        new geetData().execute();
    }

    @Override
    public void onClick(DialogInterface dialog, int i) {
        Toast.makeText(getContext(), String.valueOf(i), Toast.LENGTH_LONG).show();
        switch (i){
            case 0:
                req.ShowMessage(getString(R.string.konfir), getString(R.string.cDelete), this);
                break;
            case -1:
                if (db.Delete("notif", "id="+adapter.getList().get(ids).getId())){
                    adapter.Delete(ids);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(
                mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            adapter.add(i.getIntExtra("id", 0),
                    i.getStringExtra("title"),i.getStringExtra("time"),
                    i.getStringExtra("scope"), i.getStringExtra("data"));
        }
    };

    class geetData extends AsyncTask<String, Integer, LinkedList<hModel>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            adapter.Clear();
        }

        @Override
        protected LinkedList<hModel> doInBackground(String... strings) {
            LinkedList<hModel> list = new LinkedList<>();
            Cursor c = db.getData("notif", "1 ORDER BY id DESC", "*");
            if (c!=null){
                while (c.moveToNext()){
                    list.add(new hModel(c.getInt(0), c.getString(1),
                            c.getString(2),c.getString(3), c.getString(4), ""));
                }
            }
            return list;
        }

        @Override
        protected void onPostExecute(LinkedList<hModel> hModels) {
            super.onPostExecute(hModels);
            adapter.setList(hModels);
        }
    }

    class lAdapter extends RecyclerView.Adapter<lAdapter.VH>{

        LinkedList<hModel> list = new LinkedList<>();

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
            View v = getLayoutInflater().inflate(R.layout.n_item, viewGroup, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ids = Integer.valueOf(v.getTag().toString());
                    AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
                    ArrayAdapter aL = new ArrayAdapter<String>(getContext(),R.layout.d_item);
                    aL.add("Hapus");
                    ab.setAdapter(aL, Notif.this);
                    ab.show();
                }
            });
            return new VH(v);
        }

        public void setList(LinkedList<hModel> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public void add(int idd, String title, String time, String scope, String data){
            list.addFirst(new hModel(idd, title, time, scope, data, ""));
            notifyDataSetChanged();
        }

        public void Delete(int index){
            list.remove(index);
            notifyItemChanged(index);
        }

        public LinkedList<hModel> getList() {
            return list;
        }

        public void Clear(){
            list.clear();
        }

        @Override
        public void onBindViewHolder(@NonNull VH vh, int i) {
            vh.txttitle.setText(list.get(i).getName());
            vh.txttime.setText(list.get(i).getTime());
            vh.txtcontent.setText(list.get(i).getContent());
            vh.v.setTag(i);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class VH extends RecyclerView.ViewHolder{
            TextView txttitle, txttime, txtcontent;
            View v;

            public VH(@NonNull View v) {
                super(v);
                txttitle = v.findViewById(R.id.txtTitile);
                txttime = v.findViewById(R.id.txtTime);
                txtcontent = v.findViewById(R.id.txtContent);
                this.v=v;
            }
        }
    }
}
