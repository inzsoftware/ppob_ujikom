package me.zp.opppob.tabItem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import java.util.LinkedList;

import me.zp.opppob.R;
import me.zp.opppob.UserHome;
import me.zp.opppob.utils.DService;
import me.zp.opppob.utils.dBHelper;
import me.zp.opppob.utils.hModel;

public class History extends Fragment implements DialogInterface.OnClickListener, View.OnClickListener {

    RecyclerView rv;
    String id, idpel;
    EditText txtSearch;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.layout_history, container, false);
        rv = (RecyclerView)v.findViewById(R.id.hList);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        txtSearch = (EditText)v.findViewById(R.id.txtSearch);
        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                new geetData().execute(txtSearch.getText().toString());
                return true;
            }
        });
        ((Button)v.findViewById(R.id.btnSearch)).setOnClickListener(this);
        ((Button)v.findViewById(R.id.btnClF)).setOnClickListener(this);
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
                if (db.Delete("history", "id="+adapter.getList().get(ids).getId())){
                    adapter.Delete(ids);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSearch:
                new geetData().execute(txtSearch.getText().toString());
                break;
            case R.id.btnClF:
                txtSearch.setText("");
                showData();
                break;
        }
    }

    class geetData extends AsyncTask<String, Integer, LinkedList<hModel>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            adapter.Clear();
        }

        @Override
        protected LinkedList<hModel> doInBackground(String... strings) {
            LinkedList<hModel> list = new LinkedList<>();
            String search = strings.length < 1 ? "1" : "name LIKE '"+strings[0]+"%' OR idpel LIKE '"+strings[0]+"%'";
            Cursor c = db.getData("history", search, "*");
            if (c!=null){
                while (c.moveToNext()){
                    list.add(new hModel(c.getInt(0), c.getString(1),c.getString(2),c.getString(3), c.getString(4)));
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
            View v = getLayoutInflater().inflate(R.layout.h_item, viewGroup, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ids = i;
                    AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
                    ArrayAdapter aL = new ArrayAdapter<String>(getContext(),R.layout.d_item);
                    aL.addAll( "Hapus");
                    ab.setAdapter(aL, History.this);
                    ab.show();
                }
            });
            return new VH(v);
        }

        public void setList(LinkedList<hModel> list) {
            this.list = list;
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
            try {
                int jbayar = Integer.valueOf(list.get(i).getData().getString("jumlah"))+Integer.valueOf(list.get(i).getData().getString("admin"));
                vh.txtDesc.setText(getString(R.string.hItem, list.get(i).getName(), DService.getCurrency(jbayar)));
                vh.txtTgl.setText(list.get(i).getTime());
                vh.txtIdpel.setText(list.get(i).getIdPel());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class VH extends RecyclerView.ViewHolder{
            TextView txtDesc, txtTgl, txtIdpel;

            public VH(@NonNull View v) {
                super(v);
                txtDesc = v.findViewById(R.id.txtdesc);
                txtTgl = v.findViewById(R.id.txttgl);
                txtIdpel = v.findViewById(R.id.txtIdpl);
            }
        }
    }
}
