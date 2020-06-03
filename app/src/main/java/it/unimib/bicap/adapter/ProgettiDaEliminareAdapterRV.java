package it.unimib.bicap.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import it.unimib.bicap.EliminaProgetti;
import it.unimib.bicap.EliminaSomministratore;
import it.unimib.bicap.R;
import it.unimib.bicap.service.EliminaDialog;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class ProgettiDaEliminareAdapterRV extends RecyclerView.Adapter<ProgettiDaEliminareAdapterRV.MyViewHolder> {

    private static List<String> nomi;
    private static HashMap<String, String> nomiSomministratori;
    private static List<String> emails;
    private LayoutInflater layoutInflater;
    public static JSONArray listaProgetti;
    private JSONObject listaProgettiTot;
    GetterInfo getterInfo = new GetterLocal();
    private EliminaProgetti eliminaActivity;
    private EliminaSomministratore eliminaActivitysomm;
    ProgettiDaEliminareAdapterRV istanzaProgettiAdapter;
    private final String TAG = "ProgettiDaEliminareAdapter";

    private boolean context = true;

    public static void setListaProgetti(JSONArray listaProgetti) {
        ProgettiDaEliminareAdapterRV.listaProgetti = listaProgetti;
    }

    public static void setSomministratori(HashMap<String, String> nuoviSomm){
        ProgettiDaEliminareAdapterRV.nomiSomministratori = nuoviSomm;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome;
        Button elimina;

        public MyViewHolder (View itemView){
            super(itemView);
            nome = itemView.findViewById(R.id.idNomeProgetto);
            elimina = itemView.findViewById(R.id.btnElimina);
        }
    }

    public ProgettiDaEliminareAdapterRV(Context context, JSONArray progettiAutore, JSONObject listaProgettiTot, EliminaProgetti eliminaActivity){
        nomi = getterInfo.getNomiProgetti(progettiAutore);
        listaProgetti = progettiAutore;
        this.listaProgettiTot = listaProgettiTot;
        this.eliminaActivity = eliminaActivity;
        this.istanzaProgettiAdapter =this;
        layoutInflater = (LayoutInflater.from(context));
    }

    @SuppressLint("LongLogTag")
    public ProgettiDaEliminareAdapterRV(Context context, HashMap<String, String> nomiSomm, List<String> emails, EliminaSomministratore eliminaActivity){
        nomiSomministratori = nomiSomm;
        Log.d(TAG, "HashMap: " + nomiSomm.toString());
        this.emails = emails;
        this.eliminaActivitysomm = eliminaActivity;
        this.istanzaProgettiAdapter = this;
        layoutInflater = (LayoutInflater.from(context));
        this.context = false;
    }

    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        if(this.context) {
            View v = layoutInflater.inflate(R.layout.activity_item_elimina_progetto, parent, false);
            MyViewHolder mV = new MyViewHolder(v);
            return mV;
        }
        View v = layoutInflater.inflate(R.layout.activity_item_elimina_somministratore, parent, false);
        MyViewHolder mV = new MyViewHolder(v);
        return mV;
    }

    @SuppressLint("LongLogTag")
    public void onBindViewHolder (final MyViewHolder holder, final int position){
        if (eliminaActivity != null)
                holder.nome.setText(nomi.get(position));
        else {
            String key = emails.get(position);
            Log.d(TAG, "chiave: " + key);
            holder.nome.setText(nomiSomministratori.get(key));
        }

            holder.elimina.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EliminaDialog eliminaDialog = null;
                    if (eliminaActivity != null) {
                        eliminaDialog = new EliminaDialog(listaProgetti, listaProgettiTot, position, istanzaProgettiAdapter, eliminaActivity);
                        eliminaDialog.show(eliminaActivity.getSupportFragmentManager(), "prova");
                    }
                    else {
                        String key = emails.get(position);
                        eliminaDialog = new EliminaDialog(nomiSomministratori, key, position, istanzaProgettiAdapter, eliminaActivitysomm);
                        eliminaDialog.show(eliminaActivitysomm.getSupportFragmentManager(), "prova");
                    }
                }
            });
        }

    public int getItemCount (){
        if (eliminaActivity != null)
            return nomi.size();
        else
            return nomiSomministratori.size();
    }

    public static void setNomi(List<String> nomi) {
        ProgettiDaEliminareAdapterRV.nomi = nomi;
    }
}

//TODO: implementare controllo quanado la lista dei progetti rimane vuota (Adapter non funziona altrimenti errore nel getJSONArray())
