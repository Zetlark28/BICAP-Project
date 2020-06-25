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

import it.unimib.bicap.ItemSearch;
import it.unimib.bicap.R;
import it.unimib.bicap.activity.somministratore.EliminaProgetti;
import it.unimib.bicap.activity.somministratore.EliminaSomministratore;
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
    private static List<ItemSearch> exampleList;
    private static List<ItemSearch> exampleListFull;
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

    public EliminaAdapterRV(Context context, JSONArray progettiAutore, List<ItemSearch> exampleList, JSONObject listaProgettiTot, EliminaProgetti eliminaActivity){
        listaProgetti = progettiAutore;
        EliminaAdapterRV.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
        this.listaProgettiTot = listaProgettiTot;
        this.eliminaActivity = eliminaActivity;
        this.istanzaProgettiAdapter =this;
        this.context = context;
        descrizioni = getterInfo.getDescrizioniProgetti(progettiAutore);
        this.layoutInflater = LayoutInflater.from(context);
        this.ricerca = true;
    }

    public EliminaAdapterRV(Context context, List<ItemSearch> exampleList, EliminaSomministratore eliminaActivity){
        EliminaAdapterRV.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
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
        if (holder instanceof  MyViewHolder) {
                MyViewHolder vaultItemHolder = (MyViewHolder) holder;
                ItemSearch currentItem = exampleList.get(position);
                vaultItemHolder.nome.setText(currentItem.getTextNome());
                vaultItemHolder.descrizione1.setText(currentItem.getTextEmail());
                vaultItemHolder.elimina.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EliminaDialog eliminaDialog;
                        String message = "Sei sicuro di voler eliminare il progetto: \n" + exampleList.get(position
                        ).getTextNome() + " ?";
                        eliminaDialog = new EliminaDialog(exampleList, listaProgetti, listaProgettiTot, position, eliminaActivity, message);
                        eliminaDialog.show(eliminaActivity.getSupportFragmentManager(), "prova");
                    }
                });
        } else if (holder instanceof  MyViewHolderSomm){
            MyViewHolderSomm vaultItemHolderSomm = (MyViewHolderSomm) holder;
            final ItemSearch currentItem = exampleList.get(position);
            vaultItemHolderSomm.nome.setText(currentItem.getTextNome());
            vaultItemHolderSomm.emailSomm.setText(currentItem.getTextEmail());
            vaultItemHolderSomm.elimina.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemSearch currentItem = exampleList.get(position);
                    EliminaDialog eliminaDialog;
                    String message = "Sei sicuro di voler eliminare il somministratore: " + currentItem.getTextNome() + "?";
                    eliminaDialog = new EliminaDialog(exampleList, position, eliminaActivitysomm, message);
                    eliminaDialog.show(eliminaActivitysomm.getSupportFragmentManager(), "prova");
                        }

            });
        }
    }

    public int getItemCount (){
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

