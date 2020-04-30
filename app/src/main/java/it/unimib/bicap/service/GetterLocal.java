package it.unimib.bicap.service;



import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GetterLocal implements GetterInfo {
    private  Context context;

    public GetterLocal(Context appContext) {
        this.context=appContext;
    }

    @Override
    public String getIdProgetto(JSONObject progetto) {
        try {
            return progetto.getString("id");
        } catch (JSONException e) {
            Log.d("error", "not found");
        }
        return null;
    }

    @Override
    public String getNomeProgetto(JSONObject progetto) {
        try {
            return progetto.getString("nome progetto");
        } catch (JSONException e) {
            Log.d("error", "not found");
        }
        return null;
    }

    @Override
    public String getDescrizione(JSONObject progetto) {
        try {
            return progetto.getString("descrizione progetto");
        } catch (JSONException e) {
            Log.d("error", "not found");
        }
        return null;
    }

    @Override
    public String getAutore(JSONObject progetto) {
        try {
            return progetto.getString("autore progetto");
        } catch (JSONException e) {
            Log.d("error", "not found");
        }
        return null;
    }

    @Override
    public JSONObject getProgetto(JSONArray progetti, int index) {
        try {
            return progetti.getJSONObject(index);
        } catch (JSONException e) {
            Log.d("error", "not found");
        }
        return null;
    }

    @Override
    public JSONArray getPassi(JSONObject progetti) {
        try {
            return progetti.getJSONArray("passi");
        } catch (JSONException e) {
            Log.d("error", "not found");
        }
        return null;
    }

    @Override
    public JSONObject getPasso(JSONArray passi, int index) {
        try {
            return passi.getJSONObject(index);
        } catch (JSONException e) {
            Log.d("error", "not found");
        }
        return null;
    }

    @Override
    public int getNPassi(JSONArray passi) {
        return passi.length();
    }
}
