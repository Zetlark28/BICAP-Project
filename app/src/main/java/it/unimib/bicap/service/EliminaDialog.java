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

import it.unimib.bicap.EliminaProgetti;
import it.unimib.bicap.adapter.ProgettiDaEliminareAdapterRV;

public class EliminaDialog extends AppCompatDialogFragment {

    private String nomeProgetto;
    private JSONArray listaProgetti;
    private Integer index;
    private ProgettiDaEliminareAdapterRV progettiAdapterRV;
    private JSONObject listaProgettiTot;
    private EliminaProgetti activity;
    private GetterInfo getterInfo = new GetterLocal();
    public EliminaDialog(JSONArray listaProgetti, JSONObject listaProgettiTot, Integer index, ProgettiDaEliminareAdapterRV istanzaProgettiAdapter, EliminaProgetti eliminaActivity){
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

    //public EliminaDialog();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Elimina")
                .setMessage("Sicuro di voler eliminare il progetto\n"+ nomeProgetto + "?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONArray listaNuova = new JSONArray();
                        JSONObject nuoviProgetti = null;
                        Integer idElimina = null;
                        //primo for serve  per identificare il json da eliminare all'interno della lista intera di progetti
                        //secondo for serve per inizializzare una nuova lista di jsonObject che l'utente vedrà senza inserire il json identificato precedentemente
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
                            //crea un nuovo jsonObject dove inserisce il JsonArray ricavato precedentemente
                            nuoviProgetti = new JSONObject();
                            nuoviProgetti.put("progetti",nuovaListaTotProgetti);
                            Log.d("oggetto", nuoviProgetti.toString());
                    } catch (JSONException e) {
                            e.printStackTrace();
                    }
                        //TODO: dialog on process e metodo di riscrittura file progetti.json
                        Utility.write(nuoviProgetti,activity,null);
                        ProgettiDaEliminareAdapterRV.setNomi(getterInfo.getNomiProgetti(listaNuova));
                            ProgettiDaEliminareAdapterRV.setListaProgetti(listaNuova);
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
}
