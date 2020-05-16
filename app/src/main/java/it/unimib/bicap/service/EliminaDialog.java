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

import it.unimib.bicap.EliminaProgetti;
import adapter.ProgettiAdapterRV;
public class EliminaDialog extends AppCompatDialogFragment {

    private String nomeProgetto;
    private JSONArray listaProgetti;
    private Integer index;
    private EliminaProgetti eliminaActivity;
    private ProgettiAdapterRV progettiAdapterRV;
    public EliminaDialog(JSONArray listaProgetti, Integer index, EliminaProgetti eliminaActivity, ProgettiAdapterRV istanzaProgettiAdapter){
        try {
            this.nomeProgetto=listaProgetti.getJSONObject(index).getString("nome");
            this.listaProgetti=listaProgetti;
            this.index = index;
            this.eliminaActivity=eliminaActivity;
            this.progettiAdapterRV = istanzaProgettiAdapter;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //TODO : fix dialog di cancellazione visuale
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
                        for(int i = 1; i<listaProgetti.length(); i++)
                            if(i!=index) {
                                try {
                                    listaNuova.put(listaProgetti.getJSONObject(i));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        EliminaProgetti.setProgetti(listaNuova);
                        ProgettiAdapterRV.setListaProgetti(listaNuova);
                        progettiAdapterRV.notifyItemRemoved(index);


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
