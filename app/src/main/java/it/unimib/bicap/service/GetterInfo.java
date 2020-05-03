package it.unimib.bicap.service;

import org.json.JSONArray;
import org.json.JSONObject;

public interface GetterInfo {
    String getIdProgetto(JSONObject progetto);
    String getNomeProgetto(JSONObject progetto);
    String getDescrizione(JSONObject progetto);
    String getAutore(JSONObject progetto);
    JSONObject getProgetto(JSONArray progetti,int index);
    JSONArray getPassi (JSONObject progetti);
    JSONObject getPasso (JSONArray passi,int index);
    int getNPassi(JSONArray passi);
}