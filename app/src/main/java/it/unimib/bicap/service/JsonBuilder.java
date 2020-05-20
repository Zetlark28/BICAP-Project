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

    public  JSONObject creaPasso(String tipo, String link) {
        JSONObject passo = new JSONObject();
        try {
            passo.put("tipo", tipo);
            passo.put("link", link);
            passo.put("completato", 0);
        } catch (JSONException e) {
            Log.d("errore", "jsonBuilderError");
            e.printStackTrace();
        }
        return passo;
    }


    public  JSONObject creaProgetto(String nomeProgetto, String descrizioneProgetto, String autore)  {
        JSONObject progetto = new JSONObject();

        try {
            progetto.put("nome", nomeProgetto);
            progetto.put("descrizione", descrizioneProgetto);
            progetto.put("autore", autore);
            progetto.put("completato",0);
        } catch (JSONException e) {
            Log.d("errore", "jsonBuilderError");
            e.printStackTrace();
        }


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

    public JSONObject aggiungiProgettoInLista(JSONArray listaProgetti, JSONObject progetto){
        JSONObject listaCompleta  =null;
        try {
            int id= listaProgetti.getJSONObject(listaProgetti.length()-1).getInt("id")+1;
            progetto.put("id",id);
            listaProgetti.put(progetto);
            listaCompleta=new JSONObject();
            listaCompleta.put("progetti",listaProgetti);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listaCompleta;
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
