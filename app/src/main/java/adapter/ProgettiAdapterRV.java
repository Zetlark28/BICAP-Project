package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.List;

import it.unimib.bicap.EliminaProgetti;
import it.unimib.bicap.R;
import it.unimib.bicap.service.EliminaDialog;
import it.unimib.bicap.service.GetterInfo;
import it.unimib.bicap.service.GetterLocal;

public class ProgettiAdapterRV extends RecyclerView.Adapter<ProgettiAdapterRV.MyViewHolder> {

    Context context;
    List<String> nomi;
    String from;
    LayoutInflater layoutInflater;
    public static JSONArray listaProgetti;
    GetterInfo getterInfo = new GetterLocal();
    EliminaProgetti eliminaActivity;
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

    public ProgettiAdapterRV(Context context, JSONArray progetti, EliminaProgetti eliminaActivity, String from){
        this.context = context;
        this.nomi = getterInfo.getNomiProgetti(progetti);
        this.from = from;
        listaProgetti=progetti;
        this.eliminaActivity = eliminaActivity;
        this.istanzaProgettiAdapter =this;
        layoutInflater = (LayoutInflater.from(context));
    }
    public ProgettiAdapterRV(Context context, JSONArray progetti, String from){
        this.context = context;
        this.nomi = getterInfo.getNomiProgetti(progetti);
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
                    //TODO : dialog di conferma -> activity questionari da eliminare.
                    //TODO: fix cancellazione visuale e variabili
                    EliminaDialog eliminaDialog = null;
                    eliminaDialog = new EliminaDialog(listaProgetti, position, eliminaActivity, istanzaProgettiAdapter);
                    eliminaDialog.show(eliminaActivity.getSupportFragmentManager(), "prova");
                    ProgettiAdapterRV progettiAdapter = new ProgettiAdapterRV(eliminaActivity.getApplicationContext(), EliminaProgetti.getProgetti(), eliminaActivity,"eliminaProgetti");




                }
            });
        }
    }

    public int getItemCount (){
        return nomi.size();
    }

}
