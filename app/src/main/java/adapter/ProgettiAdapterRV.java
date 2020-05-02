package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import it.unimib.bicap.R;

public class ProgettiAdapterRV extends RecyclerView.Adapter<ProgettiAdapterRV.MyViewHolder> {

    Context context;
    String [] nomi;
    LayoutInflater layoutInflater;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome;

        public MyViewHolder (View itemView){
            super(itemView);
            nome = itemView.findViewById(R.id.idNomeProgetto);
        }
    }

    public ProgettiAdapterRV (Context context, String [] progetti){
        this.context = context;
        this.nomi = progetti;
        layoutInflater = (LayoutInflater.from(context));
    }

    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        View v = layoutInflater.inflate(R.layout.activity_item_progetto, parent, false);
        MyViewHolder mV = new MyViewHolder(v);

        return mV;
    }

    public void onBindViewHolder (MyViewHolder holder, final int position){
        holder.nome.setText(nomi[position]);
    }

    public int getItemCount (){
        return nomi.length;
    }

}
