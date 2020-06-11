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

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import it.unimib.bicap.PresentazioneProgetto;
import it.unimib.bicap.R;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

//import it.unimib.bicap.PresentazioneProgetto;

public class ProgettiDaTerminareAdapterRV extends RecyclerView.Adapter<ProgettiDaTerminareAdapterRV.MyViewHolder> {

    Context context;
    public static List <String> nomi;
    String from;
    LayoutInflater layoutInflater;
    public static List <String> descrizioni;
    public static  JSONArray listaProgetti;
    GetterInfo getterInfo = new GetterLocal();
    ProgettiDaTerminareAdapterRV istanzaProgettiAdapter;

    public static void setListaProgetti(JSONArray listaProgetti) {
        ProgettiDaTerminareAdapterRV.listaProgetti = listaProgetti;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome;
        Button info;
        TextView descrizione1;
        Button start;

        public MyViewHolder (View itemView){
            super(itemView);
            nome = itemView.findViewById(R.id.idNomeProgetto);
            info = itemView.findViewById(R.id.btnInfo);
            start = itemView.findViewById(R.id.btnCrea);
            descrizione1 = itemView.findViewById(R.id.descrizioneprog);
        }
    }
    public ProgettiDaTerminareAdapterRV(Context context, JSONArray progetti, String from){
        this.context = context;
        nomi = getterInfo.getNomiProgetti(progetti);
        this.from = from;
        listaProgetti=progetti;
        descrizioni = getterInfo.getDescrizioniProgetti(progetti);
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
                    intentProg.putExtra("mode","daTerminare");
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

    public int getItemCount (){
        return nomi.size();
    }

    public static void setNomi(List<String> nomi) {
        ProgettiDaTerminareAdapterRV.nomi = nomi;
    }
}
