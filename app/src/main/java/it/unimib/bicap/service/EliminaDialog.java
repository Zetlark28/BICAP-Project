package it.unimib.bicap.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;

import adapter.ProgettiAdapterRV;
public class EliminaDialog extends AppCompatDialogFragment {

    private String nomeProgetto;
    private JSONArray listaProgetti;
    private Integer index;
    private ProgettiAdapterRV progettiAdapterRV;
    private EliminaDialog instance;
    private GetterInfo getterInfo = new GetterLocal();
    public EliminaDialog(JSONArray listaProgetti, Integer index, ProgettiAdapterRV istanzaProgettiAdapter){
        try {
            this.nomeProgetto=listaProgetti.getJSONObject(index).getString("nome");
            this.listaProgetti=listaProgetti;
            this.index = index;
            this.progettiAdapterRV = istanzaProgettiAdapter;
            this.instance=this;
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
                        for(int i = 0; i<listaProgetti.length(); i++)
                            if(i!=index) {
                                try {
                                    listaNuova.put(listaProgetti.getJSONObject(i));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            ProgettiAdapterRV.setNomi(getterInfo.getNomiProgetti(listaNuova));
                            ProgettiAdapterRV.setListaProgetti(listaNuova);
                            progettiAdapterRV.notifyDataSetChanged();

                            //TODO: dialog on process e metodo di riscrittura file progetti.json
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
