package it.unimib.bicap.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.unimib.bicap.adapter.ProgettiAdapterRV;
import it.unimib.bicap.EliminaProgetti;

public class EliminaDialog extends AppCompatDialogFragment {

    private String nomeProgetto;
    private JSONArray listaProgetti;
    private Integer index;
    private ProgettiAdapterRV progettiAdapterRV;
    private JSONObject listaProgettiTot;
    private EliminaProgetti activity;
    private GetterInfo getterInfo = new GetterLocal();
    public EliminaDialog(JSONArray listaProgetti, JSONObject listaProgettiTot, Integer index, ProgettiAdapterRV istanzaProgettiAdapter, EliminaProgetti eliminaActivity){
        try {
            this.nomeProgetto=listaProgetti.getJSONObject(index).getString("nome");
            this.listaProgetti=listaProgetti;
            this.index = index;
            this.progettiAdapterRV = istanzaProgettiAdapter;
            this.listaProgettiTot = listaProgettiTot;
            this.activity = eliminaActivity;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Elimina")
                .setMessage("Sicuro di voler eliminare il progetto \n "+ nomeProgetto)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONArray listaNuova = new JSONArray();
                        JSONObject nuoviProgetti = null;
                        Integer idElimina = null;
                        try {
                            for(int i = 0; i<listaProgetti.length(); i++) {
                                if (i != index)
                                    listaNuova.put(listaProgetti.getJSONObject(i));
                                else
                                    idElimina=listaProgetti.getJSONObject(i).getInt("id");
                            }
                            JSONArray lista = listaProgettiTot.getJSONArray("progetti");
                            JSONArray nuovaListaTotProgetti = new JSONArray();
                            for(int j=0; j<lista.length(); j++) {
                                int idnuovo = lista.getJSONObject(j).getInt("id");
                                if (idnuovo != idElimina) {
                                    nuovaListaTotProgetti.put(lista.getJSONObject(j));
                                }
                            }
                            nuoviProgetti = new JSONObject();
                            nuoviProgetti.put("progetti",nuovaListaTotProgetti);
                            Log.d("oggetto", nuoviProgetti.toString());
                    } catch (JSONException e) {
                            e.printStackTrace();
                    }
                        //TODO: dialog on process e metodo di riscrittura file progetti.json
                        Utility.write(nuoviProgetti,activity,null);
                        ProgettiAdapterRV.setNomi(getterInfo.getNomiProgetti(listaNuova));
                            ProgettiAdapterRV.setListaProgetti(listaNuova);
                            progettiAdapterRV.notifyDataSetChanged();
                        dismiss();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return  builder.create();
    }

    //DA TENERE NEL CASO SERVE
  /*  public class EliminaTask extends AsyncTask<Integer, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(Integer ...id) {
            JSONArray totProgetti = null;
            JSONObject nuoviProgetti = null;
            Log.d("oggetto", "dentro");
            try {
                totProgetti = listaProgettiTot.getJSONArray("progetti");
                JSONArray nuovaListaTotProgetti = new JSONArray();
                for(int j=0; j<totProgetti.length(); j++){
                  int idnuovo=  totProgetti.getJSONObject(j).getInt("id");
                    if(idnuovo!=id[0]){
                        nuovaListaTotProgetti.put(nuovaListaTotProgetti.put(totProgetti.getJSONObject(j)));
                    }
                }
                nuoviProgetti = new JSONObject();
                nuoviProgetti.put("progetti",nuovaListaTotProgetti);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("oggetto", "finito");
          return nuoviProgetti;
        }


        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            //TODO: write file json and upload

            Log.d("oggetto", result.toString());
        }


    }*/

  /* public void write(JSONObject progetti){
        try {
            Writer output;
            FileOutputStream fOut = openFileOutput(FILE_NAME, MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(progetti.toString());
            osw.flush();
            Toast.makeText(getApplicationContext(), "Composition saved", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        filePath = Uri.parse("file:///data/data/it.unimib.bicap/files/progetti.json");
        uploadFile("Progetti/progetti.json");

    }*/
}
