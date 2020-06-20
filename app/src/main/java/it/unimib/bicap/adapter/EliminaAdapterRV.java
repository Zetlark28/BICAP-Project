package it.unimib.bicap.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unimib.bicap.ItemSearch;
import it.unimib.bicap.R;
import it.unimib.bicap.activity.somministratore.EliminaProgetti;
import it.unimib.bicap.activity.somministratore.EliminaSomministratore;
import it.unimib.bicap.service.EliminaDialog;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class EliminaAdapterRV extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static List<String> nomi;
    private static List<String> nomiSommLista = new ArrayList<>();
    private static List<String> emailSommLista = new ArrayList<>();
    public static List <String> descrizioni;
    private static HashMap<String, String> nomiSomministratori;
    private static List<String> emails;
    public static JSONArray listaProgetti;
    private JSONObject listaProgettiTot;
    GetterInfo getterInfo = new GetterLocal();
    private EliminaProgetti eliminaActivity;
    private EliminaSomministratore eliminaActivitysomm;
    EliminaAdapterRV istanzaProgettiAdapter;
    private final static String TAG = "ProgettiDaEliminareAdapter";
    private static final int TYPE_PROJ = 0;
    private static final int TYPE_SOMM = 1;
    private LayoutInflater layoutInflater;
    Context context;
    private String key = "";
    private List<String> nomiSommListaFull;
    private static List<ItemSearch> exampleList;
    private static List<ItemSearch> exampleListFull;
    private boolean ricerca;
    private SharedPreferences.Editor editor;

    @SuppressLint("LongLogTag")
    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "position: " + position);
        if (eliminaActivity != null)
            return TYPE_PROJ;
        else
            return TYPE_SOMM;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome;
        Button elimina;
        TextView descrizione1;

        public MyViewHolder (View itemView){
            super(itemView);
            nome = itemView.findViewById(R.id.idNomeProgetto);
            elimina = itemView.findViewById(R.id.btnEliminaProgetto);
            descrizione1 = itemView.findViewById(R.id.descrizioneprog);
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

    public EliminaAdapterRV(Context context, JSONArray progettiAutore, List<ItemSearch> exampleList, JSONObject listaProgettiTot, EliminaProgetti eliminaActivity){
        nomi = getterInfo.getNomiProgetti(progettiAutore);
        listaProgetti = progettiAutore;
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
        this.listaProgettiTot = listaProgettiTot;
        this.eliminaActivity = eliminaActivity;
        this.istanzaProgettiAdapter =this;
        this.context = context;
        descrizioni = getterInfo.getDescrizioniProgetti(progettiAutore);
        this.layoutInflater = LayoutInflater.from(context);
        this.ricerca = true;
    }

    @SuppressLint("LongLogTag")
    /*public ProgettiDaEliminareAdapterRV(Context context, HashMap<String, String> nomiSomm, List<String> emails, EliminaSomministratore eliminaActivity){
        this.nomiSomministratori = nomiSomm;
        for (Map.Entry<String, String> entry : nomiSomministratori.entrySet()) {
            this.nomiSommLista.add(entry.getValue());
        }
        nomiSommListaFull = new ArrayList<>(nomiSommLista);
        Log.d(TAG, "HashMap: " + nomiSomministratori.toString());
        this.emailSommLista = emails;
        this.eliminaActivitysomm = eliminaActivity;
        this.istanzaProgettiAdapter = this;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }*/

    public EliminaAdapterRV(Context context, List<ItemSearch> exampleList, EliminaSomministratore eliminaActivity){
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
        this.context = context;
        this.eliminaActivitysomm = eliminaActivity;
        this.istanzaProgettiAdapter = this;
        this.ricerca = false;
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
                ItemSearch currentItem = exampleList.get(position);
                //vaultItemHolder.nome.setText(nomi.get(position));
                vaultItemHolder.nome.setText(currentItem.getTextNome());
                //vaultItemHolder.descrizione1.setText(descrizioni.get(position));
                vaultItemHolder.descrizione1.setText(currentItem.getTextEmail());
                vaultItemHolder.elimina.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EliminaDialog eliminaDialog = null;
                        //String message = "Sei sicuto di voler eliminare il progetto: \n" + nomi.get(position) + " ?";
                        String message = "Sei sicuro di voler eliminare il progetto: \n" + exampleList.get(position
                        ).getTextNome() + " ?";
                        eliminaDialog = new EliminaDialog(exampleList, listaProgetti, listaProgettiTot, position, istanzaProgettiAdapter, eliminaActivity, message);
                        eliminaDialog.show(eliminaActivity.getSupportFragmentManager(), "prova");
                    }
                });
        } else if (holder instanceof  MyViewHolderSomm){
            MyViewHolderSomm vaultItemHolderSomm = (MyViewHolderSomm) holder;
            final ItemSearch currentItem = exampleList.get(position);
            //Log.d(TAG, "email1: " + emailSommLista.toString());
            //key = emailSommLista.get(position);
            //Log.d(TAG, "email: " + emailSommLista.get(position));
            //Log.d(TAG, "chiave: " + key);
            //vaultItemHolderSomm.nome.setText(nomiSomministratori.get(key));
            vaultItemHolderSomm.nome.setText(currentItem.getTextNome());
            //Log.d(TAG, "nome Somm rimasto: " + nomiSomministratori.get(key));
            //vaultItemHolderSomm.emailSomm.setText(key);
            vaultItemHolderSomm.emailSomm.setText(currentItem.getTextEmail());
            vaultItemHolderSomm.elimina.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (sommModificati != null && sommModificati.contains(currentItem.getTextEmail())){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Attenzione");
                        builder.setMessage("Per eliminare un somministratore precedentemente riattivato o aggiunto, si prega di riavviare l'app");
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        builder.show();
                    }
                    else {*/
                        ItemSearch currentItem = exampleList.get(position);
                        EliminaDialog eliminaDialog = null;
                        String key = currentItem.getTextEmail();
                    /*String message = "Sei sicuro di voler eliminare il somministratore: " + nomiSomministratori.get(key
                    ) + "?";*/
                        String message = "Sei sicuro di voler eliminare il somministratore: " + exampleList.get(position
                        ).getTextNome() + "?";
                        eliminaDialog = new EliminaDialog(exampleList, key, position, istanzaProgettiAdapter, eliminaActivitysomm, message);
                        eliminaDialog.show(eliminaActivitysomm.getSupportFragmentManager(), "prova");
                        }

            });
        }
    }

    public int getItemCount (){
        if (eliminaActivity != null)
            //return nomi.size();
            return exampleList.size();
        else
            //return nomiSomministratori.size();
            return exampleList.size();
    }


    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ItemSearch> filteredList = new ArrayList<>();

            if (ricerca) {

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(exampleListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (ItemSearch item : exampleListFull) {
                        if (item.getTextNome().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();

                results.values = filteredList;

                return results;
            }

            else {
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(exampleListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (ItemSearch item : exampleListFull) {
                        if (item.getTextNome().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();

                results.values = filteredList;

                return results;
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            exampleList.clear();
            exampleList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}

//TODO: implementare controllo quanado la lista dei progetti rimane vuota (Adapter non funziona altrimenti errore nel getJSONArray())
