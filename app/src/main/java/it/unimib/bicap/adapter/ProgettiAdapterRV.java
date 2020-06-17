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

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.unimib.bicap.EliminaProgetti;
import it.unimib.bicap.ExampleItem;
import it.unimib.bicap.PresentazioneProgetto;
import it.unimib.bicap.QuestionariDaFare;
import it.unimib.bicap.R;
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
    private JSONObject listaProgettiTot;
    private QuestionariDaFare questionariDaFare;
    GetterInfo getterInfo = new GetterLocal();
    ProgettiAdapterRV istanzaProgettiAdapter;
    private static List<ExampleItem> exampleList;
    private static List<ExampleItem> exampleListFull;
    private boolean ricerca;

    public static void setListaProgetti(JSONArray listaProgetti) {
        ProgettiAdapterRV.listaProgetti = listaProgetti;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome;
        TextView descrizione1;
        Button info;
        Button start;

        public MyViewHolder (View itemView){
            super(itemView);
            nome = itemView.findViewById(R.id.idNomeProgetto);
            descrizione1= itemView.findViewById(R.id.descrizioneprog);
            info = itemView.findViewById(R.id.btnInfo);
        }
    }

    public ProgettiAdapterRV(Context context, JSONArray progetti, List<ExampleItem> exampleList, String from){
        this.context = context;
        nomi = getterInfo.getNomiProgetti(progetti);
        descrizioni = getterInfo.getDescrizioniProgetti(progetti);
        this.exampleList = exampleList;
        exampleListFull = new ArrayList<>(exampleList);
        this.from = from;
        this.ricerca = true;
        listaProgetti=progetti;
        this.istanzaProgettiAdapter =this;
        layoutInflater = (LayoutInflater.from(context));
    }

    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        View v = layoutInflater.inflate(R.layout.activity_item_progetto, parent, false);
        MyViewHolder mV = new MyViewHolder(v);

        return mV;
    }

    public void onBindViewHolder (final MyViewHolder holder, final int position){
            holder.nome.setText(nomi.get(position));
            holder.descrizione1.setText(descrizioni.get(position));
            ExampleItem currentItem = exampleList.get(position);
            holder.nome.setText(currentItem.getTextNome());
            holder.descrizione1.setText(currentItem.getTextEmail());
            holder.info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject p = getterInfo.getProgetto(listaProgetti, position);
                    Intent intentProg = new Intent(context, PresentazioneProgetto.class);
                    intentProg.putExtra(ActivityConstants.INTENT_PROGETTO, p.toString());
                    intentProg.putExtra(ActivityConstants.INTENT_MODALITA,"daFare");
                    context.startActivity(intentProg);
                    ((Activity) context).finish();
                    try {
                        String descrizione = getterInfo.getDescrizione((JSONObject) listaProgetti.get(position));
                        Snackbar.make(v, "Descrizione: " + descrizione, Snackbar.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    // TODO: implementare update della lista
    public static void updateList(JSONArray list){
        listaProgetti = list;


    }

    public int getItemCount (){
        if (questionariDaFare != null)
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
            List<ExampleItem> filteredList = new ArrayList<>();

            if (ricerca) {

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(exampleListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (ExampleItem item : exampleListFull) {
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

                    for (ExampleItem item : exampleListFull) {
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

    public static void setNomi(List<String> nomi) {
        ProgettiAdapterRV.nomi = nomi;
    }
}
