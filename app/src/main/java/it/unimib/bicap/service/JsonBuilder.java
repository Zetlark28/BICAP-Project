package it.unimib.bicap.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonBuilder {
    private static JsonBuilder jsonBuilder;

    public static JsonBuilder getJsonBuilder() {
        if(jsonBuilder==null)
            return new JsonBuilder();
        else
            return jsonBuilder;
    }

    private JsonBuilder(){
    }

    public  JSONObject creaPasso(String tipo, String link) throws JSONException {
        JSONObject passo = new JSONObject();

            passo.put("tipo", tipo);
            passo.put("link", link);
            passo.put("completato", 0);

            Log.d("errore", "jsonBuilderError");

      return passo;
    }


    public  JSONObject creaProgetto(String nomeProgetto, String descrizioneProgetto, String autore) throws JSONException {
        JSONObject progetto = new JSONObject();

            progetto.put("nome", nomeProgetto);
            progetto.put("descrizione", descrizioneProgetto);
            progetto.put("autore", autore);
            progetto.put("completato",0);
            Log.d("errore", "jsonBuilderError");

        return progetto;
    }

    public void aggiungiPassoAllaLista(JSONArray listaPassi, JSONObject passo){
        listaPassi.put(passo);
    }

    public void aggiungiListaPassi(JSONObject progetto, JSONArray passi){
        try {
            progetto.put("nPassi",passi.length());
            progetto.put("passi", passi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public  void aggiungiProgettoInLista(JSONArray listaProgetti , JSONObject progetto){
//        listaProgetti.put(progetto);
//    }
//
//    public  JSONObject getListaProgettiDaScrivereInFile(JSONArray listaProgetti) throws JSONException {
//        JSONObject listaDaScrivere = new JSONObject();
//        listaDaScrivere.put("progetti",listaProgetti);
//        return listaDaScrivere;
//    }

}
