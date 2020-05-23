package it.unimib.bicap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import it.unimib.bicap.EliminaProgetti;
import it.unimib.bicap.R;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

//import it.unimib.bicap.PresentazioneProgetto;

public class ProgettiDaTerminareAdapterRV extends RecyclerView.Adapter<ProgettiDaTerminareAdapterRV.MyViewHolder> {

    Context context;
    public static List <String> nomi;
    String from;
    LayoutInflater layoutInflater;
    public static  JSONArray listaProgetti;
    public  JSONObject listaProgettiTot;
    GetterInfo getterInfo = new GetterLocal();
    private EliminaProgetti eliminaActivity;
    ProgettiDaTerminareAdapterRV istanzaProgettiAdapter;

    public static void setListaProgetti(JSONArray listaProgetti) {
        ProgettiAdapterRV.listaProgetti = listaProgetti;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome;
        Button info;
        Button start;

        public MyViewHolder (View itemView){
            super(itemView);
            nome = itemView.findViewById(R.id.idNomeProgetto);
            info = itemView.findViewById(R.id.btnInfo);
            start = itemView.findViewById(R.id.btnCrea);
        }
    }

    public ProgettiDaTerminareAdapterRV(Context context, JSONArray progettiAutore, JSONObject listaProgettiTot, EliminaProgetti eliminaActivity, String from){
        this.context = context;
        nomi = getterInfo.getNomiProgetti(progettiAutore);
        this.from = from;
        listaProgetti = progettiAutore;
        this.listaProgettiTot = listaProgettiTot;
        this.eliminaActivity = eliminaActivity;
        this.istanzaProgettiAdapter =this;
        layoutInflater = (LayoutInflater.from(context));
    }
    public ProgettiDaTerminareAdapterRV(Context context, JSONArray progetti, String from){
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

            holder.nome.setText(nomi.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

    }

    public int getItemCount (){
        return nomi.size();
    }

    public static void setNomi(List<String> nomi) {
        ProgettiAdapterRV.nomi = nomi;
    }
}
