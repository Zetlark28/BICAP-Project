package it.unimib.bicap.service;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class GetterLocal implements GetterInfo {
    private static JSONObject converter;

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
            return progetto.getString("nome");
        } catch (JSONException e) {
            Log.d("error", "not found");
        }
        return null;
    }

    @Override
    public String getDescrizione(JSONObject progetto) {
        try {
            return progetto.getString("descrizione");
        } catch (JSONException e) {
            Log.d("error", "not found");
        }
        return null;
    }

    @Override
    public String getAutore(JSONObject progetto) {
        try {
            return progetto.getString("autore");
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

    @Override
    public JSONArray getlistaProgetti(String listaProgeti) {
        try {
            JSONObject jsonLista = converter.getJSONObject(listaProgeti);
            return jsonLista.getJSONArray("progetti");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getNomiProgetti(JSONArray listaProgetti){
        List<String> result = new ArrayList<>();
        for(int i = 0; i<listaProgetti.length(); i++){
            try {
                result.add(listaProgetti.getJSONObject(i).getString("nome"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String getTipo (JSONObject progetto){
        try {
            return progetto.getString("tipo");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  null;
    }

    public String getLink (JSONObject progetto){
        try {
            return progetto.getString("link");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
