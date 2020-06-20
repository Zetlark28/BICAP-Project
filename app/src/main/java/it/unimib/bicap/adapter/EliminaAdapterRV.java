package it.unimib.bicap.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.unimib.bicap.activity.somministratore.EliminaProgetti;
import it.unimib.bicap.activity.somministratore.EliminaSomministratore;
import it.unimib.bicap.activity.somministratore.ExampleItem;
import it.unimib.bicap.R;
import it.unimib.bicap.service.EliminaDialog;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class EliminaAdapterRV extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public static List <String> descrizioni;
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
    private static List<ExampleItem> exampleLista;
    private static List<ExampleItem> exampleListaPiena;
    private boolean ricerca;

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

    public EliminaAdapterRV(Context context, JSONArray progettiAutore, List<ExampleItem> exampleLista, JSONObject listaProgettiTot, EliminaProgetti eliminaActivity){
        listaProgetti = progettiAutore;
        EliminaAdapterRV.exampleLista = exampleLista;
        exampleListaPiena = new ArrayList<>(exampleLista);
        this.listaProgettiTot = listaProgettiTot;
        this.eliminaActivity = eliminaActivity;
        this.istanzaProgettiAdapter =this;
        this.context = context;
        descrizioni = getterInfo.getDescrizioniProgetti(progettiAutore);
        this.layoutInflater = LayoutInflater.from(context);
        this.ricerca = true;
    }
    public EliminaAdapterRV(Context context, List<ExampleItem> exampleLista, EliminaSomministratore eliminaActivity){
        EliminaAdapterRV.exampleLista = exampleLista;
        exampleListaPiena = new ArrayList<>(exampleLista);
        this.context = context;
        this.eliminaActivitysomm = eliminaActivity;
        this.istanzaProgettiAdapter = this;
        this.ricerca = false;
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){

        if (viewType == TYPE_PROJ){
            View view = this.layoutInflater.inflate(R.layout.activity_item_elimina_progetto, parent, false);
            return new MyViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_elimina_somministratore, parent, false);
            return new MyViewHolderSomm(view);
        }
    }

    @SuppressLint("LongLogTag")
    public void onBindViewHolder (final RecyclerView.ViewHolder holder, final int position) {
        Log.d(TAG, "eliminaActivity: " + eliminaActivity);
        Log.d(TAG, "type: " + holder.getItemViewType());
        if (holder instanceof  MyViewHolder) {
                MyViewHolder vaultItemHolder = (MyViewHolder) holder;
                ExampleItem currentItem = exampleLista.get(position);
                vaultItemHolder.nome.setText(currentItem.getTextNome());
                vaultItemHolder.descrizione1.setText(currentItem.getTextEmail());
                vaultItemHolder.elimina.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EliminaDialog eliminaDialog;
                        String message = "Sei sicuro di voler eliminare il progetto: \n" + exampleLista.get(position
                        ).getTextNome() + " ?";
                        eliminaDialog = new EliminaDialog(exampleLista, listaProgetti, listaProgettiTot, position, eliminaActivity, message);
                        eliminaDialog.show(eliminaActivity.getSupportFragmentManager(), "prova");
                    }
                });
        } else if (holder instanceof  MyViewHolderSomm){
            MyViewHolderSomm vaultItemHolderSomm = (MyViewHolderSomm) holder;
            ExampleItem currentItem = exampleLista.get(position);
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
                    ExampleItem currentItem = exampleLista.get(position);
                    EliminaDialog eliminaDialog = null;
                    String key = currentItem.getTextEmail();
                    /*String message = "Sei sicuro di voler eliminare il somministratore: " + nomiSomministratori.get(key
                    ) + "?";*/
                    String message = "Sei sicuro di voler eliminare il somministratore: " + exampleLista.get(position
                    ).getTextNome() + "?";
                    eliminaDialog = new EliminaDialog(exampleLista, position, eliminaActivitysomm, message);
                    eliminaDialog.show(eliminaActivitysomm.getSupportFragmentManager(), "prova");

                    }
            });
        }
    }

    public int getItemCount (){
        if (eliminaActivity != null)
            return exampleLista.size();
        else
            return exampleLista.size();
    }


    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ExampleItem> filteredList = new ArrayList<>();

            if (ricerca) {

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(exampleListaPiena);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (ExampleItem item : exampleListaPiena) {
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
                    filteredList.addAll(exampleListaPiena);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (ExampleItem item : exampleListaPiena) {
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
            exampleLista.clear();
            exampleLista.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}

