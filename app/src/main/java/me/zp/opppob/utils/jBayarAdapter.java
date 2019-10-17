package me.zp.opppob.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.zp.opppob.R;

public class jBayarAdapter extends RecyclerView.Adapter<jBayarAdapter.VH> {

    Context ctx;
    JSONArray jK = new JSONArray();
    JSONArray jArray;

    public jBayarAdapter(Context c){
        ctx = c;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.b_item, viewGroup, false);
        return new VH(v);
    }

    public void setjArray(JSONArray jArray) {
        this.jArray = jArray;
        notifyDataSetChanged();
    }

    public void Clear(){
        jArray = jK;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        try {
            JSONObject jo = jArray.getJSONObject(i);
            vh.txtblth.setText(jo.getString("blth"));
            vh.txtkwh.setText(jo.getString("kwh"));
            vh.txtbayar.setText(DService.getCurrency(jo.getInt("bayar"),
                    jo.getBoolean("denda") ? ctx.getString(R.string.withDenda) : ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jArray.length();
    }

    class VH extends RecyclerView.ViewHolder {
        TextView txtblth, txtkwh, txtbayar;
        public VH(@NonNull View v) {
            super(v);
            txtblth = v.findViewById(R.id.txtBlth);
            txtkwh = v.findViewById(R.id.txtKwh);
            txtbayar = v.findViewById(R.id.txtbiaya);
        }
    }
}
