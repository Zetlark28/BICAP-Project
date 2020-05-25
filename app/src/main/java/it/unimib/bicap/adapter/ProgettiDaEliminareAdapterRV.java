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
import it.unimib.bicap.service.EliminaDialog;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class ProgettiDaEliminareAdapterRV extends RecyclerView.Adapter<ProgettiDaEliminareAdapterRV.MyViewHolder> {

    private Context context;
    private static List<String> nomi;
    private String from;
    private LayoutInflater layoutInflater;
    public static JSONArray listaProgetti;
    private JSONObject listaProgettiTot;
    GetterInfo getterInfo = new GetterLocal();
    private EliminaProgetti eliminaActivity;
    ProgettiDaEliminareAdapterRV istanzaProgettiAdapter;

    public static void setListaProgetti(JSONArray listaProgetti) {
        ProgettiDaEliminareAdapterRV.listaProgetti = listaProgetti;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome;
        Button elimina;

        public MyViewHolder (View itemView){
            super(itemView);
            nome = itemView.findViewById(R.id.idNomeProgetto);
            elimina = itemView.findViewById(R.id.btnElimina);
        }
    }

    public ProgettiDaEliminareAdapterRV(Context context, JSONArray progettiAutore, JSONObject listaProgettiTot, EliminaProgetti eliminaActivity, String from){
        this.context = context;
        nomi = getterInfo.getNomiProgetti(progettiAutore);
        this.from = from;
        listaProgetti = progettiAutore;
        this.listaProgettiTot = listaProgettiTot;
        this.eliminaActivity = eliminaActivity;
        this.istanzaProgettiAdapter =this;
        layoutInflater = (LayoutInflater.from(context));
    }
    public ProgettiDaEliminareAdapterRV(Context context, JSONArray progetti, String from){
        this.context = context;
        nomi = getterInfo.getNomiProgetti(progetti);
        this.from = from;
        listaProgetti=progetti;
        this.istanzaProgettiAdapter =this;
        layoutInflater = (LayoutInflater.from(context));
    }

    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        View v = layoutInflater.inflate(R.layout.activity_item_elimina_progetto, parent, false);
        MyViewHolder mV = new MyViewHolder(v);

        return mV;
    }

    public void onBindViewHolder (final MyViewHolder holder, final int position){
            holder.nome.setText(nomi.get(position));
            holder.elimina.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EliminaDialog eliminaDialog = null;
                    eliminaDialog = new EliminaDialog(listaProgetti, listaProgettiTot, position, istanzaProgettiAdapter, eliminaActivity);
                    eliminaDialog.show(eliminaActivity.getSupportFragmentManager(), "prova");
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
