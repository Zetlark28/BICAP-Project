package it.unimib.bicap.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.unimib.bicap.ItemSearch;
import it.unimib.bicap.R;
import it.unimib.bicap.activity.utente.PresentazioneProgetto;
import it.unimib.bicap.constanti.ActivityConstants;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class ProgettiAdapterRV extends RecyclerView.Adapter<ProgettiAdapterRV.MyViewHolder> {

    Context context;
    public static List <String> nomi;
    public static List <String> descrizioni;
    String from;
    LayoutInflater layoutInflater;
    public static JSONArray listaProgetti;
    GetterInfo getterInfo = new GetterLocal();
    ProgettiAdapterRV istanzaProgettiAdapter;
    private static List<ItemSearch> exampleList;
    private static List<ItemSearch> exampleListFull;
    private boolean ricerca;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome;
        TextView descrizione1;
        Button start;

        public MyViewHolder (View itemView){
            super(itemView);
            nome = itemView.findViewById(R.id.idNomeProgetto);
            descrizione1= itemView.findViewById(R.id.descrizioneprog);
            start = itemView.findViewById(R.id.btnInfo);
        }
    }

    public ProgettiAdapterRV(Context context, JSONArray progetti, List<ItemSearch> exampleList, String from){
        this.context = context;
        nomi = getterInfo.getNomiProgetti(progetti);
        descrizioni = getterInfo.getDescrizioniProgetti(progetti);
        ProgettiAdapterRV.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
        this.from = from;
        this.ricerca = true;
        listaProgetti=progetti;
        this.istanzaProgettiAdapter =this;
        layoutInflater = (LayoutInflater.from(context));
    }

    @NonNull
    public MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){
        View v = layoutInflater.inflate(R.layout.activity_item_progetto, parent, false);

        return new MyViewHolder(v);
    }

    public void onBindViewHolder (final MyViewHolder holder, final int position){
            holder.nome.setText(nomi.get(position));
            holder.descrizione1.setText(descrizioni.get(position));
            ItemSearch currentItem = exampleList.get(position);
            holder.nome.setText(currentItem.getTextNome());
            holder.descrizione1.setText(currentItem.getTextEmail());
            holder.start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject p = getterInfo.getProgetto(listaProgetti, position);
                    Intent intentProg = new Intent(context, PresentazioneProgetto.class);
                    intentProg.putExtra(ActivityConstants.INTENT_PROGETTO, p.toString());
                    intentProg.putExtra(ActivityConstants.INTENT_MODALITA,"daFare");
                    context.startActivity(intentProg);
                    ((Activity) context).finish();
                }
            });
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
