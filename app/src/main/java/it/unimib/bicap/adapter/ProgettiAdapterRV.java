package it.unimib.bicap.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import it.unimib.bicap.PresentazioneProgetto;
import it.unimib.bicap.R;
import it.unimib.bicap.Survey;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class ProgettiAdapterRV extends RecyclerView.Adapter<ProgettiAdapterRV.MyViewHolder> {

    Context context;
    public static List <String> nomi;
    public static List <String> descrizioni;
    String from;
    LayoutInflater layoutInflater;
    public static  JSONArray listaProgetti;
    GetterInfo getterInfo = new GetterLocal();
    ProgettiAdapterRV istanzaProgettiAdapter;
    String descrizionegoogle;

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

    public ProgettiAdapterRV(Context context, JSONArray progetti, String from){
        this.context = context;
        nomi = getterInfo.getNomiProgetti(progetti);
        descrizioni = getterInfo.getDescrizioniProgetti(progetti);
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
            holder.nome.setText(nomi.get(position));
            holder.descrizione1.setText(descrizioni.get(position));
            holder.info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject p = getterInfo.getProgetto(listaProgetti, position);
                    Intent intentProg = new Intent(context, PresentazioneProgetto.class);
                    intentProg.putExtra("obj", p.toString());
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
        return nomi.size();
    }

    public static void setNomi(List<String> nomi) {
        ProgettiAdapterRV.nomi = nomi;
    }
}
