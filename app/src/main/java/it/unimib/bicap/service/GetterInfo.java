package it.unimib.bicap.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public interface GetterInfo {
    Integer getIdProgetto(JSONObject progetto);
    String getNomeProgetto(JSONObject progetto);
    String getDescrizione(JSONObject progetto);
    String getAutore(JSONObject progetto);
    JSONObject getProgetto(JSONArray progetti,int index);
    JSONArray getPassi (JSONObject progetti);
    JSONObject getPasso (JSONArray passi,int index);
    int getNPassi(JSONArray passi);
    String getTipo(JSONObject progetto) throws JSONException;

    JSONArray getlistaProgetti(String stringProgetti);

    String getLink (JSONObject progetto);

    List<String> getNomiProgetti(JSONArray listaProgetti);

    List<String> getNomiSomministratori (JSONArray listaSomministratori);
}
