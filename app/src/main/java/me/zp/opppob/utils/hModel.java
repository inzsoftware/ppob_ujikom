package me.zp.opppob.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class hModel {
    int id;String time,name,idPel,cont,scope;JSONObject data;

    public hModel(int id, String time, String name, String idPel, String data){
        try {
            JSONObject jo = new JSONObject(data);
            this.name = name;
            this.idPel = idPel;
            this.id = id;
            this.time = time;
            this.data = jo;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public hModel(int id, String name, String time, String scope, String content, String a){
        this.name = name;
        this.id = id;
        this.time = time;
        this.cont = content;
        this.scope = scope;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getName(){
        return name;
    }

    public String getIdPel(){
        return idPel;
    }

    public String getContent() {
        return cont;
    }

    public JSONObject getData() {
        return data;
    }

    public String getScope() {
        return scope;
    }
}
