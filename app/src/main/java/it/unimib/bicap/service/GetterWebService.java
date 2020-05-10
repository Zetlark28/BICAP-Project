package it.unimib.bicap.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class GetterWebService implements GetterInfo {


    @Override
    public String getIdProgetto(JSONObject progetto) {
        return null;
    }

    @Override
    public String getNomeProgetto(JSONObject progetto) {
        return null;
    }

    @Override
    public String getDescrizione(JSONObject progetto) {
        return null;
    }

    @Override
    public String getAutore(JSONObject progetto) {
        return null;
    }

    @Override
    public JSONObject getProgetto(JSONArray progetti, int index) {
        return null;
    }

    @Override
    public JSONArray getPassi(JSONObject progetti) {
        return null;
    }

    @Override
    public JSONObject getPasso(JSONArray passi, int index) {
        return null;
    }

    @Override
    public int getNPassi(JSONArray passi) {
        return 0;
    }

    @Override
    public JSONArray getlistaProgetti(String stringProgetti) {
        return null;
    }

    @Override
    public List<String> getNomiProgetti(JSONArray listaProgetti) {
        return null;
    }
}
