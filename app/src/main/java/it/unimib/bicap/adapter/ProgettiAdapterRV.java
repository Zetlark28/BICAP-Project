package it.unimib.bicap.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import it.unimib.bicap.EliminaProgetti;
//import it.unimib.bicap.PresentazioneProgetto;
import it.unimib.bicap.PresentazioneProgetto;
import it.unimib.bicap.R;
import it.unimib.bicap.service.EliminaDialog;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class ProgettiAdapterRV extends RecyclerView.Adapter<ProgettiAdapterRV.MyViewHolder> {

    Context context;
    public static List <String> nomi;
    String from;
    LayoutInflater layoutInflater;
    public static  JSONArray listaProgetti;
    public  JSONObject listaProgettiTot;
    GetterInfo getterInfo = new GetterLocal();
    private EliminaProgetti eliminaActivity;
    ProgettiAdapterRV istanzaProgettiAdapter;

    public static void setListaProgetti(JSONArray listaProgetti) {
        ProgettiAdapterRV.listaProgetti = listaProgetti;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome;

        public MyViewHolder (View itemView){
            super(itemView);
            nome = itemView.findViewById(R.id.idNomeProgetto);
        }
    }

    public ProgettiAdapterRV(Context context, JSONArray progettiAutore, JSONObject listaProgettiTot, EliminaProgetti eliminaActivity, String from){
        this.context = context;
        nomi = getterInfo.getNomiProgetti(progettiAutore);
        this.from = from;
        listaProgetti = progettiAutore;
        this.listaProgettiTot = listaProgettiTot;
        this.eliminaActivity = eliminaActivity;
        this.istanzaProgettiAdapter =this;
        layoutInflater = (LayoutInflater.from(context));
    }
    public ProgettiAdapterRV(Context context, JSONArray progetti, String from){
        this.context = context;
        nomi = getterInfo.getNomiProgetti(progetti);
        this.from = from;
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
        if (from.equals("listaProgetti")) {
            holder.nome.setText(nomi.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
        else if(from.equals("eliminaProgetti")){
            holder.nome.setText(nomi.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EliminaDialog eliminaDialog = null;
                    eliminaDialog = new EliminaDialog(listaProgetti, listaProgettiTot, position,  istanzaProgettiAdapter,eliminaActivity);
                    eliminaDialog.show(eliminaActivity.getSupportFragmentManager(), "prova");
                }
            });

        }else if(from.equals("daFare")){
            holder.nome.setText(nomi.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject p = getterInfo.getProgetto(listaProgetti, position);
                    Intent intentProg = new Intent(context, PresentazioneProgetto.class);
                    intentProg.putExtra("obj", p.toString());
                    context.startActivity(intentProg);
                    ((Activity)context).finish();
                    try {
                        String descrizione = getterInfo.getDescrizione((JSONObject) listaProgetti.get(position));
                        Snackbar.make(v, "Descrizione: " + descrizione, Snackbar.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else if(from.equals("daTerminare")){
            holder.nome.setText(nomi.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public int getItemCount (){
        return nomi.size();
    }

    public static void setNomi(List<String> nomi) {
        ProgettiAdapterRV.nomi = nomi;
    }
}
