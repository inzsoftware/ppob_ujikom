package me.zp.opppob.utils;

import org.json.JSONException;
import org.json.JSONObject;

public interface hCallback {
    public void Selesai(Boolean status, JSONObject hasil, String err) throws JSONException;
}
