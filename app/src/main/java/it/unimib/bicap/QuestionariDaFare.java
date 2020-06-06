package it.unimib.bicap;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.unimib.bicap.adapter.ProgettiAdapterRV;
import it.unimib.bicap.db.DBManager;

public class QuestionariDaFare extends Fragment {

    private static final String TAG = "QuestionariDaFare";
    private static JSONArray progetti;
    private DBManager db=null;
    private JSONObject progettiTot;

    private ProgettiAdapterRV progettiAdapterRV;
    private String from = "daFare";

    public QuestionariDaFare(JSONObject progettiTot) {
        this.progettiTot=progettiTot;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_lista_progetti, container, false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.rvProgetti);

       final String idUtente = "prova";

        try {
            progetti = progettiTot.getJSONArray("progetti");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        db = new DBManager(getContext());


        JSONArray progettiDaFare = new JSONArray();

        try {
        for(int i = 0; i<progetti.length(); i++) {
            if (!db.isCompletato( progetti.getJSONObject(i).getInt("id")))
                if (!db.isDaCompletare( progetti.getJSONObject(i).getInt("id")))
                    progettiDaFare.put(progetti.getJSONObject(i));
        }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        progettiAdapterRV = new ProgettiAdapterRV(getContext(), progettiDaFare,  from);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(progettiAdapterRV);


        return rootView;
    }
}
