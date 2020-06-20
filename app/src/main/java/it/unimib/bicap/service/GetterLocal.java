package it.unimib.bicap.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.unimib.bicap.exception.GetterException;


public class GetterLocal implements GetterInfo {
    private static JSONObject converter;

    @Override
    public Integer getIdProgetto(JSONObject progetto) {
        try {
            return progetto.getInt("id");
        } catch (JSONException e) {
            throw new GetterException("id").GETTER_JSON_FAIL;
        }
    }

    @Override
    public String getNomeProgetto(JSONObject progetto) {
        try {
            return progetto.getString("nome");
        } catch (JSONException e) {
            throw new GetterException("nome").GETTER_JSON_FAIL;
        }
    }

    @Override
    public String getDescrizione(JSONObject progetto) {
        try {
            return progetto.getString("descrizione");
        } catch (JSONException e) {
            throw new GetterException("descrizione").GETTER_JSON_FAIL;
        }
    }

    @Override
    public String getAutore(JSONObject progetto) {
        try {
            return progetto.getString("autore");
        } catch (JSONException e) {
            throw new GetterException("autore").GETTER_JSON_FAIL;
        }
    }

    @Override
    public JSONObject getProgetto(JSONArray progetti, int index) {
        try {
            return progetti.getJSONObject(index);
        } catch (JSONException e) {
            throw new GetterException("progetto index" + index).GETTER_JSON_FAIL;
        }
    }

    @Override
    public JSONArray getPassi(JSONObject progetti) {
        try {
            return progetti.getJSONArray("passi");
        } catch (JSONException e) {
            throw new GetterException("passi").GETTER_JSON_FAIL;
        }
    }

    @Override
    public JSONObject getPasso(JSONArray passi, int index) {
        try {
            return passi.getJSONObject(index);
        } catch (JSONException e) {
            throw new GetterException("passo index" + index).GETTER_JSON_FAIL;
        }
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
            throw new GetterException("progetti").GETTER_JSON_FAIL;
        }
    }

    @Override
    public List<String> getNomiProgetti(JSONArray listaProgetti){
        List<String> result = new ArrayList<>();
        for(int i = 0; i<listaProgetti.length(); i++){
            try {
                result.add(listaProgetti.getJSONObject(i).getString("nome"));
            } catch (JSONException e) {
                throw new GetterException("nomeProgetto").GETTER_JSON_FAIL;
            }
        }
        return result;
    }

    public List<String> getDescrizioniProgetti(JSONArray listaProgetti){
        List<String> result = new ArrayList<>();
        for(int i = 0; i<listaProgetti.length(); i++){
            try {
                result.add(listaProgetti.getJSONObject(i).getString("descrizione"));
            } catch (JSONException e) {
                throw new GetterException("descrizioni dei progetti").GETTER_JSON_FAIL;
            }
        }
        return result;
    }

    public String getTipo (JSONObject progetto){
        try {
            return progetto.getString("tipo");
        } catch (JSONException e) {
            throw new GetterException("tipo").GETTER_JSON_FAIL;
        }
    }

    public String getLink (JSONObject progetto){
        try {
            return progetto.getString("link");
        } catch (JSONException e) {
            throw new GetterException("link").GETTER_JSON_FAIL;
        }
    }

    public List<String> getNomiSomministratori(JSONArray listaProgetti){
        List<String> result = new ArrayList<>();
        for(int i = 0; i<listaProgetti.length(); i++){
            try {
                if(!result.contains(listaProgetti.getJSONObject(i).getString("nome"))){
                    result.add(listaProgetti.getJSONObject(i).getString("nome"));
                }
            } catch (JSONException e){
                throw new GetterException("lista nomi somministratori").GETTER_JSON_FAIL;
            }
        }
        return result;
    }
}
