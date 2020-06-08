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
import java.util.List;
import java.util.Map;

import it.unimib.bicap.EliminaProgetti;
import it.unimib.bicap.EliminaSomministratore;
import it.unimib.bicap.R;
import it.unimib.bicap.service.EliminaDialog;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class ProgettiDaEliminareAdapterRV extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static List<String> nomi;
    private static List<String> nomiSommLista = new ArrayList<>();
    private static List<String> emailSommLista = new ArrayList<>();
    private static HashMap<String, String> nomiSomministratori;
    private static List<String> emails;
    public static JSONArray listaProgetti;
    private JSONObject listaProgettiTot;
    GetterInfo getterInfo = new GetterLocal();
    private EliminaProgetti eliminaActivity;
    private EliminaSomministratore eliminaActivitysomm;
    ProgettiDaEliminareAdapterRV istanzaProgettiAdapter;
    private final static String TAG = "ProgettiDaEliminareAdapter";
    private static final int TYPE_PROJ = 0;
    private static final int TYPE_SOMM = 1;
    private LayoutInflater layoutInflater;
    Context context;
    private String key = "";

    @SuppressLint("LongLogTag")
    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "position: " + position);
        if (eliminaActivity != null)
            return TYPE_PROJ;
        else
            return TYPE_SOMM;
    }

    public static void setListaProgetti(JSONArray listaProgetti) {
        ProgettiDaEliminareAdapterRV.listaProgetti = listaProgetti;
    }

    @SuppressLint("LongLogTag")
    public static void setNomiSomministratori(HashMap<String, String> nomiSomministratori){
        ProgettiDaEliminareAdapterRV.nomiSomministratori = nomiSomministratori;
        Log.d(TAG, "nuovi nomi: " + nomiSomministratori.toString());
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome;
        Button elimina;

        public MyViewHolder (View itemView){
            super(itemView);
            nome = itemView.findViewById(R.id.idNomeProgetto);
            elimina = itemView.findViewById(R.id.btnEliminaProgetto);
        }
    }

    public static class MyViewHolderSomm extends  RecyclerView.ViewHolder{
        TextView nome;
        TextView emailSomm;
        Button elimina;

        public MyViewHolderSomm (View itemView){
            super(itemView);
            nome = itemView.findViewById(R.id.tvNomeSommD);
            emailSomm = itemView.findViewById(R.id.tvEmailSommD);
            elimina = itemView.findViewById(R.id.btnEliminaSomm);
        }
    }

    public ProgettiDaEliminareAdapterRV(Context context, JSONArray progettiAutore, JSONObject listaProgettiTot, EliminaProgetti eliminaActivity){
        nomi = getterInfo.getNomiProgetti(progettiAutore);
        listaProgetti = progettiAutore;
        this.listaProgettiTot = listaProgettiTot;
        this.eliminaActivity = eliminaActivity;
        this.istanzaProgettiAdapter =this;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @SuppressLint("LongLogTag")
    public ProgettiDaEliminareAdapterRV(Context context, HashMap<String, String> nomiSomm, List<String> emails, EliminaSomministratore eliminaActivity){
        this.nomiSomministratori = nomiSomm;
        for (Map.Entry<String, String> entry : nomiSomministratori.entrySet()) {
            this.nomiSommLista.add(entry.getValue());
        }
        Log.d(TAG, "HashMap: " + nomiSomministratori.toString());
        this.emailSommLista = emails;
        this.eliminaActivitysomm = eliminaActivity;
        this.istanzaProgettiAdapter = this;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){

        if (viewType == TYPE_PROJ){
            View view = this.layoutInflater.inflate(R.layout.activity_item_elimina_progetto, parent, false);
            return new MyViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_elimina_somministratore, parent, false);
            return new MyViewHolderSomm(view);
        }
        /*View v = layoutInflater.inflate(R.layout.activity_item_elimina_progetto, parent, false);
        MyViewHolder mV = new MyViewHolder(v);
        return mV;*/
    }


    @SuppressLint("LongLogTag")
    public void onBindViewHolder (final RecyclerView.ViewHolder holder, final int position) {
        Log.d(TAG, "eliminaActivity: " + eliminaActivity);
        Log.d(TAG, "type: " + holder.getItemViewType());
        if (holder instanceof  MyViewHolder) {
                MyViewHolder vaultItemHolder = (MyViewHolder) holder;
                vaultItemHolder.nome.setText(nomi.get(position));
                vaultItemHolder.elimina.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EliminaDialog eliminaDialog = null;
                        String message = "Sei sicuto di voler eliminare il progetto: " + nomi.get(position) + " ?";
                        eliminaDialog = new EliminaDialog(listaProgetti, listaProgettiTot, position, istanzaProgettiAdapter, eliminaActivity, message);
                        eliminaDialog.show(eliminaActivity.getSupportFragmentManager(), "prova");
                    }
                });
        } else if (holder instanceof  MyViewHolderSomm){
            MyViewHolderSomm vaultItemHolderSomm = (MyViewHolderSomm) holder;
            Log.d(TAG, "email1: " + emailSommLista.toString());
            key = emailSommLista.get(position);
            Log.d(TAG, "email: " + emailSommLista.get(position));
            Log.d(TAG, "chiave: " + key);
            vaultItemHolderSomm.nome.setText(nomiSomministratori.get(key));
            Log.d(TAG, "nome Somm rimasto: " + nomiSomministratori.get(key));
            vaultItemHolderSomm.emailSomm.setText(key);
            vaultItemHolderSomm.elimina.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EliminaDialog eliminaDialog = null;
                    String key = emailSommLista.get(position);
                    String message = "Sei sicuro di voler eliminare il somministratore: " + nomiSomministratori.get(key
                    ) + "?";
                    eliminaDialog = new EliminaDialog(nomiSomministratori, key, position, istanzaProgettiAdapter, eliminaActivitysomm, message);
                    eliminaDialog.show(eliminaActivitysomm.getSupportFragmentManager(), "prova");

                    }
            });
        }
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

    public static void setNomiSomm (List<String> nomi){
        ProgettiDaEliminareAdapterRV.nomiSommLista = nomi;
    }

    public static void setEmailSomm (List<String> email){
        ProgettiDaEliminareAdapterRV.emailSommLista = email;
    }
}

//TODO: implementare controllo quanado la lista dei progetti rimane vuota (Adapter non funziona altrimenti errore nel getJSONArray())
